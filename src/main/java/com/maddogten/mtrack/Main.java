package com.maddogten.mtrack;

import com.maddogten.mtrack.gui.ConfirmBox;
import com.maddogten.mtrack.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.SQLException;
import java.util.logging.Logger;

/*
      Main is the entrance into the program.
 */

public class Main extends Application implements Runnable {
    private static final Logger log = Logger.getLogger(Main.class.getName());
    private final static int timer = GenericMethods.getTimeSeconds();
    public static boolean programRunning = true, programFullyRunning = false;
    public static Stage stage;
    private static Thread thread;

    public static void main(String args[]) {
        launch(args);
    }

    public synchronized static void stop(final Stage stage, final boolean forceStop) {
        if (forceStop || new ConfirmBox().confirm(Strings.AreYouSure, stage)) {
            programFullyRunning = false;
            programRunning = false;
            int timeRan = GenericMethods.timeTakenSeconds(timer);
            if (timeRan > 60) log.info("The program has been running for " + (timeRan / 60) + " Minute(s).");
            else log.info("The program has been running for " + timeRan + " Seconds.");
            log.warning("Program is exiting");
            while (ClassHandler.checkShowFiles().isRecheckingShowFile()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    GenericMethods.printStackTrace(log, e, Main.class);
                }
            }
            Controller.closeChangeBoxStage();
            Controller.closeShowPlayingBoxStage();
            try {
                ClassHandler.getDBManager().closeConnection();
            } catch (SQLException e) {
                GenericMethods.printStackTrace(log, e, Main.class);
            }
            if (Variables.specialEffects) GenericMethods.fadeStageOut(stage, 10, log, Main.class);
            if (stage != null) stage.close();
            Platform.exit();
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    GenericMethods.printStackTrace(log, e, Main.class);
                }
            }
            if (!DeveloperStuff.devMode) System.exit(0);
        }
    }

    @Override
    public void start(final Stage primaryStage) throws Exception {
        GenericMethods.initLogger();
        GenericMethods.initExceptionHandler(log);
        boolean continueStarting = ClassHandler.mainRun().startBackend();
        DeveloperStuff.startupTest();
        if (continueStarting) {
            stage = primaryStage;
            GenericMethods.setIcon(stage);
            stage.initStyle(StageStyle.UNDECORATED);
            Pane root = FXMLLoader.load(getClass().getResource("/gui/MainGui.fxml"));
            stage.setWidth(Variables.SIZE_WIDTH);
            stage.setHeight(Variables.SIZE_HEIGHT);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/gui/MainGui.css");
            scene.setFill(Color.WHITESMOKE);
            stage.setOnCloseRequest(e -> {
                e.consume();
                stop(stage, true);
            });
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
            if (Variables.specialEffects) GenericMethods.fadeStageIn(stage, 10, log, Main.class);
            start();
        } else stop(null, true);
    }

    private synchronized void start() {
        if (!programFullyRunning) {
            programFullyRunning = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        while (programFullyRunning) {
            ClassHandler.mainRun().tick();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
    }
}
