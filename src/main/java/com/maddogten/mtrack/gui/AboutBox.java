package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveStage;
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

    public void display(Stage oldStage) throws Exception {
        log.finest("AboutBox has been opened.");
        Stage stage = new Stage();
        GenericMethods.setIcon(stage);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.initModality(Modality.APPLICATION_MODAL);
        Pane root = FXMLLoader.load(getClass().getResource("/gui/About.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(Color.WHITESMOKE);
        stage.setResizable(false);
        stage.setScene(scene);
        Platform.runLater(() -> {
            stage.setX(oldStage.getX() + (oldStage.getWidth() / 2) - (stage.getWidth() / 2));
            stage.setY(oldStage.getY() + (oldStage.getHeight() / 2) - (stage.getHeight() / 2));
            new MoveStage().moveStage(stage, oldStage);
        });
        stage.showAndWait();
    }
}
