package com.maddogten.mtrack;

import com.maddogten.mtrack.gui.ConfirmBox;
import com.maddogten.mtrack.information.*;
import com.maddogten.mtrack.io.CheckShowFiles;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Filter;
import java.util.logging.Level;
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
        if (!forceStop) answer = confirmBox.confirm(Strings.AreYouSure, stage);
        if (answer) {
            if (saveSettings) saveSettings();
            programFullyRunning = false;
            programRunning = false;
            int timeRan = GenericMethods.timeTakenSeconds(timer);
            if (timeRan > 60) log.info("The program has been running for " + (timeRan / 60) + " Minute(s).");
            else log.info("The program has been running for " + timeRan + " Seconds.");
            log.warning("Program is exiting");
            while (checkShowFiles.getRecheckShowFileRunning()) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    GenericMethods.printStackTrace(log, e, Main.class);
                }
            }
            Platform.exit();
            Controller.closeChangeBoxStage();
            Controller.getSettingsWindow().closeSettings();
            Controller.closeShowPlayingBoxStage();
            if (stage != null) stage.close();
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    GenericMethods.printStackTrace(log, e, Main.class);
                }
            }
        }
    }

    private static void saveSettings() {
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
        // Logger stuff // Logging to a file works, Just need to figure out how exactly I want to do it. Disabled until then.
        //FileHandler fileHandler = new FileHandler("");
        Logger rootLog = Logger.getLogger("");
        rootLog.setLevel(Level.FINEST);
        rootLog.getHandlers()[0].setLevel(Level.FINEST);
        //rootLog.addHandler(fileHandler);
        //fileHandler.setFormatter(new SimpleFormatter());
        Filter filter = record -> record.getSourceClassName().contains("com.maddogten.mtrack");
        //fileHandler.setFilter(filter);
        rootLog.setFilter(filter);
        rootLog.getHandlers()[0].setFilter(filter);
        // End logger stuff

        boolean continueStarting = mainRun.startBackend();
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
                stop(stage, true, true);
            });
            stage.setResizable(true);
            stage.setScene(scene);
            stage.show();
            start();
        } else stop(null, true, false);
    }

    private synchronized void start() {
        if (!programFullyRunning) {
            programFullyRunning = true;
            thread = new Thread(this);
            Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
                /*String[] stackTrace = new String[exception.getStackTrace().length + 1];
                stackTrace[0] = exception.toString();
                for (int i = 1; i < exception.getStackTrace().length; i++)
                    stackTrace[i] = exception.getStackTrace()[i].toString();
                log.severe(Arrays.toString(stackTrace));

                //Note- Temporarily suppressing these until I figure out how to solve a issue with the controller.*/
            });
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
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
    }
}
