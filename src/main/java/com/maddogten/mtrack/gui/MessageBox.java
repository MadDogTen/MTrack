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

import java.util.logging.Logger;

/*
      MessageBox simply displays a message to the user.
 */

public class MessageBox {
    private static final Logger log = Logger.getLogger(MessageBox.class.getName());

    public MessageBox(final StringProperty[] message, final Stage oldStage) {
        log.fine("message has been opened.");

        Stage messageStage = new Stage();
        if (oldStage != null) messageStage.initOwner(oldStage);
        messageStage.initStyle(StageStyle.UNDECORATED);
        messageStage.initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(messageStage);

        VBox layout = new VBox();
        if (message.length == 1) {
            Label messageLabel = new Label();
            messageLabel.textProperty().bind(message[0]);
            layout.getChildren().add(messageLabel);
        } else if (message.length > 1) {
            for (StringProperty aMessage : message) {
                if (aMessage != null) {
                    Label messageLabel = new Label();
                    messageLabel.textProperty().bind(aMessage);
                    layout.getChildren().add(messageLabel);
                }
            }
        }

        Button close = new Button();
        close.textProperty().bind(Strings.Close);
        close.setMinHeight(20);
        close.setMinWidth(30);
        close.setOnAction(e -> messageStage.close());

        layout.getChildren().add(close);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));

        Platform.runLater(() -> new MoveStage(layout, oldStage, false));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/GenericStyle.css");

        messageStage.setScene(scene);
        messageStage.show();
        messageStage.hide();
        if (messageStage.getOwner() != null) {
            messageStage.setX(messageStage.getOwner().getX() + (messageStage.getOwner().getWidth() / 2) - (messageStage.getWidth() / 2));
            messageStage.setY(messageStage.getOwner().getY() + (messageStage.getOwner().getHeight() / 2) - (messageStage.getHeight() / 2));
        }
        messageStage.showAndWait();

        log.fine("message has been closed.");
    }
}
