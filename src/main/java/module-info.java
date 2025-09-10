module com.example.g14 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires com.google.gson;

    opens com.example.g14 to javafx.fxml;
    exports com.example.g14;
    exports com.example.g14.GUI;
    opens com.example.g14.GUI to javafx.fxml;
    requires java.desktop; 
}