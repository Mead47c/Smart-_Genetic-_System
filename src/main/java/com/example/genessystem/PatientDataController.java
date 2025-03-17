package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.objects.Patient;
import com.example.genessystem.utils.UsernameReceiver;
import com.example.genessystem.utils.database.DatabaseConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Toolkit;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class PatientDataController implements Initializable, UsernameReceiver {

    // ********************************* Declaration Area ***********************************
    @FXML
    public BorderPane patientData_root;

    @FXML
    public Label patientData_usernameLabel;

    @FXML
    public Button patientData_dashboardButton;

    @FXML
    public Button patientData_patientDataButton;

    @FXML
    public Button patientData_diagnosticsButton;

    @FXML
    public Button patientData_logoutButton;

    @FXML
    public Button patientData_exit;

    @FXML
    public TableView<Patient> patientData_tableView;

    @FXML
    public TableColumn<Patient, Long> patientData_nationalIDCol;

    @FXML
    public TableColumn<Patient, String> patientData_medicalRecCol;

    @FXML
    public TableColumn<Patient, String> patientData_ageCol;

    @FXML
    public TableColumn<Patient, String> patientData_genderCol;

    @FXML
    public TableColumn<Patient, String> patientData_firstNameCol;

    @FXML
    public TableColumn<Patient, String> patientData_lastNameCol;

    @FXML
    public TableColumn<Patient, String> patientData_phoneCol;

    @FXML
    public TableColumn<Patient, String> patientData_emailCol;

    @FXML
    public Button patientData_addNewPatientButton;

    @FXML
    public TextField patientData_searchField;

    @FXML
    public Label patientData_loginDateLabel;

    @FXML
    public Label patientData_loginTimeLabel;

    @FXML
    public Button patientData_reportsButton;

// ****************************** END OF Declaration Area ********************************


    // *********************************************************************************************
    // ******************************  Setting Screen Requirements  ********************************
    // *********************************************************************************************

    @Override
    public void setUsername(String username) {
        if (patientData_usernameLabel != null) {
            patientData_usernameLabel.setText(username.substring(0, 1).toUpperCase() + username.substring(1));
        } else {
            System.out.println("usernameLabel is null!");
        }
    }

    public void setLoginTime() {
        patientData_loginDateLabel.setText(String.valueOf(LocalDate.now()));
        patientData_loginTimeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @FXML
    public void logout(ActionEvent event) {
        String username = patientData_usernameLabel.getText();
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
                () -> ((Stage) patientData_root.getScene().getWindow()).close());
    }

    public void buttonEffects() {
        Effects.buttonEffect(patientData_dashboardButton);
        Effects.buttonEffect(patientData_diagnosticsButton);
        Effects.buttonEffect(patientData_logoutButton);
        Effects.buttonEffect(patientData_exit);
    }

    public void setTableViewAlignment() {
        patientData_nationalIDCol.setStyle("-fx-alignment: CENTER;");
        patientData_medicalRecCol.setStyle("-fx-alignment: CENTER;");
        patientData_firstNameCol.setStyle("-fx-alignment: CENTER;");
        patientData_lastNameCol.setStyle("-fx-alignment: CENTER;");
        patientData_ageCol.setStyle("-fx-alignment: CENTER;");
        patientData_emailCol.setStyle("-fx-alignment: CENTER-LEFT;");
        patientData_phoneCol.setStyle("-fx-alignment: CENTER;");
        patientData_genderCol.setStyle("-fx-alignment: CENTER;");
    }

    public void setTableViewColWidth() {
//        patientData_tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//
//        patientData_idCol.setPrefWidth(75);
//        patientData_firstNameCol.setPrefWidth(100);
//        patientData_lastNameCol.setPrefWidth(100);
//        patientData_phoneCol.setPrefWidth(125);
//        patientData_emailCol.setPrefWidth(200);
    }

    // *********************************************************************************************
    // **********************************  Switch among Screens  ***********************************
    // *********************************************************************************************

    @FXML
    public void switchToDashboard(ActionEvent actionEvent) {
        String username = patientData_usernameLabel.getText();
        Main.switchScreen("dashboard-view.fxml", username);
    }


    @FXML
    public void switchToDiagnostics(ActionEvent actionEvent) {
        String username = patientData_usernameLabel.getText();
        Main.switchScreen("diagnostics-view.fxml", username);
    }

    @FXML
    public void switchToReports(ActionEvent actionEvent) {
        String username = patientData_usernameLabel.getText();
        Main.switchScreen("reports-view.fxml", username);
    }


    // *********************************************************************************************
    // *************************************  Functionality  ***************************************
    // *********************************************************************************************

    // Test Data for Table View
    public void tableViewRecords() {
        ObservableList<Patient> patientData = FXCollections.observableArrayList();
        String query = "SELECT * FROM patient";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Patient patient = new Patient(
                        resultSet.getLong("nationalid"),
                        resultSet.getString("medicalrecord"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getString("dob"),
                        resultSet.getString("age"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("gender")
                );
                patientData.add(patient);
            }

            if (patientData.isEmpty()) {
                patientData_tableView.getItems().clear();
            } else {
                patientData_tableView.setItems(patientData);
                setTableViewAlignment();
                setTableViewColWidth();
                searchPatientData();
            }

        } catch (SQLException e) {
            Dialogs.showAlertMessage("Database Error", "Failed to load patient data. Please check your connection.", Dialogs.MessageType.ERROR_MESSAGE);
        }
    }


    public void searchPatientData() {
        FilteredList<Patient> filteredData = new FilteredList<>(patientData_tableView.getItems(), p -> true);

        patientData_searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(patient -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return String.valueOf(patient.getNationalId()).contains(lowerCaseFilter) ||
                        patient.getMedicalRecord().contains(lowerCaseFilter) ||
                        patient.getFirstName().toLowerCase().contains(lowerCaseFilter) ||
                        patient.getLastName().toLowerCase().contains(lowerCaseFilter) ||
                        patient.getPhone().toLowerCase().contains(lowerCaseFilter) ||
                        patient.getEmail().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Patient> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(patientData_tableView.comparatorProperty());
        patientData_tableView.setItems(sortedData);
    }

    @FXML
    public void addNewPatient(ActionEvent event) throws IOException {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-new-patient-view.fxml"));
            Parent root = loader.load();

            NewPatientController newPatientController = loader.getController();
            newPatientController.setPatientDataController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            tableViewRecords();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // *********************************************************************************************
    // **************************************  Initialize ******************************************
    // *********************************************************************************************

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonEffects();
        setLoginTime();

        patientData_nationalIDCol.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        patientData_medicalRecCol.setCellValueFactory(new PropertyValueFactory<>("medicalRecord"));
        patientData_firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        patientData_lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        patientData_ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        patientData_emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        patientData_phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        patientData_genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        tableViewRecords();
    }

}
