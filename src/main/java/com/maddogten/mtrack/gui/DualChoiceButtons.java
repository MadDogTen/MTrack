package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Logger;

/*
      DualChoiceButtons will display two separate choices and the related buttons, then returns the string of whichever button was pressed.
      It gives the option to display a separate message per a button if wanted, otherwise displays only the first message.
 */

public class DualChoiceButtons {
    private static final Logger log = Logger.getLogger(DualChoiceButtons.class.getName());

    @SuppressWarnings("SameParameterValue")
    public StringProperty display(StringProperty message, StringProperty message2, StringProperty choice1, StringProperty choice2, String tooltip1, String tooltip2, Stage oldStage) {
        log.finest("DualChoiceButtons has been ran.");
        Stage stage = new Stage();
        GenericMethods.setIcon(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        Label label = new Label();
        label.textProperty().bind(message);
        label.setPadding(new Insets(0, 0, 4, 0));
        Button button1 = new Button(), button2 = new Button();
        button1.textProperty().bind(choice1);
        button2.textProperty().bind(choice2);
        final StringProperty[] answer = new StringProperty[1];
        button1.setOnAction(e -> {
            answer[0] = choice1;
            stage.close();
        });
        button2.setOnAction(e -> {
            answer[0] = choice2;
            stage.close();
        });
        if (!tooltip1.isEmpty() && !tooltip2.isEmpty()) {
            button1.setTooltip(new Tooltip(tooltip1));
            button2.setTooltip(new Tooltip(tooltip2));
        }
        VBox button1Box = new VBox();
        button1Box.getChildren().add(button1);
        button1Box.setPadding(new Insets(0, 3, 0, 0));
        VBox button2Box = new VBox();
        button2Box.getChildren().add(button2);
        button2Box.setPadding(new Insets(0, 0, 0, 3));
        HBox layout = new HBox();
        layout.getChildren().addAll(button1Box, button2Box);
        layout.setAlignment(Pos.CENTER);
        VBox mainLayout = new VBox();
        if (message2.getValue().isEmpty()) mainLayout.getChildren().addAll(label, layout);
        else {
            Label label1 = new Label();
            label1.textProperty().bind(message2);
            VBox vBox = new VBox();
            vBox.getChildren().addAll(label, label1);
            vBox.setAlignment(Pos.CENTER);
            mainLayout.getChildren().addAll(vBox, layout);
        }
        mainLayout.setPadding(new Insets(6, 6, 6, 6));
        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
        Platform.runLater(() -> {
            if (oldStage != null) {
                stage.setX(oldStage.getX() + (oldStage.getWidth() / 2) - (stage.getWidth() / 2));
                stage.setY(oldStage.getY() + (oldStage.getHeight() / 2) - (stage.getHeight() / 2));
            }
            new MoveStage().moveStage(stage, oldStage);
        });
        stage.showAndWait();
        return answer[0];
    }
}
