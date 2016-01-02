package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Logger;

/*
      ShowConfirmBox is displayed when the user plays a show. It allows them increment the episode number and close, leave the episode number as is and close,
      or to increment the episode number and automatically start the next episode and redisplay this window.
 */

public class ShowConfirmBox {
    private static final Logger log = Logger.getLogger(ShowConfirmBox.class.getName());

    @SuppressWarnings("SameParameterValue")
    public int showConfirm(StringProperty message, boolean disableNextEpisodeButton, Stage oldStage) {
        log.fine("showConfirm has been opened.");

        Stage showConfirmStage = new Stage();
        showConfirmStage.initOwner(oldStage);
        showConfirmStage.initStyle(StageStyle.UNDECORATED);
        showConfirmStage.initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(showConfirmStage);

        Label label = new Label();
        label.textProperty().bind(message);

        Button yesButton = new Button(), noButton = new Button(), nextEpisode = new Button();
        yesButton.textProperty().bind(Strings.Yes);
        noButton.textProperty().bind(Strings.No);
        nextEpisode.textProperty().bind(Strings.NextEpisode);
        yesButton.setMinHeight(20);
        yesButton.setMinWidth(30);
        noButton.setMinHeight(20);
        noButton.setMinWidth(30);
        nextEpisode.setMinHeight(20);
        nextEpisode.setMinWidth(30);
        final int[] answer = new int[1];
        yesButton.setOnAction(e -> {
            answer[0] = 1;
            showConfirmStage.close();
        });
        noButton.setOnAction(e -> {
            answer[0] = 0;
            showConfirmStage.close();
        });
        nextEpisode.setOnAction(e -> {
            answer[0] = 2;
            showConfirmStage.close();
        });

        HBox layout2 = new HBox();
        layout2.getChildren().addAll(yesButton, noButton);
        layout2.setAlignment(Pos.CENTER);
        layout2.setPadding(new Insets(4, 6, 6, 6));
        layout2.setSpacing(3);

        if (disableNextEpisodeButton) nextEpisode.setDisable(true);

        VBox layout = new VBox();
        layout.getChildren().addAll(label, layout2, nextEpisode);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));

        Platform.runLater(() -> new MoveStage().moveStage(layout, oldStage));

        showConfirmStage.setScene(new Scene(layout));
        showConfirmStage.show();
        showConfirmStage.hide();
        showConfirmStage.setX(showConfirmStage.getOwner().getX() + (showConfirmStage.getOwner().getWidth() / 2) - (showConfirmStage.getWidth() / 2));
        showConfirmStage.setY(showConfirmStage.getOwner().getY() + (showConfirmStage.getOwner().getHeight() / 2) - (showConfirmStage.getHeight() / 2));
        showConfirmStage.showAndWait();

        log.fine("showConfirm has been closed.");
        return answer[0];
    }
}
