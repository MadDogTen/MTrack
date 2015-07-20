package main.java.com.maddogten.mtrack.gui;

import javafx.application.Platform;
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
import main.java.com.maddogten.mtrack.io.MoveWindow;
import main.java.com.maddogten.mtrack.util.ImageLoader;
import main.java.com.maddogten.mtrack.util.Strings;

import java.util.logging.Logger;

public class ShowConfirmBox {
    private static final Logger log = Logger.getLogger(ShowConfirmBox.class.getName());

    @SuppressWarnings("SameParameterValue")
    public int display(String message, Window oldWindow) {
        log.finest("ShowConfirmBox has been opened.");
        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label();
        label.setText(message);

        Button yesButton = new Button(Strings.Yes), noButton = new Button(Strings.No);
        yesButton.setMinHeight(20);
        yesButton.setMinWidth(30);
        noButton.setMinHeight(20);
        noButton.setMinWidth(30);

        final int[] answer = new int[1];
        yesButton.setOnAction(e -> {
            answer[0] = 1;
            window.close();
        });

        noButton.setOnAction(e -> {
            answer[0] = 0;
            window.close();
        });

        HBox layout2 = new HBox();
        layout2.getChildren().addAll(yesButton, noButton);
        layout2.setAlignment(Pos.CENTER);

        layout2.setPadding(new Insets(4, 6, 6, 6));

        Button nextEpisode = new Button(Strings.NextEpisode);
        nextEpisode.setMinHeight(20);
        nextEpisode.setMinWidth(30);

        nextEpisode.setOnAction(e -> {
            answer[0] = 2;
            window.close();
        });

        VBox layout = new VBox();
        layout.getChildren().addAll(label, layout2, nextEpisode);
        layout.setAlignment(Pos.CENTER);

        layout.setPadding(new Insets(6, 0, 0, 0));

        Scene scene = new Scene(layout);

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window, oldWindow);
        });
        window.showAndWait();

        return answer[0];
    }
}
