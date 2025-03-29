package com.example.genessystem.objects;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.utils.Email;
import com.example.genessystem.utils.PatientPDFGenerator;
import com.example.genessystem.utils.database.DatabaseConnection;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class PrintPatient {
    private final String nationalId, medicalRecord, firstName, lastName, phone;
    private final Button printButton = new Button();
    private final Button shareButton = new Button();
    private final ImageView loadingGif = new ImageView(new Image(getClass().getResourceAsStream("/images/loading.gif")));


    public PrintPatient(String nationalId, String medicalRecord, String firstName, String lastName, String phone) {
        this.nationalId = nationalId;
        this.medicalRecord = medicalRecord;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;

        this.printButton.setOnAction(e -> handlePrint());
        this.printButton.getStyleClass().add("functional-button");
        this.printButton.getStyleClass().add("print-button");
        Effects.buttonEffect(this.printButton);

        this.shareButton.getStyleClass().add("functional-button");
        this.shareButton.getStyleClass().add("share-button");
        Effects.buttonEffect(this.shareButton);
        this.shareButton.setOnAction(e -> handleShare());
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getMedicalRecord() {
        return medicalRecord;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public Button getPrintButton() {
        return printButton;
    }

    public Button getShareButton() {
        return shareButton;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }


    // ===============================================================================================
    // ======================== Generate PDF File that prints full analysis ==========================
    // ===============================================================================================
    private void handlePrint() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT p.firstname, p.lastname, p.nationalid, p.medicalrecord, p.age, p.gender, p.phone, p.email, " +
                    "d.disease, d.condition, d.firstrelative, d.secondrelative, " +
                    "a.diseaseweight, a.totalriskscore, a.risklevel, a.wikipediasummary " +
                    "FROM patient p " +
                    "JOIN diagnostic d ON p.medicalrecord = d.medicalrecord " +
                    "JOIN analysis a ON d.medicalrecord = a.medicalrecord " +
                    "WHERE p.medicalrecord = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, this.getMedicalRecord());

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                Dialogs.showAlertMessage(
                        "No Analysis Found",
                        "This patient does not have analysis information.",
                        Dialogs.MessageType.ERROR_MESSAGE);
                return;
            }

            Map<String, Object> patientData = new HashMap<>();
            patientData.put("firstName", rs.getString("firstname"));
            patientData.put("lastName", rs.getString("lastname"));
            patientData.put("nationalId", rs.getString("nationalid"));
            patientData.put("medicalRecord", rs.getString("medicalrecord"));
            patientData.put("age", rs.getString("age"));
            patientData.put("gender", rs.getString("gender"));
            patientData.put("phone", rs.getString("phone"));
            patientData.put("email", rs.getString("email"));
            patientData.put("disease", rs.getString("disease"));
            patientData.put("condition", rs.getString("condition"));
            patientData.put("firstRelative", rs.getString("firstrelative"));
            patientData.put("secondRelative", rs.getString("secondrelative"));
            patientData.put("diseaseWeight", rs.getString("diseaseweight"));
            patientData.put("totalRiskScore", rs.getString("totalriskscore"));
            patientData.put("riskLevel", rs.getString("risklevel"));
            patientData.put("wikiSummary", rs.getString("wikipediasummary"));

            String riskLevel = rs.getString("risklevel");
            String recommendations;
            switch (riskLevel.toLowerCase()) {
                case "high":
                    recommendations = "Immediate medical consultation is advised. Consider genetic counseling.";
                    break;
                case "medium":
                    recommendations = "Monitor regularly and consult a specialist for further tests.";
                    break;
                case "low":
                    recommendations = "Maintain a healthy lifestyle and schedule routine check-ups.";
                    break;
                default:
                    recommendations = "No specific recommendation available.";
            }
            patientData.put("recommendations", recommendations);

            Stage stage = (Stage) printButton.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF Report");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

            String fileName = getFullName().replaceAll("\\s+", "_") + "_" + getNationalId() + ".pdf";
            fileChooser.setInitialFileName(fileName);

            File file;
            boolean fileSelected;
            boolean overwriteConfirmed = false;

            do {
                fileSelected = true;
                file = fileChooser.showSaveDialog(stage);

                if (file == null) return;

                if (file.exists() && !overwriteConfirmed) {
                    final File finalFile = file;
                    Dialogs.showConfirmationAlert(
                            "File Exists",
                            "The file \"" + finalFile.getName() + "\" already exists.\nDo you want to overwrite it?",
                            () -> {
                                try {
                                    PatientPDFGenerator.generatePDF(patientData, finalFile);
                                    Dialogs.showAlertMessage(
                                            "PDF Generated",
                                            "Patient report has been successfully saved to:\n" + finalFile.getAbsolutePath(),
                                            Dialogs.MessageType.INFO_MESSAGE);
                                    openPDFFile(finalFile); // Add this line to open the PDF
                                } catch (Exception e) {
                                    Dialogs.showAlertMessage(
                                            "PDF Error",
                                            "Could not generate PDF: " + e.getMessage(),
                                            Dialogs.MessageType.ERROR_MESSAGE);
                                }
                            });
                    overwriteConfirmed = true;
                    fileSelected = false;
                } else {
                    try {
                        PatientPDFGenerator.generatePDF(patientData, file);
                        Dialogs.showAlertMessage(
                                "PDF Generated",
                                "Patient report has been successfully saved to:\n" + file.getAbsolutePath(),
                                Dialogs.MessageType.INFO_MESSAGE);
                        openPDFFile(file);
                    } catch (Exception e) {
                        Dialogs.showAlertMessage(
                                "PDF Error",
                                "Could not generate PDF: " + e.getMessage(),
                                Dialogs.MessageType.ERROR_MESSAGE);
                        fileSelected = false;
                    }
                }
            } while (!fileSelected);

        } catch (Exception e) {
            Dialogs.showAlertMessage(
                    "PDF Error",
                    "Could not generate PDF: " + e.getMessage(),
                    Dialogs.MessageType.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    private void openPDFFile(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                Dialogs.showAlertMessage(
                        "Open PDF Failed",
                        "Unable to open PDF automatically. Please open it manually from: " + file.getAbsolutePath(),
                        Dialogs.MessageType.INFO_MESSAGE);
            }
        } catch (IOException e) {
            Dialogs.showAlertMessage(
                    "Open PDF Failed",
                    "Could not open PDF automatically: " + e.getMessage() + "\nPlease open it manually from: " + file.getAbsolutePath(),
                    Dialogs.MessageType.INFO_MESSAGE);
        }
    }


    // ===============================================================================================
    // ========================= Send a copy of this PDF to Patient's Email ==========================
    // ===============================================================================================
    private void handleShare() {
        // Create loading image
        loadingGif.setFitHeight(20);
        loadingGif.setFitWidth(20);

        // Replace button with loading gif
        HBox parentBox = (HBox) shareButton.getParent();
        Platform.runLater(() -> {
            parentBox.getChildren().remove(shareButton);
            parentBox.getChildren().add(loadingGif);
        });

        new Thread(() -> {
            try (Connection conn = DatabaseConnection.connect()) {
                // Fetch patient data
                String query = "SELECT p.firstname, p.lastname, p.nationalid, p.medicalrecord, p.age, p.gender, p.phone, p.email, " +
                        "d.disease, d.condition, d.firstrelative, d.secondrelative, " +
                        "a.diseaseweight, a.totalriskscore, a.risklevel, a.wikipediasummary " +
                        "FROM patient p " +
                        "JOIN diagnostic d ON p.medicalrecord = d.medicalrecord " +
                        "JOIN analysis a ON d.medicalrecord = a.medicalrecord " +
                        "WHERE p.medicalrecord = ?";

                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, this.getMedicalRecord());
                ResultSet rs = stmt.executeQuery();

                if (!rs.next()) {
                    Platform.runLater(() -> Dialogs.showAlertMessage("No Analysis", "No analysis found.", Dialogs.MessageType.ERROR_MESSAGE));
                    return;
                }

                Map<String, Object> patientData = new HashMap<>();
                patientData.put("firstName", rs.getString("firstname"));
                patientData.put("lastName", rs.getString("lastname"));
                patientData.put("nationalId", rs.getString("nationalid"));
                patientData.put("medicalRecord", rs.getString("medicalrecord"));
                patientData.put("age", rs.getString("age"));
                patientData.put("gender", rs.getString("gender"));
                patientData.put("phone", rs.getString("phone"));
                patientData.put("email", rs.getString("email"));
                patientData.put("disease", rs.getString("disease"));
                patientData.put("condition", rs.getString("condition"));
                patientData.put("firstRelative", rs.getString("firstrelative"));
                patientData.put("secondRelative", rs.getString("secondrelative"));
                patientData.put("diseaseWeight", rs.getString("diseaseweight"));
                patientData.put("totalRiskScore", rs.getString("totalriskscore"));
                patientData.put("riskLevel", rs.getString("risklevel"));
                patientData.put("wikiSummary", rs.getString("wikipediasummary"));

                String riskLevel = rs.getString("risklevel");
                String recommendations;
                switch (riskLevel.toLowerCase()) {
                    case "high":
                        recommendations = "Immediate medical consultation is advised.";
                        break;
                    case "medium":
                        recommendations = "Monitor regularly and consult a specialist.";
                        break;
                    case "low":
                        recommendations = "Maintain a healthy lifestyle.";
                        break;
                    default:
                        recommendations = "No specific recommendation.";
                }
                patientData.put("recommendations", recommendations);

                // Generate PDF in temp location
                String fileName = getFullName().replaceAll("\\s+", "_") + "_" + getNationalId() + ".pdf";
                File pdfFile = new File("reports/" + fileName);
                pdfFile.getParentFile().mkdirs(); // Ensure folder exists
                PatientPDFGenerator.generatePDF(patientData, pdfFile);

                // Send email
                Email.sendEmailWithAttachment(rs.getString("email"), getFullName(), pdfFile, new Email.EmailCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Dialogs.showAlertMessage("Success", "Email sent successfully!", Dialogs.MessageType.INFO_MESSAGE);
                        restoreShareButton(parentBox);
                    }

                    @Override
                    public void onFailure(String error) {
                        Dialogs.showAlertMessage("Failed", error, Dialogs.MessageType.ERROR_MESSAGE);
                        restoreShareButton(parentBox);
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> {
                    Dialogs.showAlertMessage("Error", "Failed to prepare email: " + ex.getMessage(), Dialogs.MessageType.ERROR_MESSAGE);
                    restoreShareButton((HBox) shareButton.getParent());
                });
            }
        }).start();
    }

    // Restore button method
    private void restoreShareButton(HBox parentBox) {
        Platform.runLater(() -> {
            parentBox.getChildren().remove(loadingGif);
            parentBox.getChildren().add(shareButton);
        });
    }


}