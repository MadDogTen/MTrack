package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.FXMLControllers.VideoPlayerSelector;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Variables;
import com.maddogten.mtrack.util.VideoPlayer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Logger;

public class VideoPlayerSelectorBox {
    private static final Logger log = Logger.getLogger(com.maddogten.mtrack.gui.VideoPlayerSelectorBox.class.getName());

    @SuppressWarnings("SameParameterValue")
    public VideoPlayer videoPlayerSelector(final Stage oldStage) throws IOException {
        log.fine("videoPlayerSelector has been opened.");

        Stage stage = new Stage();
        if (Variables.haveStageBlockParentStage) stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(oldStage);
        stage.initStyle(StageStyle.UNDECORATED);
        GenericMethods.setIcon(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/VideoPlayerSelector.fxml"));

        stage.setResizable(false);

        Scene scene = new Scene(fxmlLoader.load());
        //scene.getStylesheets().add("/gui/ShowPlaying.css");

        stage.setScene(scene);
        stage.show();
        stage.hide();

        if (stage.getOwner() != null) {
            stage.setX(stage.getOwner().getX() + (stage.getOwner().getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(stage.getOwner().getY() + (stage.getOwner().getHeight() / 2) - (stage.getHeight() / 2));
        }

        stage.showAndWait();

        log.fine("videoPlayerSelector has been closed.");
        return ((VideoPlayerSelector) fxmlLoader.getController()).getResult();
    }
}
