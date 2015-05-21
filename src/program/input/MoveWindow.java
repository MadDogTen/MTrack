package program.input;

import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.stage.Window;

import java.util.logging.Logger;

public class MoveWindow {
    private static final Logger log = Logger.getLogger(MoveWindow.class.getName());

    public void moveWindow(Window window) {
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

    public void moveWindow(TabPane window) {
        final double[] xOffset = new double[1], yOffset = new double[1];
        window.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                xOffset[0] = e.getSceneX();
                yOffset[0] = e.getSceneY();
            }
        });
        window.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                window.getScene().getWindow().setX(e.getScreenX() - xOffset[0]);
                window.getScene().getWindow().setY(e.getScreenY() - yOffset[0]);
            }
        });
    }

    public void moveWindow(Pane window) {
        final double[] xOffset = new double[1], yOffset = new double[1];
        window.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                xOffset[0] = e.getSceneX();
                yOffset[0] = e.getSceneY();
            }
        });
        window.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                window.getScene().getWindow().setX(e.getScreenX() - xOffset[0]);
                window.getScene().getWindow().setY(e.getScreenY() - yOffset[0]);
            }
        });
    }
}