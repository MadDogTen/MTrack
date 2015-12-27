package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.Main;
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
import javafx.stage.Window;

import java.util.logging.Logger;

/*
      ShowConfirmBox is displayed when the user plays a show. It allows them increment the episode number and close, leave the episode number as is and close,
      or to increment the episode number and automatically start the next episode and redisplay this window.
 */

public class ShowConfirmBox {
    private static final Logger log = Logger.getLogger(ShowConfirmBox.class.getName());

    @SuppressWarnings("SameParameterValue")
    public int display(StringProperty message, Window oldWindow, boolean disableNextEpisodeButton) {
        log.finest("ShowConfirmBox has been opened.");
        Stage stage = new Stage();
        stage.initOwner(Main.stage); // TODO - Decide if I want to do it this way? Works for now.
        GenericMethods.setIcon(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label();
        label.textProperty().bind(message);

        Button yesButton = new Button(), noButton = new Button();
        yesButton.textProperty().bind(Strings.Yes);
        noButton.textProperty().bind(Strings.No);
        yesButton.setMinHeight(20);
        yesButton.setMinWidth(30);
        noButton.setMinHeight(20);
        noButton.setMinWidth(30);

        final int[] answer = new int[1];
        yesButton.setOnAction(e -> {
            answer[0] = 1;
            stage.close();
        });

        noButton.setOnAction(e -> {
            answer[0] = 0;
            stage.close();
        });

        HBox layout2 = new HBox();
        layout2.getChildren().addAll(yesButton, noButton);
        layout2.setAlignment(Pos.CENTER);
        layout2.setPadding(new Insets(4, 6, 6, 6));

        Button nextEpisode = new Button();
        nextEpisode.textProperty().bind(Strings.NextEpisode);
        nextEpisode.setMinHeight(20);
        nextEpisode.setMinWidth(30);
        nextEpisode.setOnAction(e -> {
            answer[0] = 2;
            stage.close();
        });
        if (disableNextEpisodeButton) {
            nextEpisode.setDisable(true);
        }

        VBox layout = new VBox();
        layout.getChildren().addAll(label, layout2, nextEpisode);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 0, 0, 0));

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        Platform.runLater(() -> {
            stage.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (stage.getHeight() / 2));
            new MoveStage().moveWindow(stage, oldWindow);
        });
        stage.showAndWait();

        return answer[0];
    }
}
