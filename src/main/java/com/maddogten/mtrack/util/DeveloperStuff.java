package com.maddogten.mtrack.util;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.information.ChangeReporter;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeveloperStuff {
    public static final int InternalVersion = 55; // To help keep track of what I'm currently working on / testing.
    public static final boolean showOptionToToggleDevMode = true; // false
    public static final boolean startFresh = false; // false -- Won't work unless devMode is true.
    public static final boolean showInternalVersion = true; // Set to false or remove before full release
    private static final Logger log = Logger.getLogger(DeveloperStuff.class.getName());
    public static boolean devMode = true; // false

    //---- ProgramSettingsController ----\\

    @SuppressWarnings({"CallToPrintStackTrace", "EmptyMethod", "RedundantThrows"})
    public static void startupTest() throws Exception { // Place to test code before the rest of the program is started.
    }

    //---- DirectoryController ----\\
    // Debugging tool - Prints all directories to console.
    public void printAllDirectories() {
        log.info("Printing out all directories:");
        Set<Integer> directories = ClassHandler.directoryController().getAllDirectories(false, false);
        if (directories.isEmpty())
            log.info("No directories.");
        else {
            directories.forEach(directoryID -> {
                String[] print = new String[2];
                print[0] = ClassHandler.directoryController().getDirectoryFromID(directoryID).toString();
                print[1] = String.valueOf(directoryID);
                log.info(Arrays.toString(print));
            });
        }
        log.info("Finished printing out all directories.");
    }

    public void clearDirectory(final Stage stage) {
       /* ArrayList<Directory> directories = ClassHandler.directoryController().findDirectories(true, false, true);
        if (directories.isEmpty())
            new MessageBox(new StringProperty[]{Strings.ThereAreNoDirectoriesToClear}, stage);
        else {
            Directory directoryToClear = new ListSelectBox().pickDirectory(Strings.DirectoryToClear, directories, stage);
            if (directoryToClear != null && !directoryToClear.getDirectory().toString().isEmpty()) {
                boolean confirm = new ConfirmBox().confirm(new SimpleStringProperty(Strings.AreYouSureToWantToClear.getValue() + directoryToClear + Strings.QuestionMark.getValue()), stage);
                if (confirm) {
                    directoryToClear.getShows().keySet().forEach(aShow -> {
                        boolean showExistsElsewhere = ClassHandler.showInfoController().doesShowExistElsewhere(aShow, ClassHandler.directoryController().findDirectories(directoryToClear.getDirectoryID(), true, false, true));
                        if (!showExistsElsewhere)
                            ClassHandler.userInfoController().setIgnoredStatus(aShow, true);
                        Controller.updateShowField(aShow, showExistsElsewhere);
                    });
                    directoryToClear.setShows(new HashMap<>());
                    ClassHandler.directoryController().saveDirectory(directoryToClear, true);
                    ClassHandler.programSettingsController().setMainDirectoryVersion(ClassHandler.programSettingsController().getSettingsFile().getMainDirectoryVersion() + 1);
                }
            }
        }*/
    }

    //---- ShowInfoController ----\\
    public void printShowInformation(int showID) {
        String showName = ClassHandler.showInfoController().getShowNameFromShowID(showID);
        log.info("Printing out information for: " + showName);
        Set<Integer> showSeasons = ClassHandler.showInfoController().getSeasonsList(showID);
        String[] print = new String[1 + (showSeasons.size() * 2)];
        final int[] i = {0};
        print[i[0]++] = "\nShow: " + showName;
        showSeasons.forEach(aSeason -> {
            print[i[0]++] = "\nSeason: " + aSeason;
            Set<Integer> seasonEpisodes = ClassHandler.showInfoController().getEpisodesList(showID, aSeason);
            int[] episodes = new int[seasonEpisodes.size()];
            final int[] iterator = {0};
            seasonEpisodes.forEach(aEpisode -> {
                episodes[iterator[0]] = aEpisode;
                iterator[0]++;
            });
            print[i[0]++] = Arrays.toString(episodes);
        });
        log.info(Arrays.toString(print));
        log.info("Finished printing out information for: " + showName);
    }

    // Debug tool to find out all found shows, the seasons in the shows, and the episodes in the seasons.
    public void printOutAllShowsAndEpisodes() {
        log.info("Printing out all Shows and Episodes:");
        final int[] numberOfShows = {0};
        ClassHandler.showInfoController().getShows().forEach(showID -> {
            String showName = ClassHandler.showInfoController().getShowNameFromShowID(showID);
            Set<Integer> seasons = ClassHandler.showInfoController().getSeasonsList(showID);
            String[] print = new String[1 + (seasons.size() * 2)];
            final int[] i = {0};
            print[i[0]++] = "\nShow: " + showName;
            seasons.forEach(aSeason -> {
                print[i[0]++] = "\nSeason: " + aSeason;
                Set<Integer> episodes = ClassHandler.showInfoController().getEpisodesList(showID, aSeason);
                print[i[0]++] = Arrays.toString(episodes.toArray());
            });
            log.info(Arrays.toString(print));
            numberOfShows[0]++;
        });
        log.info("Total Number of Shows: " + numberOfShows[0]);
        log.info("Finished printing out all Shows and Episodes.");
    }

    public void printIgnoredShows() {
        log.info("Printing ignored shows:");
        ArrayList<String> ignoredShows = new ArrayList<>();
        ClassHandler.userInfoController().getIgnoredShows(Variables.getCurrentUser()).forEach(showID -> ignoredShows.add(ClassHandler.showInfoController().getShowNameFromShowID(showID)));
        if (ignoredShows.isEmpty()) log.info("No ignored shows.");
        else GenericMethods.printArrayList(Level.INFO, log, ignoredShows, false);
        log.info("Finished printing ignored shows.");
    }

    public void printHiddenShows() {
        log.info("Printing hidden shows:");
        ArrayList<String> hiddenShows = new ArrayList<>();
        ClassHandler.userInfoController().getHiddenShows(Variables.getCurrentUser()).forEach(showID -> hiddenShows.add(ClassHandler.showInfoController().getShowNameFromShowID(showID)));
        if (hiddenShows.isEmpty()) log.info("No hidden shows.");
        else GenericMethods.printArrayList(Level.INFO, log, hiddenShows, false);
        log.info("Finished printing hidden shows.");
    }

    public void unHideAllShows() {
        log.info("Un-hiding all shows...");
        Set<Integer> hiddenShows = ClassHandler.userInfoController().getHiddenShows(Variables.getCurrentUser());
        if (hiddenShows.isEmpty()) log.info("No shows to un-hide.");
        else hiddenShows.forEach(showID -> {
            log.info(ClassHandler.showInfoController().getShowNameFromShowID(showID) + " is no longer hidden.");
            ClassHandler.userInfoController().setHiddenStatus(Variables.getCurrentUser(), showID, false);
        });
        log.info("Finished un-hiding all shows.");
    }

    public void toggleAllShowsWithActiveStatus(boolean activeStatus) {
        Set<Integer> showIDsList = ClassHandler.userInfoController().getShowsWithActiveStatus(Variables.getCurrentUser(), activeStatus);
        if (showIDsList.isEmpty()) log.info("No shows to change.");
        else {
            showIDsList.forEach(showID -> {
                ClassHandler.userInfoController().setActiveStatus(Variables.getCurrentUser(), showID, !activeStatus);
                Controller.updateShowField(showID, true);
            });
            log.info("Set all shows " + ((activeStatus) ? "inactive." : "active."));
        }
    }

    //---- UserSettingsController ----\\
    // Debug setting to print out all the current users settings.
    public void printAllInfoForCurrentUser() {
        int userID = Variables.getCurrentUser();
        String userName = ClassHandler.userInfoController().getUserNameFromID(userID);
        log.info("\n\n\n\nPrinting all user info for " + userName + "...");
        Set<String> printInfo = new LinkedHashSet<>();
        printInfo.add("Username: " + userName + "\n");
        printInfo.add("UserID: " + userID + "\n");
        printInfo.add("Show username: " + ClassHandler.userInfoController().getUserBooleanSetting(userID, StringDB.COLUMN_SHOWUSERNAME) + "\n");
        printInfo.add("Update speed: " + ClassHandler.userInfoController().getUpdateSpeed(userID) + "\n");
        printInfo.add("Automatic show updating: " + ClassHandler.userInfoController().doShowUpdating(userID) + "\n");
        printInfo.add("Time to wait for directory: " + ClassHandler.userInfoController().getTimeToWaitForDirectory(userID) + "\n");
        printInfo.add("Show 0 remaining: " + ClassHandler.userInfoController().show0Remaining(userID) + "\n");
        printInfo.add("Show active shows: " + ClassHandler.userInfoController().showActiveShows(userID) + "\n");
        printInfo.add("Language: " + ClassHandler.userInfoController().getLanguage(userID) + "\n");
        printInfo.add("Record changes for non-active shows: " + ClassHandler.userInfoController().getRecordChangesForNonActiveShows(userID) + "\n");
        printInfo.add("Record changed seasons lower than current: " + ClassHandler.userInfoController().getRecordChangedSeasonsLowerThanCurrent(userID) + "\n");
        printInfo.add("Move stage with parent: " + ClassHandler.userInfoController().getMoveStageWithParent(userID) + "\n");
        printInfo.add("Have stage block parent stage: " + ClassHandler.userInfoController().getHaveStageBlockParentStage(userID) + "\n");
        printInfo.add("Enable special effects: " + ClassHandler.userInfoController().doSpecialEffects(userID) + "\n");
        printInfo.add("Enable file logging: " + ClassHandler.userInfoController().doFileLogging(userID) + "\n");
        printInfo.add("Show column width: " + ClassHandler.userInfoController().getColumnWidth(userID, StringDB.COLUMN_SHOWCOLUMNWIDTH) + "\n");
        printInfo.add("Remaining column Width: " + ClassHandler.userInfoController().getColumnWidth(userID, StringDB.COLUMN_REMAININGCOLUMNWIDTH) + "\n");
        printInfo.add("Season column Width: " + ClassHandler.userInfoController().getColumnWidth(userID, StringDB.COLUMN_SEASONCOLUMNWIDTH) + "\n");
        printInfo.add("Episode column Width: " + ClassHandler.userInfoController().getColumnWidth(userID, StringDB.COLUMN_EPISODECOLUMNWIDTH) + "\n");
        printInfo.add("Show column visibility: " + ClassHandler.userInfoController().getColumnVisibilityStatus(userID, StringDB.COLUMN_SHOWCOLUMNVISIBILITY) + "\n");
        printInfo.add("Remaining column visibility: " + ClassHandler.userInfoController().getColumnVisibilityStatus(userID, StringDB.COLUMN_REMAININGCOLUMNVISIBILITY) + "\n");
        printInfo.add("Season column visibility: " + ClassHandler.userInfoController().getColumnVisibilityStatus(userID, StringDB.COLUMN_SEASONCOLUMNVISIBILITY) + "\n");
        printInfo.add("Episode column visibility: " + ClassHandler.userInfoController().getColumnVisibilityStatus(userID, StringDB.COLUMN_EPISODECOLUMNVISIBILITY) + "\n");
        VideoPlayer videoPlayer = ClassHandler.userInfoController().getVideoPlayer(userID);
        printInfo.add("Video player type: " + videoPlayer.getVideoPlayerEnum() + "\n");
        printInfo.add("Video player location: " + videoPlayer.getVideoPlayerLocation());
        log.info(String.valueOf(printInfo));
        log.info("Finished printing all user info.\n\n\n\n");
    }

    //---- CheckShowFiles ----\\

    //---- Test Stuff ----\\

    public void toggleIsChanges() {
        ChangeReporter.setIsChanges(!ChangeReporter.getIsChanges());
    }

}
