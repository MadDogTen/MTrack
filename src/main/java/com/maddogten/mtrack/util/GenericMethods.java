package com.maddogten.mtrack.util;

import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.logging.Logger;

/*
      GenericMethods is for methods that don't really fit anywhere else, and doesn't need their own class.
 */

@SuppressWarnings("ClassWithoutLogger")
public class GenericMethods {

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

    // Handles Exceptions
    public static void printStackTrace(Logger log, Exception exception, Class exceptionClass) {
        String[] stackTrace = new String[exception.getStackTrace().length + 2];
        stackTrace[0] = '\n' + exceptionClass.getName();
        stackTrace[1] = '\n' + exception.toString();
        for (int i = 2; i < exception.getStackTrace().length; i++)
            stackTrace[i] = '\n' + exception.getStackTrace()[i].toString();
        log.severe(Arrays.toString(stackTrace));
    }
}
