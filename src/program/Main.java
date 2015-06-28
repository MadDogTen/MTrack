package program;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import program.graphics.ImageLoader;
import program.gui.ChangesBox;
import program.gui.ConfirmBox;
import program.gui.SettingsWindow;
import program.information.ProgramSettingsController;
import program.information.UserInfoController;
import program.util.Clock;
import program.util.Strings;
import program.util.Variables;

import java.util.logging.Logger;

public class Main extends Application implements Runnable {
    private static final Logger log = Logger.getLogger(Main.class.getName());
    private final static int timer = Clock.getTimeSeconds();
    public static boolean programRunning = true, programFullyRunning = false;
    public static Stage stage;
    private static Thread thread;

    public static void main(String args[]) {
        launch(args);
    }

    public synchronized static void stop(Stage stage, Boolean forceStop, Boolean saveSettings) {
        ConfirmBox confirmBox = new ConfirmBox();
        Boolean answer = true;
        if (!forceStop) {
            answer = confirmBox.display(Strings.AreYouSure, stage.getScene().getWindow());
        }
        if (answer) {
            if (saveSettings) {
                ProgramSettingsController.saveSettingsFile();
                UserInfoController.saveUserSettingsFile();
            }
            programFullyRunning = false;
            programRunning = false;

            int timeRan = Clock.timeTakenSeconds(timer);
            if (timeRan > 60) {
                log.info("The program has been running for " + (timeRan / 60) + " Minute(s).");
            } else log.info("The program has been running for " + timeRan + " Seconds.");
            log.warning("Program is exiting");

            if (ChangesBox.getStage() != null)
                ChangesBox.getStage().close();

            if (SettingsWindow.getStage() != null)
                SettingsWindow.getStage().close();
            stage.close();
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.severe(e.toString());
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
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
            MainRun.tick();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                log.severe(e.toString());
            }
        }
    }
}