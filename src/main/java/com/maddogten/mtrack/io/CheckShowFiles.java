package com.maddogten.mtrack.io;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.FindShows;
import com.maddogten.mtrack.util.Variables;

import java.util.*;
import java.util.logging.Logger;

/*
      CheckShowFiles handles checking for new shows, if any are found,
      saves the new file. It sends the newly generated shows file to
      FindChangedShows.
 */


public class CheckShowFiles {
    private final Logger log = Logger.getLogger(CheckShowFiles.class.getName());
    private final FindShows findShows = new FindShows();

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // isRecheckingShowFile - If the method is currently running, then this will be true and stop other non-forced checks from running.
    // keepRunning - This will be true if the program is doing a normal run currently, or false if not running, or currently being forced ran. If isRecheckingShowFile is true, and this is false, it will ignore further force run attempts.
    // currentlyCheckingDirectories - This is true when it is checking if the folders exist / is waiting for a response. Otherwise it is false. This is to show a notification when it is running. Used by the Controller.
    // stopRunning - True either when the program is shutting down, or a forceRun is stopping a normal run. When it is true, recheckShowFileOld stops what it is doing and discards everything.
    private boolean isRecheckingShowFile = false, currentlyCheckingDirectories = false;
    // checkInterval - 1 is added to this each time hasShowsChangedOld is ran. This is used so that the above emptyShows are only checked when the program is first started, or every 5 runs after that.
    private int checkInterval = Variables.checkInterval;
    // recheckShowFilePercentage - Is increased as recheckShowFileOld is running. The percentage is currently split between each directory, and then that is further split by each active show (Plus 2 shows worth is reserved when checking for new shows). Used by the controller.
    private double recheckShowFilePercentage = 0;

    public void checkShowFiles() {
        if (isRecheckingShowFile) return;
        log.info("Checking for new show files...");
        isRecheckingShowFile = true;
        ClassHandler.directoryController().checkDirectories(true);
        FindShows findShows = new FindShows();
        Map<Integer, Set<FindShows.Show>> directoryShows = new HashMap<>();
        checkInterval++;
        currentlyCheckingDirectories = true;
        ClassHandler.directoryController().getActiveDirectories(true, !(checkInterval >= Variables.checkInterval)).forEach(directoryID -> directoryShows.put(directoryID, findShows.findShows(ClassHandler.directoryController().getDirectoryFromID(directoryID))));
        currentlyCheckingDirectories = false;
        final int[] numberOfShows = {0};
        directoryShows.forEach((integer, shows) -> numberOfShows[0] += shows.size());


        double percentageInterval = 100 / ((numberOfShows[0] != 0) ? numberOfShows[0] : 1);
        Set<Integer> allShows = new HashSet<>();
        directoryShows.forEach((directoryID, shows) -> shows.forEach(show -> {
            log.info("Checking \"" + show.getShow() + "\" for changes.");
            int[] showAddedInfo = ClassHandler.showInfoController().addShow(show.getShow());
            int showID = showAddedInfo[0];
            boolean showAdded = showAddedInfo[1] == 1;
            if (showAdded) log.info("\"" + show.getShow() + "\" was added.");
            show.getSeasons().forEach(season -> {
                boolean doesNotExist = showAdded || !ClassHandler.showInfoController().doesSeasonExist(showID, season.getSeason());
                if (doesNotExist) {
                    log.info("Season \"" + season + "\" for \"" + show.getShow() + "\" was found and added.");
                    ClassHandler.showInfoController().addSeason(showID, season.getSeason());
                }
                season.getEpisodes().forEach(episode -> {
                    if (episode.getEpisode() != -2) {
                        if (doesNotExist || !ClassHandler.showInfoController().doesEpisodeExist(showID, season.getSeason(), episode.getEpisode())) {
                            log.fine("Adding episode \"" + episode.getEpisode() + " for \"" + show.getShow() + "\" Season \"" + season.getSeason() + "\".");
                            ClassHandler.showInfoController().addEpisodeFile(ClassHandler.showInfoController().addEpisode(showID, season.getSeason(), episode.getEpisode(), episode.isDoubleEpisode()), directoryID, episode.getEpisodeFilename());
                        } else {
                            boolean episodeFileNotFound = true;
                            int episodeID = ClassHandler.showInfoController().getEpisodeID(showID, season.getSeason(), episode.getEpisode());
                            for (String episodeFile : ClassHandler.showInfoController().getEpisodeFiles(episodeID)) {
                                if (episode.getEpisodeHash() == episodeFile.hashCode()) {
                                    episodeFileNotFound = false;
                                    break;
                                }
                            }
                            if (episodeFileNotFound) {
                                log.fine("Adding new file for episode \"" + episode.getEpisode() + " for \"" + show.getShow() + "\" Season \"" + season.getSeason() + "\".");
                                ClassHandler.showInfoController().addEpisodeFile(episodeID, directoryID, episode.getEpisodeFilename());

                            }
                        }
                        if (episode.isDoubleEpisode()) {
                            if (doesNotExist || !ClassHandler.showInfoController().doesEpisodeExist(showID, season.getSeason(), episode.getEpisode2())) {
                                log.fine("Adding episode \"" + episode.getEpisode2() + " for \"" + show.getShow() + "\" Season \"" + season.getSeason() + "\".");
                                ClassHandler.showInfoController().addEpisodeFile(ClassHandler.showInfoController().addEpisode(showID, season.getSeason(), episode.getEpisode2(), true), directoryID, episode.getEpisodeFilename());
                            } else {
                                boolean episodeFileNotFound = true;
                                int episodeID = ClassHandler.showInfoController().getEpisodeID(showID, season.getSeason(), episode.getEpisode2());
                                for (String episodeFile : ClassHandler.showInfoController().getEpisodeFiles(episodeID)) {
                                    if (episode.getEpisodeHash() == episodeFile.hashCode()) {
                                        episodeFileNotFound = false;
                                        break;
                                    }
                                }
                                if (episodeFileNotFound) {
                                    log.fine("Adding new file for episode \"" + episode.getEpisode2() + " for \"" + show.getShow() + "\" Season \"" + season.getSeason() + "\".");
                                    ClassHandler.showInfoController().addEpisodeFile(episodeID, directoryID, episode.getEpisodeFilename());

                                }
                            }
                        }
                    }
                });
                allShows.add(showID);
            });
            ClassHandler.userInfoController().addShowForUsers(showID);
            recheckShowFilePercentage += percentageInterval;
        }));
        ArrayList<Integer> shows = ClassHandler.showInfoController().getShows();
        shows.removeIf(allShows::contains);
        shows.forEach(showID -> {
            log.info("\"" + ClassHandler.showInfoController().getShowNameFromShowID(showID) + "\" was no longer found, and is now ignored.");
            ClassHandler.showInfoController().removeShow(showID);
            ClassHandler.userInfoController().setIgnoredStatusAllUsers(showID, true);
        });

        if (checkInterval == Variables.checkInterval) checkInterval = 0;
        isRecheckingShowFile = false;
        recheckShowFilePercentage = 0;
        log.info("Finished checking for new show files.");
    }

    public boolean isRecheckingShowFile() {
        return isRecheckingShowFile;
    }

    public double getRecheckShowFilePercentage() {
        return recheckShowFilePercentage / 100;
    }

    public boolean isCurrentlyCheckingDirectories() {
        return currentlyCheckingDirectories;
    }
}
