package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.utils.UsernameReceiver;
import com.example.genessystem.utils.database.DatabaseConnection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable, UsernameReceiver {

    // ********************************* Declaration Area ***********************************
    @FXML
    public Label dashboard_usernameLabel;

    @FXML
    public BorderPane dashboard_root;

    @FXML
    public Button dashboard_patientDataButton;

    @FXML
    public Button dashboard_diagnosticsButton;

    @FXML
    public Button dashboard_logoutButton;

    @FXML
    public Button dashboard_exit;

    @FXML
    public Label dashboard_loginTimeLabel;

    @FXML
    public Label dashboard_loginDateLabel;

    @FXML
    public Button dashboard_reportsButton;

    @FXML
    public Label dashboard_totalPatientsLabel;

    @FXML
    public Label dashboard_commonGeneticDiseaseLabel;

    @FXML
    public Label dashboard_maleLabel;

    @FXML
    public Label dashboard_femaleLabel;

    @FXML
    public Label dashboard_averageAgeDiagnosedLabel;

    @FXML
    public Label dashboard_geneticDiagnosesCoverageLabel;

    @FXML
    public BarChart dashboard_barChart;

    @FXML
    public ScatterChart dashboard_scatterChart;

    @FXML
    public LineChart dashboard_LineChart;

    @FXML
    public PieChart dashboard_pieChart;

    @FXML
    public Text dashboard_mainTitleText;


    // ****************************** END OF Declaration Area ********************************


    // *********************************************************************************************
    // ******************************  Setting Screen Requirements  ********************************
    // *********************************************************************************************

    @Override
    public void setUsername(String username) {
        if (dashboard_usernameLabel != null) {
            dashboard_usernameLabel.setText(username.substring(0, 1).toUpperCase() + username.substring(1));
        } else {
            System.out.println("usernameLabel is null!");
        }
    }

    public void setLoginTime() {
        dashboard_loginDateLabel.setText(String.valueOf(LocalDate.now()));
        dashboard_loginTimeLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @FXML
    public void logout(ActionEvent event) {
        String username = dashboard_usernameLabel.getText();
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
                () -> ((Stage) dashboard_root.getScene().getWindow()).close());
    }

    public void buttonEffects() {
        Effects.buttonEffect(dashboard_patientDataButton);
        Effects.buttonEffect(dashboard_diagnosticsButton);
        Effects.buttonEffect(dashboard_logoutButton);
        Effects.buttonEffect(dashboard_reportsButton);
        Effects.buttonEffect(dashboard_exit);
    }


    // *********************************************************************************************
    // **********************************  Switch among Screens  ***********************************
    // *********************************************************************************************

    @FXML
    public void switchToPatientData(ActionEvent event) {
        String username = dashboard_usernameLabel.getText();
        Main.switchScreen("patient-data-view.fxml", username);
    }

    @FXML
    public void switchToDiagnostics(ActionEvent actionEvent) {
        String username = dashboard_usernameLabel.getText();
        Main.switchScreen("diagnostics-view.fxml", username);
    }

    @FXML
    public void switchToReports(ActionEvent actionEvent) {
        String username = dashboard_usernameLabel.getText();
        Main.switchScreen("reports-view.fxml", username);
    }

    @FXML
    void aboutUsPopup(ActionEvent event) {
        AboutUs.showAboutUsStage();
    }

    // *********************************************************************************************
    // *************************************  Visualization  ***************************************
    // *********************************************************************************************

    // fill cards with data taken from DB
    private void loadDashboardStats() throws SQLException {
        try {
            Connection conn = DatabaseConnection.connect();

            // 1. Total Patients
            PreparedStatement totalStmt = conn.prepareStatement("SELECT COUNT(*) FROM patient");
            ResultSet rsTotal = totalStmt.executeQuery();
            if (rsTotal.next()) {
                dashboard_totalPatientsLabel.setText(String.valueOf(rsTotal.getInt(1)));
            }

            // 2. Common Genetic Disease
            PreparedStatement commonDiseaseStmt = conn.prepareStatement(
                    "SELECT disease FROM diagnostic GROUP BY disease ORDER BY COUNT(*) DESC LIMIT 1");
            ResultSet rsCommon = commonDiseaseStmt.executeQuery();
            if (rsCommon.next()) {
                dashboard_commonGeneticDiseaseLabel.setText(rsCommon.getString("disease"));
            }

            // 3. Gender Distribution
            PreparedStatement genderStmt = conn.prepareStatement(
                    "SELECT gender, COUNT(*) FROM patient GROUP BY gender");
            ResultSet rsGender = genderStmt.executeQuery();
            while (rsGender.next()) {
                String gender = rsGender.getString("gender");
                int count = rsGender.getInt(2);
                if ("Male".equalsIgnoreCase(gender)) {
                    dashboard_maleLabel.setText(String.valueOf(count));
                } else if ("Female".equalsIgnoreCase(gender)) {
                    dashboard_femaleLabel.setText(String.valueOf(count));
                }
            }

            // 4. Average Age (Diagnosed)
            PreparedStatement avgAgeStmt = conn.prepareStatement(
                    "SELECT AVG(age) FROM patient WHERE medicalrecord IN (SELECT DISTINCT medicalrecord FROM diagnostic)");
            ResultSet rsAvg = avgAgeStmt.executeQuery();
            if (rsAvg.next()) {
                double avgAge = rsAvg.getDouble(1);
                dashboard_averageAgeDiagnosedLabel.setText(String.format("%.1f", avgAge));
            }

            // 5. Genetic Diagnoses Coverage
            PreparedStatement diagnosedStmt = conn.prepareStatement("SELECT COUNT(DISTINCT medicalrecord) FROM diagnostic");
            ResultSet rsDiagnosed = diagnosedStmt.executeQuery();
            int diagnosedCount = 0;
            if (rsDiagnosed.next()) {
                diagnosedCount = rsDiagnosed.getInt(1);
            }


            int totalPatients = dashboard_totalPatientsLabel.getText().isEmpty() ? 0 : Integer.parseInt(dashboard_totalPatientsLabel.getText());


            if (totalPatients > 0) {
                double coverage = (double) diagnosedCount / totalPatients * 100;
                dashboard_geneticDiagnosesCoverageLabel.setText(String.format("%.1f%%", coverage));
            }


            rsTotal.close();
            rsCommon.close();
            rsGender.close();
            rsAvg.close();
            rsDiagnosed.close();
            totalStmt.close();
            commonDiseaseStmt.close();
            genderStmt.close();
            avgAgeStmt.close();
            diagnosedStmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadDiseaseBarChart() {
        try {
            Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT disease, COUNT(*) as count FROM diagnostic GROUP BY disease ORDER BY count DESC LIMIT 10"
            );
            ResultSet rs = stmt.executeQuery();

            dashboard_barChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Disease Cases");

            List<XYChart.Data<String, Number>> dataList = new ArrayList<>();

            while (rs.next()) {
                String disease = rs.getString("disease");
                int count = rs.getInt("count");
                XYChart.Data<String, Number> data = new XYChart.Data<>(disease, count);
                dataList.add(data);
            }

            series.getData().addAll(dataList);
            dashboard_barChart.getData().add(series);

            // Define your custom colors here
            String[] gradientColors = {
                    "#FFB6C1", "#FFD580", "#B4F8C8", "#A2D2FF", "#DDBDF1",
                    "#FBC2EB", "#F6D365", "#84FAB0", "#FCCF31", "#B0BEC5"
            };

            // Apply colors AFTER rendering
            Platform.runLater(() -> {
                for (int i = 0; i < dataList.size(); i++) {
                    XYChart.Data<String, Number> data = dataList.get(i);
                    Node node = data.getNode();
                    if (node != null) {
                        node.setStyle("-fx-bar-fill: " + gradientColors[i % gradientColors.length] + ";");
                    }
                }
            });

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadDiseasePieChart() {
        try {
            Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT disease, COUNT(*) as count FROM diagnostic GROUP BY disease ORDER BY count DESC LIMIT 6"
            );
            ResultSet rs = stmt.executeQuery();

            dashboard_pieChart.setTitle("Genetic Disease Distribution");
            dashboard_pieChart.setLabelsVisible(true);
            dashboard_pieChart.setLegendVisible(false);

            ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();

            while (rs.next()) {
                String disease = rs.getString("disease");
                int count = rs.getInt("count");
                pieData.add(new PieChart.Data(disease, count));
            }

            dashboard_pieChart.setData(pieData);

            // Add colors
            String[] colors = {
                    "#FFB6C1", "#FFD580", "#B4F8C8", "#A2D2FF", "#DDBDF1", "#FCCF31"
            };

            Platform.runLater(() -> {
                int i = 0;
                for (PieChart.Data data : dashboard_pieChart.getData()) {
                    String color = colors[i % colors.length];
                    Node node = data.getNode();
                    if (node != null) {
                        node.setStyle("-fx-pie-color: " + color + ";");
                    }
                    i++;
                }
            });

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadScatterChart() {
        try {
            Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT p.age, COUNT(d.id) as diagnosis_count " +
                            "FROM patient p JOIN diagnostic d ON p.medicalrecord = d.medicalrecord " +
                            "GROUP BY p.nationalid, p.age"
            );
            ResultSet rs = stmt.executeQuery();

            dashboard_scatterChart.getData().clear();

            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName("Age vs Diagnosis Count");

            List<XYChart.Data<Number, Number>> dataList = new ArrayList<>();

            while (rs.next()) {
                int age = rs.getInt("age");
                int count = rs.getInt("diagnosis_count");
                XYChart.Data<Number, Number> data = new XYChart.Data<>(age, count);
                dataList.add(data);
            }

            series.getData().addAll(dataList);
            dashboard_scatterChart.getData().add(series);
            dashboard_scatterChart.setTitle("Diagnosis Frequency Among Ages");
            dashboard_scatterChart.lookup(".chart-title").setStyle(
                    "-fx-font-size: 12px;"
            );

            // Optional: Color points individually
            String[] colors = {
                    "#FFB6C1", "#FFD580", "#B4F8C8", "#A2D2FF", "#DDBDF1", "#FCCF31",
                    "#E0BBE4", "#957DAD", "#FEC8D8", "#FFDFD3"
            };

            Platform.runLater(() -> {
                for (int i = 0; i < dataList.size(); i++) {
                    Node node = dataList.get(i).getNode();
                    if (node != null) {
                        node.setStyle("-fx-background-color: " + colors[i % colors.length] + ";");
                    }
                }
            });

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void loadLineChart() {
        try {
            Connection conn = DatabaseConnection.connect();
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT strftime('%Y', p.dob) as birth_year, COUNT(*) as count " +
                            "FROM diagnostic d JOIN patient p ON d.medicalrecord = p.medicalrecord " +
                            "GROUP BY birth_year ORDER BY birth_year"
            );
            ResultSet rs = stmt.executeQuery();

            dashboard_LineChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Diagnoses by Birth Year");

            while (rs.next()) {
                String year = rs.getString("birth_year");
                int count = rs.getInt("count");
                series.getData().add(new XYChart.Data<>(year, count));
            }

            dashboard_LineChart.getData().add(series);
            dashboard_LineChart.setTitle("Diagnoses by Birth");
            dashboard_LineChart.lookup(".chart-title").setStyle(
                    "-fx-font-size: 12px;"
            );

            // Styling for line and dots
            Platform.runLater(() -> {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    Node node = data.getNode();
                    if (node != null) {
                        node.setStyle("-fx-background-color: #2196F3, white; -fx-background-radius: 4px;");
                    }
                }
                Node line = dashboard_LineChart.lookup(".chart-series-line");
                if (line != null) {
                    line.setStyle("-fx-stroke: #2196F3; -fx-stroke-width: 1px;");
                }
            });

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    // *********************************************************************************************
    // ***************************************  Initialize  ****************************************
    // *********************************************************************************************
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonEffects();
        setLoginTime();

        try {
            loadDashboardStats();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        loadDiseaseBarChart();
        loadDiseasePieChart();
        loadScatterChart();
        loadLineChart();

        dashboard_mainTitleText.setFill(new ImagePattern(new Image(getClass().getResourceAsStream("/images/black-wallpaper.jpg"))));
    }
}

