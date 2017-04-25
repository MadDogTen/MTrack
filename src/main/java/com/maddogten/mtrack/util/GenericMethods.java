package com.maddogten.mtrack.util;

import com.maddogten.mtrack.io.FileManager;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
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

    private static final Filter filter = record -> record.getSourceClassName().contains("com.maddogten.mtrack");
    private static Logger rootLog;
    private static FileHandler fileHandler = null;

    // Clock Methods to help with timing.
    @SuppressWarnings("WeakerAccess")
    public static long getTimeMilliSeconds() {
        return (System.nanoTime() / 1000000);
    }

    public static int getTimeSeconds() {
        return (int) (System.nanoTime() / 1000000000);
    }

    public static long timeTakenMilli(final long timer) {
        return (getTimeMilliSeconds() - timer);
    }

    public static int timeTakenSeconds(final int timer) {
        return getTimeSeconds() - timer;
    }

    // Loads Images.
    @SuppressWarnings("SameParameterValue")
    public static Image getImage(final String directory) {
        return new Image(directory);
    }

    public static void setIcon(final Stage stage) {
        stage.getIcons().add(getImage(Variables.Logo));
    }

    @SuppressWarnings("SameParameterValue")
    static void printArrayList(final Level level, final Logger log, final ArrayList arrayList, final boolean splitWithNewLine) {
        String[] print = new String[arrayList.size()];
        final int[] i = {0};
        String newLine = splitWithNewLine ? "\n" : Strings.EmptyString;
        //noinspection unchecked
        arrayList.forEach(printString -> print[i[0]++] = newLine + printString);
        log.log(level, Arrays.toString(print));
    }

    // Handles Exceptions
    public static void printStackTrace(final Logger log, final Exception exception, final Class exceptionClass) {
        if (DeveloperStuff.devMode) //noinspection CallToPrintStackTrace
            exception.printStackTrace();
        else {
            String[] stackTrace = new String[exception.getStackTrace().length + 2];
            stackTrace[0] = '\n' + exceptionClass.getName();
            stackTrace[1] = '\n' + exception.toString();
            for (int i = 2; i < exception.getStackTrace().length; i++)
                stackTrace[i] = '\n' + exception.getStackTrace()[i].toString();
            log.severe(Arrays.toString(stackTrace));
        }
    }

    // Initiate logging rules.
    public static void initLogger() {
        rootLog = Logger.getLogger("");
        setLoggerLevel(DeveloperStuff.devMode ? Level.ALL : Variables.loggerLevel);
        rootLog.setFilter(filter);
        rootLog.getHandlers()[0].setFilter(filter);
    }

    public static void setLoggerLevel(final Level loggerLevel) {
        rootLog.setLevel(loggerLevel);
        rootLog.getHandlers()[0].setLevel(loggerLevel);
    }

    public static void initFileLogging(final Logger log) throws IOException, SecurityException {
        if (ClassHandler.userInfoController().doFileLogging(Variables.currentUser) && !isFileLoggingStarted()) {
            File logFolder = new File(Variables.dataFolder + Variables.LogsFolder);
            if (!logFolder.exists())
                new FileManager().createFolder(Variables.LogsFolder);
            for (File file : logFolder.listFiles((dir, name) -> name.endsWith(".lck")))
                if (file.delete())
                    log.info("\"" + file.getName() + "\" was deleted."); // Clear the lock files, They aren't needed.
            File[] files = logFolder.listFiles((dir, name) -> name.endsWith(".txt") || name.endsWith(Variables.LogExtension));
            while (files.length > Variables.logMaxNumberOfFiles - 1) { // Delete any extra log files.
                Matcher lowest = null;
                String toDelete = null;
                Pattern pattern = Pattern.compile("M?Log(?:_\\d{1,2}){1,2}-(\\d\\d)[\\-.](\\d\\d)[\\-.](\\d\\d)_(\\d\\d)[\\-.](\\d\\d)[\\-.](\\d\\d)");
                for (File file : files) {
                    Matcher matcher = pattern.matcher(file.getName());
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
                log.info("\"" + files.length + "\" log files were found (Limit: " + Variables.logMaxNumberOfFiles + "). Deleted: \"" + toDelete + "\".");
                if (!new FileManager().deleteFile(Variables.LogsFolder, toDelete, "")) break;
                files = logFolder.listFiles((dir, name) -> name.endsWith(".txt") || name.endsWith(Variables.LogExtension));
            }
            fileHandler = new FileHandler(logFolder + Strings.FileSeparator + "MLog_%u_%g-" + new SimpleDateFormat("MM-dd-yy_HH-mm-ss").format(new Date()) + Variables.LogExtension, Variables.logMaxFileSize, Variables.logMaxNumberOfFiles);
            rootLog.addHandler(fileHandler);
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setFilter(filter);
            log.info("-------- Program Logging Started --------");
        }
    }

    public static void initExceptionHandler(@SuppressWarnings("SameParameterValue") Logger log) {
        Thread.setDefaultUncaughtExceptionHandler((thread, exception) -> {
            if (DeveloperStuff.devMode) //noinspection CallToPrintStackTrace
                exception.printStackTrace();
            else {
                String[] stackTrace = new String[exception.getStackTrace().length + 1];
                stackTrace[0] = exception.toString();
                for (int i = 1; i < exception.getStackTrace().length; i++)
                    stackTrace[i] = exception.getStackTrace()[i].toString();
                log.severe(Arrays.toString(stackTrace));
            }
        });
    }

    public static void fadeStageIn(final Stage stage, final int fadeTime, final Logger log, final Class stageClass) {
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

    public static void fadeStageOut(final Stage stage, final int fadeTime, final Logger log, final Class stageClass) {
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

    public static void stopFileLogging(final Logger log) {
        if (isFileLoggingStarted()) {
            fileHandler.close();
            fileHandler = null;
            log.info("-------- Program Logging Stopped --------");
        }
    }

    public static boolean isFileLoggingStarted() {
        return fileHandler != null;
    }

    public static String getSeasonFolderName(final File dir, final String showName, final int season) {
        Pattern pattern = Pattern.compile(Strings.seasonRegex + "\\s" + season);
        Pattern pattern1 = Pattern.compile("s" + ((season < 10) ? 0 : "") + season);
        for (String fileName : new File(dir + Strings.FileSeparator + showName + Strings.FileSeparator).list()) {
            Matcher matcher = pattern.matcher(fileName.toLowerCase());
            if (matcher.find()) return fileName;
            else {
                matcher = pattern1.matcher(fileName.toLowerCase());
                if (matcher.find()) return fileName;
            }
        }
        return "";
    }

    public static int concatenateDigits(int... values) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int value : values) stringBuilder.append(value).append("");
        return Integer.valueOf(stringBuilder.toString());
    }

    public static boolean doesTableExistsFromError(SQLException e) {
        return e.getSQLState().toLowerCase().matches("X0Y32".toLowerCase());
    }
}
