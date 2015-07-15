package program.io;

import program.Controller;
import program.Main;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.information.show.Episode;
import program.information.show.Season;
import program.information.show.Show;
import program.util.FindChangedShows;
import program.util.FindShows;
import program.util.Strings;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CheckShowFiles {
    private final Logger log = Logger.getLogger(CheckShowFiles.class.getName());
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

    private ProgramSettingsController programSettingsController;
    private ShowInfoController showInfoController;
    private UserInfoController userInfoController;

    public CheckShowFiles(ProgramSettingsController programSettingsController, ShowInfoController showInfoController, UserInfoController userInfoController) {
        this.programSettingsController = programSettingsController;
        this.showInfoController = showInfoController;
        this.userInfoController = userInfoController;
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
        // hasChanged - If anything is found differently, this will be set to true. Used at the end to determine if it should reload the showInfoController showsFile and scan for changes.
        final boolean[] hasChanged = {false};
        // timer - Used purely for log purposes to see how long the run takes.
        int timer = Main.clock.getTimeSeconds();
        FindChangedShows findChangedShows = new FindChangedShows(showInfoController.getShowsFile());
        if (!recheckShowFileRunning || (forceRun && keepRunning)) {
            log.info("Started rechecking shows...");
            recheckShowFileRunning = true;
            keepRunning = !forceRun;
            FileManager fileManager = new FileManager();
            recheckShowFilePercentage = 0;
            final double[] percentagePerDirectory = {0};
            if (!programSettingsController.getDirectories().isEmpty()) {
                percentagePerDirectory[0] = 100 / programSettingsController.getDirectories().size();
            }
            currentlyCheckingDirectories = true;
            programSettingsController.getDirectories().forEach(directory -> programSettingsController.isDirectoryCurrentlyActive(new File(directory)));
            currentlyCheckingDirectories = false;
            programSettingsController.getDirectoriesIndexes().forEach(aIndex -> {
                Map<String, Show> showsMap = showInfoController.getDirectoryMap(aIndex);
                double percentagePerShow = percentagePerDirectory[0] / (userInfoController.getActiveShows().size() + 12);
                File folderLocation = programSettingsController.getDirectory(aIndex);
                log.info("Directory currently being rechecked: \"" + folderLocation + "\".");
                if (programSettingsController.isDirectoryCurrentlyActive(folderLocation)) {
                    ArrayList<String> removedShows = new ArrayList<>();
                    // Check if any shows were removed.
                    userInfoController.getActiveShows().stream().filter(aShow -> (showsMap.containsKey(aShow) && !fileManager.checkFolderExists(folderLocation + Strings.FileSeparator + aShow))).forEach(aShow -> { // TODO Change this to search inactive shows as well.
                        showsMap.remove(aShow);
                        removedShows.add(aShow);
                    });
                    if (!removedShows.isEmpty()) {
                        showInfoController.saveShowsMapFile(showsMap, aIndex);
                        showInfoController.loadShowsFile();
                        removedShows.forEach(aShow -> {
                            log.info(aShow + " is no longer found in \"" + folderLocation + "\".");
                            boolean doesShowExistElsewhere = showInfoController.doesShowExistElsewhere(aShow, showInfoController.getDirectoriesMaps(aIndex));
                            if (!doesShowExistElsewhere) {
                                userInfoController.setIgnoredStatus(aShow, true);
                            }
                            Controller.updateShowField(aShow, doesShowExistElsewhere);
                        });
                        hasChanged[0] = true;
                    }
                    for (String aShow : userInfoController.getActiveShows()) {
                        log.info("Currently rechecking " + aShow);
                        int currentSeason = userInfoController.getCurrentSeason(aShow);
                        final boolean[] hasShowChanged = {false};
                        ArrayList<Integer> seasons = new ArrayList<>();
                        Show show1 = showsMap.get(aShow);
                        for (int season : show1.getSeasons().keySet()) {
                            if (season < currentSeason) {
                                log.finest("Season " + season + " was skipped in rechecking as the current user is past it.");
                            } else if (season >= currentSeason) {
                                seasons.add(season);
                            }
                        }
                        UpdateShowFiles updateShowFiles = new UpdateShowFiles(showInfoController);
                        seasons.forEach(aSeason -> {
                            if (fileManager.checkFolderExists(String.valueOf(folderLocation) + Strings.FileSeparator + aShow + "/Season " + aSeason + Strings.FileSeparator)) {
                                //log.info("Checking for new episodes for " + aShow + " - Season: " + aSeason);
                                ArrayList<Integer> changedEpisodes = hasEpisodesChanged(aShow, aSeason, folderLocation, showsMap);
                                if (!changedEpisodes.isEmpty()) {
                                    hasChanged[0] = true;
                                    hasShowChanged[0] = true;
                                    updateShowFiles.checkForNewOrRemovedEpisodes(folderLocation, aShow, aSeason, showsMap, aIndex);
                                }
                            }
                        });
                        ArrayList<Integer> changedSeasons = hasSeasonsChanged(aShow, folderLocation, showsMap);
                        Iterator<Integer> changedSeasonIterator = changedSeasons.iterator();
                        while (changedSeasonIterator.hasNext()) {
                            int aSeason = changedSeasonIterator.next();
                            if (aSeason < currentSeason) {
                                changedSeasonIterator.remove();
                            }
                        }
                        if (!changedSeasons.isEmpty()) {
                            hasChanged[0] = true;
                            hasShowChanged[0] = true;
                            updateShowFiles.checkForNewOrRemovedSeasons(folderLocation, aShow, changedSeasons, showsMap, aIndex);
                        }
                        if (hasShowChanged[0]) {
                            Controller.updateShowField(aShow, true);
                        }

                        if (!Main.programFullyRunning || (!forceRun && !keepRunning)) {
                            stopRunning = true;
                            break;
                        }
                        recheckShowFilePercentage += percentagePerShow;
                    }
                }
                if (!stopRunning) {
                    double percentagePer = percentagePerShow * 12;
                    Map<String, Show> changedShows = hasShowsChanged(folderLocation, showsMap, percentagePer / 6, forceRun);
                    if (changedShows.isEmpty()) recheckShowFilePercentage += (percentagePerShow * 12);
                    else {
                        log.info("Current Shows have changed.");
                        hasChanged[0] = true;
                        ArrayList<String> ignoredShows = userInfoController.getIgnoredShows();
                        changedShows.keySet().forEach(aNewShow -> {
                            showsMap.put(aNewShow, changedShows.get(aNewShow));
                            if (ignoredShows.contains(aNewShow)) {
                                userInfoController.setIgnoredStatus(aNewShow, false);
                            }
                            recheckShowFilePercentage += percentagePer / 3;
                        });
                        showInfoController.saveShowsMapFile(showsMap, aIndex);
                        showInfoController.loadShowsFile();
                        changedShows.keySet().forEach(aShow -> {
                            userInfoController.addNewShow(aShow);
                            Controller.updateShowField(aShow, true);
                            recheckShowFilePercentage += percentagePer / 3;
                        });
                        programSettingsController.setMainDirectoryVersion(programSettingsController.getMainDirectoryVersion() + 1);
                    }
                }
            });
            if (!stopRunning) {
                recheckShowFilePercentage = 100;
                if (hasChanged[0] && Main.programFullyRunning) {
                    showInfoController.loadShowsFile();
                    findChangedShows.findShowFileDifferences(showInfoController.getShowsFile());
                    log.info("Some shows have been updated.");
                    log.info("Finished Rechecking Shows! - It took " + Main.clock.timeTakenSeconds(timer) + " seconds.");
                } else if (Main.programFullyRunning) {
                    log.info("All shows were the same.");
                    log.info("Finished Rechecking Shows! - It took " + Main.clock.timeTakenSeconds(timer) + " seconds.");
                }
                recheckShowFileRunning = false;
            }
            if (stopRunning) {
                stopRunning = false;
            }
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
    private ArrayList<Integer> hasSeasonsChanged(String aShow, File folderLocation, Map<String, Show> showsFile) {
        Set<Integer> oldSeasons = showsFile.get(aShow).getSeasons().keySet();
        ArrayList<Integer> newSeasons = new FindShows().findSeasons(folderLocation, aShow);
        Iterator<Integer> newSeasonsIterator = newSeasons.iterator();
        while (newSeasonsIterator.hasNext()) {
            if (isSeasonEmpty(aShow, newSeasonsIterator.next(), folderLocation)) {
                newSeasonsIterator.remove();
            }
        }
        ArrayList<Integer> ChangedSeasons = new ArrayList<>();
        ChangedSeasons.addAll(oldSeasons.stream().filter(aOldSeason -> !newSeasons.contains(aOldSeason)).collect(Collectors.toList()));
        ChangedSeasons.addAll(newSeasons.stream().filter(aNewSeason -> !oldSeasons.contains(aNewSeason)).collect(Collectors.toList()));
        return ChangedSeasons;
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
}
