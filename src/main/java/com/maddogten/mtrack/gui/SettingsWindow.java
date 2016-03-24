package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.FXMLControllers.Settings;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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

    public void settings(int tab) throws Exception {
        log.fine("settings has been opened.");

        stage = new Stage();
        if (Variables.haveStageBlockParentStage) stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(Main.stage);
        stage.initStyle(StageStyle.UNDECORATED);
        GenericMethods.setIcon(stage);

        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/gui/Settings.fxml")));
        scene.setFill(Color.WHITESMOKE);
        stage.setResizable(false);
        stage.setScene(scene);

        if (tab != -2)
            Platform.runLater(() -> Settings.getSettings().getTabPane().getSelectionModel().clearAndSelect(tab));

        stage.show();
        stage.hide();
        stage.setX(stage.getOwner().getX() + (stage.getOwner().getWidth() / 2) - (stage.getWidth() / 2));
        stage.setY(stage.getOwner().getY() + (stage.getOwner().getHeight() / 2) - (stage.getHeight() / 2));
        if (Variables.specialEffects) GenericMethods.fadeStageIn(stage, 2, log, this.getClass());
        stage.showAndWait();

        log.fine("settings has been closed.");
        stage = null;
    }
}
