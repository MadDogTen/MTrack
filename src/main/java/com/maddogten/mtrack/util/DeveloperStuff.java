package com.maddogten.mtrack.util;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.gui.ConfirmBox;
import com.maddogten.mtrack.gui.ListSelectBox;
import com.maddogten.mtrack.gui.MessageBox;
import com.maddogten.mtrack.information.ChangeReporter;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.Season;
import com.maddogten.mtrack.information.show.Show;
import com.maddogten.mtrack.io.FileManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DeveloperStuff {
    private final Logger log = Logger.getLogger(DeveloperStuff.class.getName());

    //---- ProgramSettingsController ----\\

    //---- DirectoryController ----\\
    // Debugging tool - Prints all directories to console.
    public void printAllDirectories() {
        log.info("Printing out all directories:");
        if (ClassHandler.directoryController().findDirectories(true, false, true).isEmpty())
            log.info("No directories.");
        else {
            ArrayList<Directory> directories = ClassHandler.directoryController().findDirectories(true, false, true);
            directories.forEach(directory -> {
                String[] print = new String[4];
                print[0] = directory.getFileName();
                print[1] = directory.getDirectory().getPath();
                print[2] = String.valueOf(directory.getDirectoryFileVersion());
                print[3] = String.valueOf(directory.getDirectoryID());
                log.info(Arrays.toString(print));
            });
        }
        log.info("Finished printing out all directories.");
    }

    public void clearDirectory(Stage stage) {
        ArrayList<Directory> directories = ClassHandler.directoryController().findDirectories(true, false, true);
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
        }
    }

    //---- ShowInfoController ----\\
    public void printShowInformation(String aShow) {
        log.info("Printing out information for: " + aShow);
        Show show = ClassHandler.showInfoController().getShowsFile().get(aShow);
        String[] print = new String[1 + (show.getSeasons().keySet().size() * 2)];
        final int[] i = {0};
        print[i[0]++] = "\nShow: " + show.getName();
        show.getSeasons().keySet().forEach(aSeason -> {
            Season season = show.getSeasons().get(aSeason);
            print[i[0]++] = "\nSeason: " + season.getSeason();
            int[] episodes = new int[season.getEpisodes().size()];
            final int[] iterator = {0};
            season.getEpisodes().keySet().forEach(aEpisode -> {
                episodes[iterator[0]] = season.getEpisodes().get(aEpisode).getEpisode();
                iterator[0]++;
            });
            print[i[0]++] = Arrays.toString(episodes);
        });
        log.info(Arrays.toString(print));
        log.info("Finished printing out information for: " + aShow);
    }

    // Debug tool to find out all found shows, the seasons in the shows, and the episodes in the seasons.
    public void printOutAllShowsAndEpisodes() {
        log.info("Printing out all Shows and Episodes:");
        final int[] numberOfShows = {0};
        ClassHandler.showInfoController().getShowsFile().keySet().forEach(aShow -> {
            Show show = ClassHandler.showInfoController().getShowsFile().get(aShow);
            String[] print = new String[1 + (show.getSeasons().keySet().size() * 2)];
            final int[] i = {0};
            print[i[0]++] = "\nShow: " + show.getName();
            show.getSeasons().keySet().forEach(aSeason -> {
                Season season = show.getSeasons().get(aSeason);
                print[i[0]++] = "\nSeason: " + season.getSeason();
                int[] episodes = new int[season.getEpisodes().size()];
                final int[] iterator = {0};
                season.getEpisodes().keySet().forEach(aEpisode -> {
                    episodes[iterator[0]] = season.getEpisodes().get(aEpisode).getEpisode();
                    iterator[0]++;
                });
                print[i[0]++] = Arrays.toString(episodes);
            });
            log.info(Arrays.toString(print));
            numberOfShows[0]++;
        });
        log.info("Total Number of Shows: " + numberOfShows[0]);
        log.info("Finished printing out all Shows and Episodes.");
    }

    public void printEmptyShows() {
        log.info("Printing empty shows:");
        ArrayList<String> emptyShows = ClassHandler.checkShowFiles().getEmptyShows();
        if (emptyShows.isEmpty()) log.info("No empty shows");
        else {
            ArrayList<Directory> directories = ClassHandler.directoryController().findDirectories(false, true, false);
            FileManager fileManager = new FileManager();
            directories.forEach(aDirectory -> {
                ArrayList<String> emptyShowsDir = new ArrayList<>();
                emptyShows.forEach(aShow -> {
                    if (fileManager.checkFolderExistsAndReadable(new File(aDirectory + Strings.FileSeparator + aShow)) && !aDirectory.getShows().containsKey(aShow))
                        emptyShowsDir.add(aShow);
                });
                log.info("Empty shows in \"" + aDirectory + "\": " + emptyShowsDir);
            });
        }
        log.info("Finished printing empty shows.");
    }

    public void printIgnoredShows() {
        log.info("Printing ignored shows:");
        ArrayList<String> ignoredShows = ClassHandler.userInfoController().getIgnoredShows();
        if (ignoredShows.isEmpty()) log.info("No ignored shows.");
        else {
            GenericMethods.printArrayList(Level.INFO, log, ignoredShows, false);
        }
        log.info("Finished printing ignored shows.");
    }

    public void printHiddenShows() {
        log.info("Printing hidden shows:");
        ArrayList<String> hiddenShows = ClassHandler.userInfoController().getHiddenShows();
        if (hiddenShows.isEmpty()) log.info("No hidden shows.");
        else GenericMethods.printArrayList(Level.INFO, log, hiddenShows, false);
        log.info("Finished printing hidden shows.");
    }

    public void unHideAllShows() {
        log.info("Un-hiding all shows...");
        ArrayList<String> ignoredShows = ClassHandler.userInfoController().getHiddenShows();
        if (ignoredShows.isEmpty()) log.info("No shows to un-hide.");
        else {
            ignoredShows.forEach(aShow -> {
                log.info(aShow + " is no longer hidden.");
                ClassHandler.userInfoController().setHiddenStatus(aShow, false);
            });
        }
        log.info("Finished un-hiding all shows.");
    }

    public void setAllShowsActive() {
        ArrayList<String> showsList = ClassHandler.showInfoController().getShowsList();
        if (showsList.isEmpty()) log.info("No shows to change.");
        else {
            showsList.forEach(aShow -> {
                ClassHandler.userInfoController().setActiveStatus(aShow, true);
                Controller.updateShowField(aShow, true);
            });
            log.info("Set all shows active.");
        }
    }

    public void setAllShowsInactive() {
        ArrayList<String> showsList = ClassHandler.showInfoController().getShowsList();
        if (showsList.isEmpty()) log.info("No shows to change.");
        else {
            showsList.forEach(aShow -> {
                ClassHandler.userInfoController().setActiveStatus(aShow, false);
                Controller.updateShowField(aShow, true);
            });
            log.info("Set all shows inactive.");
        }
    }

    //---- UserSettingsController ----\\
    // Debug setting to print out all the current users settings.
    public void printAllUserInfo() {
        log.info("Printing all user info for " + Strings.UserName.getValue() + "...");
        String[] print = new String[1 + ClassHandler.userInfoController().getUserSettings().getShowSettings().values().size()];
        final int[] i = {0};
        print[i[0]++] = '\n' + String.valueOf(ClassHandler.userInfoController().getUserSettings().getUserSettingsFileVersion()) + " - " + String.valueOf(ClassHandler.userInfoController().getUserSettings().getUserDirectoryVersion());
        ClassHandler.userInfoController().getUserSettings().getShowSettings().values().forEach(aShowSettings -> print[i[0]++] = '\n' + aShowSettings.getShowName() + " - " + aShowSettings.isActive() + ", " + aShowSettings.isIgnored() + ", " + aShowSettings.isHidden() + " - Season: " + aShowSettings.getCurrentSeason() + " | Episode: " + aShowSettings.getCurrentEpisode());
        log.info(Arrays.toString(print));
        log.info("Finished printing all user info.");
    }

    public void toggleIsChanges() {
        ChangeReporter.setIsChanges(!ChangeReporter.getIsChanges());
    }

    //---- CheckShowFiles ----\\
}
