package com.maddogten.mtrack.io;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.information.DirectoryController;
import com.maddogten.mtrack.information.ProgramSettingsController;
import com.maddogten.mtrack.information.ShowInfoController;
import com.maddogten.mtrack.information.UserInfoController;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.Episode;
import com.maddogten.mtrack.information.show.Season;
import com.maddogten.mtrack.information.show.Show;
import com.maddogten.mtrack.util.*;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
      CheckShowFiles handles checking for new shows, if any are found,
      saves the new file. It sends the newly generated shows file to
      FindChangedShows.
 */

public class CheckShowFiles {
    private final Logger log = Logger.getLogger(CheckShowFiles.class.getName());
    private final ProgramSettingsController programSettingsController;
    private final ShowInfoController showInfoController;
    private final UserInfoController userInfoController;
    private final DirectoryController directoryController;
    private final FindShows findShows = new FindShows();
    // recheckShowFileRunning - If the method is currently running, then this will be true and stop other non-forced checks from running.
    // keepRunning - This will be true if the program is doing a normal run currently, or false if not running, or currently being forced ran. If recheckShowFileRunning is true, and this is false, it will ignore further force run attempts.
    // currentlyCheckingDirectories - This is true when it is checking if the folders exist / is waiting for a response. Otherwise it is false. This is to show a notification when it is running. Used by the Controller.
    // stopRunning - True either when the program is shutting down, or a forceRun is stopping a normal run. When it is true, recheckShowFile stops what it is doing and discards everything.
    private boolean recheckShowFileRunning = false, keepRunning = false, currentlyCheckingDirectories = false, stopRunning = false;
    // emptyShows - This is populated with folders that the checking has found are empty, and combined with runNumber, prevents these from unnecessarily being checked.
    private ArrayList<String> emptyShows = new ArrayList<>();
    // runNumber - 1 is added to this each time hasShowsChanged is ran. This is used so that the above emptyShows are only checked when the program is first started, or every 5 runs after that.
    private int runNumber = 0;
    // recheckShowFilePercentage - Is increased as recheckShowFile is running. The percentage is currently split between each directory, and then that is further split by each active show (Plus 2 shows worth is reserved when checking for new shows). Used by the controller.
    private double recheckShowFilePercentage = 0;

    @SuppressWarnings("SameParameterValue")
    public CheckShowFiles(ProgramSettingsController programSettingsController, ShowInfoController showInfoController, UserInfoController userInfoController, DirectoryController directoryController) {
        this.programSettingsController = programSettingsController;
        this.showInfoController = showInfoController;
        this.userInfoController = userInfoController;
        this.directoryController = directoryController;
    }

    public ArrayList<String> getEmptyShows() {
        return emptyShows;
    }

    public boolean getRecheckShowFileRunning() {
        return recheckShowFileRunning;
    }

    public double getRecheckShowFilePercentage() {
        return recheckShowFilePercentage / 100;
    }

    public boolean isCurrentlyCheckingDirectories() {
        return currentlyCheckingDirectories;
    }

