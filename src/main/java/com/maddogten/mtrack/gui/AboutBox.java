package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveWindow;
import com.maddogten.mtrack.util.ImageLoader;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.logging.Logger;

public class AboutBox {
    private static final Logger log = Logger.getLogger(AboutBox.class.getName());

    public void display(Window oldWindow) throws Exception {
        log.finest("AboutBox has been opened.");
        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);

        Pane root = FXMLLoader.load(getClass().getResource("/gui/About.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(Color.WHITESMOKE);

        window.setResizable(false);
        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window, oldWindow);
        });
        window.showAndWait();
    }
}
