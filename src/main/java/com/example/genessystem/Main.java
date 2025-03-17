package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.utils.UsernameReceiver;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static Stage primaryStage;
    public static Parent root;

    public static void switchScreen(String fxmlFile, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
            Parent root = loader.load();

            // If the controller has a method to set the username, call it
            Object controller = loader.getController();
            if (controller instanceof UsernameReceiver) {
                ((UsernameReceiver) controller).setUsername(username);
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            Effects.movableStage(primaryStage, root);
        } catch (Exception e) {
            e.printStackTrace();
            Dialogs.showAlertMessage("System Error!", "Something Went Wrong !!!: " + e.getMessage(), Dialogs.MessageType.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/mainLogo.png"))));
        switchScreen("login-view.fxml", null);
        stage.show();
    }
}