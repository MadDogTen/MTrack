package com.maddogten.mtrack.util;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.io.FileManager;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
      GenericMethods is for methods that don't really fit anywhere else, and doesn't need their own class.
 */

@SuppressWarnings("ClassWithoutLogger")
public class GenericMethods {

    private static Logger rootLog;
    private static Filter filter;
    private static FileHandler fileHandler = null;

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
        ClassHandler.programSettingsController().getSettingsFile().setNumberOfDirectories(ClassHandler.directoryController().findDirectories(true, false, true).size());
        ClassHandler.userInfoController().getUserSettings().setChanges(ChangeReporter.getChanges());
        ClassHandler.userInfoController().getUserSettings().setChangedShowsStatus(ClassHandler.controller().getChangedShows());
        ClassHandler.programSettingsController().saveSettingsFile();
        ClassHandler.userInfoController().saveUserSettingsFile();
    }

    // Initiate logging rules.
    public static void initLogger() {
        rootLog = Logger.getLogger("");
        rootLog.setLevel(Variables.loggingLevel);
        rootLog.getHandlers()[0].setLevel(Variables.loggingLevel);
        filter = record -> record.getSourceClassName().contains("com.maddogten.mtrack");
        rootLog.setFilter(filter);
        rootLog.getHandlers()[0].setFilter(filter);
    }

    public static void initFileLogging(Logger log) throws IOException, SecurityException {
        if (Variables.enableFileLogging) {
            File logFolder = new File(Variables.dataFolder + Variables.LogsFolder);
            if (!logFolder.exists())
                new FileManager().createFolder(Variables.LogsFolder);
            for (File file : logFolder.listFiles((dir, name) -> name.endsWith(".lck")))
                if (file.delete())
                    log.info("\"" + file.getName() + "\" was deleted."); // Clear the lock files, They aren't needed.
            File[] files = logFolder.listFiles((dir, name) -> name.endsWith(".txt"));
            log.info(Arrays.toString(files));
            while (files.length > Variables.logMaxNumberOfFiles - 1) { // Delete any extra log files.
                Matcher lowest = null;
                String toDelete = null;
                for (File file : files) {
                    Matcher matcher = Pattern.compile("(\\d\\d)\\.(\\d\\d)\\.(\\d\\d)_(\\d\\d)\\.(\\d\\d)\\.(\\d\\d)").matcher(file.getName());
                    if (matcher.find()) {
                        if (lowest == null) {
                            lowest = matcher;
                            toDelete = file.getName();
                            continue;
                        }
                        short year = Short.parseShort(matcher.group(3)) < Short.parseShort(lowest.group(3)) ? (short) 1 : Short.parseShort(matcher.group(3)) == Short.parseShort(lowest.group(3)) ? (short) 2 : 0;
                        short month = Short.parseShort(matcher.group(1)) < Short.parseShort(lowest.group(1)) ? (short) 1 : Short.parseShort(matcher.group(1)) == Short.parseShort(lowest.group(1)) ? (short) 2 : 0;
                        short day = Short.parseShort(matcher.group(2)) < Short.parseShort(lowest.group(2)) ? (short) 1 : Short.parseShort(matcher.group(2)) == Short.parseShort(lowest.group(2)) ? (short) 2 : 0;
                        short hour = Short.parseShort(matcher.group(4)) < Short.parseShort(lowest.group(4)) ? (short) 1 : Short.parseShort(matcher.group(4)) == Short.parseShort(lowest.group(4)) ? (short) 2 : 0;
                        short minute = Short.parseShort(matcher.group(5)) < Short.parseShort(lowest.group(5)) ? (short) 1 : Short.parseShort(matcher.group(5)) == Short.parseShort(lowest.group(5)) ? (short) 2 : 0;
                        boolean second = Short.parseShort(matcher.group(6)) < Short.parseShort(lowest.group(6));
                        if (year == 1 || year == 2 && (month == 1 || month == 2 && (day == 1 || day == 2 && (hour == 1 || hour == 2 && (minute == 1 || minute == 2 && second))))) {
                            lowest = matcher;
                            toDelete = file.getName();
                        }
                    }
                }
                log.info(files.length + " log files were found (Limit: " + Variables.logMaxNumberOfFiles + "). Deleted: \"" + toDelete + "\".");
                if (!new FileManager().deleteFile(Variables.LogsFolder, toDelete, "")) break;

                files = logFolder.listFiles((dir, name) -> name.endsWith(".txt"));
            }
            fileHandler = new FileHandler(logFolder + Strings.FileSeparator + "Log_%g-" + new SimpleDateFormat("MM.dd.yy_HH.mm.ss").format(new Date()) + Variables.TextExtension, Variables.logMaxFileSize, Variables.logMaxNumberOfFiles);
            rootLog.addHandler(fileHandler);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setFilter(filter);

            log.info("-------- Program Logging Started --------");
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

    public static void stopFileLogging(Logger log) {
        if (fileHandler != null) {
            fileHandler.close();
            fileHandler = null;
            log.info("File logger has been stopped.");
        }
    }
}
