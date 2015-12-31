package com.maddogten.mtrack;

import com.maddogten.mtrack.gui.ConfirmBox;
import com.maddogten.mtrack.information.*;
import com.maddogten.mtrack.io.CheckShowFiles;
import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Logger;

/*
      Main is the entrance into the program. Loads, holds, and passed out the DirectoryController, ShowInfoController, UserInfoController, CheckShowFiles, and MainRun.
 */

public class Main extends Application implements Runnable {
    private static final Logger log = Logger.getLogger(Main.class.getName());
    private final static DirectoryController directoryController = new DirectoryController();
    private final static ShowInfoController showInfoController = new ShowInfoController(directoryController);
    private final static UserInfoController userInfoController = new UserInfoController(showInfoController);
    private final static ProgramSettingsController programSettingsController = new ProgramSettingsController(userInfoController);
    private final static CheckShowFiles checkShowFiles = new CheckShowFiles(programSettingsController, showInfoController, userInfoController, directoryController);
    private final static MainRun mainRun = new MainRun(programSettingsController, showInfoController, userInfoController, checkShowFiles, directoryController);
    private final static int timer = GenericMethods.getTimeSeconds();
    public static boolean programRunning = true, programFullyRunning = false;
    public static Stage stage;
    private static Thread thread;

    public static void main(String args[]) {
        launch(args);
    }

    public synchronized static void stop(Stage stage, boolean forceStop, boolean saveSettings) {
        ConfirmBox confirmBox = new ConfirmBox();
        boolean answer = true;
        if (!forceStop) answer = confirmBox.display(Strings.AreYouSure, stage);
        if (answer) {
            if (saveSettings) {
                programSettingsController.getSettingsFile().setShowColumnWidth(Controller.getShowColumnWidth());
                programSettingsController.getSettingsFile().setShowColumnVisibility(Controller.getShowColumnVisibility());
                programSettingsController.getSettingsFile().setRemainingColumnWidth(Controller.getRemainingColumnWidth());
                programSettingsController.getSettingsFile().setRemainingColumnVisibility(Controller.getRemainingColumnVisibility());
                programSettingsController.getSettingsFile().setSeasonColumnWidth(Controller.getSeasonColumnWidth());
                programSettingsController.getSettingsFile().setSeasonColumnVisibility(Controller.getSeasonColumnVisibility());
                programSettingsController.getSettingsFile().setEpisodeColumnWidth(Controller.getEpisodeColumnWidth());
                programSettingsController.getSettingsFile().setEpisodeColumnVisibility(Controller.getEpisodeColumnVisibility());
                programSettingsController.getSettingsFile().setNumberOfDirectories(directoryController.getDirectories().size());
                userInfoController.getUserSettings().setChanges(ChangeReporter.getChanges());
                programSettingsController.saveSettingsFile();
                userInfoController.saveUserSettingsFile();
            }
            programFullyRunning = false;
            programRunning = false;
            int timeRan = GenericMethods.timeTakenSeconds(timer);
            if (timeRan > 60) log.info("The program has been running for " + (timeRan / 60) + " Minute(s).");
            else log.info("The program has been running for " + timeRan + " Seconds.");
            log.warning("Program is exiting");
            Controller.closeChangeBoxStage();
            Controller.getSettingsWindow().closeSettings();
            if (stage != null) stage.close();
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    GenericMethods.printStackTrace(log, e);
                }
            }
        }
    }

    public static ProgramSettingsController getProgramSettingsController() {
        return programSettingsController;
    }

    public static ShowInfoController getShowInfoController() {
        return showInfoController;
    }

    public static UserInfoController getUserInfoController() {
        return userInfoController;
    }

    public static CheckShowFiles getCheckShowFiles() {
        return checkShowFiles;
    }

    public static MainRun getMainRun() {
        return mainRun;
    }

    public static DirectoryController getDirectoryController() {
        return directoryController;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        boolean continueStarting = mainRun.startBackend();
        if (continueStarting) {
            stage = primaryStage;
            GenericMethods.setIcon(stage);
            stage.initStyle(StageStyle.UNDECORATED);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/MainGui.fxml"));
            Parent root = fxmlLoader.load();
            stage.setWidth(Variables.SIZE_WIDTH);
            stage.setHeight(Variables.SIZE_HEIGHT);
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/gui/MainGui.css");
            scene.setFill(Color.WHITESMOKE);
            stage.setOnCloseRequest(e -> {
                e.consume();
                stop(stage, true, true);
            });
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
            new MoveStage().moveStage(primaryStage, null);
            start();
        } else stop(null, true, false);
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
            mainRun.tick();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                GenericMethods.printStackTrace(log, e);
            }
        }
    }
}