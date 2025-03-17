package com.example.genessystem;

import com.example.genessystem.dialog.Dialogs;
import com.example.genessystem.effects.Effects;
import com.example.genessystem.utils.UsernameReceiver;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Toolkit;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    public PieChart dashboard_pieChart;

    @FXML
    public Button dashboard_reportsButton;

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


    // *********************************************************************************************
    // *************************************  Visualization  ***************************************
    // *********************************************************************************************
    public void showPieChart() {
        dashboard_pieChart.setTitle(null);
        dashboard_pieChart.setLegendVisible(false);
        dashboard_pieChart.setClockwise(true);
        dashboard_pieChart.setStartAngle(90);

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Cured", 40),
                new PieChart.Data("Under Treatment", 30),
                new PieChart.Data("New Cases", 20),
                new PieChart.Data("Critical", 10)
        );

        dashboard_pieChart.setData(pieChartData);
    }


    // *********************************************************************************************
    // ***************************************  Initialize  ****************************************
    // *********************************************************************************************
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        buttonEffects();
        setLoginTime();
        showPieChart();
    }
}

