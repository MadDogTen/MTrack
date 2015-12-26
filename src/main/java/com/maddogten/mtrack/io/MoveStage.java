package com.maddogten.mtrack.io;

import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Variables;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.logging.Logger;

/*
      MoveWindow is used to allow the user to move stages. This is needed since all stages are UNDECORATED.
 */
public class MoveStage {
    private static final Logger log = Logger.getLogger(MoveStage.class.getName());
    private final int moveWaitTime = 120;

    public void moveWindow(Window window, Window parent) {
        log.finest("moveWindow is now running.");
        final double[] offset = new double[2];
        final long[] timePressed = new long[1];
        window.getScene().setOnMousePressed(e -> {
            timePressed[0] = GenericMethods.getTimeMilliSeconds();
            if (e.isPrimaryButtonDown()) {
                offset[0] = e.getSceneX();
                offset[1] = e.getSceneY();
            }
        });
        window.getScene().setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown() && GenericMethods.timeTakenMilli(timePressed[0]) > moveWaitTime) {
                window.setX(e.getScreenX() - offset[0]);
                window.setY(e.getScreenY() - offset[1]);

                if (parent != null && Variables.moveStageWithParent) {
                    parent.getScene().getWindow().setX(window.getScene().getWindow().getX() - (parent.getWidth() / 2) + (window.getWidth() / 2));
                    parent.getScene().getWindow().setY(window.getScene().getWindow().getY() - (parent.getHeight() / 2) + (window.getHeight() / 2));
                }
            }
        });
        window.getScene().setOnMouseReleased(e -> {
            if (!e.isPrimaryButtonDown()) {
                offset[0] = 0;
                offset[1] = 0;
                timePressed[0] = -2;
            }
        });
    }

    public void moveWindow(TabPane tabPane, Stage parent) {
        log.finest("moveWindow TabPane is now running.");
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

                if (parent != null && Variables.moveStageWithParent) {
                    parent.getScene().getWindow().setX(tabPane.getScene().getWindow().getX() - (parent.getWidth() / 2) + (tabPane.getWidth() / 2));
                    parent.getScene().getWindow().setY(tabPane.getScene().getWindow().getY() - (parent.getHeight() / 2) + (tabPane.getHeight() / 2));
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

    public void moveWindow(Pane pane) {
        log.finest("moveWindow Pane is now running.");
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