    // This fully rechecks for any new / removed shows, seasons, and episodes from all directories. If directory is unresponsive, it skips it.
    public void recheckShowFile(boolean forceRun) {
        if (!recheckShowFileRunning || (forceRun && keepRunning)) {
            // hasChanged - If anything is found differently, this will be set to true. Used at the end to determine if it should reload the showInfoController showsFile and scan for changes.
            boolean[] hasChanged = {false};
            ArrayList<String> updatedShows = new ArrayList<>();
            // timer - Used purely for log purposes to see how long the run takes.
            int timer = GenericMethods.getTimeSeconds();
            log.info("Started rechecking shows...");
            keepRunning = !forceRun;
            FileManager fileManager = new FileManager();
            // Need to set the FindChangedShows showsFile to the current, unchanged one to later find the changes.
            FindChangedShows findChangedShows = new FindChangedShows(showInfoController.getShowsFile(), userInfoController);
            if (forceRun) runNumber = 0;
            else runNumber++;
            while (recheckShowFileRunning) { // Just in case it had interrupted a run and it hasn't full finished stopping.
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    GenericMethods.printStackTrace(log, e, this.getClass());
                }
            }
            if (!recheckShowFileRunning) recheckShowFileRunning = true;
            final double[] percentagePerDirectory = {100};
            if (!directoryController.getDirectories().isEmpty())
                percentagePerDirectory[0] = 100 / directoryController.getDirectories().size();
            // Just so the user knows when it is a directory that is delaying the search, and not the program hanging.
            currentlyCheckingDirectories = true;
            ArrayList<Directory> activeDirectories = directoryController.getActiveDirectories(!forceRun);
            currentlyCheckingDirectories = false;
            if (Main.programFullyRunning) {
                activeDirectories.forEach(aDirectory -> {
                    boolean checkAllShows = forceRun || runNumber % Variables.checkAllNonIgnoredShowsInterval == 0;
                    double percentageSplit = percentagePerDirectory[0] / 6, percentagePerShow;
                    if (checkAllShows)
                        percentagePerShow = (percentageSplit * 4) / userInfoController.getAllNonIgnoredShows().size();
                    else percentagePerShow = (percentageSplit * 4) / userInfoController.getActiveShows().size();
                    log.info("Directory currently being rechecked: \"" + aDirectory.getDirectory() + "\".");
                    ArrayList<String> removedShows = new ArrayList<>();
                    // Check if any shows were removed.
                    userInfoController.getAllNonIgnoredShows().stream().filter(aShow ->
                            (aDirectory.getShows().containsKey(aShow) && !fileManager.checkFolderExistsAndReadable(new File(aDirectory.getDirectory() + Strings.FileSeparator + aShow)))).forEach(aShow -> {
                        aDirectory.getShows().remove(aShow);
                        removedShows.add(aShow);
                    });
                    if (handleRemovedShows(removedShows, aDirectory, aDirectory.getDirectory(), aDirectory.getIndex()))
                        hasChanged[0] = true;
                    ArrayList<String> showsToBeChecked;
                    if (checkAllShows) showsToBeChecked = userInfoController.getAllNonIgnoredShows();
                    else showsToBeChecked = userInfoController.getActiveShows();
                    if (showsToBeChecked.isEmpty()) recheckShowFilePercentage += percentagePerDirectory[0];
                    else
                        updatedShows.addAll(checkShows(aDirectory, fileManager, aDirectory.getDirectory(), percentagePerShow, hasChanged, aDirectory.getShows(), showsToBeChecked, forceRun));
                    if ((Main.programFullyRunning && forceRun) || !stopRunning) {
                        Map<String, Show> changedShows = hasShowsChanged(aDirectory.getDirectory(), aDirectory.getShows(), percentageSplit, forceRun);
                        if (changedShows.isEmpty()) recheckShowFilePercentage += percentageSplit;
                        else {
                            log.info("Current Shows have changed.");
                            hasChanged[0] = true;
                            ArrayList<String> ignoredShows = userInfoController.getIgnoredShows();
                            double percentagePer = percentageSplit / (changedShows.size() * 2);
                            changedShows.keySet().forEach(aNewShow -> {
                                aDirectory.getShows().put(aNewShow, changedShows.get(aNewShow));
                                if (ignoredShows.contains(aNewShow))
                                    userInfoController.setIgnoredStatus(aNewShow, false);
                                recheckShowFilePercentage += percentagePer;
                            });
                            directoryController.saveDirectory(aDirectory, false);
                            showInfoController.loadShowsFile();
                            changedShows.keySet().forEach(aShow -> {
                                userInfoController.addNewShow(aShow);
                                Controller.updateShowField(aShow, true);
                                recheckShowFilePercentage += percentagePer;
                            });
                            programSettingsController.setMainDirectoryVersion(programSettingsController.getSettingsFile().getMainDirectoryVersion() + 1);
                        }
                    }
                });

                if ((Main.programFullyRunning && forceRun) || !stopRunning) {
                    if (recheckShowFilePercentage > 99 && recheckShowFilePercentage <= 100)
                        log.info("recheckShowFilePercentage was within proper range.");
                    else
                        log.warning("recheckShowFilePercentage was: \"" + (int) recheckShowFilePercentage + "\" and not 100, Must be an error in the calculation, Please correct.");
                    recheckShowFilePercentage = 100;
                    if (hasChanged[0] && Main.programFullyRunning) {
                        showInfoController.loadShowsFile();
                        if (!updatedShows.isEmpty())
                            updatedShows.forEach(aShow -> Controller.updateShowField(aShow, true));
                        findChangedShows.findShowFileDifferences(showInfoController.getShowsFile());
                        log.info("Some shows have been updated.");
                        log.info("Finished Rechecking Shows! - It took " + GenericMethods.timeTakenSeconds(timer) + " seconds.");
                    } else if (Main.programFullyRunning) {
                        log.info("All shows were the same.");
                        log.info("Finished Rechecking Shows! - It took " + GenericMethods.timeTakenSeconds(timer) + " seconds.");
                    }
                }
                if (stopRunning) stopRunning = false;
            }
            recheckShowFileRunning = false;
            recheckShowFilePercentage = 0;
        }
    }

    private ArrayList<String> checkShows(Directory directory, FileManager fileManager, File folderLocation, double percentagePerShow, boolean[] hasChanged, Map<String, Show> showsMap, ArrayList<String> showsToBeChecked, boolean forceRun) {
        ArrayList<String> updatedShows = new ArrayList<>();
        for (String aShow : showsToBeChecked) {
            if (showsMap.containsKey(aShow)) {
                log.info("Currently rechecking " + aShow);
                int currentSeason = userInfoController.getCurrentSeason(aShow);
                Set<Integer> seasons;
                if (forceRun || runNumber % Variables.checkSeasonsLowerThanCurrentInterval == 0)
                    seasons = showsMap.get(aShow).getSeasons().keySet();
                else seasons = removeLowerSeasons(currentSeason, showsMap.get(aShow).getSeasons().keySet());
                seasons.forEach(aSeason -> {
                    if (fileManager.checkFolderExistsAndReadable(new File(String.valueOf(folderLocation) + Strings.FileSeparator + aShow + "/Season " + aSeason + Strings.FileSeparator))) {
                        ArrayList<Integer> changedEpisodes = hasEpisodesChanged(aShow, aSeason, folderLocation, showsMap);
                        if (!changedEpisodes.isEmpty()) {
                            log.info("Episode changes detected for " + aShow + ", updating files...");
                            hasChanged[0] = true;
                            updatedShows.add(aShow);
                            checkForNewOrRemovedEpisodes(folderLocation, aShow, aSeason, directory);
                        }
                    } else
                        log.info("Couldn't find folder for " + aShow + " season " + aShow + " so it was skipped in rechecking.");
                });
                Set<Integer> changedSeasons;
                if (forceRun || runNumber % Variables.checkSeasonsLowerThanCurrentInterval == 0)
                    changedSeasons = hasSeasonsChanged(aShow, folderLocation, showsMap);
                else
                    changedSeasons = removeLowerSeasons(currentSeason, hasSeasonsChanged(aShow, folderLocation, showsMap));
                if (!changedSeasons.isEmpty()) {
                    log.info("Season changes detected for " + aShow + ", updating files...");
                    hasChanged[0] = true;
                    updatedShows.add(aShow);
                    checkForNewOrRemovedSeasons(folderLocation, aShow, changedSeasons, directory);
                }
                if (!Main.programFullyRunning || (!forceRun && !keepRunning)) {
                    stopRunning = true;
                    break;
                }
            }
            recheckShowFilePercentage += percentagePerShow;
        }
        return updatedShows;
    }

    // This compares the given showsFile Map episodes for any new episodes found in the shows folder. Adds anything new it finds to a ArrayList and returns.
    private ArrayList<Integer> hasEpisodesChanged(String aShow, Integer aSeason, File folderLocation, Map<String, Show> showsFile) {
        Set<Integer> oldEpisodeList = showsFile.get(aShow).getSeason(aSeason).getEpisodes().keySet();
        ArrayList<String> newEpisodesList = new FindShows().findEpisodes(folderLocation, aShow, aSeason);
        ArrayList<Integer> newEpisodesListFixed = new ArrayList<>();
        if ((oldEpisodeList.isEmpty()) && newEpisodesList.isEmpty()) return new ArrayList<>();
        newEpisodesList.forEach(aNewEpisode -> {
            int[] EpisodeInfo = showInfoController.getEpisodeInfo(aNewEpisode);
            newEpisodesListFixed.add(EpisodeInfo[0]);
            if (EpisodeInfo.length == 2) newEpisodesListFixed.add(EpisodeInfo[1]);
        });
        ArrayList<Integer> changedEpisodes = new ArrayList<>();
        changedEpisodes.addAll(oldEpisodeList.stream().filter(aOldEpisode -> !newEpisodesListFixed.contains(aOldEpisode)).collect(Collectors.toList()));
        changedEpisodes.addAll(newEpisodesListFixed.stream().filter(newEpisode -> !oldEpisodeList.contains(newEpisode)).collect(Collectors.toList()));
        return changedEpisodes;
    }

    // Same as above, but instead scans the shows folder for new seasons.
    private Set<Integer> hasSeasonsChanged(String aShow, File folderLocation, Map<String, Show> showsFile) {
        Set<Integer> oldSeasons = showsFile.get(aShow).getSeasons().keySet();
        ArrayList<Integer> newSeasons = new FindShows().findSeasons(folderLocation, aShow);
        Iterator<Integer> newSeasonsIterator = newSeasons.iterator();
        while (newSeasonsIterator.hasNext()) {
            if (isSeasonEmpty(aShow, newSeasonsIterator.next(), folderLocation)) newSeasonsIterator.remove();
        }
        Set<Integer> changedSeasons = new HashSet<>();
        changedSeasons.addAll(oldSeasons.stream().filter(aOldSeason -> !newSeasons.contains(aOldSeason)).collect(Collectors.toList()));
        changedSeasons.addAll(newSeasons.stream().filter(aNewSeason -> !oldSeasons.contains(aNewSeason)).collect(Collectors.toList()));
        return changedSeasons;
    }

    private Map<Integer, Season> putSeasonInMap(String aShow, File folderLocation) {
        Map<Integer, Season> seasonEpisode = new HashMap<>();
        findShows.findSeasons(folderLocation, aShow).forEach(aSeason -> seasonEpisode.put(aSeason, new Season(aSeason, putEpisodesInMap(aShow, folderLocation, aSeason))));
        return seasonEpisode;
    }

    private Map<Integer, Episode> putEpisodesInMap(String aShow, File folderLocation, int aSeason) {
        Map<Integer, Episode> episodes = new HashMap<>();
        ArrayList<String> episodesFull = findShows.findEpisodes(folderLocation, aShow, aSeason);
        if (!episodesFull.isEmpty()) {
            episodesFull.forEach(aEpisode -> {
                int[] episode = showInfoController.getEpisodeInfo(aEpisode);
                episodes.put(episode[0], new Episode(episode[0], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, false));
                if (episode.length == 2)
                    episodes.put(episode[1], new Episode(episode[1], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, true));
            });
        }
        return episodes;
    }

    // Scans the folder for any changes between it and the given showsFile, and for new shows it finds (that aren't empty) it gets all seasons & episodes for it, adds them to a Map and returns that.
    private Map<String, Show> hasShowsChanged(File folderLocation, Map<String, Show> showsFile, double percentIncrease, boolean forceRun) {
        Map<String, Show> newShows = new HashMap<>();
        Set<String> oldShows = showsFile.keySet();
        ArrayList<String> ignoredShows = userInfoController.getIgnoredShows();
        FindShows findShows = new FindShows();
        ArrayList<String> showsToCheck = findShows.findShows(folderLocation);
        if (showsToCheck.isEmpty()) recheckShowFilePercentage += percentIncrease;
        else {
            double percentagePer = percentIncrease / showsToCheck.size();
            showsToCheck.forEach(aShow -> {
                if (forceRun || runNumber % Variables.recheckPreviouslyFoundEmptyShowsInterval == 0)
                    emptyShows = new ArrayList<>();
                if (Main.programFullyRunning && !oldShows.contains(aShow) && !emptyShows.contains(aShow) || ignoredShows.contains(aShow) && !emptyShows.contains(aShow)) {
                    log.info("Currently checking if new & valid: " + aShow);
                    Map<Integer, Season> seasonEpisode = putSeasonInMap(aShow, folderLocation);
                    if (seasonEpisode.keySet().isEmpty()) emptyShows.add(aShow);
                    else newShows.put(aShow, new Show(aShow, seasonEpisode));
                }
                recheckShowFilePercentage += percentagePer;
            });
        }
        return newShows;
    }

    // Checks if the season folder episodes of the show, and if none are found, returns true. Returns false for first episode found.
    private boolean isSeasonEmpty(String aShow, Integer aSeason, File folderLocation) {
        ArrayList<String> episodesFull = new FindShows().findEpisodes(folderLocation, aShow, aSeason);
        final boolean[] answer = {true};
        if (!episodesFull.isEmpty()) {
            episodesFull.forEach(aEpisode -> {
                if (showInfoController.getEpisodeInfo(aEpisode) != null) answer[0] = false;
            });
        }
        return answer[0];
    }

    private void checkForNewOrRemovedSeasons(File folderLocation, String aShow, Set<Integer> changedSeasons, Directory directory) {
        Show seasonEpisode = directory.getShows().get(aShow);
        changedSeasons.forEach(aSeason -> {
            Map<Integer, Episode> episodeNum = putEpisodesInMap(aShow, folderLocation, aSeason);
            if (episodeNum.isEmpty()) seasonEpisode.removeSeason(aSeason);
            else if (seasonEpisode.getSeasons().containsKey(aSeason))
                seasonEpisode.addOrReplaceSeason(aSeason, new Season(aSeason, episodeNum));
        });
        directory.getShows().replace(aShow, seasonEpisode);
        directoryController.saveDirectory(directory, true);
    }

    private void checkForNewOrRemovedEpisodes(File folderLocation, String aShow, Integer aSeason, Directory directory) {
        Show seasonEpisode = directory.getShows().get(aShow);
        seasonEpisode.addOrReplaceSeason(aSeason, new Season(aSeason, putEpisodesInMap(aShow, folderLocation, aSeason)));
        directory.getShows().replace(aShow, seasonEpisode);
        directoryController.saveDirectory(directory, true);
    }

    private Set<Integer> removeLowerSeasons(int baseInt, Set<Integer> listToCheck) {
        Set<Integer> result = new HashSet<>();
        listToCheck.forEach(integer -> {
            if (integer >= baseInt) result.add(integer);
            else log.finest("Season " + integer + " was skipped in rechecking as the current user is past it.");
        });
        return result;
    }

    private boolean handleRemovedShows(ArrayList<String> removedShows, Directory directory, File folder, int index) {
        boolean wereShowsRemoved = false;
        if (!removedShows.isEmpty()) {
            wereShowsRemoved = true;
            directoryController.saveDirectory(directory, true);
            removedShows.forEach(aShow -> {
                log.info(aShow + " is no longer found in \"" + folder + "\".");
                boolean doesShowExistElsewhere = showInfoController.doesShowExistElsewhere(aShow, directoryController.getDirectories(index));
                if (!doesShowExistElsewhere) userInfoController.setIgnoredStatus(aShow, true);
                Controller.updateShowField(aShow, doesShowExistElsewhere);
            });
        }
        return wereShowsRemoved;
    }
}
