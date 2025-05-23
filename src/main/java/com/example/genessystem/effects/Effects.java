package com.example.genessystem.effects;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Effects {
    private static double x;
    private static double y;


    public static void movableStage(Stage stage, Parent root) {
        x = 0;
        y = 0;

        root.setOnMousePressed(event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
            stage.setOpacity(0.7);
            root.setStyle("-fx-cursor: move ;");
        });
        root.setOnMouseReleased(event -> {
            stage.setOpacity(1);
            root.setStyle("-fx-cursor: default;");
        });
    }

    public static void fadeOut(Node node, Duration duration, double setFrom, Runnable onFinished) {
        FadeTransition fadeout = new FadeTransition(duration, node);
        fadeout.setFromValue(setFrom);
        fadeout.setToValue(0);
        fadeout.play();

        fadeout.setOnFinished(e -> {
            node.setVisible(false);
            if (onFinished != null) {
                onFinished.run();
            }
        });
    }

    public static void fadeIn(Node node, Duration duration, double setTo) {
        node.setOpacity(0);
        node.setVisible(true);

        FadeTransition fadein = new FadeTransition(duration, node);
        fadein.setFromValue(0);
        fadein.setToValue(setTo);
        fadein.play();
    }

    public static void buttonEffect(Button button) {
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(300), button);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.setToZ(1.2);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(300), button);
            scale.setToX(1);
            scale.setToY(1);
            scale.setToZ(1);
            scale.play();
        });
    }

    public static void buttonEffectSoftScale(Button button) {
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1.09);
            scale.setToY(1.09);
            scale.setToZ(1.09);
            scale.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(100), button);
            scale.setToX(1);
            scale.setToY(1);
            scale.setToZ(1);
            scale.play();
        });
    }

    public static void textEffect(Text text) {
        DropShadow outerShadow = new DropShadow();
        outerShadow.setOffsetX(8.0);
        outerShadow.setOffsetY(8.0);
        outerShadow.setRadius(10.0);
        outerShadow.setSpread(0.2);
        outerShadow.setColor(Color.rgb(0, 0, 0, 0.75));

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setOffsetX(2.0);
        innerShadow.setOffsetY(2.0);
        innerShadow.setRadius(5.0);
        innerShadow.setColor(Color.rgb(255, 255, 255, 0.3));

        outerShadow.setInput(innerShadow);

        text.setEffect(outerShadow);
    }

    public static void fieldEffect(TextField textField, Pane parentContainer) {
        Rectangle underline = new Rectangle();
        underline.setHeight(2);
        underline.setFill(Color.web("#00BFFF")); // A nice blue shade.
        underline.setScaleX(0); // Start collapsed (invisible).

        // Bind the underline's width to the TextField's width.
        underline.widthProperty().bind(textField.widthProperty());

        // Position the underline properly in the parent container.
        underline.layoutXProperty().bind(textField.layoutXProperty());
        underline.layoutYProperty().bind(textField.layoutYProperty().add(textField.heightProperty()));

        parentContainer.getChildren().add(underline);

        textField.focusedProperty().addListener((obs, oldFocused, isNowFocused) -> {
            Timeline timeline = new Timeline();
            if (isNowFocused) {
                textField.setStyle("-fx-border-width: 0;");
                // Expand the underline to full width when focused.
                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.millis(300),
                                new KeyValue(underline.scaleXProperty(), 1, Interpolator.EASE_BOTH)
                        )
                );
            } else {
                // Collapse the underline when focus is lost.
                timeline.getKeyFrames().add(
                        new KeyFrame(Duration.millis(300),
                                new KeyValue(underline.scaleXProperty(), 0, Interpolator.EASE_BOTH)
                        )
                );
                timeline.setOnFinished(e -> textField.setStyle(""));
            }
            timeline.play();
        });
    }


    private static final Font robotoFont = Font.loadFont(
            Effects.class.getResourceAsStream("/fonts/Roboto-Regular.ttf"), 14
    );

    public static void applyHoverPrompt(StackPane stack, Button button, TextFlow textFlow, String message) {
        // Set initial style for text flow
        textFlow.setVisible(false);
        textFlow.setDisable(true);

        final boolean[] isAnimating = {false};  // flag to prevent re-trigger

        stack.setOnMouseEntered(e -> {
            if (isAnimating[0]) return; // prevent re-triggering
            isAnimating[0] = true;

            stack.setAlignment(Pos.CENTER_LEFT);
            textFlow.getChildren().clear();
            textFlow.setPadding(new Insets(10, 10, 10, 35));
            textFlow.setVisible(true);
            textFlow.setDisable(false);

            RotateTransition rotate = new RotateTransition(Duration.millis(500), button);
            rotate.setByAngle(360);
            rotate.setInterpolator(Interpolator.LINEAR);
            rotate.play();

            Timeline timeline = new Timeline();
            for (int i = 0; i < message.length(); i++) {
                final int index = i;
                KeyFrame keyFrame = new KeyFrame(Duration.millis(40 * i), evt -> {
                    Text letter = new Text(String.valueOf(message.charAt(index)));
                    letter.setFont(robotoFont);
                    letter.setFill(Color.web("#2196f3"));
                    textFlow.getChildren().add(letter);
                });
                timeline.getKeyFrames().add(keyFrame);
            }

            timeline.setOnFinished(evt -> isAnimating[0] = false); // reset after animation
            timeline.play();
        });

        stack.setOnMouseExited(e -> {
            button.setRotate(0);
            stack.setAlignment(Pos.CENTER);
            textFlow.setVisible(false);
            textFlow.setDisable(true);
            textFlow.getChildren().clear();
            isAnimating[0] = false; // allow animation again
        });
    }


    public static void setOpacity(Node node) {
        node.setOpacity(1);
    }
}
