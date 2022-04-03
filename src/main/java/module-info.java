module com.example.videoplayer {
    requires javafx.fxml;
    requires javafx.media;
    requires slf4j.api;
    requires log4j;
    requires javafx.graphics;
    requires uk.co.caprica.vlcj;
    requires uk.co.caprica.vlcj.javafx;
    requires javafx.controls;

    opens bootstrap to javafx.fxml;
    exports bootstrap;
}