package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.utils.database.DatabaseConnection;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class NewPatientController extends Application implements Initializable {
    @FXML
    public AnchorPane root;

    @FXML
    public TextField addNewPatient_firstNameField;

    @FXML
    public TextField addNewPatient_lastNameField;

    @FXML
    public DatePicker addNewPatient_dateOfBirthPicker;

    @FXML
    public TextField addNewPatient_ageField;

    @FXML
    public TextField addNewPatient_emailField;

    @FXML
    public TextField addNewPatient_phoneNumberField;

    @FXML
    public RadioButton addNewPatient_maleRadioButton;

    @FXML
    public RadioButton addNewPatient_femaleRadioButton;

    @FXML
    public Button addNewPatient_confirm;

    @FXML
    public Button addNewPatient_cancel;

    @FXML
    public Label addNewPatient_statusLabel;

    @FXML
    public TextField addNewPatient_nationalIDField;

    @FXML
    public TextField addNewPatient_medicalRecordField;

    ToggleGroup genderGroup = new ToggleGroup();
    private PatientDataController patientDataController;


    public void setPatientDataController(PatientDataController controller) {
        this.patientDataController = controller;
    }

    public static void main(String[] args) {
        Application.launch();
    }

    public void buttonEffects() {
        Effects.buttonEffectSoftScale(addNewPatient_confirm);
        Effects.buttonEffectSoftScale(addNewPatient_cancel);
    }

    public void validateNationalID() {
        // the user enters the ID based on Saudi Arabian National ID
        // letters are not allowed (digits only)
        // 10 digits are allowed
        Pattern pattern = Pattern.compile("\\d{0,10}");

        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (pattern.matcher(newText).matches()) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        addNewPatient_nationalIDField.setTextFormatter(textFormatter);
    }

    public void createMedicalRecord() {
        // addNewPatient_medicalRecordField is disabled and not editable by default
        // after the user enters th national ID,
        // it generates a medical record that starts with MAMA (Program owners initials(Students))
        // along with the patient's id number
        addNewPatient_nationalIDField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() >= 5) {
                String lastFiveDigits = newValue.substring(Math.max(0, newValue.length() - 5));
                String medicalRecord = String.format("MAMA-%02d%02d%s",
                        LocalDate.now().getMonthValue(),
                        LocalDate.now().getYear() % 100,
                        lastFiveDigits);

                addNewPatient_medicalRecordField.setText(medicalRecord);
            } else {
                addNewPatient_medicalRecordField.setText("");
            }
        });
    }

    public void validateDateOfBirth() {
        // Check the Date ofBirth entered,  if correct, calculate Age.
        // Otherwise, show an error alert.
        // Also, style the date picker with red color to make the user pay attention
        // Also, a status label show up to correct the user
        addNewPatient_dateOfBirthPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDate today = LocalDate.now();

            if (newValue == null || newValue.isAfter(today)) {
                addNewPatient_statusLabel.setText("❌ Invalid Date of Birth");
                addNewPatient_statusLabel.setStyle("-fx-text-fill: red;");
                addNewPatient_statusLabel.setVisible(true);
                addNewPatient_dateOfBirthPicker.getEditor().setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
                addNewPatient_confirm.setDisable(true);
            } else {
                addNewPatient_statusLabel.setVisible(false);
                addNewPatient_dateOfBirthPicker.getEditor().setStyle("");
                addNewPatient_confirm.setDisable(false);
                addNewPatient_ageField.setText(calculateAge(newValue));
            }
        });
    }

    public String calculateAge(LocalDate dateOfBirth) {
        LocalDate today = LocalDate.now();
        long years = ChronoUnit.YEARS.between(dateOfBirth, today);

        if (years < 1) {
            long weeks = ChronoUnit.WEEKS.between(dateOfBirth, today);
            return weeks + " wks";
        } else {
            return years + " yrs";
        }
    }

    public void validateEmail() {
        // Check if the email address is written correctly
        // Otherwise, user cannot proceed
        addNewPatient_emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

            if (!newValue.matches(emailPattern)) {
                addNewPatient_statusLabel.setText("❌ Invalid email address");
                addNewPatient_statusLabel.setStyle("-fx-text-fill: red;");
                addNewPatient_statusLabel.setVisible(true);
                addNewPatient_emailField.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
                addNewPatient_confirm.setDisable(true);
            } else {
                addNewPatient_statusLabel.setVisible(false);
                addNewPatient_emailField.setStyle("");
                addNewPatient_confirm.setDisable(false);
            }
        });

    }

    public void validatePhoneNumber() {
        // Phone Number should start with 5
        // Also, Phone number should have 9 digits
        // Phone number must not contain any letter !!!
        addNewPatient_phoneNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            String filteredValue = newValue.replaceAll("[^0-9]", "");

            if (filteredValue.length() > 9) {
                filteredValue = filteredValue.substring(0, 9);
            }
            addNewPatient_phoneNumberField.setText(filteredValue);
            String phonePattern = "^5\\d{8}$";

            if (!filteredValue.matches(phonePattern)) {
                addNewPatient_statusLabel.setText("❌ Invalid phone number\nPhone number should start with 5 and contains 9 digits");
                addNewPatient_statusLabel.setStyle("-fx-text-fill: red;");
                addNewPatient_statusLabel.setVisible(true);
                addNewPatient_phoneNumberField.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
                addNewPatient_confirm.setDisable(true);
            } else {
                addNewPatient_statusLabel.setVisible(false);
                addNewPatient_phoneNumberField.setStyle("");
                addNewPatient_confirm.setDisable(false);
            }
        });
    }

    public void genderRadioButtonsGroup() {
        addNewPatient_maleRadioButton.setToggleGroup(genderGroup);
        addNewPatient_femaleRadioButton.setToggleGroup(genderGroup);

    }

    @FXML
    public void ConfirmSavingNewPatient(ActionEvent actionEvent) {
        // First check if any field is empty
        List<TextInputControl> requiredFields = Arrays.asList(
                addNewPatient_nationalIDField,
                addNewPatient_medicalRecordField,
                addNewPatient_firstNameField,
                addNewPatient_lastNameField,
                addNewPatient_ageField,
                addNewPatient_emailField,
                addNewPatient_phoneNumberField
        );

        for (TextInputControl field : requiredFields) {
            if (field.getText().trim().isEmpty()) {
                Dialogs.showAlertMessage("Error",
                        "Fill all credintials, please", Dialogs.MessageType.ERROR_MESSAGE);
                return;
            }
        }

        if (addNewPatient_dateOfBirthPicker.getValue() == null) {
            Dialogs.showAlertMessage("Error",
                    "Fill all credintials, please", Dialogs.MessageType.ERROR_MESSAGE);
            return;
        }

        if (genderGroup.getSelectedToggle() == null) {
            Dialogs.showAlertMessage("Error",
                    "Fill all credintials, please", Dialogs.MessageType.ERROR_MESSAGE);
            return;
        }


        // if everything goes ok proceed with saving data to the database.
        // but, check if the patient with the id entered is already exists.
        long nationalId = Long.parseLong(addNewPatient_nationalIDField.getText());
        String query = "SELECT * FROM patient WHERE nationalid = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setLong(1, nationalId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Dialogs.showAlertMessage("Error",
                        "This patient with national ID provided is already exists",
                        Dialogs.MessageType.ERROR_MESSAGE);

                return;
            }

        } catch (SQLException ex) {
            Dialogs.showAlertMessage("System Error", ex.getMessage(), Dialogs.MessageType.ERROR_MESSAGE);
        }

        // Now, we can proceed saving data to the DB
        query = "INSERT INTO patient(nationalid, medicalrecord, firstname, lastname, dob, age, email, phone, gender) VALUES (?,?,?,?,?,?,?,?,?)";
        String medicalRecord = addNewPatient_medicalRecordField.getText();
        String firstName = addNewPatient_firstNameField.getText();
        String lastName = addNewPatient_lastNameField.getText();
        // first convert datepicker data to String so that can be saved into the database.
        LocalDate selectedDate = addNewPatient_dateOfBirthPicker.getValue();
        String dob = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String age = addNewPatient_ageField.getText();
        String email = addNewPatient_emailField.getText();
        String phone = addNewPatient_phoneNumberField.getText();
        Toggle selectedToggle = genderGroup.getSelectedToggle();
        String gender = (selectedToggle != null) ? ((RadioButton) selectedToggle).getText() : null;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            preparedStatement.setLong(1, nationalId);
            preparedStatement.setString(2, medicalRecord);
            preparedStatement.setString(3, firstName);
            preparedStatement.setString(4, lastName);
            preparedStatement.setString(5, dob);
            preparedStatement.setString(6, age);
            preparedStatement.setString(7, email);
            preparedStatement.setString(8, phone);
            preparedStatement.setString(9, gender);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                Dialogs.showAlertMessage("Success", "Patient record saved successfully!", Dialogs.MessageType.INFO_MESSAGE);

                // ✅ Close the new patient window
                Stage stage = (Stage) addNewPatient_nationalIDField.getScene().getWindow();
                stage.close();

                // ✅ Refresh the TableView in PatientDataController
                if (patientDataController != null) {
                    patientDataController.tableViewRecords();
                } else {
                    Dialogs.showAlertMessage("Error", "Failed to save patient data.", Dialogs.MessageType.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            Dialogs.showAlertMessage("System Error", ex.getMessage(), Dialogs.MessageType.ERROR_MESSAGE);
        }
    }



    @FXML
    public void cancelAddingNewPatient(ActionEvent actionEvent) {
        Dialogs.showConfirmationAlert("Cancel adding new patient?",
                "Data provided will not be saved!\nAre you sure?",
                () -> ((Stage) root.getScene().getWindow()).close());
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        validateNationalID();
        createMedicalRecord();
        buttonEffects();
        validateDateOfBirth();
        validateEmail();
        validatePhoneNumber();
        genderRadioButtonsGroup();
    }

    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("add-new-patient-view.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }
}
