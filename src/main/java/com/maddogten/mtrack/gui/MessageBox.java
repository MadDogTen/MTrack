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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.logging.Logger;

/*
      MessageBox simply displays a message to the user.
 */

public class MessageBox {
    private final Logger log = Logger.getLogger(MessageBox.class.getName());

    public void display(StringProperty[] message, Window oldWindow) {
        log.finest("MessageBox has been opened.");
        Stage stage = new Stage();
        GenericMethods.setIcon(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        VBox layout = new VBox();
        if (message.length == 1) {
            Label messageLabel = new Label();
            messageLabel.textProperty().bind(message[0]);
            layout.getChildren().add(messageLabel);
        } else if (message.length > 1) {
            for (StringProperty aMessage : message) {
                Label messageLabel = new Label();
                messageLabel.textProperty().bind(aMessage);
                layout.getChildren().add(messageLabel);
            }
        }

        Button close = new Button();
        close.textProperty().bind(Strings.Close);
        close.setMinHeight(20);
        close.setMinWidth(30);
        close.setOnAction(e -> stage.close());

        layout.getChildren().add(close);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 0, 0, 0));

        Scene scene = new Scene(layout);

        stage.setScene(scene);
        Platform.runLater(() -> {
            if (oldWindow != null) {
                stage.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (stage.getWidth() / 2));
                stage.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (stage.getHeight() / 2));
            }
            new MoveStage().moveWindow(stage, oldWindow);
        });
        stage.show();
    }
}
