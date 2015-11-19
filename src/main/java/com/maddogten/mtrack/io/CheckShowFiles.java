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
import com.maddogten.mtrack.util.Clock;
import com.maddogten.mtrack.util.FindChangedShows;
import com.maddogten.mtrack.util.FindShows;
import com.maddogten.mtrack.util.Strings;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
    private double recheckShowFilePercentage;

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
            int timer = Clock.getTimeSeconds();
            log.info("Started rechecking shows...");
            recheckShowFileRunning = true;
            keepRunning = !forceRun;
            FileManager fileManager = new FileManager();
            // Need to set the FindChangedShows showsFile to the current, unchanged one to later find the changes.
            FindChangedShows findChangedShows = new FindChangedShows(showInfoController.getShowsFile());

            final double[] percentagePerDirectory = {0};
            if (!directoryController.getDirectories().isEmpty()) {
                percentagePerDirectory[0] = 100 / directoryController.getDirectories().size();
            }
            // Just so the user knows when it is a directory that is delaying the search, and not the program hanging.
            ArrayList<Directory> activeDirectories = new ArrayList<>();
            currentlyCheckingDirectories = true;
            directoryController.getDirectories().forEach(directory -> {
                if (Main.programRunning && directoryController.isDirectoryCurrentlyActive(directory.getDirectory(), !forceRun)) {
                    activeDirectories.add(directory);
                }
            });
            currentlyCheckingDirectories = false;
            activeDirectories.forEach(aDirectory -> {
                Map<String, Show> showsMap = aDirectory.getShows();
                double percentagePerShow = percentagePerDirectory[0] / (userInfoController.getActiveShows().size() + 12);
                File folderLocation = aDirectory.getDirectory();
                log.info("Directory currently being rechecked: \"" + folderLocation + "\".");
                ArrayList<String> removedShows = new ArrayList<>();
                // Check if any shows were removed.
                userInfoController.getAllNonIgnoredShows().stream().filter(aShow -> (showsMap.containsKey(aShow) && !fileManager.checkFolderExistsAndReadable(new File(folderLocation + Strings.FileSeparator + aShow)))).forEach(aShow -> {
                    showsMap.remove(aShow);
                    removedShows.add(aShow);
                });
                if (handleRemovedShows(removedShows, aDirectory, folderLocation, aDirectory.getIndex())) {
                    hasChanged[0] = true;
                }
                for (String aShow : userInfoController.getActiveShows()) {
                    if (showsMap.containsKey(aShow)) {
                        log.info("Currently rechecking " + aShow);
                        int currentSeason = userInfoController.getCurrentSeason(aShow);
                        removeLowerSeasons(currentSeason, showsMap.get(aShow).getSeasons().keySet()).forEach(aSeason -> {
                            if (fileManager.checkFolderExistsAndReadable(new File(String.valueOf(folderLocation) + Strings.FileSeparator + aShow + "/Season " + aSeason + Strings.FileSeparator))) {
                                ArrayList<Integer> changedEpisodes = hasEpisodesChanged(aShow, aSeason, folderLocation, showsMap);
                                if (!changedEpisodes.isEmpty()) {
                                    log.info("Episode changes detected for " + aShow + ", updating files...");
                                    hasChanged[0] = true;
                                    updatedShows.add(aShow);
                                    checkForNewOrRemovedEpisodes(folderLocation, aShow, aSeason, aDirectory);
                                }
                            } else
                                log.info("Couldn't find folder for " + aShow + " season " + aShow + " so it was skipped in rechecking.");
                        });
                        ArrayList<Integer> changedSeasons = removeLowerSeasons(currentSeason, hasSeasonsChanged(aShow, folderLocation, showsMap));
                        if (!changedSeasons.isEmpty()) {
                            log.info("Season changes detected for " + aShow + ", updating files...");
                            hasChanged[0] = true;
                            updatedShows.add(aShow);
                            checkForNewOrRemovedSeasons(folderLocation, aShow, changedSeasons, aDirectory);
                        }
                        if (!Main.programFullyRunning || (!forceRun && !keepRunning)) {
                            stopRunning = true;
                            break;
                        }
                    }
                    recheckShowFilePercentage += percentagePerShow;
                }
                if (!stopRunning) {
                    double percentagePer = percentagePerShow * 12;
                    Map<String, Show> changedShows = hasShowsChanged(folderLocation, showsMap, percentagePer / 6, forceRun);
                    if (changedShows.isEmpty()) recheckShowFilePercentage += (percentagePerShow * 6);
                    else {
                        log.info("Current Shows have changed.");
                        hasChanged[0] = true;
                        ArrayList<String> ignoredShows = userInfoController.getIgnoredShows();
                        changedShows.keySet().forEach(aNewShow -> {
                            showsMap.put(aNewShow, changedShows.get(aNewShow));
                            if (ignoredShows.contains(aNewShow)) {
                                userInfoController.setIgnoredStatus(aNewShow, false);
                            }
                            recheckShowFilePercentage += percentagePer / changedShows.size();
                        });
                        aDirectory.setShows(showsMap);
                        directoryController.saveDirectory(aDirectory, false);
                        showInfoController.loadShowsFile();
                        changedShows.keySet().forEach(aShow -> {
                            userInfoController.addNewShow(aShow);
                            Controller.updateShowField(aShow, true);
                            recheckShowFilePercentage += percentagePer / changedShows.size();
                        });
                        programSettingsController.setMainDirectoryVersion(programSettingsController.getMainDirectoryVersion() + 1);
                    }
                }
            });
            if (!stopRunning) {
                recheckShowFilePercentage = 100;
                if (hasChanged[0] && Main.programFullyRunning) {
                    showInfoController.loadShowsFile();
                    if (!updatedShows.isEmpty()) {
                        updatedShows.forEach(aShow -> Controller.updateShowField(aShow, true));
                    }
                    findChangedShows.findShowFileDifferences(showInfoController.getShowsFile());
                    log.info("Some shows have been updated.");
                    log.info("Finished Rechecking Shows! - It took " + Clock.timeTakenSeconds(timer) + " seconds.");
                } else if (Main.programFullyRunning) {
                    log.info("All shows were the same.");
                    log.info("Finished Rechecking Shows! - It took " + Clock.timeTakenSeconds(timer) + " seconds.");
                }
                recheckShowFileRunning = false;
            }
            if (stopRunning) {
                stopRunning = false;
            }
            recheckShowFilePercentage = 0;
        }
    }

    // This compares the given showsFile Map episodes for any new episodes found in the shows folder. Adds anything new it finds to a ArrayList and returns.
    @SuppressWarnings("unchecked")
    private ArrayList<Integer> hasEpisodesChanged(String aShow, Integer aSeason, File folderLocation, Map<String, Show> showsFile) {
        Set<Integer> oldEpisodeList = showsFile.get(aShow).getSeason(aSeason).getEpisodes().keySet();
        ArrayList<String> newEpisodesList = new FindShows().findEpisodes(folderLocation, aShow, aSeason);
        ArrayList<Integer> newEpisodesListFixed = new ArrayList<>(0);
        ArrayList<Integer> changedEpisodes = new ArrayList<>();
        if ((oldEpisodeList.isEmpty()) && newEpisodesList.isEmpty()) {
            return changedEpisodes;
        }
        if (newEpisodesList != null) {
            newEpisodesList.forEach(aNewEpisode -> {
                int[] EpisodeInfo = showInfoController.getEpisodeInfo(aNewEpisode);
                if (EpisodeInfo != null) {
                    if (EpisodeInfo.length == 1) {
                        newEpisodesListFixed.add(EpisodeInfo[0]);
                    } else if (EpisodeInfo.length == 2) {
                        newEpisodesListFixed.add(EpisodeInfo[0]);
                        newEpisodesListFixed.add(EpisodeInfo[1]);
                    }
                }
            });
            changedEpisodes.addAll(oldEpisodeList.stream().filter(aOldEpisode -> !newEpisodesListFixed.contains(aOldEpisode)).collect(Collectors.toList()));
            changedEpisodes.addAll(newEpisodesListFixed.stream().filter(newEpisode -> !oldEpisodeList.contains(newEpisode)).collect(Collectors.toList()));
        } else return (ArrayList<Integer>) oldEpisodeList;
        return changedEpisodes;
    }

    // Same as above, but instead scans the shows folder for new seasons.
    private Set<Integer> hasSeasonsChanged(String aShow, File folderLocation, Map<String, Show> showsFile) {
        Set<Integer> oldSeasons = showsFile.get(aShow).getSeasons().keySet();
        ArrayList<Integer> newSeasons = new FindShows().findSeasons(folderLocation, aShow);
        Iterator<Integer> newSeasonsIterator = newSeasons.iterator();
        while (newSeasonsIterator.hasNext()) {
            if (isSeasonEmpty(aShow, newSeasonsIterator.next(), folderLocation)) {
                newSeasonsIterator.remove();
            }
        }
        Set<Integer> changedSeasons = new HashSet<>();
        changedSeasons.addAll(oldSeasons.stream().filter(aOldSeason -> !newSeasons.contains(aOldSeason)).collect(Collectors.toList()));
        changedSeasons.addAll(newSeasons.stream().filter(aNewSeason -> !oldSeasons.contains(aNewSeason)).collect(Collectors.toList()));
        return changedSeasons;
    }

    // Scans the folder for any changes between it and the given showsFile, and for new shows it finds (that aren't empty) it gets all seasons & episodes for it, adds them to a Map and returns that.
    private Map<String, Show> hasShowsChanged(File folderLocation, Map<String, Show> showsFile, double percentIncrease, boolean forceRun) {
        Map<String, Show> newShows = new HashMap<>();
        Set<String> oldShows = showsFile.keySet();
        FindShows findShows = new FindShows();
        ArrayList<String> showsToCheck = findShows.findShows(folderLocation);
        double percentagePer = percentIncrease / showsToCheck.size();
        showsToCheck.forEach(aShow -> {
            if (forceRun || runNumber == 5) {
                emptyShows = new ArrayList<>();
                runNumber = 0;
            }
            if (Main.programFullyRunning && !oldShows.contains(aShow) && !emptyShows.contains(aShow)) {
                log.info("Currently checking if new & valid: " + aShow);
                Map<Integer, Season> seasonEpisode = new HashMap<>(0);
                findShows.findSeasons(folderLocation, aShow).forEach(aSeason -> {
                    // First String = Episode Number -- Second String = Episode Location
                    Map<Integer, Episode> episodeNumEpisode = new HashMap<>();
                    ArrayList<String> episodesFull = findShows.findEpisodes(folderLocation, aShow, aSeason);
                    if (!episodesFull.isEmpty()) {
                        episodesFull.forEach(aEpisode -> {
                            int[] episode = showInfoController.getEpisodeInfo(aEpisode);
                            if (episode != null) {
                                if (episode.length == 1) {
                                    episodeNumEpisode.put(episode[0], new Episode(episode[0], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, false));
                                } else if (episode.length == 2) {
                                    episodeNumEpisode.put(episode[0], new Episode(episode[0], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, true));
                                    episodeNumEpisode.put(episode[1], new Episode(episode[1], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, true));
                                } else {
                                    log.warning("Error 1 if at this point!" + " + " + Arrays.toString(episode));
                                }
                            }
                        });
                        seasonEpisode.put(aSeason, new Season(aSeason, episodeNumEpisode));
                    }
                });
                if (seasonEpisode.keySet().isEmpty()) emptyShows.add(aShow);
                else newShows.put(aShow, new Show(aShow, seasonEpisode));
            }
            recheckShowFilePercentage += percentagePer;
        });
        runNumber++;
        return newShows;
    }

    // Checks if the season folder episodes of the show, and if none are found, returns true. Returns false for first episode found.
    private boolean isSeasonEmpty(String aShow, Integer aSeason, File folderLocation) {
        ArrayList<String> episodesFull = new FindShows().findEpisodes(folderLocation, aShow, aSeason);
        final boolean[] answer = {true};
        if (!episodesFull.isEmpty()) {
            episodesFull.forEach(aEpisode -> {
                if (showInfoController.getEpisodeInfo(aEpisode) != null) {
                    answer[0] = false;
                }
            });
        }
        return answer[0];
    }

    private void checkForNewOrRemovedSeasons(File folderLocation, String aShow, ArrayList<Integer> changedSeasons, Directory directory) {
        Show seasonEpisode = directory.getShows().get(aShow);
        changedSeasons.forEach(aSeason -> {
            Map<Integer, Episode> episodeNum = new HashMap<>(0);
            ArrayList<String> episodesFullList = findShows.findEpisodes(folderLocation, aShow, aSeason);
            if (episodesFullList.isEmpty()) seasonEpisode.removeSeason(aSeason);
            else {
                episodesFullList.forEach(aEpisode -> {
                    int[] episode = showInfoController.getEpisodeInfo(aEpisode);
                    if (episode != null) {
                        if (episode.length == 1) {
                            episodeNum.put(episode[0], new Episode(episode[0], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, false));
                        } else if (episode.length == 2) {
                            episodeNum.put(episode[0], new Episode(episode[0], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, true));
                            episodeNum.put(episode[1], new Episode(episode[1], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, true));
                        } else {
                            log.warning("Error 1 if at this point!" + " + " + Arrays.toString(episode));
                        }
                    }
                });
                if (!episodeNum.isEmpty() && seasonEpisode.getSeasons().containsKey(aSeason) || !episodeNum.isEmpty()) {
                    seasonEpisode.addOrReplaceSeason(aSeason, new Season(aSeason, episodeNum));
                }
            }
        });
        directory.getShows().replace(aShow, seasonEpisode);
        directoryController.saveDirectory(directory, true);
    }

    private void checkForNewOrRemovedEpisodes(File folderLocation, String aShow, Integer aSeason, Directory directory) {
        Show seasonEpisode = directory.getShows().get(aShow);
        Map<Integer, Episode> episodeNum = new HashMap<>();
        ArrayList<String> episodesFullList = findShows.findEpisodes(folderLocation, aShow, aSeason);
        if (!episodesFullList.isEmpty()) {
            episodesFullList.forEach(aEpisode -> {
                int[] episode = showInfoController.getEpisodeInfo(aEpisode);
                if (episode != null) {
                    if (episode.length == 1) {
                        episodeNum.put(episode[0], new Episode(episode[0], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, false));
                    } else if (episode.length == 2) {
                        episodeNum.put(episode[0], new Episode(episode[0], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, true));
                        episodeNum.put(episode[1], new Episode(episode[1], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode, true));
                    } else {
                        log.warning("Error 2 if at this point!" + " + " + Arrays.toString(episode));
                    }
                }
            });
        }
        seasonEpisode.addOrReplaceSeason(aSeason, new Season(aSeason, episodeNum));
        directory.getShows().replace(aShow, seasonEpisode);
        directoryController.saveDirectory(directory, true);
    }

    private ArrayList<Integer> removeLowerSeasons(int baseInt, Set<Integer> listToCheck) {
        ArrayList<Integer> result = new ArrayList<>();
        listToCheck.forEach(integer -> {
            if (integer >= baseInt) {
                result.add(integer);
            } else {
                log.finest("Season " + integer + " was skipped in rechecking as the current user is past it.");
            }
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
                if (!doesShowExistElsewhere) {
                    userInfoController.setIgnoredStatus(aShow, true);
                }
                Controller.updateShowField(aShow, doesShowExistElsewhere);
            });
        }
        return wereShowsRemoved;
    }
}
