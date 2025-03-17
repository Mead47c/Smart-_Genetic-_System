module com.example.genessystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.mail;
    requires mail;
    requires java.desktop;
    requires java.sql;
    requires java.compiler;

    opens com.example.genessystem.objects to javafx.base, javafx.fxml;
    opens com.example.genessystem to javafx.fxml;
    opens com.example.genessystem.dialog to javafx.base, javafx.fxml, javafx.graphics;
    exports com.example.genessystem;
}