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
    public StringProperty dualChoiceButton(StringProperty message, StringProperty message2, StringProperty choice1, StringProperty choice2, String tooltip1, String tooltip2) {
        log.fine("dualChoiceButtons has been opened.");

        Stage dualChoiceStage = new Stage();
        dualChoiceStage.initStyle(StageStyle.UNDECORATED);
        dualChoiceStage.initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(dualChoiceStage);

        Label label = new Label();
        label.textProperty().bind(message);
        label.setPadding(new Insets(0, 0, 4, 0));

        Button button1 = new Button(), button2 = new Button();
        button1.textProperty().bind(choice1);
        button2.textProperty().bind(choice2);
        final StringProperty[] answer = new StringProperty[1];
        button1.setOnAction(e -> {
            answer[0] = choice1;
            dualChoiceStage.close();
        });
        button2.setOnAction(e -> {
            answer[0] = choice2;
            dualChoiceStage.close();
        });

        if (!tooltip1.isEmpty() && !tooltip2.isEmpty()) {
            button1.setTooltip(new Tooltip(tooltip1));
            button2.setTooltip(new Tooltip(tooltip2));
        }

        VBox buttonVBox1 = new VBox(), buttonVBox2 = new VBox(), mainLayout = new VBox();
        buttonVBox1.getChildren().add(button1);
        buttonVBox1.setPadding(new Insets(0, 3, 0, 0));
        buttonVBox2.getChildren().add(button2);
        buttonVBox2.setPadding(new Insets(0, 0, 0, 3));

        HBox layout = new HBox();
        layout.getChildren().addAll(buttonVBox1, buttonVBox2);
        layout.setAlignment(Pos.CENTER);

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

        Platform.runLater(() -> new MoveStage().moveStage(layout, null));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("/gui/GenericStyle.css");

        dualChoiceStage.setScene(scene);

        dualChoiceStage.showAndWait();

        log.fine("dualChoiceButtons has been closed.");
        return answer[0];
    }
}
