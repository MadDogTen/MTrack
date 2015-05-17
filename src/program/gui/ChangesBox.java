package program.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import program.graphics.ImageLoader;
import program.information.ChangeReporter;
import program.input.MoveWindow;

import java.util.logging.Logger;

public class ChangesBox {
    private static final Logger log = Logger.getLogger(ChangesBox.class.getName());

    private static Boolean currentlyOpen = false;

    public Object[] display(String title, Window oldWindow) {
        if (currentlyOpen) {
            Object[] object = new Object[1];
            object[0] = false;
            return object;
        } else {
            currentlyOpen = true;
        }
        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);

        window.setTitle(title);

        final VBox[] vBox = {new VBox()};
        for (String aMessage : ChangeReporter.changes) {
            Label label = new Label();
            label.setText(aMessage);
            label.setPadding(new Insets(0, 6, 2, 6));
            vBox[0].getChildren().add(label);
        }

        Button clear = new Button("Clear");
        Button refresh = new Button("Refresh");
        Button close = new Button("Close");

        final Boolean[] answerBoolean = {false};
        final Stage[] thisWindow = new Stage[1];
        clear.setOnAction(e -> {
            ChangeReporter.resetChanges();
            vBox[0].getChildren().clear();
            answerBoolean[0] = true;
            thisWindow[0] = window;
            window.close();
        });

        refresh.setOnAction(e -> {
            answerBoolean[0] = true;
            thisWindow[0] = window;
            window.close();
        });

        close.setOnAction(e -> {
            answerBoolean[0] = false;
            window.close();
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(clear, refresh, close);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(12, 12, 12, 12));

        VBox layout = new VBox();
        layout.getChildren().addAll(hBox, vBox[0]);
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window);
        });
        window.showAndWait();

        Object[] answer = new Object[2];
        answer[0] = answerBoolean[0];
        answer[1] = thisWindow[0];
        currentlyOpen = false;
        return answer;
    }
}
