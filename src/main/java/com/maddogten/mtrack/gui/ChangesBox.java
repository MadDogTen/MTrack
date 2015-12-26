package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.io.MoveStage;
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

import java.util.Arrays;
import java.util.logging.Logger;

/*
      ChangeBox displays any changes the program reported during show scans. Does NOT display the changelog.
 */

public class ChangesBox {
    private static final Logger log = Logger.getLogger(ChangesBox.class.getName());

    // This is true when the openChanges stage is open. If it open, then you are unable to open another instance of it.
    private boolean currentlyOpen = false;
    private Stage stage;

    public Stage getStage() {
        return stage;
    }

    // Displays a stage showing everything contained in the changes String[]. It will automatically updated when changes are found.
    public Object[] openChanges(Window oldWindow) {
        log.finest("ChangesBox has been opened.");
        if (currentlyOpen) {
            stage.toFront();
            stage.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (stage.getHeight() / 2));
            return new Object[]{false};
        } else {
            currentlyOpen = true;
        }
        stage = new Stage();
        ImageLoader.setIcon(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setWidth(Variables.SIZE_WIDTH - 30);

        ListView<String> listView = new ListView<>();
        listView.setEditable(false);

        ObservableList<String> observableList = FXCollections.observableArrayList();
        final String[][] changes = {ChangeReporter.getChanges()};
        observableList.addAll(changes[0]);

        listView.setItems(observableList);
        listView.setMaxHeight(Variables.SIZE_HEIGHT / 1.5);

        Button clear = new Button();
        clear.textProperty().bind(Strings.Clear);
        Button close = new Button();
        close.textProperty().bind(Strings.Close);

        Object[] answer = new Object[2];
        VBox layout = new VBox();
        clear.setOnAction(e -> {
            ChangeReporter.resetChanges();
            listView.getItems().clear();
        });

        close.setOnAction(e -> {
            answer[0] = false;
            stage.close();
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(clear, close);
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(12, 12, 12, 12));

        layout.getChildren().addAll(hBox, listView);

        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);
        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (currentlyOpen) {
                    Thread.sleep(1000);
                    if (stage.isShowing() && !Arrays.equals(changes[0], ChangeReporter.getChanges())) {
                        changes[0] = ChangeReporter.getChanges();
                        Platform.runLater(() -> {
                            observableList.clear();
                            observableList.addAll(changes[0]);
                        });
                    }
                }
                //noinspection ReturnOfNull
                return null;
            }
        };
        new Thread(task).start();

        stage.setScene(scene);
        Platform.runLater(() -> {
            stage.setX(oldWindow.getX() + (oldWindow.getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(oldWindow.getY() + (oldWindow.getHeight() / 2) - (stage.getHeight() / 2));
            new MoveStage().moveWindow(stage, null);
        });
        stage.showAndWait();

        currentlyOpen = false;
        stage = null;
        return answer;
    }
}