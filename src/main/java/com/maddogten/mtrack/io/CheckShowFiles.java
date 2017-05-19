package com.maddogten.mtrack.io;

import com.maddogten.mtrack.util.*;

import java.util.*;
import java.util.logging.Logger;

/*
      CheckShowFiles handles checking for new shows, if any are found,
      saves the new file. It sends the newly generated shows file to
      FindChangedShows.
 */


public class CheckShowFiles {
    private final Logger log = Logger.getLogger(CheckShowFiles.class.getName());

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // isRecheckingShowFile - If the method is currently running, then this will be true and stop other non-forced checks from running.
    // keepRunning - This will be true if the program is doing a normal run currently, or false if not running, or currently being forced ran. If isRecheckingShowFile is true, and this is false, it will ignore further force run attempts.
    // currentlyCheckingDirectories - This is true when it is checking if the folders exist / is waiting for a response. Otherwise it is false. This is to show a notification when it is running. Used by the Controller.
    // stopRunning - True either when the program is shutting down, or a forceRun is stopping a normal run. When it is true, recheckShowFileOld stops what it is doing and discards everything.
    private boolean isRecheckingShowFile = false, currentlyCheckingDirectories = false;
    // checkInterval - 1 is added to this each time hasShowsChangedOld is ran. This is used so that the above emptyShows are only checked when the program is first started, or every 5 runs after that.
    private int checkInterval = Variables.checkInterval;
    // recheckShowFilePercentage - Is increased as recheckShowFileOld is running. The percentage is currently split between each directory, and then that is further split by each active show (Plus 2 shows worth is reserved when checking for new shows). Used by the controller.
    private double recheckShowFilePercentage = 0.0;

    private synchronized void addRecheckShowFilePercentage(double amountToAdd) {
        if (amountToAdd > 0.0) {
            if (amountToAdd <= 100.0) {
                if (recheckShowFilePercentage != 100.0) {
                    if (recheckShowFilePercentage + amountToAdd > 100.0) {
                        log.warning("recheckShowFilePercentage was attempting to be set too \"" + recheckShowFilePercentage + "\", That shouldn't happen, Please correct.");
                        recheckShowFilePercentage = 100.0;
                    } else recheckShowFilePercentage += amountToAdd;
                } else
                    log.warning("Warning- Percentage is already at 100, Nothing should be trying to add to it, Please correct.");
            } else log.warning("Warning- Cannot add a number greater then 100.0 to the percentage, Please correct.");
        } else log.warning("Warning- Cannot add 0 or negative amount to percentage, This needs to be corrected.");
    }

