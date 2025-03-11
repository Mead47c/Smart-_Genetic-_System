package com.example.genessystem.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;
import java.util.Optional;

public class Dialogs {

    public static void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Message");
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();

        dialogPane.getStylesheets().add(Dialogs.class.getResource("/styles/dialog-style.css").toExternalForm());
        dialogPane.getStyleClass().add("my-dialog");

        ImageView alertIcon = new ImageView(new Image(Objects.requireNonNull(Dialogs.class.getResourceAsStream("/images/error-alert.png"))));
        alertIcon.setFitHeight(50);
        alertIcon.setFitWidth(50);
        alert.setGraphic(alertIcon);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Objects.requireNonNull(Dialogs.class.getResourceAsStream("/images/gene-stage-logo.png"))));
        alertStage.initStyle(StageStyle.UNDECORATED);
        alert.show();
    }

    public static void showInfoAlert(String header, String contenet) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info Message");
        alert.setHeaderText(header);
        alert.setContentText(contenet);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Dialogs.class.getResource("/styles/dialog-style.css").toExternalForm());
        dialogPane.getStyleClass().add("my-dialog");

        ImageView alertIcon = new ImageView(new Image(Objects.requireNonNull(Dialogs.class.getResourceAsStream("/images/info-alert.png"))));
        alertIcon.setFitHeight(50);
        alertIcon.setFitWidth(50);
        alert.setGraphic(alertIcon);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Objects.requireNonNull(Dialogs.class.getResourceAsStream("/images/gene-stage-logo.png"))));
        alertStage.initStyle(StageStyle.UNDECORATED);

        alert.show();
    }

    public static void showAndWaitInfoAlert(String header, String contenet) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info Message");
        alert.setHeaderText(header);
        alert.setContentText(contenet);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Dialogs.class.getResource("/styles/dialog-style.css").toExternalForm());
        dialogPane.getStyleClass().add("my-dialog");

        ImageView alertIcon = new ImageView(new Image(Objects.requireNonNull(Dialogs.class.getResourceAsStream("/images/info-alert.png"))));
        alertIcon.setFitHeight(50);
        alertIcon.setFitWidth(50);
        alert.setGraphic(alertIcon);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Objects.requireNonNull(Dialogs.class.getResourceAsStream("/images/gene-stage-logo.png"))));
        alertStage.initStyle(StageStyle.UNDECORATED);

        alert.showAndWait();
    }

    public static boolean showConfirmationAlert(String header, String content, boolean waitForResponse) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(Dialogs.class.getResource("/styles/dialog-style.css")).toExternalForm());
        dialogPane.getStyleClass().add("my-dialog");
        dialogPane.setStyle("-fx-pref-height: 250 ;");
        dialogPane.applyCss();
        dialogPane.layout();

        ImageView alertIcon = new ImageView(new Image(Objects.requireNonNull(Dialogs.class.getResourceAsStream("/images/confirmation-alert.png"))));
        alertIcon.setFitHeight(50);
        alertIcon.setFitWidth(50);
        alert.setGraphic(alertIcon);

        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Objects.requireNonNull(Dialogs.class.getResourceAsStream("/images/gene-stage-logo.png"))));
        alertStage.initStyle(StageStyle.UNDECORATED);

        if (waitForResponse) {
            Optional<ButtonType> response = alert.showAndWait();
            return response.isPresent() && response.get() == ButtonType.OK;
        } else {
            alert.show();
            return false;
        }
    }

}
