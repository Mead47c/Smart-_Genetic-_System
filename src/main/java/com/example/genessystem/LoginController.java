package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.utils.Email;
import com.example.genessystem.utils.database.DatabaseConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    public Button loginScreen_SignupButton;

    @FXML
    public Button closeButton;

    @FXML
    public Button loginScreen_loginButton;

    @FXML
    public TextField loginScreen_usernameField;

    @FXML
    public PasswordField loginScreen_passwordField;

    @FXML
    public TextField loginScreen_VisiblePasswordField;

    @FXML
    public CheckBox loginScreen_showPasswordCheckbox;

    @FXML
    public TextField signupScreen_usernameField;

    @FXML
    public PasswordField signupScreen_passwordField;

    @FXML
    public PasswordField signupScreen_repeatPasswordField;

    @FXML
    public CheckBox signupScreen_showPasswordCheckbox;

    @FXML
    public TextField signupScreen_VisiblePasswordField;

    @FXML
    public TextField signupScreen_emailField;

    @FXML
    public Button signupScreen_signupButton;

    @FXML
    public Button signupScreen_loginButton;

    @FXML
    public Label mainTitleLabel;

    @FXML
    public VBox signupScreen;

    @FXML
    public VBox loginScreen;

    @FXML
    public AnchorPane root;

    @FXML
    public ImageView mainAppLogo;

    @FXML
    public StackPane login_passwordPane;

    @FXML
    public Label usernameStatusLabel;

    @FXML
    public Label passwordStatusLabel;

    @FXML
    public Label rePasswordStatusLabel;

    @FXML
    public Label emailStatusLabel;

    private ProgressIndicator indicator;
    private Label statusLabel;
    private int generatedOTP = -1;


    // ***************************************************************************************************
    // ********************** Main Login & Signup Operations (closing and effects)   *********************
    // ***************************************************************************************************

    // close the program
    @FXML
    public void closeApplication() {
        Toolkit.getDefaultToolkit().beep();
        Dialogs.showConfirmationAlert("Close Program?",
                "You are about to close the program!\nAre you sure?",
                () -> ((Stage) root.getScene().getWindow()).close());
    }

    // show or hide password
    private void togglePasswordVisibility(CheckBox showPasswordCheckbox,
                                          PasswordField passwordField,
                                          TextField visiblePasswordField) {
        if (showPasswordCheckbox.isSelected()) {
            String password = passwordField.getText();
            visiblePasswordField.setText(password);
            visiblePasswordField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            String password = visiblePasswordField.getText();
            passwordField.setText(password);
            passwordField.setVisible(true);
            visiblePasswordField.setVisible(false);
        }
    }


    // switch among screens
    public void switchToSignupScreen() {
        loginScreen_SignupButton.setOnAction(e -> {
            clearLogin();

            Effects.fadeOut(mainTitleLabel, Duration.millis(500), 1, () -> {
                mainTitleLabel.setText(" Signup");
                Effects.fadeIn(mainTitleLabel, Duration.millis(500), 1);
            });

            Effects.fadeOut(mainAppLogo, Duration.millis(500), 1, () -> {
                mainAppLogo.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/signupLogo.png"))));
                Effects.fadeIn(mainAppLogo, Duration.millis(500), 1);
            });

            Effects.fadeOut(loginScreen, Duration.millis(500), 1, () -> {
                Effects.fadeIn(signupScreen, Duration.millis(500), 1);
            });
        });
    }

    public void switchToLoginScreen() {
        signupScreen_loginButton.setOnAction(e -> {
            clearSignup();

            Effects.fadeOut(mainTitleLabel, Duration.millis(500), 1, () -> {
                mainTitleLabel.setText(" Login");
                Effects.fadeIn(mainTitleLabel, Duration.millis(500), 1);
            });

            Effects.fadeOut(mainAppLogo, Duration.millis(500), 1, () -> {
                mainAppLogo.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/loginLogo.png"))));
                Effects.fadeIn(mainAppLogo, Duration.millis(500), 1);
            });

            Effects.fadeOut(signupScreen, Duration.millis(500), 1, () -> {
                Effects.fadeIn(loginScreen, Duration.millis(500), 1);
            });
        });
    }

    // show username status label
    public void checkUsernameWhileSignup() {
        String username = signupScreen_usernameField.getText().trim();

        if (username.isEmpty()) {
            usernameStatusLabel.setVisible(false);
            return;
        }

        new Thread(() -> {
            boolean isAvailable = checkUsernameAvailability(username);

            Platform.runLater(() -> {
                if (isAvailable) {
                    signupScreen_usernameField.setStyle("");
                    usernameStatusLabel.setText("✅ Username is available");
                    usernameStatusLabel.setStyle("-fx-text-fill: green;");
                } else {
                    signupScreen_usernameField.setStyle("-fx-background-color: #ff6666; -fx-text-fill: #fff;");
                    usernameStatusLabel.setText("❌ Username is already in use");
                    usernameStatusLabel.setStyle("-fx-text-fill: red;");
                }
                usernameStatusLabel.setVisible(true);
            });
        }).start();
    }

    private boolean checkUsernameAvailability(String username) {
        String query = "SELECT COUNT(*) FROM accounts WHERE username = ?";

        try (PreparedStatement pstmt = DatabaseConnection.connect().prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            return rs.next() && rs.getInt(1) == 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // check passwords matching
    public void checkPasswordWhileSignup() {
        String password = signupScreen_passwordField.getText();
        String repeatedPassword = signupScreen_repeatPasswordField.getText();

        if (!password.equals(repeatedPassword)) {
            signupScreen_signupButton.setDisable(true);
            signupScreen_repeatPasswordField.setStyle("-fx-background-color: #ff6666; " +
                    "-fx-text-fill: #fff;");
            rePasswordStatusLabel.setVisible(true);
            rePasswordStatusLabel.setText("❌ Passwords do not match");
            rePasswordStatusLabel.setStyle("-fx-text-fill: red;");
        } else {
            signupScreen_signupButton.setDisable(false);
            signupScreen_repeatPasswordField.setStyle("");
            rePasswordStatusLabel.setVisible(false);
        }
    }

    // show email status label
    public void checkEmailWhileSignup() {
        String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String email = signupScreen_emailField.getText().trim();

        // Hide the label if the email field is empty
        if (email.isEmpty()) {
            emailStatusLabel.setVisible(false);
            signupScreen_emailField.setStyle("");
            signupScreen_signupButton.setDisable(true);
            return;
        }

        // Validate email format
        if (!email.matches(EMAIL_PATTERN)) {
            emailStatusLabel.setText("❌ Invalid Email Format");
            emailStatusLabel.setStyle("-fx-text-fill: red;");
            signupScreen_emailField.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
            emailStatusLabel.setVisible(true);
            signupScreen_signupButton.setDisable(true);
            return;
        }

        // Check email availability in database (Run in a separate thread)
        new Thread(() -> {
            boolean isAvailable = checkEmailAvailability(email);

            Platform.runLater(() -> {
                if (isAvailable) {
                    emailStatusLabel.setText("✅ Email is available");
                    emailStatusLabel.setStyle("-fx-text-fill: green;");
                    signupScreen_emailField.setStyle("");
                    signupScreen_signupButton.setDisable(false);
                } else {
                    emailStatusLabel.setText("❌ Email is already in use");
                    emailStatusLabel.setStyle("-fx-text-fill: red;");
                    signupScreen_emailField.setStyle("-fx-background-color: #ff6666; -fx-text-fill: white;");
                    signupScreen_signupButton.setDisable(true);
                }
                emailStatusLabel.setVisible(true);
            });
        }).start();
    }

    // Database Check for Email Availability
    private boolean checkEmailAvailability(String email) {
        String query = "SELECT COUNT(*) FROM accounts WHERE email = ?";

        try (PreparedStatement pstmt = DatabaseConnection.connect().prepareStatement(query)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) == 0; // True if email is available
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Assume unavailable if an error occurs
        }
    }

    // clear login & signup screens
    public void clearLogin() {
        loginScreen_usernameField.clear();
        loginScreen_passwordField.clear();
        loginScreen_VisiblePasswordField.clear();
        loginScreen_showPasswordCheckbox.setSelected(false);
    }

    public void clearSignup() {
        signupScreen_usernameField.clear();
        signupScreen_usernameField.setStyle("");
        signupScreen_passwordField.clear();
        signupScreen_passwordField.setStyle("");
        signupScreen_VisiblePasswordField.clear();
        signupScreen_repeatPasswordField.clear();
        signupScreen_repeatPasswordField.setStyle("");
        signupScreen_showPasswordCheckbox.setSelected(false);
        signupScreen_emailField.clear();
        signupScreen_emailField.setStyle("");
        signupScreen_signupButton.setDisable(false);
    }

    // ***************************************************************************************************
    // *********************** Logging in to the system and checking credentials   ***********************
    // ***************************************************************************************************

    // Login process includes:
    // 1. Check the database if the user exist?
    // 2. Close the login screen and open the main screen of the program
    @FXML
    public void login(ActionEvent event) {
        if (loginScreen_usernameField.getText().isEmpty()) {
            Dialogs.showAlertMessage("Username Required!", "Cannot leave username Empty!!!", Dialogs.MessageType.ERROR_MESSAGE);
            return;
        }

        if (loginScreen_passwordField.getText().isEmpty()) {
            Dialogs.showAlertMessage("Password Required!", "Cannot leave password Empty!!!", Dialogs.MessageType.ERROR_MESSAGE);
            return;
        }

        String username = loginScreen_usernameField.getText();
        String password = loginScreen_passwordField.getText();
        String query = "SELECT * FROM accounts WHERE username = ? AND password = ?";

        try (PreparedStatement pstmt = DatabaseConnection.connect().prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Dialogs.showAlertMessage("Success", "Welcome back: " + username + " :)", Dialogs.MessageType.INFO_MESSAGE);
                Effects.fadeOut(root, Duration.millis(500), 1, () -> {
                    Main.switchScreen("dashboard-view.fxml", username);
                    Effects.fadeIn(Main.primaryStage.getScene().getRoot(), Duration.millis(500), 1);
                });

            } else {
                Dialogs.showAlertMessage("Failed!!!", "Invalid Username or Password", Dialogs.MessageType.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            Dialogs.showAlertMessage("System Error!", ex.getMessage(), Dialogs.MessageType.ERROR_MESSAGE);
        }
    }


    // ***************************************************************************************************
    // ************************* Create a new account after checking existence   *************************
    // ***************************************************************************************************

    // Signup process includes:
    // 1. OTP Verification
    // 2. if OTP is correct it process the signup and add the new user to the database
    @FXML
    public void createNewAccount(ActionEvent event) throws UnsupportedEncodingException, SQLException {
        if (signupScreen_usernameField.getText().isEmpty()
                || signupScreen_repeatPasswordField.getText().isEmpty()
                || signupScreen_emailField.getText().isEmpty()) {
            Dialogs.showAlertMessage("Error!", "Please fill all the fields correctly.", Dialogs.MessageType.ERROR_MESSAGE);
            return;
        }

        String username = signupScreen_usernameField.getText();
        String email = signupScreen_emailField.getText();
        String query = "Select username from accounts where username = ?";

        try (PreparedStatement preparedStatement = DatabaseConnection.connect().prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Dialogs.showAlertMessage("Failed", "User already exists", Dialogs.MessageType.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException ex) {
            Dialogs.showAlertMessage("System Error!", ex.getMessage(), Dialogs.MessageType.ERROR_MESSAGE);
        }

        query = "Select email from accounts where email = ?";
        try (PreparedStatement preparedStatement = DatabaseConnection.connect().prepareStatement(query)) {
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Dialogs.showAlertMessage("Failed", "Email is already in use", Dialogs.MessageType.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException ex) {
            Dialogs.showAlertMessage("System Error!", ex.getMessage(), Dialogs.MessageType.ERROR_MESSAGE);
        }

        String otp;
        Scene scene = new Scene(createOTPDialog(), 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles/otp-stage-style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);

        Stage otpStage = new Stage();
        otpStage.setScene(scene);
        otpStage.initModality(Modality.APPLICATION_MODAL);
        otpStage.initStyle(StageStyle.UNDECORATED);
        otpStage.setAlwaysOnTop(true);
        otpStage.show();

    }

    private VBox createOTPDialog() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);

        Label statusLabel = new Label("Sending OTP...");
        statusLabel.setWrapText(true);

        ProgressIndicator indicator = new ProgressIndicator();
        root.getChildren().addAll(indicator, statusLabel);

        TextField[] otpFields = new TextField[6];
        HBox fieldsBox = new HBox(5);
        fieldsBox.setAlignment(Pos.CENTER);

        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        okButton.setVisible(false);
        cancelButton.setVisible(true);

        for (int i = 0; i < otpFields.length; i++) {
            otpFields[i] = new TextField();
            otpFields[i].setAlignment(Pos.CENTER);
            otpFields[i].setVisible(false);
            otpFields[i].setPrefWidth(40);
            otpFields[i].setMaxWidth(40);
            fieldsBox.getChildren().add(otpFields[i]);

            int currentIndex = i;
            otpFields[i].textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.isEmpty() && Character.isDigit(newValue.charAt(0))) {
                    otpFields[currentIndex].setText(String.valueOf(newValue.charAt(0)));

                    Platform.runLater(() -> {
                        if (currentIndex < otpFields.length - 1) {
                            otpFields[currentIndex + 1].requestFocus();
                        } else {
                            okButton.requestFocus();
                        }
                    });
                } else {
                    otpFields[currentIndex].setText("");
                }
            });
        }

        HBox buttonBox = new HBox(10, okButton, cancelButton);
        buttonBox.setPadding(new Insets(30, 0, 0, 0));
        buttonBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(fieldsBox, buttonBox);

        // Generate new OTP @ every time this method is called
        generatedOTP = Email.sendOTPEmail(signupScreen_emailField.getText(),
                signupScreen_usernameField.getText(), statusLabel, new Email.EmailCallback() {
                    @Override
                    public void onSuccess(String message) {
                        statusLabel.setText("OTP Sent Successfully.\nCheck your email. If you couldn't find it,\ncheck your junk mails, please");
                        indicator.setVisible(false);
                        for (TextField field : otpFields) field.setVisible(true);
                        okButton.setVisible(true);
                        cancelButton.setVisible(true);
                    }

                    @Override
                    public void onFailure(String error) {
                        statusLabel.setText("Check the internet connection!");
                        indicator.setVisible(false);
                        cancelButton.setVisible(true);
                    }
                });

        okButton.setOnAction(e -> {
            StringBuilder enteredOTP = new StringBuilder();
            for (TextField field : otpFields) enteredOTP.append(field.getText());

            if (enteredOTP.toString().equals(String.valueOf(generatedOTP))) {
                ((Stage) root.getScene().getWindow()).close();
                saveNewAcoountCredintials();
                Dialogs.showAlertMessage("Success", "Your account has been created successfully.", Dialogs.MessageType.INFO_MESSAGE);

                clearSignup();
                Effects.fadeOut(mainTitleLabel, Duration.millis(500), 1, () -> {
                    mainTitleLabel.setText(" Login");
                    Effects.fadeIn(mainTitleLabel, Duration.millis(500), 1);
                });

                Effects.fadeOut(mainAppLogo, Duration.millis(500), 1, () -> {
                    mainAppLogo.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/loginLogo.png"))));
                    Effects.fadeIn(mainAppLogo, Duration.millis(500), 1);
                });

                Effects.fadeOut(signupScreen, Duration.millis(500), 1, () -> {
                    Effects.fadeIn(loginScreen, Duration.millis(500), 1);
                });
            } else {
                statusLabel.setText("Invalid OTP. Try again.");
            }
        });

        cancelButton.setOnAction(e -> {
            Stage currentStage = (Stage) root.getScene().getWindow();
            currentStage.close();
        });

        System.out.println("Generated OTP: " + generatedOTP);

        return root;
    }

    public void saveNewAcoountCredintials() {
        String username = signupScreen_usernameField.getText();
        String password = signupScreen_passwordField.getText();
        String email = signupScreen_emailField.getText();

        String query = "INSERT INTO accounts (username, password, email) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = DatabaseConnection.connect().prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, email);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Dialogs.showAlertMessage("System Error", ex.getMessage(), Dialogs.MessageType.ERROR_MESSAGE);
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set Effects
        Effects.buttonEffect(loginScreen_loginButton);
        Effects.buttonEffect(loginScreen_SignupButton);
        Effects.buttonEffect(signupScreen_signupButton);
        Effects.buttonEffect(signupScreen_loginButton);

        // Switch between Login & Signup
        switchToSignupScreen();
        switchToLoginScreen();
        checkPasswordWhileSignup();

        // Show and Hide statusLabels
        // Hide the label when the text field loses focus
        signupScreen_usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkUsernameWhileSignup();
        });
        signupScreen_usernameField.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                usernameStatusLabel.setVisible(false);
            }
        });
        signupScreen_emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            checkEmailWhileSignup();
        });
        signupScreen_emailField.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue) {
                emailStatusLabel.setVisible(false);
            }
        });

        // Show || Hide password
        // For sign in screen ->
        loginScreen_showPasswordCheckbox.setOnAction(e ->
                togglePasswordVisibility(loginScreen_showPasswordCheckbox,
                        loginScreen_passwordField,
                        loginScreen_VisiblePasswordField)
        );
        // For signup screen ->
        signupScreen_showPasswordCheckbox.setOnAction(e ->
                togglePasswordVisibility(signupScreen_showPasswordCheckbox,
                        signupScreen_passwordField,
                        signupScreen_VisiblePasswordField)
        );

        // Check if passwords matching or not
        signupScreen_passwordField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        checkPasswordWhileSignup());
        signupScreen_repeatPasswordField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        checkPasswordWhileSignup());
    }
}