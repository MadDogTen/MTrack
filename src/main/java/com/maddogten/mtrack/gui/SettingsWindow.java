package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.FXMLControllers.Settings;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.util.GenericMethods;
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

/*
      SettingsWindow handles opening the Settings class, and Settings can be opened to a specific tab.
      closeSettings() allows the parent class to close it.
      isSettingsOpen() allows the parent class to check its status.
 */

public class SettingsWindow {
    private final Logger log = Logger.getLogger(SettingsWindow.class.getName());
    private Stage stage;

    public void closeSettings() {
        if (isSettingsOpen()) {
            log.fine("Setting was open, closing...");
            stage.close();
        }
    }

    public boolean isSettingsOpen() {
        return stage != null;
    }

    public void display(int tab) throws Exception {
        log.info("SettingsWindow has been opened.");
        stage = new Stage();
        stage.initOwner(Main.stage);
        GenericMethods.setIcon(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        if (Variables.haveStageBlockParentStage) stage.initModality(Modality.APPLICATION_MODAL);
        Pane root = FXMLLoader.load(getClass().getResource("/gui/Settings.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(Color.WHITESMOKE);
        stage.setResizable(false);
        stage.setScene(scene);
        Platform.runLater(() -> {
            stage.setX(Main.stage.getX() + (Main.stage.getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(Main.stage.getY() + (Main.stage.getHeight() / 2) - (stage.getHeight() / 2));
            if (tab != -2) Settings.getSettings().getTabPane().getSelectionModel().clearAndSelect(tab);
        });
        stage.showAndWait();
        stage = null;
        log.info("SettingsWindow has been closed.");
    }
}
