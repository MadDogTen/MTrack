package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.io.MoveWindow;
import com.maddogten.mtrack.util.ImageLoader;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.logging.Logger;

public class ChangesBox {
    private static final Logger log = Logger.getLogger(ChangesBox.class.getName());

    // This is true when the openChanges stage is open. If it open, then you are unable to open another instance of it.
    private boolean currentlyOpen = false;
    private Stage window;

    public Stage getStage() {
        return window;
    }

    // Displays a window showing everything contained in the changes String[].
    public Object[] openChanges(Window oldWindow, String[] changes) {
        log.finest("ChangesBox has been opened.");
        if (currentlyOpen) {
            window.toFront();
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            return new Object[]{false};
        } else {
            currentlyOpen = true;
        }
        window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);

        ListView<String> listView = new ListView<>();
        listView.setEditable(false);

        ObservableList<String> observableList = FXCollections.observableArrayList();
        observableList.addAll(changes);

        listView.setItems(observableList);
        if (changes.length >= 17) {
            listView.setMaxHeight(Variables.SIZE_HEIGHT);
        } else {
            listView.setMaxHeight(25 * changes.length);
        }

        Button clear = new Button(Strings.Clear);
        /*Button refresh = new Button(Strings.Refresh);*/
        Button close = new Button(Strings.Close);

        Object[] answer = new Object[2];
        clear.setOnAction(e -> {
            ChangeReporter.resetChanges();
            listView.getItems().clear();
            answer[0] = true;
            answer[1] = window;
            window.close();
        });

        /*refresh.setOnAction(e -> {
            answerBoolean[0] = true;
            thisWindow[0] = window;
            window.close();
        });*/

        close.setOnAction(e -> {
            answer[0] = false;
            window.close();
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(clear, /*refresh,*/ close);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(12, 12, 12, 12));

        VBox layout = new VBox();
        if (listView.getItems().isEmpty()) {
            layout.getChildren().addAll(hBox);
        } else {
            layout.getChildren().addAll(hBox, listView);
        }
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception { //TODO Finish automatic refreshing
                while (currentlyOpen) {
                    Thread.sleep(1000);
                    if (ChangeReporter.getNumberOfChanges() != changes.length) {
                        answer[0] = true;
                        answer[1] = window;
                        window.close();
                    }
                    log.info("Ran");
                }
                //noinspection ReturnOfNull
                return null;
            }
        };
        new Thread(task);

        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (window.getHeight() / 2));
            new MoveWindow().moveWindow(window, null);
        });
        window.showAndWait();

        currentlyOpen = false;
        window = null;
        return answer;
    }
}
