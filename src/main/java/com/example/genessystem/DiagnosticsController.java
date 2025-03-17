package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.utils.UsernameReceiver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


public class DiagnosticsController implements Initializable, UsernameReceiver {

    // ********************************* Declaration Area ***********************************
    @FXML
    public Button diagnostics_dashboardButton;

    @FXML
    public Button diagnostics_patientDataButton;

    @FXML
    public Button patientData_diagnosticsButton;

    @FXML
    public Button diagnostics_reportsButton;

    @FXML
    public Button diagnostics_logoutButton;

    @FXML
    public Button diagnostics_exit;

    @FXML
    public Label diagnostics_usernameLabel;

    @FXML
    public Label diagnostics_loginDateLabel;

    @FXML
    public Label diagnostics_loginTimeLabel;

    @FXML
    public BorderPane diagnostics_root;

    @FXML
    private ComboBox<String> diagnostics_diseaseCombobox;

    @FXML
    private ComboBox<String> diagnostics_diseaseConditionCombobox;

    @FXML
    private ComboBox<String> diagnostics_kinshipCombobox;
    // ****************************** END OF Declaration Area ********************************


    // *********************************************************************************************
    // ******************************  Setting Screen Requirements  ********************************
    // *********************************************************************************************
    @Override
    public void setUsername(String username) {
        if (diagnostics_usernameLabel != null) {
            diagnostics_usernameLabel.setText(username.substring(0, 1).toUpperCase() + username.substring(1));
        } else {
            System.out.println("usernameLabel is null!");
        }
    }

    public void setLoginTime() {
        diagnostics_loginDateLabel.setText(String.valueOf(LocalDate.now()));
        diagnostics_loginTimeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @FXML
    public void logout(ActionEvent event) {
        String username = diagnostics_usernameLabel.getText();
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
                () -> ((Stage) diagnostics_root.getScene().getWindow()).close());
    }

    public void diseaseComboboxItems() {
        diagnostics_diseaseCombobox.getItems().addAll(
                "Sickle Cell Disease",
                "Beta-Thalassemia",
                "Glucose-6-Phosphate Dehydrogenase (G6PD) Deficiency",
                "Cystic Fibrosis",
                "Familial Mediterranean Fever (FMF)",
                "Phenylketonuria (PKU)",
                "Autosomal Recessive Non-Syndromic Hearing Loss",
                "Spinal Muscular Atrophy (SMA)",
                "Mucopolysaccharidosis Type I (Hurler Syndrome)",
                "Congenital Adrenal Hyperplasia (CAH)");
    }

    public void diseaseConditionCombobox() {
        diagnostics_diseaseConditionCombobox.getItems().addAll(
                "Injured",
                "Safe",
                "Carrier");
    }

    public void degreeOfKinshipComboboxItems() {
        diagnostics_kinshipCombobox.getItems().addAll("1", "2");
    }

    public void buttonEffects() {
        Effects.buttonEffect(diagnostics_patientDataButton);
        Effects.buttonEffect(diagnostics_dashboardButton);
        Effects.buttonEffect(diagnostics_logoutButton);
        Effects.buttonEffect(diagnostics_reportsButton);
        Effects.buttonEffect(diagnostics_exit);
    }


    // *********************************************************************************************
    // **********************************  Switch among Screens  ***********************************
    // *********************************************************************************************


    @FXML
    public void switchToDashboard(ActionEvent actionEvent) {
        String username = diagnostics_usernameLabel.getText();
        Main.switchScreen("dashboard-view.fxml", username);
    }

    @FXML
    public void switchToPatientData(ActionEvent actionEvent) {
        String username = diagnostics_usernameLabel.getText();
        Main.switchScreen("patient-data-view.fxml", username);
    }

    @FXML
    public void switchToReports(ActionEvent actionEvent) {
        String username = diagnostics_usernameLabel.getText();
        Main.switchScreen("reports-view.fxml", username);
    }


    // *********************************************************************************************
    // ***************************************  Initialize  ****************************************
    // *********************************************************************************************
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonEffects();
        diseaseComboboxItems();
        diseaseConditionCombobox();
        degreeOfKinshipComboboxItems();
        setLoginTime();

    }

}
