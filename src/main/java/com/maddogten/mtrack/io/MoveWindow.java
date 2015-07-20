package main.java.com.maddogten.mtrack.io;

import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.logging.Logger;

public class MoveWindow {
    private static final Logger log = Logger.getLogger(MoveWindow.class.getName());

    public void moveWindow(Window window, Window parent) {
        log.finest("MessageBox is now running.");
        final double[] xOffset = new double[1], yOffset = new double[1];
        window.getScene().setOnMousePressed(e -> {
            if (e.isPrimaryButtonDown()) {
                xOffset[0] = e.getSceneX();
                yOffset[0] = e.getSceneY();
            }
        });
        window.getScene().setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()) {
                window.setX(e.getScreenX() - xOffset[0]);
                window.setY(e.getScreenY() - yOffset[0]);
                if (parent != null) {
                    parent.getScene().getWindow().setX(window.getScene().getWindow().getX() - (parent.getWidth() / 2) + (window.getWidth() / 2));
                    parent.getScene().getWindow().setY(window.getScene().getWindow().getY() - (parent.getHeight() / 2) + (window.getHeight() / 2));
                }
            }
        });
    }

    public void moveWindow(TabPane tabPane, Stage parent) {
        log.finest("MessageBox TabPane is now running.");
        final double[] xOffset = new double[1], yOffset = new double[1];
        tabPane.setOnMousePressed(e -> {
            if (e.isPrimaryButtonDown()) {
                xOffset[0] = e.getSceneX();
                yOffset[0] = e.getSceneY();
            }
        });
        tabPane.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()) {
                tabPane.getScene().getWindow().setX(e.getScreenX() - xOffset[0]);
                tabPane.getScene().getWindow().setY(e.getScreenY() - yOffset[0]);
                if (parent != null) {
                    parent.getScene().getWindow().setX(tabPane.getScene().getWindow().getX() - (parent.getWidth() / 2) + (tabPane.getWidth() / 2));
                    parent.getScene().getWindow().setY(tabPane.getScene().getWindow().getY() - (parent.getHeight() / 2) + (tabPane.getHeight() / 2));
                }
            }
        });
    }

    public void moveWindow(Pane pane) {
        log.finest("MessageBox Pane is now running.");
        final double[] xOffset = new double[1], yOffset = new double[1];
        pane.setOnMousePressed(e -> {
            if (e.isPrimaryButtonDown()) {
                xOffset[0] = e.getSceneX();
                yOffset[0] = e.getSceneY();
            }
        });
        pane.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown()) {
                pane.getScene().getWindow().setX(e.getScreenX() - xOffset[0]);
                pane.getScene().getWindow().setY(e.getScreenY() - yOffset[0]);
            }
        });
    }
}