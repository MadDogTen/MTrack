package com.maddogten.mtrack.information;

import com.maddogten.mtrack.util.ClassHandler;

import java.util.logging.Logger;

/*
      ChangeReporter holds all the strings that the ChangesBox displays.
      Any changes added are put on top of the list.
 */

public class ChangeReporter {
    private static final Logger log = Logger.getLogger(ChangeReporter.class.getName());
    // Stores all the changes that are added with addChange().
    private static String[] changes = new String[0];
    private static boolean isChanges = false;

    // This first saves the current list, Reinitialize changes as the the old length + 1, Adds the newInfo to changes[0], then iterates thorough the rest adding them started at changes[1].
    public static void addChange(final String newInfo) {
        int toRemove = -1; // This is to verify no duplicates are added to the list. If one is found, it removes it, and continues adding the new one (So it appears at the top of the list).
        for (int i = 0; i < changes.length; i++) {
            if (changes[i].replace("+", "a").replace("-", "m").matches(newInfo.replace("+", "a").replace("-", "m"))) {
                toRemove = i;
                break;
            }
        }
        if (toRemove != -1) {
            String[] correctedList = new String[changes.length - 1];
            for (int i = 0; i < changes.length; i++)
                if (toRemove != i) correctedList[i > toRemove ? i - 1 : i] = changes[i];
            changes = correctedList;
        }

        log.fine("Adding new change: \"" + newInfo + "\".");
        String[] currentList = changes.clone();
        changes = new String[currentList.length + 1];
        changes[0] = newInfo;
        int iterator = 1;
        for (String aString : currentList) {
            changes[iterator] = aString;
            iterator++;
        }
        if (!isChanges) isChanges = true;
    }

    // This completely clears the changes String[] so it can start new.
    public static void resetChanges() {
        if (changes.length > 0) {
            changes = new String[0];
            isChanges = false;
        }
        ClassHandler.controller().resetChangedShows();
        log.info("Change list has been cleared.");
    }

    public static String[] getChanges() {
        return changes;
    }

    public static void setChanges(final String[] newChangeList) {
        if (changes.length == 0) changes = newChangeList;
        else {
            String[] tempSave = changes.clone();
            changes = newChangeList;
            for (int i = tempSave.length - 1; i >= 0; i--) addChange(tempSave[i]);
        }
        if (changes.length > 0 && !isChanges) isChanges = true;
    }

    public static boolean getIsChanges() {
        return isChanges;
    }

    public static void setIsChanges(final boolean isChanges) {
        ChangeReporter.isChanges = isChanges;
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
