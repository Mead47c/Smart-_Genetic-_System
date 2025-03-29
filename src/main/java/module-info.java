module com.example.genessystem {
    requires javafx.fxml;
    requires jakarta.mail;
    requires mail;
    requires java.compiler;
    requires org.json;
    requires okhttp3;
    requires com.google.gson;
    requires java.sql;
    requires javafx.controls;
    requires com.github.librepdf.openpdf;
    requires java.desktop;
    requires activation;

    opens com.example.genessystem.objects to javafx.base, javafx.fxml;
    opens com.example.genessystem to javafx.fxml;
    opens com.example.genessystem.dialog to javafx.base, javafx.fxml, javafx.graphics;
    exports com.example.genessystem;
}