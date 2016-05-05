package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Logger;

/*
      ConfirmBox is a simple stage that displays a message and a Yes or No buttons and returns the answer.
 */

public class ConfirmBox {
    private static final Logger log = Logger.getLogger(ConfirmBox.class.getName());

    public boolean confirm(StringProperty message, Stage oldStage) {
        log.finest("confirm has been opened.");

        Stage confirmStage = new Stage();
        confirmStage.initOwner(oldStage);
        confirmStage.initStyle(StageStyle.UNDECORATED);
        confirmStage.initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(confirmStage);

        Label label = new Label();
        label.textProperty().bind(message);

        Button yesButton = new Button(), noButton = new Button();
        yesButton.textProperty().bind(Strings.Yes);
        noButton.textProperty().bind(Strings.No);
        yesButton.setMinHeight(20);
        yesButton.setMinWidth(30);
        noButton.setMinHeight(20);
        noButton.setMinWidth(30);
        final boolean[] answer = new boolean[1];
        yesButton.setOnAction(e -> {
            answer[0] = true;
            confirmStage.close();
        });
        noButton.setOnAction(e -> {
            answer[0] = false;
            confirmStage.close();
        });

        HBox buttonHBox = new HBox();
        buttonHBox.getChildren().addAll(yesButton, noButton);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.setPadding(new Insets(4, 6, 6, 6));
        buttonHBox.setSpacing(3);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, buttonHBox);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));

        Platform.runLater(() -> new MoveStage(layout, oldStage, false));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/GenericStyle.css");

        confirmStage.setScene(scene);
        confirmStage.show();
        confirmStage.hide();
        if (confirmStage.getOwner() != null) {
            confirmStage.setX(confirmStage.getOwner().getX() + (confirmStage.getOwner().getWidth() / 2) - (confirmStage.getWidth() / 2));
            confirmStage.setY(confirmStage.getOwner().getY() + (confirmStage.getOwner().getHeight() / 2) - (confirmStage.getHeight() / 2));
        }
        confirmStage.showAndWait();

        log.finest("confirm has been closed.");
        return answer[0];
    }
}
