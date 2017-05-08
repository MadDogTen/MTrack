package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.FXMLControllers.ShowEpisodeSelect;
import com.maddogten.mtrack.information.show.DisplayShow;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Logger;

public class ShowEpisodeSelectBox {
    private static final Logger log = Logger.getLogger(ShowEpisodeSelectBox.class.getName());

    private Stage stage;

    public boolean isStageOpen() {
        return stage != null;
    }

    public void closeStage() {
        stage.close();
        stage = null;
    }

    public int[] seasonEpisodeSelect(final DisplayShow show, final Stage oldStage) throws IOException {
        log.fine("seasonEpisodeSelect has been opened.");
        stage = new Stage();
        if (ClassHandler.userInfoController().getHaveStageBlockParentStage(Variables.getCurrentUser()))
            stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(oldStage);
        stage.initStyle(StageStyle.UNDECORATED);
        GenericMethods.setIcon(stage);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/ShowEpisodeSelect.fxml"));
        fxmlLoader.setController(new ShowEpisodeSelect(this, show));

        stage.setResizable(false);

        Scene scene = new Scene(fxmlLoader.load());

        stage.setScene(scene);
        Platform.runLater(() -> new MoveStage(((ShowEpisodeSelect) fxmlLoader.getController()).getMainPane(), oldStage, false));
        stage.show();
        stage.hide();
        stage.setX(stage.getOwner().getX() + (stage.getOwner().getWidth() / 2) - (stage.getWidth() / 2));
        stage.setY(stage.getOwner().getY() + (stage.getOwner().getHeight() / 2) - (stage.getHeight() / 2));

        stage.showAndWait();

        log.fine("seasonEpisodeSelect has been closed.");
        stage = null;
        return ((ShowEpisodeSelect) fxmlLoader.getController()).getSeasonEpisode();
    }
}
