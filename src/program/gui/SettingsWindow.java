package program.gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import program.Main;
import program.graphics.ImageLoader;

import java.util.logging.Logger;

public class SettingsWindow {
    private static final Logger log = Logger.getLogger(SettingsWindow.class.getName());
    public void display() throws Exception {
        log.finest("SettingsWindow has been opened.");
        Stage window = new Stage();
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);
        window.initModality(Modality.APPLICATION_MODAL);
        TabPane root = FXMLLoader.load(getClass().getResource("/gui/Settings.fxml"));

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
    }
}
