package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

/*
      ChangeBox displays any changes the program reported during show scans. Does NOT display the changelog.
 */

public class ChangesBox {
    private static final Logger log = Logger.getLogger(ChangesBox.class.getName());

    // This is true when the openChanges stage is open. If it open, then you are unable to open another instance of it.
    private boolean currentlyOpen = false;
    private Stage changesStage;

    public void closeStage() {
        if (changesStage != null) changesStage.close();
    }

    // Displays a stage showing everything contained in the changes String[]. It will automatically updated when changes are found.
    public void openChanges(Stage oldStage) {
        log.finest("openChanges has been opened.");
        if (currentlyOpen) {
            changesStage.toFront();
            changesStage.setX(changesStage.getOwner().getX() + (changesStage.getOwner().getWidth() / 2) - (changesStage.getWidth() / 2));
            changesStage.setY(changesStage.getOwner().getY() + (changesStage.getOwner().getHeight() / 2) - (changesStage.getHeight() / 2));
            return;
        } else currentlyOpen = true;

        changesStage = new Stage();
        changesStage.initOwner(oldStage);
        changesStage.initStyle(StageStyle.UNDECORATED);
        changesStage.setWidth(Variables.SIZE_WIDTH - 30);
        GenericMethods.setIcon(changesStage);

        ListView<String> listView = new ListView<>();
        listView.setMaxHeight(Variables.SIZE_HEIGHT / 1.5);
        listView.setEditable(false);

        ObservableList<String> observableList = FXCollections.observableArrayList();
        final ArrayList<String[]> changes = new ArrayList<>(ChangeReporter.getChanges().length);
        changes.add(0, ChangeReporter.getChanges());
        observableList.addAll(changes.get(0));
        listView.setItems(observableList);

        Button clear = new Button(), close = new Button();
        clear.textProperty().bind(Strings.Clear);
        close.textProperty().bind(Strings.Close);
        clear.setOnAction(e -> {
            ChangeReporter.resetChanges();
            listView.getItems().clear();
        });
        close.setOnAction(e -> {
            GenericMethods.fadeStageOut(changesStage, 2, log, this.getClass());
            changesStage.close();
        });

        HBox buttonHBox = new HBox();
        buttonHBox.getChildren().addAll(clear, close);
        buttonHBox.setAlignment(Pos.CENTER);
        buttonHBox.setPadding(new Insets(12, 12, 12, 12));
        buttonHBox.setSpacing(3);

        VBox layout = new VBox();
        layout.getChildren().addAll(buttonHBox, listView);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 6, 6, 6));

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (currentlyOpen) {
                    Thread.sleep(1000);
                    if (changesStage.isShowing() && !Arrays.equals(changes.get(0), ChangeReporter.getChanges())) {
                        changes.set(0, ChangeReporter.getChanges());
                        Platform.runLater(() -> {
                            observableList.clear();
                            observableList.addAll(changes.get(0));
                        });
                    }
                }
                return null;
            }
        };
        new Thread(task).start();

        Platform.runLater(() -> new MoveStage().moveStage(layout, null));

        Scene scene = new Scene(layout);
        scene.getStylesheets().add("/gui/GenericStyle.css");

        changesStage.setScene(scene);
        changesStage.show();
        changesStage.hide();
        changesStage.setX(changesStage.getOwner().getX() + (changesStage.getOwner().getWidth() / 2) - (changesStage.getWidth() / 2));
        changesStage.setY(changesStage.getOwner().getY() + (changesStage.getOwner().getHeight() / 2) - (changesStage.getHeight() / 2));
        GenericMethods.fadeStageIn(changesStage, 2, log, this.getClass());
        changesStage.showAndWait();

        currentlyOpen = false;
        log.finest("openChanges has been closed.");
        changesStage = null;
    }
}
