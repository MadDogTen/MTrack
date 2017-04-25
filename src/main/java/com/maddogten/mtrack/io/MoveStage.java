package com.maddogten.mtrack.io;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Variables;
import javafx.scene.Cursor;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.logging.Logger;

/*
      MoveStage is used to allow the user to move stages. This is needed since all stages are UNDECORATED.
 */
public class MoveStage {
    private static final Logger log = Logger.getLogger(MoveStage.class.getName());
    private final int moveWaitTime = 120;

    public MoveStage(final Region region, final Stage parentStage, final boolean removeDelay) {
        log.finest("moveStage Pane is now running.");
        final double[] offset = new double[2];
        final long[] timePressed = new long[1];
        region.setOnMousePressed(e -> {
            timePressed[0] = GenericMethods.getTimeMilliSeconds();
            if (e.isPrimaryButtonDown()) {
                offset[0] = e.getSceneX();
                offset[1] = e.getSceneY();
            }
        });
        region.setOnMouseDragged(e -> {
            if (e.isPrimaryButtonDown() && (removeDelay || GenericMethods.timeTakenMilli(timePressed[0]) > moveWaitTime)) {
                region.setCursor(Cursor.CLOSED_HAND);
                region.getScene().getWindow().setX(e.getScreenX() - offset[0]);
                region.getScene().getWindow().setY(e.getScreenY() - offset[1]);
                if (parentStage != null && ClassHandler.userInfoController().getMoveStageWithParent(Variables.currentUser)) {
                    parentStage.getScene().getWindow().setX(region.getScene().getWindow().getX() - (parentStage.getWidth() / 2) + (region.getWidth() / 2));
                    parentStage.getScene().getWindow().setY(region.getScene().getWindow().getY() - (parentStage.getHeight() / 2) + (region.getHeight() / 2));
                    if (parentStage.getOwner() != null) {
                        parentStage.getOwner().setX(region.getScene().getWindow().getX() - (parentStage.getOwner().getWidth() / 2) + (region.getWidth() / 2));
                        parentStage.getOwner().setY(region.getScene().getWindow().getY() - (parentStage.getOwner().getHeight() / 2) + (region.getHeight() / 2));
                    }
                }
            }
        });
        region.setOnMouseReleased(e -> {
            if (!e.isPrimaryButtonDown()) {
                region.setCursor(Cursor.DEFAULT);
                offset[0] = 0;
                offset[1] = 0;
                timePressed[0] = -2;
            }
        });
    }
}
