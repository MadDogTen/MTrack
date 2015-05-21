package program;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import program.graphics.ImageLoader;
import program.gui.ConfirmBox;
import program.information.ProgramSettingsController;
import program.information.UserInfoController;
import program.util.Clock;
import program.util.Variables;

import java.util.logging.Logger;

public class Main extends Application implements Runnable {
    private static final Logger log = Logger.getLogger(Main.class.getName());
    private final static int timer = Clock.getTimeSeconds();
    public static boolean running = false;
    public static Stage window;
    private static Thread thread;

    public static void main(String args[]) {
        launch(args);
    }

    public synchronized static void stop(Stage stage, Boolean forceStop, Boolean saveSettings) {
        ConfirmBox confirmBox = new ConfirmBox();
        Boolean answer = true;
        if (!forceStop) {
            answer = confirmBox.display("Are you sure?", stage.getScene().getWindow());
        }
        if (answer) {
            if (saveSettings) {
                ProgramSettingsController.saveSettingsFile();
                UserInfoController.saveUserSettingsFile();
            }
            running = false;

            int timeRan = Clock.timeTakenSeconds(timer);
            if (timeRan > 60) {
                log.info("The program has been running for " + (timeRan / 60) + " Minute(s).");
            } else log.info("The program has been running for " + timeRan + " Seconds.");
            log.warning("Program is exiting");

            window.close();
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.severe(e.toString());
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        ImageLoader.setIcon(window);
        window.initStyle(StageStyle.UNDECORATED);
        Parent root = FXMLLoader.load(getClass().getResource("/gui/MainGui.fxml"));
        window.setTitle("MTrack");
        window.setWidth(Variables.SIZE_WIDTH);
        window.setHeight(Variables.SIZE_HEIGHT);

        Scene scene = new Scene(root);

        scene.setFill(Color.WHITESMOKE);

        window.setOnCloseRequest(e -> {
            e.consume();
            stop(window, true, true);
        });

        window.setResizable(true);
        window.setScene(scene);
        window.show();

        start();
    }

    private synchronized void start() {
        if (!running) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    @Override
    public void run() {
        while (running) {
            MainRun.tick();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                log.severe(e.toString());
            }
        }
    }
}