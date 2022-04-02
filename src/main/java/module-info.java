module com.example.videoplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires slf4j.api;
    requires log4j;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.javafx;


    opens bootstrap to javafx.fxml;
    exports bootstrap;
}