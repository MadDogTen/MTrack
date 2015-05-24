package program.io;

import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.util.logging.Logger;

public class MoveWindow {
    private static final Logger log = Logger.getLogger(MoveWindow.class.getName());

    public void moveWindow(Window window) {
        log.finest("MessageBox is now running.");
        final double[] xOffset = new double[1], yOffset = new double[1];
        window.getScene().setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                xOffset[0] = e.getSceneX();
                yOffset[0] = e.getSceneY();
            }
        });
        window.getScene().setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                window.setX(e.getScreenX() - xOffset[0]);
                window.setY(e.getScreenY() - yOffset[0]);
            }
        });
    }

    public void moveWindow(TabPane tabPane) {
        log.finest("MessageBox TabPane is now running.");
        final double[] xOffset = new double[1], yOffset = new double[1];
        tabPane.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                xOffset[0] = e.getSceneX();
                yOffset[0] = e.getSceneY();
            }
        });
        tabPane.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                tabPane.getScene().getWindow().setX(e.getScreenX() - xOffset[0]);
                tabPane.getScene().getWindow().setY(e.getScreenY() - yOffset[0]);
            }
        });
    }

    public void moveWindow(Pane pane) {
        log.finest("MessageBox Pane is now running.");
        final double[] xOffset = new double[1], yOffset = new double[1];
        pane.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                xOffset[0] = e.getSceneX();
                yOffset[0] = e.getSceneY();
            }
        });
        pane.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                pane.getScene().getWindow().setX(e.getScreenX() - xOffset[0]);
                pane.getScene().getWindow().setY(e.getScreenY() - yOffset[0]);
            }
        });
    }
}