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
    private final int userID;
    private Stage stage;

    public ShowPlayingBox(int userID) {
        this.userID = userID;
    }

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
    public void showConfirm(final DisplayShow show, final Stage oldStage) throws IOException {
        log.fine("showConfirm has been opened.");
        if (ClassHandler.showInfoController().doesEpisodeExist(show.getShowID(), show.getSeason(), show.getEpisode()) || ClassHandler.userInfoController().isProperEpisodeInNextSeason(userID, show.getShowID())) {
            if (!ClassHandler.userInfoController().playAnyEpisode(Variables.getCurrentUser(), ClassHandler.showInfoController().getEpisodeID(show.getShowID(), show.getSeason(), show.getEpisode()))) {
                log.info("1: Unable to play: " + show.getShow() + " | Season: " + show.getSeason() + " | Episode: " + show.getEpisode());
                new MessageBox(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
                return;
            }
        } else {
            log.info("2: Unable to play: " + show.getShow() + " | Season: " + show.getSeason() + " | Episode: " + show.getEpisode());
            new MessageBox(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, Main.stage);
            return;
        }

        stage = new Stage();
        if (ClassHandler.userInfoController().getHaveStageBlockParentStage(Variables.getCurrentUser()))
            stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(oldStage);
        stage.initStyle(StageStyle.UNDECORATED);
        GenericMethods.setIcon(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ShowPlaying.fxml"));
        fxmlLoader.setController(new ShowPlaying(userID, show));

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
