package bootstrap;


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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.StatusApi;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import static uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurfaceFactory.videoSurfaceForImageView;

public class Controller implements Initializable {

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
        try {
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(null);
//            Media media = new Media(file.toURI().toURL().toString());
            stage = Main.getStage();
            stage.setTitle(file.getName());
            setupVlcJ(file.toURI().toURL().toString());

//            if(mediaPlayer!=null)
//                mediaPlayer.dispose();
//
//            mediaPlayer = new MediaPlayer(media);
//            mediaView.setMediaPlayer(mediaPlayer);
//            DoubleProperty widthProp = mediaView.fitWidthProperty();
//            DoubleProperty heightProp = mediaView.fitHeightProperty();
//
//            widthProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "width"));
//            heightProp.bind(Bindings.selectDouble(mediaView.sceneProperty(), "height"));
            

        }
        catch (MalformedURLException mu)
        {
            System.out.println(mu);
            logger.info(mu.toString());
        }

//        mediaPlayer.setOnReady(
//                ()->{
//                    timeSlider.setMin(0);
//                    timeSlider.setMax(mediaPlayer.getMedia().getDuration().toMinutes());
//                    timeSlider.setValue(0);
//                }
//
//        );
//        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
//            @Override
//            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
//                Duration currentPosition = mediaPlayer.getCurrentTime();
//                timeSlider.setValue(currentPosition.toMinutes());
//            }
//        });
//
//        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                if(timeSlider.isPressed())
//                {
//                    double position =  timeSlider.getValue();
//                    mediaPlayer.seek(new Duration(position*60*1000));
//                }
//
//            }
//        });
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
//        double requiredTime = mediaPlayer.getCurrentTime().toSeconds()+10;
        vlcJMediaPlayer.controls().skipTime(10000);
    }

    @FXML
    void rewind(ActionEvent event) {
//        double requiredTime = mediaPlayer.getCurrentTime().toSeconds()-10;
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
        videoImageView.fitWidthProperty().bind(stackPane.widthProperty());
        videoImageView.fitHeightProperty().bind(stackPane.heightProperty());
        stackPane.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            // If you need to know about resizes
        });

        stackPane.heightProperty().addListener((observableValue, oldValue, newValue) -> {
            // If you need to know about resizes
        });

    }

    public void setupVlcJ(String filePath)
    {
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        vlcJMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        vlcJMediaPlayer.videoSurface().set(videoSurfaceForImageView(this.videoImageView));
        stackPane.setStyle("-fx-background-color: black;");
//        stackPane.setCenter(videoImageView);
        vlcJMediaPlayer.media().play(filePath);
        vlcJMediaPlayer.controls().setPosition(0.4f);

    }
}