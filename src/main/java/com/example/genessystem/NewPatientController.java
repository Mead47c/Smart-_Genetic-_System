package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
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
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    public ComboBox<String> addNewPatient_diseaseCombobox;

    @FXML
    public ComboBox<String> addNewPatient_diseaseConditionCombobox;

    @FXML
    public ComboBox<String> addNewPatient_kinshipCombobox;

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
            addNewPatient_medicalRecordField.setText("MAMA-" + newValue);
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
            String phonePattern = "^5\\d{8}$";

            if (!newValue.matches(phonePattern)) {
                addNewPatient_statusLabel.setText("❌ Invalid phone number");
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
        // TODO
    }

    @FXML
    public void cancelAddingNewPatient(ActionEvent actionEvent) {
        boolean response = Dialogs.showConfirmationAlert("Cancel Adding New Patient",
                "Data will not be saved !\nAre you sure you want to cancel?", true);

        if (response) {
            ((Stage) root.getScene().getWindow()).close();
        }
    }

    public void diseaseComboboxItems() {
        addNewPatient_diseaseCombobox.getItems().addAll(
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
        addNewPatient_diseaseConditionCombobox.getItems().addAll(
                "Injured",
                "Safe",
                "Carrier");
    }

    public void degreeOfKinshipComboboxItems() {
        addNewPatient_kinshipCombobox.getItems().addAll("1", "2");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        validateNationalID();
        createMedicalRecord();
        diseaseComboboxItems();
        diseaseConditionCombobox();
        degreeOfKinshipComboboxItems();
        buttonEffects();
        validateDateOfBirth();
        validateEmail();
        validatePhoneNumber();
        genderRadioButtonsGroup();

        addNewPatient_diseaseCombobox.setStyle("-fx-background-color: transparent !important;");
    }

    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("add-new-patient-view.fxml"));

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.show();
    }
}
