package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.FXMLControllers.Settings;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.util.ImageLoader;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Logger;

public class SettingsWindow {
    private final Logger log = Logger.getLogger(SettingsWindow.class.getName());
    private Stage window;

    public void closeSettings() {
        if (isSettingsOpen()) {
            log.fine("Setting was open, closing...");
            window.close();
        }
    }

    public boolean isSettingsOpen() {
        return window != null;
    }

    public void display(int tab) throws Exception {
        log.info("SettingsWindow has been opened.");
        window = new Stage();
        window.initOwner(Main.stage);
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);
        if (Variables.haveStageBlockParentStage) window.initModality(Modality.APPLICATION_MODAL);

        Pane root = FXMLLoader.load(getClass().getResource("/gui/Settings.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(Color.WHITESMOKE);

        window.setResizable(false);
        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(Main.stage.getX() + (Main.stage.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(Main.stage.getY() + (Main.stage.getHeight() / 2) - (window.getHeight() / 2));
            if (tab != -2) {
                Settings.getSettings().getTabPane().getSelectionModel().clearAndSelect(tab);
            }
        });
        window.showAndWait();
        window = null;
        log.info("SettingsWindow has been closed.");
    }
}
