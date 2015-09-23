package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveWindow;
import com.maddogten.mtrack.util.ImageLoader;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.logging.Logger;

public class DualChoiceButtons {
    private static final Logger log = Logger.getLogger(DualChoiceButtons.class.getName());

    @SuppressWarnings("SameParameterValue")
    public String display(String message, String message2, String choice1, String choice2, String tooltip1, String tooltip2, Window oldWindow) {
        log.finest("DualChoiceButtons has been ran.");
        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);

        window.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label();
        label.setText(message);
        label.setPadding(new Insets(0, 0, 4, 0));

        Button button1 = new Button(choice1), button2 = new Button(choice2);
        final String[] answer = new String[1];
        button1.setOnAction(e -> {
            answer[0] = choice1;
            window.close();
        });

        button2.setOnAction(e -> {
            answer[0] = choice2;
            window.close();
        });

        if (!tooltip1.isEmpty() && !tooltip2.isEmpty()) {
            button1.setTooltip(new Tooltip(tooltip1));
            button2.setTooltip(new Tooltip(tooltip2));
        }

        VBox button1Box = new VBox();
        button1Box.getChildren().add(button1);
        button1Box.setPadding(new Insets(0, 3, 0, 0));

        VBox button2Box = new VBox();
        button2Box.getChildren().add(button2);
        button2Box.setPadding(new Insets(0, 0, 0, 3));

        HBox layout = new HBox();
        layout.getChildren().addAll(button1Box, button2Box);
        layout.setAlignment(Pos.CENTER);

        log.info(message2);
        VBox mainLayout = new VBox();
        if (message2.isEmpty()) mainLayout.getChildren().addAll(label, layout);
        else {
            Label label1 = new Label();
            label1.setText(message2);
            VBox vBox = new VBox();
            vBox.getChildren().addAll(label, label1);
            vBox.setAlignment(Pos.CENTER);
            mainLayout.getChildren().addAll(vBox, layout);
        }

        mainLayout.setPadding(new Insets(6, 6, 6, 6));

        Scene scene = new Scene(mainLayout);

        window.setScene(scene);
        Platform.runLater(() -> {
            if (oldWindow != null) {
                window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
                window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            }
            new MoveWindow().moveWindow(window, oldWindow);
        });
        window.showAndWait();

        return answer[0];
    }
}
