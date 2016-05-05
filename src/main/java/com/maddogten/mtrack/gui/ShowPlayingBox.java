package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.FXMLControllers.ShowPlaying;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.information.show.DisplayShow;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Logger;

/*
      ShowPlayingBox is displayed when the user plays a show. It allows them increment the episode number and close, leave the episode number as is and close,
      or to increment the episode number and automatically start the next episode and redisplay this window.
 */

public class ShowPlayingBox {
    private static final Logger log = Logger.getLogger(ShowPlayingBox.class.getName());
    private Stage stage;

    public void closeStage() {
        if (isStageOpen()) {
            log.fine("Setting was open, closing...");
            stage.close();
        }
    }

    private boolean isStageOpen() {
        return stage != null;
    }

    @SuppressWarnings("SameParameterValue")
    public void showConfirm(DisplayShow show, Stage oldStage) throws IOException {
        log.fine("showConfirm has been opened.");

        if (ClassHandler.userInfoController().doesEpisodeExistInShowFile(show.getShow()) || ClassHandler.userInfoController().isProperEpisodeInNextSeason(show.getShow())) {
            if (!ClassHandler.userInfoController().playAnyEpisode(show.getShow(), show.getSeason(), show.getEpisode())) {
                log.info("Unable to play: " + show.getShow() + " | Season: " + show.getSeason() + " | Episode: " + show.getEpisode());
                new MessageBox(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                return;
            }
        } else {
            log.info("Unable to play: " + show.getShow() + " | Season: " + show.getSeason() + " | Episode: " + show.getEpisode());
            new MessageBox(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
            return;
        }

        stage = new Stage();
        if (Variables.haveStageBlockParentStage) stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(oldStage);
        stage.initStyle(StageStyle.UNDECORATED);
        GenericMethods.setIcon(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ShowPlaying.fxml"));
        fxmlLoader.setController(new ShowPlaying(show));

        stage.setResizable(false);

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add("/gui/ShowPlaying.css");

        stage.setScene(scene);
        stage.show();
        stage.hide();
        stage.setX(stage.getOwner().getX() + (stage.getOwner().getWidth() / 2) - (stage.getWidth() / 2));
        stage.setY(stage.getOwner().getY() + (stage.getOwner().getHeight() / 2) - (stage.getHeight() / 2));

        stage.showAndWait();

        log.fine("showConfirm has been closed.");
        stage = null;
    }
}
