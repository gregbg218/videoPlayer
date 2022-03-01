package bootstrap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    MediaPlayer mediaPlayer;
    Stage stage ;
    public static Logger logger = LoggerFactory.getLogger(Controller.class);

    @FXML
    private AnchorPane anchorPane;

    @FXML
    public VBox vBox;

    @FXML
    private MediaView mediaView;

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
            Media media = new Media(file.toURI().toURL().toString());
            stage = Main.getStage();
            stage.setTitle(file.getName());

            if(mediaPlayer!=null)
                mediaPlayer.dispose();

            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
        }
        catch (MalformedURLException mu)
        {
            System.out.println(mu);
            logger.info(mu.toString());
        }

        mediaPlayer.setOnReady(
                ()->{
                    timeSlider.setMin(0);
                    timeSlider.setMax(mediaPlayer.getMedia().getDuration().toMinutes());
                    timeSlider.setValue(0);
                }

        );
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observable, Duration oldValue, Duration newValue) {
                Duration currentPosition = mediaPlayer.getCurrentTime();
                timeSlider.setValue(currentPosition.toMinutes());
            }
        });

        timeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(timeSlider.isPressed())
                {
                    double position =  timeSlider.getValue();
                    mediaPlayer.seek(new Duration(position*60*1000));
                }

            }
        });
    }



    @FXML
    void play(ActionEvent event)   {
        try{
            MediaPlayer.Status status= mediaPlayer.getStatus();

            if(status == MediaPlayer.Status.PLAYING)
            {
                mediaPlayer.pause();
                playBtn.setGraphic(new ImageView(new Image(new FileInputStream("src/main/resources/icons/play.png"))));
            }
            else
            {
                mediaPlayer.play();
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
        double requiredTime = mediaPlayer.getCurrentTime().toSeconds()+10;
        mediaPlayer.seek(new Duration(requiredTime*1000));

    }

    @FXML
    void rewind(ActionEvent event) {
        double requiredTime = mediaPlayer.getCurrentTime().toSeconds()-10;
        mediaPlayer.seek(new Duration(requiredTime*1000));

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
}