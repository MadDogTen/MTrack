package com.maddogten.mtrack.util;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.io.FileManager;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.*;

/*
      GenericMethods is for methods that don't really fit anywhere else, and doesn't need their own class.
 */

@SuppressWarnings("ClassWithoutLogger")
public class GenericMethods {

    private static Logger rootLog;
    private static Filter filter;

    // Clock Methods to help with timing.
    @SuppressWarnings("WeakerAccess")
    public static long getTimeMilliSeconds() {
        return (System.nanoTime() / 1000000);
    }

    public static int getTimeSeconds() {
        return (int) (System.nanoTime() / 1000000000);
    }

    public static long timeTakenMilli(long timer) {
        return (getTimeMilliSeconds() - timer);
    }

    public static int timeTakenSeconds(int timer) {
        return getTimeSeconds() - timer;
    }

    // Loads Images.
    @SuppressWarnings("SameParameterValue")
    public static Image getImage(String directory) {
        return new Image(directory);
    }

    public static void setIcon(Stage stage) {
        stage.getIcons().add(getImage(Variables.Logo));
    }

    @SuppressWarnings("SameParameterValue")
    static void printArrayList(Level level, Logger log, ArrayList arrayList, boolean splitWithNewLine) {
        String[] print = new String[arrayList.size()];
        final int[] i = {0};
        String newLine = splitWithNewLine ? "\n" : Strings.EmptyString;
        //noinspection unchecked
        arrayList.forEach(printString -> print[i[0]++] = newLine + printString.toString());
        log.log(level, Arrays.toString(print));
    }

    // Handles Exceptions
    public static void printStackTrace(Logger log, Exception exception, Class exceptionClass) {
        if (Variables.devMode) exception.printStackTrace();
        else {
            String[] stackTrace = new String[exception.getStackTrace().length + 2];
            stackTrace[0] = '\n' + exceptionClass.getName();
            stackTrace[1] = '\n' + exception.toString();
            for (int i = 2; i < exception.getStackTrace().length; i++)
                stackTrace[i] = '\n' + exception.getStackTrace()[i].toString();
            log.severe(Arrays.toString(stackTrace));
        }
    }

    public static void saveSettings() {
        ClassHandler.programSettingsController().getSettingsFile().setShowColumnWidth(Controller.getShowColumnWidth());
        ClassHandler.programSettingsController().getSettingsFile().setShowColumnVisibility(Controller.getShowColumnVisibility());
        ClassHandler.programSettingsController().getSettingsFile().setRemainingColumnWidth(Controller.getRemainingColumnWidth());
        ClassHandler.programSettingsController().getSettingsFile().setRemainingColumnVisibility(Controller.getRemainingColumnVisibility());
        ClassHandler.programSettingsController().getSettingsFile().setSeasonColumnWidth(Controller.getSeasonColumnWidth());
        ClassHandler.programSettingsController().getSettingsFile().setSeasonColumnVisibility(Controller.getSeasonColumnVisibility());
        ClassHandler.programSettingsController().getSettingsFile().setEpisodeColumnWidth(Controller.getEpisodeColumnWidth());
        ClassHandler.programSettingsController().getSettingsFile().setEpisodeColumnVisibility(Controller.getEpisodeColumnVisibility());
        ClassHandler.programSettingsController().getSettingsFile().setNumberOfDirectories(ClassHandler.directoryController().findDirectories(true, false).size());
        ClassHandler.userInfoController().getUserSettings().setChanges(ChangeReporter.getChanges());
        ClassHandler.programSettingsController().saveSettingsFile();
        ClassHandler.userInfoController().saveUserSettingsFile();
    }

    // Initiate logging rules.
    public static void initLogger() {
        rootLog = Logger.getLogger("");
        rootLog.setLevel(Level.FINEST);
        rootLog.getHandlers()[0].setLevel(Level.FINEST);
        filter = record -> record.getSourceClassName().contains("com.maddogten.mtrack");
        rootLog.setFilter(filter);
        rootLog.getHandlers()[0].setFilter(filter);
    }

    public static void initFileLogging(Logger log) throws IOException, SecurityException {
        if (Variables.enableFileLogging) {
            if (!new File(Variables.dataFolder + Variables.LogsFolder).exists())
                new FileManager().createFolder(Variables.LogsFolder);
            FileHandler fileHandler = new FileHandler(Variables.dataFolder.getPath() + Variables.LogsFolder + Variables.logFilename, Variables.logMaxFileSize, Variables.logMaxNumberOfFiles, true);
            rootLog.addHandler(fileHandler);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setFilter(filter);

            log.info("-------- Program Logging Started --------\n\n\n\n");
        }
    }

    public static void fadeStageIn(Stage stage, int fadeTime, Logger log, Class stageClass) {
        stage.setOpacity(0);
        Platform.runLater(() -> {
            while (true) {
                stage.setOpacity(stage.getOpacity() + .01 <= 1 ? stage.getOpacity() + .01 : 1);
                if (stage.getOpacity() < 1) {
                    try {
                        Thread.sleep(fadeTime);
                    } catch (InterruptedException e) {
                        printStackTrace(log, e, stageClass);
                    }
                } else break;
            }
        });
    }

    public static void fadeStageOut(Stage stage, int fadeTime, Logger log, Class stageClass) {
        while (true) {
            stage.setOpacity(stage.getOpacity() - .01 >= 0 ? stage.getOpacity() - .01 : 0);
            if (stage.getOpacity() > 0) {
                try {
                    Thread.sleep(fadeTime);
                } catch (InterruptedException e) {
                    printStackTrace(log, e, stageClass);
                }
            } else break;
        }
    }
}
