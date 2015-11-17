package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveWindow;
import com.maddogten.mtrack.util.ImageLoader;
import com.maddogten.mtrack.util.Strings;
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

import java.util.ArrayList;
import java.util.logging.Logger;

public class SelectBox {
    private static final Logger log = Logger.getLogger(ConfirmBox.class.getName());

    public String display(String message, String[] buttonsText, Window oldWindow) {
        log.finest("SelectBox has been opened.");
        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);

        Label label = new Label();
        label.setText(message);

        ArrayList<Button> buttons = new ArrayList<>();

        for (String aString : buttonsText) {
            buttons.add(new Button(aString));
        }

        Button close = new Button(Strings.ExitButtonText);

        final String[] answer = new String[1];
        HBox layout2 = new HBox();
        buttons.forEach(aButton -> {
            aButton.setOnAction(e -> {
                answer[0] = aButton.getText();
                window.close();
            });
            layout2.getChildren().add(aButton);
        });

        close.setOnAction(e -> {
            answer[0] = Strings.EmptyString;
            window.close();
        });

        layout2.getChildren().add(close);
        layout2.setAlignment(Pos.CENTER);
        layout2.setPadding(new Insets(4, 6, 6, 6));

        VBox layout = new VBox();
        layout.getChildren().addAll(label, layout2);
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
