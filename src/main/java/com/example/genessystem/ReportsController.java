package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.utils.UsernameReceiver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReportsController implements Initializable, UsernameReceiver {

    // ********************************* Declaration Area ***********************************
    @FXML
    public Button reports_dashboardButton;

    @FXML
    public Button reports_patientDataButton;

    @FXML
    public Button reports_diagnosticsButton;

    @FXML
    public Button reports_logoutButton;

    @FXML
    public Button reports_exit;

    @FXML
    public Label reports_usernameLabel;

    @FXML
    public Label reports_loginDateLabel;

    @FXML
    public Label reports_loginTimeLabel;

    @FXML
    public BorderPane reports_root;
// ****************************** END OF Declaration Area ********************************


    // *********************************************************************************************
    // ******************************  Setting Screen Requirements  ********************************
    // *********************************************************************************************
    @Override
    public void setUsername(String username) {
        if (reports_usernameLabel != null) {
            reports_usernameLabel.setText(username.substring(0, 1).toUpperCase() + username.substring(1));
        } else {
            System.out.println("usernameLabel is null!");
        }
    }

    public void setLoginTime() {
        reports_loginDateLabel.setText(String.valueOf(LocalDate.now()));
        reports_loginTimeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @FXML
    public void logout(ActionEvent event) {
        String username = reports_usernameLabel.getText();
        Toolkit.getDefaultToolkit().beep();

        Dialogs.showConfirmationAlert("Logout!",
                "You are about to close the program and go back to the login screen.\nAre you sure?",
                () -> Main.switchScreen("login-view.fxml", username));
    }

    @FXML
    public void closeProgram(ActionEvent actionEvent) {
        Toolkit.getDefaultToolkit().beep();

        Dialogs.showConfirmationAlert("Close Program?",
                "You are about to close the program!\nAre you sure?",
                () -> ((Stage) reports_root.getScene().getWindow()).close());
    }

    public void buttonEffects() {
        Effects.buttonEffect(reports_dashboardButton);
        Effects.buttonEffect(reports_patientDataButton);
        Effects.buttonEffect(reports_diagnosticsButton);
        Effects.buttonEffect(reports_logoutButton);
        Effects.buttonEffect(reports_exit);
    }


    // *********************************************************************************************
    // **********************************  Switch among Screens  ***********************************
    // *********************************************************************************************

    @FXML
    public void switchToDashboard(ActionEvent actionEvent) {
        String username = reports_usernameLabel.getText();
        Main.switchScreen("dashboard-view.fxml", username);
    }

    @FXML
    public void switchToPatientData(ActionEvent actionEvent) {
        String username = reports_usernameLabel.getText();
        Main.switchScreen("patient-data-view.fxml", username);
    }

    @FXML
    public void switchToDiagnostics(ActionEvent actionEvent) {
        String username = reports_usernameLabel.getText();
        Main.switchScreen("diagnostics-view.fxml", username);
    }


    // *********************************************************************************************
    // ***************************************  Initialize  ****************************************
    // *********************************************************************************************
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonEffects();
        setLoginTime();
    }


}
