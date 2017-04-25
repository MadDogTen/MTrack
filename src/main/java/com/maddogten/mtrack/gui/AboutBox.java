package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Variables;
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

    public AboutBox(final Stage oldStage) throws Exception {
        log.fine("AboutBox has been opened.");
        Stage aboutStage = new Stage();
        GenericMethods.setIcon(aboutStage);
        aboutStage.initOwner(oldStage);
        aboutStage.initStyle(StageStyle.UNDECORATED);
        if (ClassHandler.userInfoController().getHaveStageBlockParentStage(Variables.currentUser))
            aboutStage.initModality(Modality.APPLICATION_MODAL);
        Pane root = FXMLLoader.load(getClass().getResource("/gui/About.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(Color.WHITESMOKE);
        aboutStage.setResizable(false);
        aboutStage.setScene(scene);
        Platform.runLater(() -> new MoveStage(root, oldStage, false));
        aboutStage.show();
        aboutStage.hide();
        aboutStage.setX(aboutStage.getOwner().getX() + (aboutStage.getOwner().getWidth() / 2) - (aboutStage.getWidth() / 2));
        aboutStage.setY(aboutStage.getOwner().getY() + (aboutStage.getOwner().getHeight() / 2) - (aboutStage.getHeight() / 2));
        if (ClassHandler.userInfoController().doSpecialEffects(Variables.currentUser))
            GenericMethods.fadeStageIn(aboutStage, 2, log, this.getClass());
        aboutStage.showAndWait();
        log.fine("AboutBox has been closed.");
    }
}
