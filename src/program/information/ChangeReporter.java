package program.information;

import java.util.ArrayList;
import java.util.logging.Logger;

public class ChangeReporter {
    private static final Logger log = Logger.getLogger(ChangeReporter.class.getName());

    public static String[] changes = new String[0];

    public static void addChange(String newInfo) {
        ArrayList<String> newList = new ArrayList<>();
        newList.add(0, newInfo);
        int currentPlace = 1;
        for (String aString : changes) {
            newList.add(currentPlace, aString);
            currentPlace++;
        }
        changes = new String[newList.size()];
        for (int i = 0; i < currentPlace; i++) {
            changes[i] = newList.get(i);
        }
    }

    public static void resetChanges() {
        changes = new String[0];
    }

    public static void printChanges() { //TODO Temp, Removed when unneeded.
        log.info("Starting to list changes:");
        for (String changed : changes) {
            log.info(changed);
        }
        log.info("Finished listing changes.");
    }
}
