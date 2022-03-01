module com.example.videoplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires slf4j.api;
    requires log4j;


    opens bootstrap to javafx.fxml;
    exports bootstrap;
}