    public void checkShowFiles() {
        if (isRecheckingShowFile) return;
        log.info("Checking for new show files...");
        isRecheckingShowFile = true;
        ClassHandler.directoryController().checkDirectories(true);
        Map<Integer, Set<FindShows.Show>> directoryShows = new HashMap<>();
        checkInterval++;
        currentlyCheckingDirectories = true;
        Set<Integer> activeDirectories = ClassHandler.directoryController().getActiveDirectories(true, !(checkInterval >= Variables.checkInterval));
        ClassHandler.directoryController().getActiveDirectories(true, !(checkInterval >= Variables.checkInterval));
        currentlyCheckingDirectories = false;
        long timer = GenericMethods.getTimeMilliSeconds();
        if (activeDirectories.isEmpty()) {
            log.info("No active directories...Skipping check.");
            addRecheckShowFilePercentage(100.0);
        } else {
            log.info("Now mapping out all directories...");
            double percentagePerDirectory = 25.0 / activeDirectories.size();
            Set<Thread> directoriesThreads = new HashSet<>();
            activeDirectories.forEach(directoryID -> directoriesThreads.add(new Thread(() -> {
                directoryShows.put(directoryID, new FindShows().findShows(ClassHandler.directoryController().getDirectoryFromID(directoryID)));
                addRecheckShowFilePercentage(percentagePerDirectory);
            })));
            startAndWaitForThreads(directoriesThreads);
            if (DeveloperStuff.devMode)
                log.info("Mapping out directories took \"" + GenericMethods.timeTakenMilli(timer) + "\" milliseconds.");
            log.info("Finished mapping out directories.");
            final int[] numberOfShows = {0};
            directoryShows.forEach((integer, shows) -> numberOfShows[0] += shows.size());
            double percentageInterval = 70.0 / ((numberOfShows[0] != 0) ? numberOfShows[0] : 1);
            final boolean[] changesFound = {false};
            Set<Integer> allShows = new HashSet<>();
            Set<Thread> checkingThreads = new HashSet<>();
            directoryShows.forEach((directoryID, shows) ->
                    checkingThreads.add(new Thread(() -> shows.forEach(show -> {
                        log.fine("Checking \"" + show.getShow() + "\" for changes.");
                        int[] showAddedInfo = ClassHandler.showInfoController().addShow(show.getShow());
                        int showID = showAddedInfo[0];
                        boolean showAdded = showAddedInfo[1] == 1;
                        if (showAdded) printNewShowInfo(show.getShow(), -2, -2, false);
                        show.getSeasons().forEach(season -> {
                            boolean doesNotExist = showAdded || !ClassHandler.showInfoController().doesSeasonExist(showID, season.getSeason());
                            if (doesNotExist) {
                                printNewShowInfo(show.getShow(), season.getSeason(), -2, false);
                                ClassHandler.showInfoController().addSeason(showID, season.getSeason());
                            }
                            season.getEpisodes().forEach(episode -> {
                                if (episode.getEpisode() != -2) {
                                    if (doesNotExist || !ClassHandler.showInfoController().doesEpisodeExist(showID, season.getSeason(), episode.getEpisode())) {
                                        printNewShowInfo(show.getShow(), season.getSeason(), episode.getEpisode(), false);
                                        ClassHandler.showInfoController().addEpisodeFile(ClassHandler.showInfoController().addEpisode(showID, season.getSeason(), episode.getEpisode(), episode.isDoubleEpisode()), directoryID, episode.getEpisodeFilename());
                                        if (!changesFound[0]) changesFound[0] = true;
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
                                            printNewShowInfo(show.getShow(), season.getSeason(), episode.getEpisode(), true);
                                            ClassHandler.showInfoController().addEpisodeFile(episodeID, directoryID, episode.getEpisodeFilename());
                                            if (!changesFound[0]) changesFound[0] = true;
                                        }
                                    }
                                    if (episode.isDoubleEpisode()) {
                                        if (doesNotExist || !ClassHandler.showInfoController().doesEpisodeExist(showID, season.getSeason(), episode.getEpisode2())) {
                                            printNewShowInfo(show.getShow(), season.getSeason(), episode.getEpisode2(), false);
                                            ClassHandler.showInfoController().addEpisodeFile(ClassHandler.showInfoController().addEpisode(showID, season.getSeason(), episode.getEpisode2(), true), directoryID, episode.getEpisodeFilename());
                                            if (!changesFound[0]) changesFound[0] = true;
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
                                                printNewShowInfo(show.getShow(), season.getSeason(), episode.getEpisode2(), true);
                                                ClassHandler.showInfoController().addEpisodeFile(episodeID, directoryID, episode.getEpisodeFilename());
                                                if (!changesFound[0]) changesFound[0] = true;
                                            }
                                        }
                                    }
                                }
                            });
                            allShows.add(showID);
                        });
                        ClassHandler.userInfoController().addShowForUsers(showID);
                        addRecheckShowFilePercentage(percentageInterval);
                    }))));
            startAndWaitForThreads(checkingThreads);
            ArrayList<Integer> shows = ClassHandler.showInfoController().getShows();
            shows.removeIf(allShows::contains);
            if (shows.isEmpty()) addRecheckShowFilePercentage(5.0);
            else {
                double removedShowsPercentageInterval = 5.0 / shows.size();
                shows.forEach(showID -> {
                    log.info("\"" + ClassHandler.showInfoController().getShowNameFromShowID(showID) + "\" was no longer found, and is now ignored.");
                    ClassHandler.showInfoController().removeShow(showID);
                    ClassHandler.userInfoController().setIgnoredStatusAllUsers(showID, true);
                    addRecheckShowFilePercentage(removedShowsPercentageInterval);
                });
            }
            if (recheckShowFilePercentage != 100.0) recheckShowFilePercentage = 100.0;
            if (changesFound[0]) log.info("New files were found.");
        }
        if (checkInterval == Variables.checkInterval) checkInterval = 0;
        isRecheckingShowFile = false;
        recheckShowFilePercentage = 0.0;
        if (DeveloperStuff.devMode)
            log.info("Checking for new show files took \"" + GenericMethods.timeTakenMilli(timer) + "\" milliseconds.");
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

    private void startAndWaitForThreads(Set<Thread> threadsToWaitFor) {
        threadsToWaitFor.forEach(Thread::start);
        boolean threadRunning;
        do {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
            threadRunning = false;
            for (Thread thread : threadsToWaitFor) {
                if (thread.isAlive()) {
                    threadRunning = true;
                    break;
                }
            }
        } while (threadRunning);
    }

    private void printNewShowInfo(String show, int season, int episode, boolean newFile) {
        if (newFile) {
            if (season != -2 && episode != -2)
                log.info("Adding new file for \"" + show + "\" - Season: \"" + season + "\" - Episode: \"" + episode + "\".");
            else log.warning("Missing information to print about a new file that was added.");
        } else
            log.info("\"" + show + ((season != -2) ? "\" - Season: \"" + season + "\"" : "") + ((season != -2 && episode != -2) ? " - Episode: \"" + episode : "") + "\" was found and added.");
    }
}
