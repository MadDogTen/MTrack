package com.maddogten.mtrack.information;

import java.util.logging.Logger;

public class ChangeReporter {
    private static final Logger log = Logger.getLogger(ChangeReporter.class.getName());

    // Stores all the changes that are added with addChange().
    private static String[] changes = new String[0];
    private static boolean isChanges = false;

    // This first saves the current list, Reinitialize changes as the the old length + 1, Adds the newInfo to changes[0], then iterates thorough the rest adding them started at changes[1].
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

    // This completely clears the changes String[] so it can start new.
    public static void resetChanges() {
        changes = new String[0];
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
}
