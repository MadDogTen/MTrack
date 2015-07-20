package main.java.com.maddogten.mtrack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.java.com.maddogten.mtrack.gui.ConfirmBox;
import main.java.com.maddogten.mtrack.gui.SettingsWindow;
import main.java.com.maddogten.mtrack.information.ProgramSettingsController;
import main.java.com.maddogten.mtrack.information.ShowInfoController;
import main.java.com.maddogten.mtrack.information.UserInfoController;
import main.java.com.maddogten.mtrack.io.CheckShowFiles;
import main.java.com.maddogten.mtrack.util.Clock;
import main.java.com.maddogten.mtrack.util.ImageLoader;
import main.java.com.maddogten.mtrack.util.Strings;
import main.java.com.maddogten.mtrack.util.Variables;

import java.util.logging.Logger;

public class Main extends Application implements Runnable {
    private static final Logger log = Logger.getLogger(Main.class.getName());
    private final static ProgramSettingsController programSettingsController = new ProgramSettingsController();
    private final static ShowInfoController showInfoController = new ShowInfoController(programSettingsController);
    private final static UserInfoController userInfoController = new UserInfoController(showInfoController);
    private final static CheckShowFiles checkShowFiles = new CheckShowFiles(programSettingsController, showInfoController, userInfoController);
    private final static MainRun mainRun = new MainRun(programSettingsController, showInfoController, userInfoController, checkShowFiles);
    private final static int timer = Clock.getTimeSeconds();
    public static boolean programRunning = true, programFullyRunning = false;
    public static Stage stage;
    private static Thread thread;

    public static void main(String args[]) {
        launch(args);
    }

    public synchronized static void stop(Stage stage, boolean forceStop, boolean saveSettings) {
        ConfirmBox confirmBox = new ConfirmBox();
        boolean answer = true;
        if (!forceStop) {
            answer = confirmBox.display(Strings.AreYouSure, stage.getScene().getWindow());
        }

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
                programSettingsController.saveSettingsFile();
                userInfoController.saveUserSettingsFile();
            }
            programFullyRunning = false;
            programRunning = false;

            int timeRan = Clock.timeTakenSeconds(timer);
            if (timeRan > 60) {
                log.info("The program has been running for " + (timeRan / 60) + " Minute(s).");
            } else log.info("The program has been running for " + timeRan + " Seconds.");
            log.warning("Program is exiting");

            if (Controller.isChangeBoxStageOpen())
                Controller.closeChangeBoxStage();

            if (SettingsWindow.getStage() != null)
                SettingsWindow.getStage().close();
            if (stage != null) {
                stage.close();
            }
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    log.severe(e.toString());
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

    @Override
    public void start(Stage primaryStage) throws Exception {
        programSettingsController.setShowInfoController(showInfoController);
        programSettingsController.setUserInfoController(userInfoController);
        boolean continueStarting = mainRun.startBackend();
        if (continueStarting) {
            stage = primaryStage;
            ImageLoader.setIcon(stage);
            stage.initStyle(StageStyle.UNDECORATED);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/MainGui.fxml"));
            Parent root = fxmlLoader.load();

            stage.setWidth(Variables.SIZE_WIDTH);
            stage.setHeight(Variables.SIZE_HEIGHT);

            Scene scene = new Scene(root);

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
                log.severe(e.toString());
            }
        }
    }
}