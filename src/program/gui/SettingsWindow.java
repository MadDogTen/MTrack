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

public class SettingsWindow {
    public void display() throws Exception {
        Stage window = new Stage();
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
            window.setX(Main.window.getX() + (Main.window.getWidth() / 2) - (window.getWidth() / 2));
            window.setY(Main.window.getY() + (Main.window.getHeight() / 2) - (window.getHeight() / 2));
        });
        window.showAndWait();
    }
}
