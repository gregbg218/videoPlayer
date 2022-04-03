package bootstrap;


import domain.ProgressTracker;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

public class Controller implements Initializable {

    private final AtomicBoolean tracking = new AtomicBoolean();
    private ProgressTracker progressTracker;
    private File currentFile;
    EmbeddedMediaPlayer vlcJMediaPlayer;
    Stage stage ;

    public static Logger logger = LoggerFactory.getLogger(Controller.class);

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private BorderPane borderPane;

    @FXML
    public VBox vBox;

    @FXML
    private ImageView videoImageView;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Button playBtn;

    @FXML
    private Slider timeSlider;

    @FXML
    private Button forwardBtn;

    @FXML
    private Button rewindBtn;


    @FXML
    void openSongMenu(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        currentFile = fileChooser.showOpenDialog(null);
        final String filePath = currentFile.getAbsolutePath();
        stage = Main.getStage();
        stage.setTitle(currentFile.getName());

        stage.setOnCloseRequest(closeEvent ->{
            float currentPosition=vlcJMediaPlayer.status().position();
            progressTracker.addProgress(filePath,currentPosition);
            try {
                serialize(progressTracker,currentFile.getParentFile()+File.separator+"progress.dat");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        setupVlcJ(filePath);

//        if(file.ex)
//        {
//            vlcJMediaPlayer.controls().stop();
//            vlcJMediaPlayer.release();
//            vlcJMediaPlayer.release();
//        }

        DoubleProperty widthProp = videoImageView.fitWidthProperty();
        DoubleProperty heightProp = videoImageView.fitHeightProperty();

        widthProp.bind(Bindings.selectDouble(videoImageView.sceneProperty(), "width"));
        heightProp.bind(Bindings.selectDouble(videoImageView.sceneProperty(), "height"));
        
        timeSlider.setOnMousePressed(mouseEvent -> beginTracking());
        timeSlider.setOnMouseReleased(mouseEvent -> endTracking());
        timeSlider.valueProperty().addListener((obs, oldValue, newValue) -> updateMediaPlayerPosition(newValue.floatValue() / 100));
    }

    @FXML
    void addSubtitles(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File subtitlesFile = fileChooser.showOpenDialog(null);
        vlcJMediaPlayer.subpictures().setSubTitleFile(subtitlesFile);
    }

    @FXML
    void play(ActionEvent event)   {

        try{
            if(vlcJMediaPlayer.status().isPlaying())
            {
                vlcJMediaPlayer.controls().pause();
                playBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/main/resources/icons/play.png"))));
            }
            else
            {
                vlcJMediaPlayer.controls().play();
                playBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/main/resources/icons/pause.png"))));
            }

        }
        catch(FileNotFoundException f)
        {
            System.out.println(f);
            logger.info(f.toString());
        }
    }

    @FXML
    void forward(ActionEvent event) {
        vlcJMediaPlayer.controls().skipTime(10000);
    }

    @FXML
    void rewind(ActionEvent event) {
        vlcJMediaPlayer.controls().skipTime(-10000);

    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        try {
            playBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/main/resources/icons/play.png"))));
            forwardBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/main/resources/icons/forward.png"))));
            rewindBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/main/resources/icons/rewind.png"))));
        } catch (FileNotFoundException e) {
            System.out.println(e);
            logger.info(e.toString());
        }

    }

    public void setupVlcJ(String filePath)
    {
        String serializedFileName = currentFile.getParentFile()+File.separator+"progress.dat";
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        vlcJMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        vlcJMediaPlayer.videoSurface().set(videoSurfaceForImageView(this.videoImageView));
        borderPane.setStyle("-fx-background-color: black;");
        vlcJMediaPlayer.media().startPaused(filePath);
        progressTracker = deSerialize(serializedFileName);
        if(progressTracker.checkProgressMap(filePath))
        {
            vlcJMediaPlayer.controls().setPosition(progressTracker.getProgress(filePath));
        }
        else
        {
            vlcJMediaPlayer.controls().setPosition(0);
        }
        vlcJMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
                Platform.runLater(() -> updateSliderPosition(newPosition));
            }
        });
        timeSlider.setMin(0);
        long durationInMinutes = TimeUnit.MILLISECONDS.toMinutes(vlcJMediaPlayer.status().length());
        timeSlider.setMax(durationInMinutes);
    }

    private synchronized void updateMediaPlayerPosition(float newValue) {
        if (tracking.get()) {
            vlcJMediaPlayer.controls().setPosition(newValue);
        }
    }

    private synchronized void beginTracking() {
        tracking.set(true);
    }

    private synchronized void endTracking() {
        tracking.set(false);
        // This deals with the case where there was an absolute click in the timeline rather than a drag
        vlcJMediaPlayer.controls().setPosition((float) timeSlider.getValue() / 100);
    }

    private synchronized void updateSliderPosition(float newValue) {
        if (!tracking.get()) {
            timeSlider.setValue(newValue * 100);
        }
    }

    public static void serialize(ProgressTracker progressTracker, String serializedFileName ) throws IOException {
        ObjectOutputStream objout = new ObjectOutputStream(new FileOutputStream(new File(serializedFileName)));
        objout.writeObject(progressTracker);
        objout.close();
    }

    public static ProgressTracker deSerialize(String serializedFileName)  {

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(serializedFileName));
            ProgressTracker progressTracker = (ProgressTracker) in.readObject();
            return progressTracker;
        } catch (ClassNotFoundException c) {
            System.out.println(("There was an error " + c));
//            logger.error("There was an error", c.toString());
            return new ProgressTracker();
        } catch (FileNotFoundException f) {
            System.out.println(("There was an error " + f));
//            logger.warn("No previous instance of the machine could be found", f.toString());
            return new ProgressTracker();
        } catch (IOException io) {
            System.out.println(("There was an error " + io));
//            logger.error("There was an error", io.toString());
            return new ProgressTracker();
        }

    }



}