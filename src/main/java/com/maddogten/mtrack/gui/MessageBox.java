package main.java.com.maddogten.mtrack.gui;

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
import main.java.com.maddogten.mtrack.io.MoveWindow;
import main.java.com.maddogten.mtrack.util.ImageLoader;
import main.java.com.maddogten.mtrack.util.Strings;

import java.util.logging.Logger;

public class MessageBox {
    private static final Logger log = Logger.getLogger(MessageBox.class.getName());

    public void display(String message, Window oldWindow) {
        log.finest("MessageBox has been opened.");
        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label();
        label.setText(message);

        Button close = new Button(Strings.Close);
        close.setMinHeight(20);
        close.setMinWidth(30);

        close.setOnAction(e -> window.close());

        VBox layout = new VBox();
        layout.getChildren().addAll(label, close);
        layout.setAlignment(Pos.CENTER);

        layout.setPadding(new Insets(6, 0, 0, 0));

        Scene scene = new Scene(layout);

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window, oldWindow);
        });
        window.showAndWait();
    }
}