package program.input;

import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MoveWindow {

    public void moveWindow(Stage window, Scene scene) {
        final double[] xOffset = new double[1], yOffset = new double[1];
        scene.setOnMousePressed(e -> {
            xOffset[0] = e.getSceneX();
            yOffset[0] = e.getSceneY();
        });

        scene.setOnMouseDragged(e -> {
            window.setX(e.getScreenX() - xOffset[0]);
            window.setY(e.getScreenY() - yOffset[0]);
        });
    }

    public void moveTabPane(TabPane tabPane) {
        final double[] xOffset = new double[1], yOffset = new double[1];
        tabPane.setOnMousePressed(e -> {
            xOffset[0] = e.getX();
            yOffset[0] = e.getY();
        });

        tabPane.setOnMouseDragged(e -> {
            tabPane.getScene().getWindow().setX(e.getScreenX() - xOffset[0]);
            tabPane.getScene().getWindow().setY(e.getScreenY() - yOffset[0]);
        });
    }

    public void moveWindow(Pane pane) {
        final double[] xOffset = new double[1], yOffset = new double[1];
        pane.setOnMousePressed(e -> {
            xOffset[0] = e.getSceneX();
            yOffset[0] = e.getSceneY();
        });

        pane.setOnMouseDragged(e -> {
            pane.getScene().getWindow().setX(e.getScreenX() - xOffset[0]);
            pane.getScene().getWindow().setY(e.getScreenY() - yOffset[0]);
        });
    }
}