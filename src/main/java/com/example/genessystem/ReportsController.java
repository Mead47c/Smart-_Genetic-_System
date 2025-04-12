package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.objects.PrintPatient;
import com.example.genessystem.utils.UsernameReceiver;
import com.example.genessystem.utils.database.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ReportsController implements Initializable, UsernameReceiver {

    private final ObservableList<PrintPatient> patientsData = FXCollections.observableArrayList();
    // ********************************* Declaration Area ***********************************
    @FXML
    private Button reports_dashboardButton;
    @FXML
    private Button reports_diagnosticsButton;
    @FXML
    private Button reports_exit;
    @FXML
    private TableColumn<PrintPatient, String> reports_firstNameCol;
    @FXML
    private TableColumn<PrintPatient, String> reports_lastNameCol;
    @FXML
    private Label reports_loginDateLabel;
    @FXML
    private Label reports_loginTimeLabel;
    @FXML
    private TableColumn<PrintPatient, ImageView> reports_logoCol;
    @FXML
    private Button reports_logoutButton;
    @FXML
    private TableColumn<PrintPatient, String> reports_medicalRecordCol;
    @FXML
    private TableColumn<PrintPatient, String> reports_nationalIdCol;
    @FXML
    private Button reports_patientDataButton;
    @FXML
    private TableColumn<PrintPatient, String> reports_phoneCol;
    @FXML
    private TableColumn<PrintPatient, PrintPatient> reports_printCol;
    @FXML
    private BorderPane reports_root;
    @FXML
    private TextField reports_searchField;
    @FXML
    private TableView<PrintPatient> reports_tableView;
    @FXML
    private Label reports_usernameLabel;
    @FXML
    private Text reports_mainTitleText;
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

    @FXML
    public void aboutUsPopup(ActionEvent event) {
        AboutUs.showAboutUsStage();
    }


    // *********************************************************************************************
    // *************************************  Main Operations  *************************************
    // *********************************************************************************************

    public void searchForPatient() {
        FilteredList<PrintPatient> filteredData = new FilteredList<>(patientsData, p -> true);

        reports_searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(patient -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String lowerCaseFilter = newValue.toLowerCase();
                return patient.getNationalId().toLowerCase().contains(lowerCaseFilter)
                        || patient.getMedicalRecord().toLowerCase().contains(lowerCaseFilter)
                        || patient.getFirstName().toLowerCase().contains(lowerCaseFilter)
                        || patient.getLastName().toLowerCase().contains(lowerCaseFilter)
                        || patient.getPhone().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<PrintPatient> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(reports_tableView.comparatorProperty());
        reports_tableView.setItems(sortedData);
    }


    public void tableViewStyling() {
        final double tableWidth = 1165;
        final double logoColumnWidth = 50;
        final double regularColumnWidth = 190.17;

        reports_logoCol.setMinWidth(logoColumnWidth);
        reports_logoCol.setMaxWidth(logoColumnWidth);
        reports_logoCol.setPrefWidth(logoColumnWidth);

        TableColumn<?, ?>[] otherColumns = {
                reports_nationalIdCol,
                reports_medicalRecordCol,
                reports_firstNameCol,
                reports_lastNameCol,
                reports_phoneCol,
                reports_printCol
        };

        for (TableColumn<?, ?> col : otherColumns) {
            col.setMinWidth(regularColumnWidth);
            col.setMaxWidth(regularColumnWidth);
            col.setPrefWidth(regularColumnWidth);
        }

        reports_tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        reports_tableView.setPlaceholder(new Label(""));

        reports_tableView.setFixedCellSize(50);
        reports_tableView.setPrefHeight(500);
    }


    private void configureTableColumns() {
        reports_logoCol.setCellValueFactory(data -> {
            ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/mainLogo.png")));
            logo.setFitHeight(40);
            logo.setFitWidth(40);
            return new javafx.beans.property.SimpleObjectProperty<>(logo);
        });

        reports_nationalIdCol.setCellValueFactory(new PropertyValueFactory<>("nationalId"));
        reports_medicalRecordCol.setCellValueFactory(new PropertyValueFactory<>("medicalRecord"));
        reports_firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        reports_lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        reports_phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        reports_printCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue()));

        reports_printCol.setCellFactory(column -> new TableCell<>() {
            private final HBox container = new HBox(10);

            {
                container.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(PrintPatient patient, boolean empty) {
                super.updateItem(patient, empty);
                if (empty || patient == null) {
                    setGraphic(null);
                } else {
                    container.getChildren().setAll(patient.getPrintButton(), patient.getShareButton());
                    setGraphic(container);
                }
            }
        });

        reports_tableView.setItems(patientsData);
    }

    private void loadAnalyzedPatients() {
        patientsData.clear();
        String query = "SELECT p.nationalid, p.medicalrecord, p.firstname, p.lastname, p.phone " +
                "FROM patient p " +
                "JOIN diagnostic d ON p.medicalrecord = d.medicalrecord " +
                "JOIN analysis a ON d.medicalrecord = a.medicalrecord";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String nationalId = rs.getString("nationalid");
                String medicalRecord = rs.getString("medicalrecord");
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                String phone = rs.getString("phone");

                patientsData.add(new PrintPatient(nationalId, medicalRecord, firstName, lastName, phone));
            }
        } catch (SQLException e) {
            Dialogs.showAlertMessage("Database Error", "Could not load patient data.\n" + e.getMessage(), Dialogs.MessageType.ERROR_MESSAGE);
        }
    }


    // *********************************************************************************************
    // ***************************************  Initialize  ****************************************
    // *********************************************************************************************
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonEffects();
        setLoginTime();
        configureTableColumns();
        loadAnalyzedPatients();
        tableViewStyling();
        searchForPatient();

        reports_mainTitleText.setFill(new ImagePattern(new Image(getClass().getResourceAsStream("/images/black-wallpaper.jpg"))));
    }
}
