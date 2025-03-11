package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.objects.Patient;
import com.example.genessystem.utils.UsernameReceiver;
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

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PatientDataController implements Initializable, UsernameReceiver {

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
    public TableColumn<Patient, Integer> patientData_idCol;

    @FXML
    public TableColumn<Patient, String> patientData_firstNameCol;

    @FXML
    public TableColumn<Patient, String> patientData_lastNameCol;

    @FXML
    public TableColumn<Patient, String> patientData_phoneCol;

    @FXML
    public TableColumn<Patient, String> patientData_emailCol;

    @FXML
    public TableColumn<Patient, String> patientData_commentsCol;

    @FXML
    public Button patientData_addNewPatientButton;

    @FXML
    public TextField patientData_searchField;


    @Override
    public void setUsername(String username) {
        if (patientData_usernameLabel != null) {
            patientData_usernameLabel.setText(username.substring(0, 1).toUpperCase() + username.substring(1));
        } else {
            System.out.println("usernameLabel is null!");
        }
    }

    @FXML
    public void closeProgram(ActionEvent actionEvent) {
        Toolkit.getDefaultToolkit().beep();
        boolean response = Dialogs.showConfirmationAlert("Close Program?", "You are about to close the program!\nAre you sure?", true);
        if (response) {
            Platform.exit();
        }
    }

    public void buttonEffects() {
        Effects.buttonEffect(patientData_dashboardButton);
        Effects.buttonEffect(patientData_diagnosticsButton);
        Effects.buttonEffect(patientData_logoutButton);
        Effects.buttonEffect(patientData_exit);
    }

    public void setTableViewAlignment() {
        patientData_idCol.setStyle("-fx-alignment: CENTER;");
        patientData_firstNameCol.setStyle("-fx-alignment: CENTER;");
        patientData_lastNameCol.setStyle("-fx-alignment: CENTER;");
        patientData_phoneCol.setStyle("-fx-alignment: CENTER;");
        patientData_commentsCol.setStyle("-fx-alignment: CENTER;");
        patientData_emailCol.setStyle("-fx-alignment: CENTER-LEFT;");
    }

    public void setTableViewColWidth() {
        patientData_tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        patientData_idCol.setPrefWidth(75);
        patientData_firstNameCol.setPrefWidth(100);
        patientData_lastNameCol.setPrefWidth(100);
        patientData_phoneCol.setPrefWidth(125);
        patientData_emailCol.setPrefWidth(200);
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
    public void logout(ActionEvent event) {
        String username = patientData_usernameLabel.getText();
        Toolkit.getDefaultToolkit().beep();
        boolean response = Dialogs.showConfirmationAlert(null, "You are about " +
                "to close the program and go back to the login screen\nAre you sure?", true);
        if (response) {
            Main.switchScreen("login-view.fxml", username);
        }
    }


    // Test Data for Table View
    public void tableViewRecords() {
        patientData_idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientData_firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        patientData_lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        patientData_phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        patientData_emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        patientData_commentsCol.setCellValueFactory(new PropertyValueFactory<>("comments"));

        // Create some test data
        ObservableList<Patient> testData = FXCollections.observableArrayList(
                new Patient(1, "Abcd", "Efgh", "123-456-7890", "abc.abc@example.com", "No comments aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"),
                new Patient(2, "Ijkl", "Mnopq", "987-654-3210", "ijkl.ij@example.com", "Follow up needed"),
                new Patient(3, "Rstuv", "Wxyzabc", "555-123-4567", "rstuv.rst@example.com", "Urgent case"),
                new Patient(4, "Mohammed", "Ahmed", "111-222-333", "aaa@bbb.com", "")
        );

        patientData_tableView.setItems(testData);
    }

    public void searchPatientData() {
        FilteredList<Patient> filteredData = new FilteredList<>(patientData_tableView.getItems(), p -> true);

        patientData_searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(patient -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return patient.getFirstName().toLowerCase().contains(lowerCaseFilter) ||
                        patient.getLastName().toLowerCase().contains(lowerCaseFilter) ||
                        patient.getPhone().toLowerCase().contains(lowerCaseFilter) ||
                        patient.getEmail().toLowerCase().contains(lowerCaseFilter) ||
                        patient.getComments().toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(patient.getId()).contains(lowerCaseFilter);
            });
        });

        SortedList<Patient> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(patientData_tableView.comparatorProperty());
        patientData_tableView.setItems(sortedData);
    }

    @FXML
    public void addNewPatient(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("add-new-patient-view.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonEffects();
        tableViewRecords();
        setTableViewAlignment();
        setTableViewColWidth();
        searchPatientData();

        patientData_tableView.setEditable(true);
        patientData_firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        patientData_lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        patientData_phoneCol.setCellFactory(TextFieldTableCell.forTableColumn());
        patientData_emailCol.setCellFactory(TextFieldTableCell.forTableColumn());
        patientData_commentsCol.setCellFactory(TextFieldTableCell.forTableColumn());
    }
}
