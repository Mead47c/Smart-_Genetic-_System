package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.objects.Disease;
import com.example.genessystem.objects.RiskResult;
import com.example.genessystem.utils.SGSAnalyzer;
import com.example.genessystem.utils.UsernameReceiver;
import com.example.genessystem.utils.WikipediaConnector;
import com.example.genessystem.utils.database.DatabaseConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
    public TextField diagnostics_nationalIdField;

    @FXML
    public TextField diagnostics_medicalRecField;

    @FXML
    public TextField diagnostics_firstNameField;

    @FXML
    public TextField diagnostics_lastNameField;

    @FXML
    public TextField diagnostics_ageField;

    @FXML
    public TextField diagnostics_genderField;

    @FXML
    public Button diagnostics_saveButton;

    @FXML
    public Button diagnostics_clearButton;

    @FXML
    public TextArea analysisTextArea;

    @FXML
    public Button diagnostics_pubMedButton;

    @FXML
    public Button diagnostics_sgsButton;

    @FXML
    public ImageView progressIndicator;

    @FXML
    public TextFlow wikipediaTextFlow;

    @FXML
    public TextFlow sgsTextFlow;

    @FXML
    public StackPane chatGptStackPane;

    @FXML
    public StackPane deepseekStackPane;

    @FXML
    public StackPane wikipediaStackPane;

    @FXML
    public StackPane sgsStackPane;

    @FXML
    public RadioButton diagnostics_injuredRadioButton;

    @FXML
    public RadioButton diagnostics_safeRadioButton;

    @FXML
    public RadioButton diagnostics_carrierRadioButton;

    @FXML
    public Spinner<Integer> diagnostics_firstDegreeSpinner;

    @FXML
    public Spinner<Integer> diagnostics_secondDegreeSpinner;

    @FXML
    public HBox analysisPane;

    @FXML
    public HBox diseasePane;

    @FXML
    public ComboBox<Disease> diagnostics_diseaseCombobox;

    @FXML
    public TextField diagnostics_diseaseWeightField;

    @FXML
    public TextField diagnostics_geneConditionField;

    @FXML
    public TextField diagnostics_firstRelativeField;

    @FXML
    public TextField diagnostics_secondRelativeField;

    @FXML
    public TextField diagnostics_totalRiskScoreField;

    @FXML
    public TextField diagnostics_riskLevelField;

    @FXML
    public StackPane clearEverythingStackPane;

    @FXML
    public TextFlow clearEverythingTextFlow;

    @FXML
    public StackPane saveAnalysisStackPane;

    @FXML
    public TextFlow saveAnalysisTextFlow;

    @FXML
    public Text diagnostics_mainTitleText;

    private String age;
    private ToggleGroup radioGroup;
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
                new Disease("Spinal Muscular Atrophy (SMA)", 20),
                new Disease("Hypertrophic Cardiomyopathy", 18),
                new Disease("Long QT Syndrome", 17),
                new Disease("Epidermolysis Bullosa", 16),
                new Disease("Genetic Epilepsy", 14),
                new Disease("Phenylketonuria (PKU)", 12),
                new Disease("Organic Acidemias", 11),
                new Disease("Glycogen Storage Diseases (GSDs)", 10),
                new Disease("Sickle Cell Anemia", 7),
                new Disease("Thalassemia", 6)
        );
    }

    public void setSpinnerValues() {
        diagnostics_firstDegreeSpinner.setValueFactory
                (
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0)
                );

        diagnostics_secondDegreeSpinner.setValueFactory
                (
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 20, 0)
                );
    }

    public void radioButtonGroup() {
        radioGroup = new ToggleGroup();
        diagnostics_injuredRadioButton.setToggleGroup(radioGroup);
        diagnostics_safeRadioButton.setToggleGroup(radioGroup);
        diagnostics_carrierRadioButton.setToggleGroup(radioGroup);
    }

    public void buttonEffects() {
        Effects.buttonEffect(diagnostics_patientDataButton);
        Effects.buttonEffect(diagnostics_dashboardButton);
        Effects.buttonEffect(diagnostics_logoutButton);
        Effects.buttonEffect(diagnostics_reportsButton);
        Effects.buttonEffect(diagnostics_exit);

        Effects.applyHoverPrompt(wikipediaStackPane, diagnostics_pubMedButton, wikipediaTextFlow, "Generate Wikipedia Summery");
        Effects.applyHoverPrompt(sgsStackPane, diagnostics_sgsButton, sgsTextFlow, "SGS Analysis");
        Effects.applyHoverPrompt(saveAnalysisStackPane, diagnostics_saveButton, saveAnalysisTextFlow, "Save Analysis");
        Effects.applyHoverPrompt(clearEverythingStackPane, diagnostics_clearButton, clearEverythingTextFlow, "Clear Everything");
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
    // *************************************  Functionality  ***************************************
    // *********************************************************************************************

    // Setting patient data by searching for an existing national ID or medical Record
    public void getPatientData() {
        diagnostics_nationalIdField.setOnAction(actionEvent -> {
            String nationalId = diagnostics_nationalIdField.getText();
            getDataByNationalIdOrMedicalRecord(nationalId);
        });

        diagnostics_medicalRecField.setOnAction(actionEvent -> {
            String medicalRec = diagnostics_medicalRecField.getText();
            getDataByNationalIdOrMedicalRecord(medicalRec);
        });
    }

    public void getDataByNationalIdOrMedicalRecord(String patientId) {
        if (patientId == null || patientId.trim().isEmpty()) {
            Dialogs.showAlertMessage("Provide Correct Info.",
                    "Check that you provided a correct national ID or medical record. Then try again!",
                    Dialogs.MessageType.ERROR_MESSAGE);
            return;
        }

        String query = "SELECT nationalid, medicalrecord, firstname, " +
                "lastname, age, gender FROM patient WHERE nationalid = ? OR medicalrecord = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement preparedstatement = conn.prepareStatement(query)) {

            long nationalIdValue = 0;
            boolean isNumeric = patientId.chars().allMatch(Character::isDigit);

            if (isNumeric) {
                nationalIdValue = Long.parseLong(patientId);
                preparedstatement.setLong(1, nationalIdValue);
            } else {
                preparedstatement.setLong(1, 0);
            }

            preparedstatement.setString(2, patientId);

            try (ResultSet resultSet = preparedstatement.executeQuery()) {
                if (resultSet.next()) {
                    diagnostics_nationalIdField.setText(String.valueOf(resultSet.getInt("nationalid")));
                    diagnostics_medicalRecField.setText(resultSet.getString("medicalrecord"));
                    diagnostics_firstNameField.setText(resultSet.getString("firstname"));
                    diagnostics_lastNameField.setText(resultSet.getString("lastname"));
                    diagnostics_ageField.setText(resultSet.getString("age"));
                    diagnostics_genderField.setText(resultSet.getString("gender"));

                    diseasePane.setDisable(false);
                    analysisPane.setDisable(false);
                } else {
                    Dialogs.showAlertMessage("No Data Found",
                            "No patient found with the given ID or medical record.",
                            Dialogs.MessageType.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            Dialogs.showAlertMessage("System Error",
                    "Something went wrong. Try again later.",
                    Dialogs.MessageType.ERROR_MESSAGE);
        }
    }


    // *********************************************************************************************
    // ************************************  Disease Analysis  *************************************
    // *********************************************************************************************
    @FXML
    public void getWikipediaSummary(ActionEvent actionEvent) throws InterruptedException {
        Disease selectedDisease = diagnostics_diseaseCombobox.getValue();
        String medicalRecord = diagnostics_medicalRecField.getText();

        // Check if there is any missing data
        // for the wikipedia summary we just need the disease name
        if (selectedDisease == null || medicalRecord.isEmpty()) {
            Dialogs.showAlertMessage(
                    "Missing Data",
                    "Please select a disease and enter the medical record.",
                    Dialogs.MessageType.ERROR_MESSAGE
            );
            return;
        }

        progressIndicator.setVisible(true);
        analysisTextArea.clear();

        new Thread(() -> {
            // Get summary from Wikipedia using WikipediaConnector Class
            String summary = WikipediaConnector.getDiseaseInfo(selectedDisease.getName());

            Platform.runLater(() -> {
                progressIndicator.setVisible(false);
                analysisTextArea.setText(summary);
            });
        }).start();
    }


    @FXML
    public void performSGSAnalysis(ActionEvent actionEvent) {
        Disease selectedDisease = diagnostics_diseaseCombobox.getValue();

        // Check if there is any missing data
        // for the spinners it's ok because it is already have a value of 0
        if (selectedDisease == null || radioGroup.getSelectedToggle() == null) {
            Dialogs.showAlertMessage(
                    "Missing Data",
                    "Please select a disease and condition.",
                    Dialogs.MessageType.ERROR_MESSAGE
            );
            return;
        }

        double diseaseWeight = selectedDisease.getWeight();
        String condition = ((RadioButton) radioGroup.getSelectedToggle()).getText().toLowerCase();
        int firstRel = diagnostics_firstDegreeSpinner.getValue();
        int secondRel = diagnostics_secondDegreeSpinner.getValue();


        // Analyze the risk using SGSAnalyzer Tool
        RiskResult result = SGSAnalyzer.analyzeRisk(selectedDisease.getName(), diseaseWeight, condition, firstRel, secondRel);

        diagnostics_diseaseWeightField.setText(String.valueOf(result.diseaseWeight));
        diagnostics_geneConditionField.setText(result.condition);
        diagnostics_firstRelativeField.setText(String.valueOf(result.firstRelative));
        diagnostics_secondRelativeField.setText(String.valueOf(result.secondRelative));
        diagnostics_totalRiskScoreField.setText(String.format("%.2f", result.totalScore));
        diagnostics_riskLevelField.setText(result.riskLevel);

        // Change the Risk Level background color based on how dangerous his situation is
        String level = result.riskLevel.toLowerCase();
        if (level.contains("low")) {
            diagnostics_riskLevelField.setStyle("-fx-background-color: #A5D6A7; -fx-background-radius: 8; -fx-text-fill: #fff;");
        } else if (level.contains("moderate")) {
            diagnostics_riskLevelField.setStyle("-fx-background-color: #FFF59D; -fx-background-radius: 8;");
        } else if (level.contains("high")) {
            diagnostics_riskLevelField.setStyle("-fx-background-color: #EF9A9A; -fx-background-radius: 8; -fx-text-fill: #fff;");
        }

        diagnostics_saveButton.setDisable(false);
    }


    // *********************************************************************************************
    // **********************************  Save Analysis To DB  ************************************
    // *********************************************************************************************

    // Saving Process, we should make sure that no data are saved before sending
    // data to the DB to prevent any conflict
    @FXML
    public void saveAnalysis(ActionEvent event) {
        String medicalRecord = diagnostics_medicalRecField.getText().trim();
        String wikiSummary = analysisTextArea.getText().trim();

        if (medicalRecord.isEmpty() || wikiSummary.isEmpty()) {
            Dialogs.showAlertMessage("Missing Data", "Please make sure the medical record and Wikipedia summary are available.", Dialogs.MessageType.ERROR_MESSAGE);
            return;
        }

        double diseaseWeight, totalScore;
        String riskLevel;

        try {
            diseaseWeight = Double.parseDouble(diagnostics_diseaseWeightField.getText());
            totalScore = Double.parseDouble(diagnostics_totalRiskScoreField.getText());
            riskLevel = diagnostics_riskLevelField.getText();
        } catch (NumberFormatException e) {
            Dialogs.showAlertMessage("Invalid Data", "Please perform the analysis first before saving.", Dialogs.MessageType.ERROR_MESSAGE);
            return;
        }

        String disease = diagnostics_diseaseCombobox.getValue().getName();
        String condition = ((RadioButton) radioGroup.getSelectedToggle()).getText();
        int firstRel = diagnostics_firstDegreeSpinner.getValue();
        int secondRel = diagnostics_secondDegreeSpinner.getValue();

        new Thread(() -> {
            try (Connection conn = DatabaseConnection.connect()) {
                String checkQuery = "SELECT COUNT(*) FROM analysis WHERE medicalrecord = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                    checkStmt.setString(1, medicalRecord);
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next() && rs.getInt(1) > 0) {
                        Platform.runLater(() -> Dialogs.showConfirmationAlert(
                                "Previous Analysis Found",
                                "An analysis for this patient already exists.\nDo you want to replace it?",
                                () -> insertValuesToDB(medicalRecord, wikiSummary, diseaseWeight, totalScore, riskLevel, disease, condition, firstRel, secondRel)
                        ));
                    } else {
                        insertValuesToDB(medicalRecord, wikiSummary, diseaseWeight, totalScore, riskLevel, disease, condition, firstRel, secondRel);
                    }
                }
            } catch (SQLException e) {
                Platform.runLater(() -> Dialogs.showAlertMessage("Database Error", e.getMessage(), Dialogs.MessageType.ERROR_MESSAGE));
            }
        }).start();
    }


    private void insertValuesToDB(String medicalRecord, String wikiSummary, double diseaseWeight, double totalScore, String riskLevel, String disease, String condition, int firstRel, int secondRel) {
        new Thread(() -> {
            try (Connection conn = DatabaseConnection.connect()) {
                // Delete existing records
                String delete1 = "DELETE FROM analysis WHERE medicalrecord = ?";
                String delete2 = "DELETE FROM diagnostic WHERE medicalrecord = ?";
                try (PreparedStatement stmt1 = conn.prepareStatement(delete1);
                     PreparedStatement stmt2 = conn.prepareStatement(delete2)) {
                    stmt1.setString(1, medicalRecord);
                    stmt2.setString(1, medicalRecord);
                    stmt1.executeUpdate();
                    stmt2.executeUpdate();
                }

                // Insert into analysis
                String insertAnalysis = "INSERT INTO analysis (medicalrecord, wikipediasummary, diseaseweight, totalriskscore, risklevel) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertAnalysis)) {
                    stmt.setString(1, medicalRecord);
                    stmt.setString(2, wikiSummary);
                    stmt.setDouble(3, diseaseWeight);
                    stmt.setDouble(4, totalScore);
                    stmt.setString(5, riskLevel);
                    stmt.executeUpdate();
                }

                // Insert into diagnostic
                String insertDiag = "INSERT INTO diagnostic (medicalrecord, disease, condition, firstrelative, secondrelative) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertDiag)) {
                    stmt.setString(1, medicalRecord);
                    stmt.setString(2, disease);
                    stmt.setString(3, condition);
                    stmt.setInt(4, firstRel);
                    stmt.setInt(5, secondRel);
                    stmt.executeUpdate();
                }

                Platform.runLater(() -> {
                    diagnostics_saveButton.setDisable(true);
                    Dialogs.showAlertMessage("Success", "Analysis and diagnosis saved successfully!", Dialogs.MessageType.INFO_MESSAGE);
                });
            } catch (SQLException e) {
                Platform.runLater(() -> Dialogs.showAlertMessage("Database Error", e.getMessage(), Dialogs.MessageType.ERROR_MESSAGE));
            }
        }).start();
    }

    // ---------------------------------------------------------------------------------------------------------------


    @FXML
    public void clearEverything(ActionEvent actionEvent) {
        diseasePane.setDisable(true);
        analysisPane.setDisable(true);

        diagnostics_nationalIdField.clear();
        diagnostics_medicalRecField.clear();
        diagnostics_firstNameField.clear();
        diagnostics_lastNameField.clear();
        diagnostics_ageField.clear();
        diagnostics_genderField.clear();
        analysisTextArea.clear();
        diagnostics_diseaseWeightField.clear();
        diagnostics_geneConditionField.clear();
        diagnostics_firstRelativeField.clear();
        diagnostics_secondRelativeField.clear();
        diagnostics_totalRiskScoreField.clear();
        diagnostics_riskLevelField.clear();

        diagnostics_diseaseCombobox.getSelectionModel().clearSelection();
        diagnostics_diseaseCombobox.setValue(null);
        diagnostics_diseaseCombobox.setEditable(true);
        diagnostics_diseaseCombobox.setPromptText("Choose...");
        diagnostics_diseaseCombobox.setEditable(false);


        if (radioGroup.getSelectedToggle() != null) {
            radioGroup.getSelectedToggle().setSelected(false);
        }

        diagnostics_firstDegreeSpinner.getValueFactory().setValue(0);
        diagnostics_secondDegreeSpinner.getValueFactory().setValue(0);

    }


    // *********************************************************************************************
    // ***************************************  Initialize  ****************************************
    // *********************************************************************************************

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        diagnostics_diseaseCombobox.setEditable(false);
        buttonEffects();
        diseaseComboboxItems();
        setSpinnerValues();
        radioButtonGroup();
        setLoginTime();
        getPatientData();


        diagnostics_mainTitleText.setFill(new ImagePattern(new Image(getClass().getResourceAsStream("/images/black-wallpaper.jpg"))));
    }

}
