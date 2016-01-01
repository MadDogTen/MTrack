package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.util.GenericMethods;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Logger;

public class AboutBox {
    private static final Logger log = Logger.getLogger(AboutBox.class.getName());
    private Stage aboutStage;

    public void display(Stage oldStage) throws Exception {
        log.finest("AboutBox has been opened.");
        aboutStage = new Stage();
        GenericMethods.setIcon(aboutStage);
        aboutStage.initOwner(oldStage);
        aboutStage.initStyle(StageStyle.UNDECORATED);
        aboutStage.initModality(Modality.APPLICATION_MODAL);
        Pane root = FXMLLoader.load(getClass().getResource("/gui/About.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(Color.WHITESMOKE);
        aboutStage.setResizable(false);
        aboutStage.setScene(scene);
        Platform.runLater(() -> {
            aboutStage.setX(aboutStage.getOwner().getX() + (aboutStage.getOwner().getWidth() / 2) - (aboutStage.getWidth() / 2));
            aboutStage.setY(aboutStage.getOwner().getY() + (aboutStage.getOwner().getHeight() / 2) - (aboutStage.getHeight() / 2));
        });
        aboutStage.showAndWait();
        aboutStage = null;
    }
}