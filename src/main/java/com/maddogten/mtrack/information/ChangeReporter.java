package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Database.DBChangeTracker;
import com.maddogten.mtrack.Database.DBManager;
import com.maddogten.mtrack.util.ClassHandler;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

/*
      ChangeReporter holds all the strings that the ChangesBox displays.
      Any changes added are put on top of the list.
 */

public class ChangeReporter {
    private static final Logger log = Logger.getLogger(ChangeReporter.class.getName());

    private DBChangeTracker dbChangeTracker;
    // Stores all the changes that are added with addChange().

    public void initDatabase(DBManager dbManager) {
        this.dbChangeTracker = new DBChangeTracker(dbManager);
    }

    @SuppressWarnings("UnusedReturnValue")
    public int addChange(int showID, int season, int episode, boolean found) {
        return dbChangeTracker.addChange(showID, season, episode, found);
    }

    // This completely clears the changes String[] so it can start new.
    public void resetChangesForUser(int userID) {
        dbChangeTracker.deleteAllChangesForUser(userID);
        log.info("Change list has been cleared for " + ClassHandler.userInfoController().getUserNameFromID(userID) + ".");
    }

    public Set<Integer> getUserChanges(int userID) {
        return dbChangeTracker.getUserChanges(userID);
    }

    public int[] getChangeInfo(int changeID) {
        return dbChangeTracker.getChangeInfo(changeID);
    }

    public void setAllSeenForUser(int userID) {
        dbChangeTracker.setAllSeenForUser(userID);
    }

    public String convertChangeIntoText(int changeID) {
        int[] changeInfo = getChangeInfo(changeID);
        StringBuilder stringBuilder = new StringBuilder();
        boolean found = changeInfo[4] == 1;
        stringBuilder.append((found) ? "Added - " : "Removed - ");
        stringBuilder.append(ClassHandler.showInfoController().getShowNameFromShowID(changeInfo[1]));
        if (changeInfo[2] != -2) {
            stringBuilder.append(" | S");
            if (changeInfo[2] < 10) stringBuilder.append(0);
            stringBuilder.append(changeInfo[2]);
            if (changeInfo[3] != -2) {
                stringBuilder.append("E");
                if (changeInfo[3] < 10) stringBuilder.append(0);
                stringBuilder.append(changeInfo[3]);
            }
        }
        return stringBuilder.toString();
    }

    public Set<String> getUserChangesAsStrings(int userID) {
        SortedSet<String> userChanges = new TreeSet<>();
        getUserChanges(userID).forEach(changeID -> userChanges.add(convertChangeIntoText(changeID)));
        return userChanges;
    }

    public boolean isChangesForUser(int userID) {
        return !getUserChanges(userID).isEmpty();
    }
}