package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Logger;

public class LoadingBox {
    private static final Logger log = Logger.getLogger(LoadingBox.class.getName());
    private Stage loadingBoxStage;

    public void loadingBox(Thread thread) {
        log.fine("LoadingBox has been opened.");
        loadingBoxStage = new Stage();
        GenericMethods.setIcon(loadingBoxStage);
        loadingBoxStage.initStyle(StageStyle.UNDECORATED);

        Pane pane = new Pane();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.autosize();
        progressIndicator.setMouseTransparent(true);
        pane.getChildren().add(progressIndicator);

        Scene scene = new Scene(pane);
        scene.setFill(Color.WHITESMOKE);
        loadingBoxStage.setResizable(false);
        loadingBoxStage.setScene(scene);

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                while (thread.getState() != Thread.State.TERMINATED) {
                    synchronized (this) {
                        wait(10);
                    }
                }
                Platform.runLater(() -> loadingBoxStage.close());
                return null;
            }
        };

        Platform.runLater(() -> {
            new MoveStage(pane, null, false);
            new Thread(task).start();
        });

        loadingBoxStage.showAndWait();
        log.fine("LoadingBox has been closed.");
    }
}

