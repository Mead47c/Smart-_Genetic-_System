package com.example.genessystem.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class Dialogs {

    @FXML
    public Button confAlert_closeButton;

    @FXML
    public Label confAlert_headerLabel;

    @FXML
    public Label confAlert_contentLabel;

    @FXML
    public Button confAlert_okButton;

    @FXML
    public Button confAlert_cancelButton;
    @FXML
    public ImageView info_message_graphicNode;
    @FXML
    private Label infoAlert_headerLabel;
    @FXML
    private Label infoAlert_contentLabel;
    @FXML
    private Button infoAlert_okButton;
    @FXML
    private Button info_alert_closeButton;
    private String header;
    private String content;
    private Runnable okAction;

    public static void showAlertMessage(String header, String content, MessageType type) {
        try {
            FXMLLoader loader = new FXMLLoader(Dialogs.class
                    .getResource("/dialogLayout/info-alert-view.fxml"));
            Parent root = loader.load();

            Dialogs controller = loader.getController();
            controller.setHeaderAndContent(header, content, type);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ************************************************************************************************
    // ************************************   Info & Error Alerts *************************************
    // ************************************************************************************************

    public static void showConfirmationAlert(String header, String content, Runnable runnable) {
        try {
            FXMLLoader loader = new FXMLLoader(Dialogs.class.
                    getResource("/dialogLayout/confirmation-alert-view.fxml"));
            Parent root = loader.load();

            Dialogs controller = loader.getController();
            controller.setHeaderAndContentForConfirmation(header, content, runnable);

            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setHeaderAndContent(String header, String content, MessageType type) {
        infoAlert_headerLabel.setText(header);
        infoAlert_contentLabel.setText(content);

        if (type == MessageType.ERROR_MESSAGE) {
            Image errorImage = new Image(getClass().getResourceAsStream("/images/error-alert.png"));
            info_message_graphicNode.setImage(errorImage);

            System.out.println(errorImage.getUrl());

            infoAlert_headerLabel.setStyle("-fx-text-fill: #ff6666;");
        } else {
            infoAlert_headerLabel.setStyle("-fx-text-fill: #2196f3;");
        }
    }

    @FXML
    public void infoAlertOk() {
        ((Stage) infoAlert_okButton.getScene().getWindow()).close();
    }

    @FXML
    public void infoAlertClose() {
        ((Stage) info_alert_closeButton.getScene().getWindow()).close();
    }


    // ************************************************************************************************
    // *************************************   Confirmation Alert *************************************
    // ************************************************************************************************

    private void setHeaderAndContentForConfirmation(String header, String content, Runnable okAction) {
        confAlert_headerLabel.setText(header);
        confAlert_contentLabel.setText(content);
        this.okAction = okAction;
    }

    @FXML
    public void confirmationAlertClose(ActionEvent actionEvent) {
        ((Stage) confAlert_closeButton.getScene().getWindow()).close();
    }

    @FXML
    public void confirmationAlertOk(ActionEvent actionEvent) {
        if (okAction != null) {
            okAction.run();
        }
        ((Stage) confAlert_okButton.getScene().getWindow()).close();
    }

    @FXML
    public void confirmationAlertCancel(ActionEvent actionEvent) {
        ((Stage) confAlert_cancelButton.getScene().getWindow()).close();
    }

    public static enum MessageType {
        INFO_MESSAGE,
        ERROR_MESSAGE;
    }

}



/*
public class Dialogs extends Application implements Initializable {
    @FXML
    public Button info_alert_closeButton;

    @FXML
    public Label infoAlert_headerLabel;

    @FXML
    public Label infoAlert_contentLabel;

    @FXML
    public Button infoAlert_okButton;

    private static String header, content ;

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

    public static void showInfoAlert(String header, String content) {
        Dialogs.header = header;
        Dialogs.content = content;
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Alert.class.getResource("/dialogLayout/info-alert-view.fxml"));
                Parent root = fxmlLoader.load();
                Stage stage = new Stage();
                stage.initStyle(StageStyle.UNDECORATED);
                stage.initModality(Modality.APPLICATION_MODAL);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setAlwaysOnTop(true);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    @Override
    public void start(Stage stage) throws Exception {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
*/