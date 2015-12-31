package com.maddogten.mtrack.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.logging.Logger;

/*
      GenericMethods is for methods that don't really fit anywhere else, and doesn't need their own class.
 */

public class GenericMethods {
    private static final Logger log = Logger.getLogger(GenericMethods.class.getName());

    // Clock Methods to help with timing.
    @SuppressWarnings("WeakerAccess")
    public static long getTimeMilliSeconds() {
        log.finest("getTimeMilliSeconds has been ran.");
        return (System.nanoTime() / 1000000);
    }

    public static int getTimeSeconds() {
        log.finest("getTimeSeconds has been ran.");
        return (int) (System.nanoTime() / 1000000000);
    }

    public static long timeTakenMilli(long timer) {
        log.finest("timeTakenMilli has been ran.");
        return (getTimeMilliSeconds() - timer);
    }

    public static int timeTakenSeconds(int timer) {
        log.finest("timeTakenSeconds has been ran.");
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

    // Handles Exceptions
    public static void printStackTrace(Logger log, Exception exception) {
        String[] StackTrace = new String[exception.getStackTrace().length];
        for (int i = 0; i < exception.getStackTrace().length; i++)
            StackTrace[i] = '\n' + exception.getStackTrace()[i].toString();
        log.severe(Arrays.toString(StackTrace));
    }
}
