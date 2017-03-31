package com.maddogten.mtrack.io;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.Episode;
import com.maddogten.mtrack.information.show.Season;
import com.maddogten.mtrack.information.show.Show;
import com.maddogten.mtrack.util.*;
import javafx.concurrent.Task;

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
    private final FindShows findShows = new FindShows();
    // emptyShows - This is populated with folders that the checking has found are empty, and combined with runNumber, prevents these from unnecessarily being checked.
    private final ArrayList<String> emptyShows = new ArrayList<>();
    // isRecheckingShowFile - If the method is currently running, then this will be true and stop other non-forced checks from running.
    // keepRunning - This will be true if the program is doing a normal run currently, or false if not running, or currently being forced ran. If isRecheckingShowFile is true, and this is false, it will ignore further force run attempts.
    // currentlyCheckingDirectories - This is true when it is checking if the folders exist / is waiting for a response. Otherwise it is false. This is to show a notification when it is running. Used by the Controller.
    // stopRunning - True either when the program is shutting down, or a forceRun is stopping a normal run. When it is true, recheckShowFile stops what it is doing and discards everything.
    private boolean isRecheckingShowFile = false, keepRunning = false, currentlyCheckingDirectories = false, stopRunning = false;
    // runNumber - 1 is added to this each time hasShowsChanged is ran. This is used so that the above emptyShows are only checked when the program is first started, or every 5 runs after that.
    private int runNumber = 0;
    // recheckShowFilePercentage - Is increased as recheckShowFile is running. The percentage is currently split between each directory, and then that is further split by each active show (Plus 2 shows worth is reserved when checking for new shows). Used by the controller.
    private double recheckShowFilePercentage = 0;

    public ArrayList<String> getEmptyShows() {
        return emptyShows;
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

    // This fully rechecks for any new / removed shows, seasons, and episodes from all directories. If directory is unresponsive, it skips it.
    public void recheckShowFile(final boolean forceRun) {
        if (!isRecheckingShowFile || (forceRun && keepRunning)) {
            // hasChanged - If anything is found differently, this will be set to true. Used at the end to determine if it should reload the ShowInfoController showsFile and scan for changes.
            boolean[] hasChanged = {false};
            ArrayList<String> updatedShows = new ArrayList<>();
            // timer - Used purely for log purposes to see how long the run takes.
            int timer = GenericMethods.getTimeSeconds();
            log.info("Started rechecking shows...");
            keepRunning = !forceRun;
            FileManager fileManager = new FileManager();
            // Need to set the FindChangedShows showsFile to the current, unchanged one to later find the changes.
            FindChangedShows findChangedShows = new FindChangedShows(ClassHandler.showInfoController().getShowsFile(), ClassHandler.userInfoController());
            if (forceRun) runNumber = 0;
            else runNumber++;
            while (isRecheckingShowFile) { // Just in case it had interrupted a run and it hasn't fully finished stopping.
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    GenericMethods.printStackTrace(log, e, this.getClass());
                }
            }
            if (!isRecheckingShowFile) isRecheckingShowFile = true;
            final double[] percentagePerDirectory = {100};
            // Just so the user knows when it is a directory that is delaying the search, and not the program hanging.
            currentlyCheckingDirectories = true;
            if (ClassHandler.directoryController().findDirectories(false, !forceRun, false).isEmpty())
                recheckShowFilePercentage = percentagePerDirectory[0];
            else
                percentagePerDirectory[0] = percentagePerDirectory[0] / ClassHandler.directoryController().findDirectories(false, true, false).size();
            currentlyCheckingDirectories = false;
            ArrayList<Directory> activeDirectories = ClassHandler.directoryController().findDirectories(false, true, true);

            if (Main.programFullyRunning) {
                Set<Thread> threads = new HashSet<>(activeDirectories.size());
                activeDirectories.forEach(aDirectory -> {
                    Thread thread = new Thread(new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            boolean checkAllShows = forceRun || runNumber % Variables.checkAllNonIgnoredShowsInterval == 0;
                            double percentageSplit = percentagePerDirectory[0] / 6, percentagePerShow;
                            int numberOfShows;
                            if (checkAllShows)
                                numberOfShows = ClassHandler.userInfoController().getAllNonIgnoredShows().size();
                            else numberOfShows = ClassHandler.userInfoController().getActiveShows().size();
                            if (numberOfShows == 0) numberOfShows = 1;
                            percentagePerShow = (percentageSplit * 4) / numberOfShows;
                            log.info("Directory currently being rechecked: \"" + aDirectory.getDirectory() + "\".");
                            ArrayList<String> removedShows = new ArrayList<>();
                            // Check if any shows were removed.
                            ClassHandler.userInfoController().getAllNonIgnoredShows().stream().filter(aShow ->
                                    (aDirectory.containsShow(aShow) && !fileManager.checkFolderExistsAndReadable(new File(aDirectory.getDirectory() + Strings.FileSeparator + aShow)))).forEach(aShow -> {
                                aDirectory.getShows().remove(aShow);
                                removedShows.add(aShow);
                            });
                            if (handleRemovedShows(removedShows, aDirectory, aDirectory.getDirectory(), aDirectory.getDirectoryID()))
                                hasChanged[0] = true;
                            ArrayList<String> showsToBeChecked;
                            if (checkAllShows)
                                showsToBeChecked = ClassHandler.userInfoController().getAllNonIgnoredShows();
                            else showsToBeChecked = ClassHandler.userInfoController().getActiveShows();
                            if (showsToBeChecked.isEmpty()) recheckShowFilePercentage += percentagePerShow;
                            else
                                updatedShows.addAll(checkShows(aDirectory, fileManager, percentagePerShow, hasChanged, showsToBeChecked, forceRun));
                            if ((Main.programFullyRunning && forceRun) || !stopRunning) {
                                Map<String, Show> changedShows = hasShowsChanged(aDirectory.getDirectory(), aDirectory.getShows(), percentageSplit, forceRun);
                                if (changedShows.isEmpty()) recheckShowFilePercentage += percentageSplit;
                                else {
                                    log.info("Current Shows have changed.");
                                    hasChanged[0] = true;
                                    ArrayList<String> ignoredShows = ClassHandler.userInfoController().getIgnoredShows();
                                    double percentagePer = percentageSplit / (changedShows.size() * 2);
                                    changedShows.keySet().forEach(aNewShow -> {
                                        if (changedShows.get(aNewShow).getSeasons() != null)
                                            changedShows.get(aNewShow).getSeasons().forEach((seasonInt, season) -> {
                                                if (!season.getEpisodes().isEmpty()) {
                                                    int currentHighestFoundEpisode = ClassHandler.showInfoController().findHighestInt(season.getEpisodes().keySet());
                                                    if (currentHighestFoundEpisode > season.getHighestFoundEpisode())
                                                        season.setHighestFoundEpisode(currentHighestFoundEpisode);
                                                }
                                            });
                                        aDirectory.getShows().put(aNewShow, changedShows.get(aNewShow));
                                        if (ignoredShows.contains(aNewShow))
                                            ClassHandler.userInfoController().setIgnoredStatus(aNewShow, false);
                                        recheckShowFilePercentage += percentagePer;
                                    });
                                    ClassHandler.directoryController().saveDirectory(aDirectory, false);
                                    ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true, true));
                                    changedShows.keySet().forEach(aNewShow -> {
                                        ClassHandler.userInfoController().addNewShow(aNewShow);
                                        Controller.updateShowField(aNewShow, true);
                                        recheckShowFilePercentage += percentagePer;
                                    });
                                    ClassHandler.programSettingsController().setMainDirectoryVersion(ClassHandler.programSettingsController().getSettingsFile().getMainDirectoryVersion() + 1);
                                }
                            }
                            return null;
                        }
                    });
                    thread.start();
                    threads.add(thread);
                });

                while (!threads.isEmpty()) {
                    Iterator<Thread> threadIterator = threads.iterator();
                    while (threadIterator.hasNext()) if (!threadIterator.next().isAlive()) threadIterator.remove();
                }

                if ((Main.programFullyRunning && forceRun) || !stopRunning) {
                    recheckShowFilePercentage = Math.ceil(recheckShowFilePercentage) == 100 || Math.floor(recheckShowFilePercentage) == 100 ? 100 : recheckShowFilePercentage;
                    if (recheckShowFilePercentage == 100)
                        log.finer("recheckShowFilePercentage was within proper range.");
                    else
                        log.warning("recheckShowFilePercentage was: \"" + recheckShowFilePercentage + "\" and not 100, Must be an error in the calculation, Please correct.");
                    recheckShowFilePercentage = 100;
                    if (hasChanged[0] && Main.programFullyRunning) {
                        ClassHandler.showInfoController().loadShowsFile(ClassHandler.directoryController().findDirectories(false, true, true));
                        if (!updatedShows.isEmpty())
                            updatedShows.forEach(aShow -> Controller.updateShowField(aShow, true));
                        findChangedShows.findShowFileDifferences(ClassHandler.showInfoController().getShowsFile());
                        log.info("Some shows have been updated.");
                        log.info("Finished Rechecking Shows! - It took " + GenericMethods.timeTakenSeconds(timer) + " seconds.");
                    } else if (Main.programFullyRunning) {
                        log.info("All shows were the same.");
                        log.info("Finished Rechecking Shows! - It took " + GenericMethods.timeTakenSeconds(timer) + " seconds.");
                    }
                }
                if (stopRunning) stopRunning = false;
            }
            isRecheckingShowFile = false;
            recheckShowFilePercentage = 0;
        }
    }

    private ArrayList<String> checkShows(final Directory directory, final FileManager fileManager, final double percentagePerShow, final boolean[] hasChanged, ArrayList<String> showsToBeChecked, final boolean forceRun) {
        ArrayList<String> updatedShows = new ArrayList<>();
        for (String aShow : showsToBeChecked) {
            if (directory.getShows().containsKey(aShow)) {
                log.fine("Currently rechecking " + aShow);
                int currentSeason = ClassHandler.userInfoController().getCurrentSeason(aShow);
                Set<Integer> seasons;
                if (forceRun || runNumber % Variables.checkSeasonsLowerThanCurrentInterval == 0)
                    seasons = directory.getShows().get(aShow).getSeasons().keySet();
                else seasons = removeLowerSeasons(currentSeason, directory.getShows().get(aShow).getSeasons().keySet());
                seasons.forEach(aSeason -> {
                    String seasonFolderName = GenericMethods.getSeasonFolderName(directory.getDirectory(), aShow, aSeason);
                    if (fileManager.checkFolderExistsAndReadable(new File(directory.getDirectory() + Strings.FileSeparator + aShow + Strings.FileSeparator + seasonFolderName + Strings.FileSeparator))) {
                        if (hasEpisodesChanged(aShow, aSeason, directory.getDirectory(), directory.getShows())) {
                            log.fine("Episode changes detected for " + aShow + ", updating files...");
                            hasChanged[0] = true;
                            updatedShows.add(aShow);
                            checkForNewOrRemovedEpisodes(directory.getDirectory(), aShow, aSeason, directory);
                        }
                    } else
                        log.fine("Couldn't find folder for " + aShow + " Season " + aSeason + " (" + directory.getDirectory() + Strings.FileSeparator + aShow + Strings.FileSeparator + seasonFolderName + Strings.FileSeparator + ") " + " so it was skipped in rechecking.");
                });
                Set<Integer> changedSeasons;
                if (forceRun || runNumber % Variables.checkSeasonsLowerThanCurrentInterval == 0)
                    changedSeasons = hasSeasonsChanged(aShow, directory.getDirectory(), directory.getShows());
                else
                    changedSeasons = removeLowerSeasons(currentSeason, hasSeasonsChanged(aShow, directory.getDirectory(), directory.getShows()));
                if (!changedSeasons.isEmpty()) {
                    log.fine("Season changes detected for " + aShow + ", updating files...");
                    hasChanged[0] = true;
                    updatedShows.add(aShow);
                    checkForNewOrRemovedSeasons(directory.getDirectory(), aShow, changedSeasons, directory);
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
    private boolean hasEpisodesChanged(final String aShow, final Integer aSeason, final File folderLocation, final Map<String, Show> showsFile) {
        Set<Integer> oldEpisodeList = showsFile.get(aShow).getSeason(aSeason).getEpisodes().keySet();
        ArrayList<String> newEpisodesList = new FindShows().findEpisodes(folderLocation, aShow, aSeason);
        ArrayList<Integer> newEpisodesListFixed = new ArrayList<>();
        if ((oldEpisodeList.isEmpty()) && newEpisodesList.isEmpty()) return false;
        newEpisodesList.forEach(aNewEpisode -> {
            int[] EpisodeInfo = ClassHandler.showInfoController().getEpisodeInfo(aNewEpisode);
            newEpisodesListFixed.add(EpisodeInfo[0]);
            if (EpisodeInfo[1] != -2) newEpisodesListFixed.add(EpisodeInfo[1]);
            if (oldEpisodeList.contains(EpisodeInfo[0])) {
                if (showsFile.get(aShow).getSeason(aSeason).getEpisode(EpisodeInfo[0]).getEpisodeBareFilename().hashCode() != aNewEpisode.hashCode()) {
                    log.finest("Hashes didn't match for \"" + aShow + "\' | Season \"" + aSeason + "\" Episode \"" + EpisodeInfo[0] + ((EpisodeInfo[1] != -2) ? " | " + EpisodeInfo[1] : "") + "\", Episode filename must have chanced, updating...");
                    oldEpisodeList.remove(EpisodeInfo[0]);
                    if (EpisodeInfo[1] != -2 && oldEpisodeList.contains(EpisodeInfo[1]))
                        oldEpisodeList.remove(EpisodeInfo[1]);
                }
            }
        });
        ArrayList<Integer> changedEpisodes = new ArrayList<>(oldEpisodeList.size() + newEpisodesListFixed.size());
        changedEpisodes.addAll(oldEpisodeList.stream().filter(aOldEpisode -> !newEpisodesListFixed.contains(aOldEpisode)).collect(Collectors.toList()));
        changedEpisodes.addAll(newEpisodesListFixed.stream().filter(newEpisode -> !oldEpisodeList.contains(newEpisode)).collect(Collectors.toList()));
        return !changedEpisodes.isEmpty();
    }

    // Same as above, but instead scans the shows folder for new seasons.
    private Set<Integer> hasSeasonsChanged(final String aShow, final File folderLocation, final Map<String, Show> showsFile) {
        Set<Integer> oldSeasons = showsFile.get(aShow).getSeasons().keySet();
        ArrayList<Integer> newSeasons = new FindShows().findSeasons(folderLocation, aShow);
        Iterator<Integer> newSeasonsIterator = newSeasons.iterator();
        while (newSeasonsIterator.hasNext())
            if (isSeasonEmpty(aShow, newSeasonsIterator.next(), folderLocation)) newSeasonsIterator.remove();
        Set<Integer> changedSeasons = new HashSet<>();
        changedSeasons.addAll(oldSeasons.stream().filter(aOldSeason -> !newSeasons.contains(aOldSeason)).collect(Collectors.toList()));
        changedSeasons.addAll(newSeasons.stream().filter(aNewSeason -> !oldSeasons.contains(aNewSeason)).collect(Collectors.toList()));
        return changedSeasons;
    }

    private Map<Integer, Season> putSeasonInMap(final String aShow, final File folderLocation) {
        Map<Integer, Season> seasonEpisode = new HashMap<>();
        findShows.findSeasons(folderLocation, aShow).forEach(aSeason -> seasonEpisode.put(aSeason, new Season(aSeason, putEpisodesInMap(aShow, folderLocation, aSeason))));
        return seasonEpisode;
    }

    private Map<Integer, Episode> putEpisodesInMap(final String aShow, final File folderLocation, final int aSeason) {
        Map<Integer, Episode> episodes = new HashMap<>();
        ArrayList<String> episodesFull = findShows.findEpisodes(folderLocation, aShow, aSeason);
        if (!episodesFull.isEmpty()) {
            String seasonFolderName = GenericMethods.getSeasonFolderName(folderLocation, aShow, aSeason);
            episodesFull.forEach(aEpisode -> {
                int[] episode = ClassHandler.showInfoController().getEpisodeInfo(aEpisode);
                episodes.put(episode[0], new Episode(episode[0], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + seasonFolderName + Strings.FileSeparator + aEpisode, false));
                if (episode[1] != -2)
                    episodes.put(episode[1], new Episode(episode[1], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + seasonFolderName + Strings.FileSeparator + aEpisode, true));
            });
        }
        return episodes;
    }

    // Scans the folder for any changes between it and the given showsFile, and for new shows it finds (that aren't empty) it gets all seasons & episodes for it, adds them to a Map and returns that.
    private Map<String, Show> hasShowsChanged(final File folderLocation, final Map<String, Show> showsFile, final double percentIncrease, final boolean forceRun) {
        Map<String, Show> newShows = new HashMap<>();
        Set<String> oldShows = showsFile.keySet();
        ArrayList<String> ignoredShows = ClassHandler.userInfoController().getIgnoredShows();
        FindShows findShows = new FindShows();
        ArrayList<String> showsToCheck = findShows.findShows(folderLocation);
        if (showsToCheck.isEmpty()) recheckShowFilePercentage += percentIncrease;
        else {
            double percentagePer = percentIncrease / showsToCheck.size();
            showsToCheck.forEach(aShow -> {
                if (forceRun || runNumber % Variables.recheckPreviouslyFoundEmptyShowsInterval == 0)
                    emptyShows.clear();
                if (Main.programFullyRunning && !oldShows.contains(aShow) && !emptyShows.contains(aShow) || ignoredShows.contains(aShow) && !emptyShows.contains(aShow)) {
                    log.fine("Currently checking if new & valid: " + aShow);
                    Map<Integer, Season> seasonEpisode = putSeasonInMap(aShow, folderLocation);
                    if (seasonEpisode.keySet().isEmpty()) {
                        emptyShows.add(aShow);
                        log.fine(aShow + " wasn't new & valid.");
                    } else {
                        newShows.put(aShow, new Show(aShow, seasonEpisode));
                        log.fine(aShow + " was new & valid.");
                    }
                }
                recheckShowFilePercentage += percentagePer;
            });
        }
        return newShows;
    }

    // Checks if the season folder episodes of the show, and if none are found, returns true. Returns false for first episode found.
    private boolean isSeasonEmpty(final String aShow, final Integer aSeason, final File folderLocation) {
        ArrayList<String> episodesFull = new FindShows().findEpisodes(folderLocation, aShow, aSeason);
        final boolean[] answer = {true};
        if (!episodesFull.isEmpty()) {
            episodesFull.forEach(aEpisode -> {
                if (ClassHandler.showInfoController().getEpisodeInfo(aEpisode)[0] != -2) answer[0] = false;
            });
        }
        return answer[0];
    }

    private void checkForNewOrRemovedSeasons(final File folderLocation, final String aShow, final Set<Integer> changedSeasons, final Directory directory) {
        Show seasonEpisode = directory.getShows().get(aShow);
        changedSeasons.forEach(aSeason -> {
            Map<Integer, Episode> episodeNum = putEpisodesInMap(aShow, folderLocation, aSeason);
            if (episodeNum.isEmpty()) seasonEpisode.removeSeason(aSeason);
            else seasonEpisode.addOrReplaceSeason(aSeason, new Season(aSeason, episodeNum));
        });
        directory.getShows().replace(aShow, seasonEpisode);
        ClassHandler.directoryController().saveDirectory(directory, true);
    }

    private void checkForNewOrRemovedEpisodes(final File folderLocation, final String aShow, final Integer aSeason, final Directory directory) {
        Show seasonEpisode = directory.getShows().get(aShow);
        seasonEpisode.addOrReplaceSeason(aSeason, new Season(aSeason, putEpisodesInMap(aShow, folderLocation, aSeason)));
        directory.getShows().replace(aShow, seasonEpisode);
        ClassHandler.directoryController().saveDirectory(directory, true);
    }

    private Set<Integer> removeLowerSeasons(final int baseInt, final Set<Integer> listToCheck) {
        Set<Integer> result = new HashSet<>();
        listToCheck.forEach(integer -> {
            if (integer >= baseInt) result.add(integer);
            else log.finest("Season " + integer + " was skipped in rechecking as the current user is past it.");
        });
        return result;
    }

    private boolean handleRemovedShows(final ArrayList<String> removedShows, final Directory directory, final File folder, final long directoryID) {
        boolean wereShowsRemoved = false;
        if (!removedShows.isEmpty()) {
            wereShowsRemoved = true;
            ClassHandler.directoryController().saveDirectory(directory, true);
            removedShows.forEach(aShow -> {
                log.fine(aShow + " is no longer found in \"" + folder + "\".");
                boolean doesShowExistElsewhere = ClassHandler.showInfoController().doesShowExistElsewhere(aShow, ClassHandler.directoryController().findDirectories(directoryID, false, true, true));
                if (!doesShowExistElsewhere) ClassHandler.userInfoController().setIgnoredStatus(aShow, true);
                Controller.updateShowField(aShow, doesShowExistElsewhere);
            });
        }
        return wereShowsRemoved;
    }
}
