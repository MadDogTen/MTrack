package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveWindow;
import com.maddogten.mtrack.util.ImageLoader;
import com.maddogten.mtrack.util.Strings;
import javafx.application.Platform;
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

public class MessageBox {
    private final Logger log = Logger.getLogger(MessageBox.class.getName());

    public void display(String[] message, Window oldWindow) {
        log.finest("MessageBox has been opened.");
        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox();

        if (message.length == 1) {
            layout.getChildren().add(new Label(message[0]));
        } else if (message.length > 1) {

            for (String aMessage : message) {
                layout.getChildren().add(new Label(aMessage));
            }
        }

        Button close = new Button(Strings.Close);
        close.setMinHeight(20);
        close.setMinWidth(30);

        close.setOnAction(e -> window.close());

        layout.getChildren().add(close);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 0, 0, 0));

        Scene scene = new Scene(layout);

        window.setScene(scene);
        Platform.runLater(() -> {
            if (oldWindow != null) {
                window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
                window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            }
            new MoveWindow().moveWindow(window, oldWindow);
        });
        window.showAndWait();
    }
}
