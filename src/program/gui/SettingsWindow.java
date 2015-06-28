package program.gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import program.Main;
import program.graphics.ImageLoader;

import java.util.logging.Logger;

public class SettingsWindow {
    private static final Logger log = Logger.getLogger(SettingsWindow.class.getName());
    private static Stage window;

    public static Stage getStage() {
        return window;
    }

    public void display() throws Exception {
        log.finest("SettingsWindow has been opened.");
        window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        Pane root = FXMLLoader.load(getClass().getResource("/gui/Settings.fxml"));

        window.setTitle("MTrack");

        assert root != null;
        Scene scene = new Scene(root);

        scene.setFill(Color.WHITESMOKE);

        window.setResizable(false);
        window.setScene(scene);
        Platform.runLater(() -> {
            window.setX(Main.stage.getX() + (Main.stage.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(Main.stage.getY() + (Main.stage.getHeight() / 2) - (window.getHeight() / 2));
        });
        window.showAndWait();
        window = null;
    }
}
