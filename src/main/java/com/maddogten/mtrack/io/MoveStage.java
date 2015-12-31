package com.maddogten.mtrack.io;

import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Variables;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.logging.Logger;

/*
      MoveWindow is used to allow the user to move stages. This is needed since all stages are UNDECORATED.
 */
public class MoveStage {
    private static final Logger log = Logger.getLogger(MoveStage.class.getName());
    private final int moveWaitTime = 120;

    public void moveStage(final Stage stage, final Stage parentStage) {
        log.finest("moveStage is now running.");
        final double[] offset = new double[2];
        final long[] timePressed = new long[1];
        stage.getScene().setOnMousePressed(e -> {
            timePressed[0] = GenericMethods.getTimeMilliSeconds();
            if (e.isPrimaryButtonDown()) {
                offset[0] = e.getSceneX();
                offset[1] = e.getSceneY();
            }
        });
        stage.getScene().setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown() && GenericMethods.timeTakenMilli(timePressed[0]) > moveWaitTime) {
                stage.setX(e.getScreenX() - offset[0]);
                stage.setY(e.getScreenY() - offset[1]);
                if (parentStage != null && Variables.moveStageWithParent) {
                    parentStage.getScene().getWindow().setX(stage.getScene().getWindow().getX() - (parentStage.getWidth() / 2) + (stage.getWidth() / 2));
                    parentStage.getScene().getWindow().setY(stage.getScene().getWindow().getY() - (parentStage.getHeight() / 2) + (stage.getHeight() / 2));
                }
            }
        });
        stage.getScene().setOnMouseReleased(e -> {
            if (!e.isPrimaryButtonDown()) {
                offset[0] = 0;
                offset[1] = 0;
                timePressed[0] = -2;
            }
        });
    }

    public void moveStage(final TabPane tabPane, final Stage parentStage) {
        log.finest("moveStage TabPane is now running.");
        final double[] offset = new double[2];
        final long[] timePressed = new long[1];
        tabPane.setOnMousePressed(e -> {
            timePressed[0] = GenericMethods.getTimeMilliSeconds();
            if (e.isPrimaryButtonDown()) {
                offset[0] = e.getSceneX();
                offset[1] = e.getSceneY();
            }
        });
        tabPane.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown() && GenericMethods.timeTakenMilli(timePressed[0]) > moveWaitTime) {
                tabPane.getScene().getWindow().setX(e.getScreenX() - offset[0]);
                tabPane.getScene().getWindow().setY(e.getScreenY() - offset[1]);
                if (parentStage != null && Variables.moveStageWithParent) {
                    parentStage.getScene().getWindow().setX(tabPane.getScene().getWindow().getX() - (parentStage.getWidth() / 2) + (tabPane.getWidth() / 2));
                    parentStage.getScene().getWindow().setY(tabPane.getScene().getWindow().getY() - (parentStage.getHeight() / 2) + (tabPane.getHeight() / 2));
                }
            }
        });
        tabPane.setOnMouseReleased(e -> {
            if (!e.isPrimaryButtonDown()) {
                offset[0] = 0;
                offset[1] = 0;
                timePressed[0] = -2;
            }
        });
    }

    public void moveStage(final Pane pane) {
        log.finest("moveStage Pane is now running.");
        final double[] offset = new double[2];
        final long[] timePressed = new long[1];
        pane.setOnMousePressed(e -> {
            timePressed[0] = GenericMethods.getTimeMilliSeconds();
            if (e.isPrimaryButtonDown()) {
                offset[0] = e.getSceneX();
                offset[1] = e.getSceneY();
            }
        });
        pane.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown() && GenericMethods.timeTakenMilli(timePressed[0]) > moveWaitTime) {
                pane.getScene().getWindow().setX(e.getScreenX() - offset[0]);
                pane.getScene().getWindow().setY(e.getScreenY() - offset[1]);
            }
        });
        pane.setOnMouseReleased(e -> {
            if (!e.isPrimaryButtonDown()) {
                offset[0] = 0;
                offset[1] = 0;
                timePressed[0] = -2;
            }
        });
    }
}