package com.example.genessystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class AboutUs {

    static Font H1 = Font.font("Arial", FontWeight.BOLD, 20);
    static Font H2 = Font.font("Arial", FontWeight.BOLD, 12);
    static Font H3 = Font.font("Arial, 12");


    public static void showAboutUsStage() {
        HBox root = new HBox(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));


        VBox logoBox = new VBox();
        logoBox.setMaxWidth(100);
        logoBox.setMaxWidth(100);
        logoBox.setAlignment(Pos.TOP_CENTER);

        ImageView logo = new ImageView(
                new Image(
                        Objects.requireNonNull(
                                AboutUs.class.getResourceAsStream("/images/mainLogo.png")
                        )
                )
        );
        logo.setFitHeight(75);
        logo.setFitWidth(75);


        Label title = new Label("Smart Genetic System");
        title.setFont(H1);

        Label buildNumberLabel = new Label("Build# 3.002, built on April 12, 2025");
        buildNumberLabel.setFont(H3);

        Label runtimeLabel = new Label("Runtime: JRE21");
        runtimeLabel.setFont(H3);

        Label poweredByLabel = new Label("Powered By:");
        poweredByLabel.setFont(H2);
        VBox.setMargin(poweredByLabel, new Insets(30, 0, 0, 0));
        Label sgsLabel = new Label("SGS Team.");
        sgsLabel.setFont(H3);

        Label mead = new Label("Mead S. Al Ruwaili");
        mead.setFont(H3);
        Label maryam = new Label("Maryam K. Al Shammari");
        maryam.setFont(H3);
        Label asayil = new Label("Asayil K. Al Serhani");
        asayil.setFont(H3);
        Label amlak = new Label("Amlak S. Al Fuhaigi");
        amlak.setFont(H3);


        Label copywritelabel = new Label("Copywrite © 2025 SGS Team");
        copywritelabel.setFont(H3);
        VBox.setMargin(copywritelabel, new Insets(30, 0, 0, 0));

        Label moreInfoLabel = new Label("For more Info: Contact us on the email...");
        moreInfoLabel.setFont(H3);

        TextField emailLink = new TextField("appsmartgeneticsystem.gmail.com");
        emailLink.setEditable(false);
        emailLink.setFocusTraversable(true);
        emailLink.setMouseTransparent(false);
        emailLink.setPadding(new Insets(0));
        emailLink.setFont(H3);
        emailLink.setStyle("""
                -fx-background-color: transparent;
                -fx-border-color: transparent;
                -fx-text-fill: blue;
                -fx-cursor: hand;
                """);

        VBox bodyBox = new VBox(
                0,
                title,
                buildNumberLabel,
                runtimeLabel,
                poweredByLabel,
                sgsLabel,
                mead,
                maryam,
                asayil,
                amlak,
                copywritelabel,
                moreInfoLabel,
                emailLink
        );
        bodyBox.setAlignment(Pos.TOP_LEFT);
        bodyBox.setMinWidth(290);
        bodyBox.setMaxWidth(290);


        logoBox.getChildren().add(logo);
        root.getChildren().addAll(logoBox, bodyBox);

        Scene scene = new Scene(root, 400, 300);
        root.requestFocus();

        Stage stage = new Stage();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setAlwaysOnTop(true);
        stage.setTitle("About Us");
        stage.getIcons().add(new Image(Objects.requireNonNull(
                AboutUs.class.getResourceAsStream(
                        "/images/mainLogo.png"
                )
        )));
        stage.showAndWait();
    }
}
