package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Database.DBChangeTracker;
import com.maddogten.mtrack.util.ClassHandler;

import java.sql.Connection;
import java.sql.SQLException;
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

    public void initDatabase(Connection connection) throws SQLException {
        this.dbChangeTracker = new DBChangeTracker(connection);
    }

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

    /*public class ChangedShow {
        private final String show;
        private final Status status;

        private final Map<Integer, Set<ChangedEpisode>> changedInfo;

        public ChangedShow(String show) {
            this.show = show;
            this.status = Status.UNCHANGED;

            changedInfo = new HashMap<>();
        }

        public ChangedShow(String show, Status status) {
            this.show = show;
            this.status = status;

            changedInfo = new HashMap<>();
        }

        public LinkedHashSet<String> getText() { // TODO Add localization
            LinkedHashSet<String> textResult = new LinkedHashSet<>();
            if (status != Status.UNCHANGED) textResult.add(show + " was " + status.statusTextProperty());

            Map<Integer, Set<Integer>> addedStuff = new HashMap<>();
            Map<Integer, Set<Integer>> removedStuff = new HashMap<>();

            changedInfo.forEach((seasonInt, changedEpisodes) -> changedEpisodes.forEach(changedEpisode -> {
                switch (changedEpisode.getStatus()) {
                    case ADDED:
                        if (!addedStuff.containsKey(seasonInt)) addedStuff.put(seasonInt, new HashSet<>());
                        addedStuff.get(seasonInt).add(changedEpisode.getEpisode());
                    case REMOVED:
                        if (!removedStuff.containsKey(seasonInt)) removedStuff.put(seasonInt, new HashSet<>());
                        removedStuff.get(seasonInt).add(changedEpisode.getEpisode());
                }
            }));

            StringBuilder stringBuilder = new StringBuilder();
            if (!addedStuff.isEmpty()) {
                stringBuilder.append("Added: ");
                addedStuff.forEach((seasonInt, seasonEpisodes) -> {
                    stringBuilder.append("Season: ");
                    seasonEpisodes.forEach(integer -> stringBuilder.append(integer).append(", "));
                    stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length() - 1);
                });
                textResult.add(stringBuilder.toString());
                stringBuilder.delete(0, stringBuilder.length());
            }
            if (!removedStuff.isEmpty()) {
                stringBuilder.append("Removed: ");
                removedStuff.forEach((seasonInt, seasonEpisodes) -> {
                    stringBuilder.append("Season: ");
                    seasonEpisodes.forEach(integer -> stringBuilder.append(integer).append(", "));
                    stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length() - 1);
                });
                textResult.add(stringBuilder.toString());
                stringBuilder.delete(0, stringBuilder.length());
            }
            return textResult;
        }

        class ChangedEpisode {
            private final int episode;
            private final Status status;

            public ChangedEpisode(int episode, Status status) {
                this.episode = episode;
                this.status = status;
            }

            public int getEpisode() {
                return episode;
            }

            public Status getStatus() {
                return status;
            }
        }
    }

    private enum Status {
        ADDED(new SimpleStringProperty(" added.")), REMOVED(new SimpleStringProperty(" removed.")), UNCHANGED(new SimpleStringProperty("")); //TODO Add localization
        private final StringProperty statusText;

        public String getStatusText() {
            return statusText.get();
        }

        public StringProperty statusTextProperty() {
            return statusText;
        }

        Status(StringProperty text) {
            this.statusText = text;
        }
    }*/
}
