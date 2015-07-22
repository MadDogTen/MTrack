package com.maddogten.mtrack.information;

import java.util.logging.Logger;

public class ChangeReporter {
    private static final Logger log = Logger.getLogger(ChangeReporter.class.getName());

    private static String[] changes = new String[0];
    private static boolean isChanges = false;

    public static void addChange(String newInfo) {
        log.info("Adding new change...");
        String[] currentList = changes;
        changes = new String[currentList.length + 1];
        changes[0] = newInfo;
        int iterator = 1;
        for (String aString : currentList) {
            changes[iterator] = aString;
            iterator++;
        }
        isChanges = true;
    }

    public static void resetChanges() {
        changes = new String[0];
        isChanges = false;
        log.info("Change list has been cleared.");
    }

    public static String[] getChanges() {
        return changes;
    }

    public static boolean getIsChanges() {
        return isChanges;
    }

    public static int getNumberOfChanges() {
        return changes.length;
    }
}
