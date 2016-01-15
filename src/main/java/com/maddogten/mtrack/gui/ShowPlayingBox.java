package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.FXMLControllers.ShowPlaying;
import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.information.UserInfoController;
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
      ShowConfirmBox is displayed when the user plays a show. It allows them increment the episode number and close, leave the episode number as is and close,
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
    public void showConfirm(String aShow, Controller controller, ShowInfoController showInfoController, UserInfoController userInfoController, Stage oldStage) throws IOException {
        log.fine("showConfirm has been opened.");

        if (!userInfoController.playAnyEpisode(aShow, userInfoController.getCurrentSeason(aShow), userInfoController.getCurrentEpisode(aShow))) {
            new MessageBox().message(new StringProperty[]{Strings.WasUnableToPlayTheEpisode}, oldStage);
            return;
        }
        stage = new Stage();
        if (Variables.haveStageBlockParentStage) stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(oldStage);
        stage.initStyle(StageStyle.UNDECORATED);
        GenericMethods.setIcon(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ShowPlaying.fxml"));
        fxmlLoader.setController(new ShowPlaying(aShow, controller, showInfoController, userInfoController));

        stage.setResizable(false);
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
        stage.hide();
        stage.setX(stage.getOwner().getX() + (stage.getOwner().getWidth() / 2) - (stage.getWidth() / 2));
        stage.setY(stage.getOwner().getY() + (stage.getOwner().getHeight() / 2) - (stage.getHeight() / 2));

        stage.showAndWait();

        log.fine("showConfirm has been closed.");
        stage = null;
    }
}
