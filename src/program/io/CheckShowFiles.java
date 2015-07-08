package program.io;

import program.Controller;
import program.Main;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.util.Clock;
import program.util.FindChangedShows;
import program.util.FindShows;
import program.util.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
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
        int timer = Clock.getTimeSeconds();
        FindChangedShows findChangedShows = new FindChangedShows();
        findChangedShows.findChangedShows(ShowInfoController.getShowsFile());
        if (!recheckShowFileRunning || (forceRun && keepRunning)) {
            log.info("Started rechecking shows...");
            recheckShowFileRunning = true;
            keepRunning = !forceRun;
            FileManager fileManager = new FileManager();
            recheckShowFilePercentage = 0;
            final double[] percentagePerDirectory = {0};
            if (!ProgramSettingsController.getDirectories().isEmpty()) {
                percentagePerDirectory[0] = 100 / ProgramSettingsController.getDirectories().size();
            }
            currentlyCheckingDirectories = true;
            ProgramSettingsController.getDirectories().forEach(directory -> ProgramSettingsController.isDirectoryCurrentlyActive(new File(directory)));
            currentlyCheckingDirectories = false;
            ProgramSettingsController.getDirectoriesIndexes().forEach(aIndex -> {
                HashMap<String, HashMap<Integer, HashMap<String, String>>> hashMap = ShowInfoController.getDirectoryHashMap(aIndex);
                double percentagePerShow = percentagePerDirectory[0] / (UserInfoController.getActiveShows().size() + 2);
                File folderLocation = ProgramSettingsController.getDirectory(aIndex);
                log.info("Directory currently being rechecked: \"" + folderLocation + "\".");
                if (ProgramSettingsController.isDirectoryCurrentlyActive(folderLocation)) {
                    ArrayList<String> removedShows = new ArrayList<>();
                    // Check if any shows were removed.
                    UserInfoController.getActiveShows().forEach(aShow -> {
                        if (hashMap.containsKey(aShow) && !fileManager.checkFolderExists(folderLocation + Strings.FileSeparator + aShow)) {
                            hashMap.remove(aShow);
                            removedShows.add(aShow);
                        }
                    });
                    if (!removedShows.isEmpty()) {
                        ShowInfoController.saveShowsHashMapFile(hashMap, aIndex);
                        ShowInfoController.loadShowsFile();
                        removedShows.forEach(aShow -> {
                            log.info(aShow + " is no longer found in \"" + folderLocation + "\".");
                            boolean doesShowExistElsewhere = ShowInfoController.doesShowExistElsewhere(aShow, ShowInfoController.getDirectoriesHashMaps(aIndex));
                            if (!doesShowExistElsewhere) {
                                UserInfoController.setIgnoredStatus(aShow, true);
                            }
                            Controller.updateShowField(aShow, doesShowExistElsewhere);
                        });
                        hasChanged[0] = true;
                    }
                    for (String aShow : UserInfoController.getActiveShows()) {
                        log.info("Currently rechecking " + aShow);
                        int currentSeason = UserInfoController.getCurrentSeason(aShow);
                        final boolean[] hasShowChanged = {false};
                        ArrayList<Integer> seasons = new ArrayList<>();
                        if (hashMap.containsKey(aShow)) {
                            hashMap.get(aShow).keySet().forEach(aSeason -> {
                                if (aSeason < currentSeason) {
                                    log.finest("Season " + aSeason + " was skipped in rechecking as the current user is past it.");
                                } else if (aSeason >= currentSeason) {
                                    seasons.add(aSeason);
                                }
                            });
                            UpdateShowFiles updateShowFiles = new UpdateShowFiles();
                            seasons.forEach(aSeason -> {
                                if (fileManager.checkFolderExists(String.valueOf(folderLocation) + Strings.FileSeparator + aShow + "/Season " + aSeason + Strings.FileSeparator)) {
                                    //log.info("Checking for new episodes for " + aShow + " - Season: " + aSeason);
                                    ArrayList<String> changedEpisodes = hasEpisodesChanged(aShow, aSeason, folderLocation, hashMap);
                                    if (!changedEpisodes.isEmpty()) {
                                        hasChanged[0] = true;
                                        hasShowChanged[0] = true;
                                        updateShowFiles.checkForNewOrRemovedEpisodes(folderLocation, aShow, aSeason, hashMap, aIndex);
                                    }
                                }
                            });
                            ArrayList<Integer> changedSeasons = hasSeasonsChanged(aShow, folderLocation, hashMap);
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
                                updateShowFiles.checkForNewOrRemovedSeasons(folderLocation, aShow, changedSeasons, hashMap, aIndex);
                            }
                            if (hasShowChanged[0]) {
                                Controller.updateShowField(aShow, true);
                            }
                            if (!Main.programFullyRunning || (!forceRun && !keepRunning)) {
                                stopRunning = true;
                                break;
                            }
                        }
                        recheckShowFilePercentage += percentagePerShow;
                    }
                }
                if (!stopRunning) {
                    HashMap<String, HashMap<Integer, HashMap<String, String>>> changedShows = hasShowsChanged(folderLocation, hashMap, forceRun);
                    if (!changedShows.isEmpty()) {
                        log.info("Current Shows have changed.");
                        hasChanged[0] = true;
                        ArrayList<String> ignoredShows = UserInfoController.getIgnoredShows();
                        changedShows.keySet().forEach(aNewShow -> {
                            hashMap.put(aNewShow, changedShows.get(aNewShow));
                            if (ignoredShows.contains(aNewShow)) {
                                UserInfoController.setIgnoredStatus(aNewShow, false);
                            }
                        });
                        ShowInfoController.saveShowsHashMapFile(hashMap, aIndex);
                        ShowInfoController.loadShowsFile();
                        changedShows.keySet().forEach(aShow -> {
                            UserInfoController.addNewShow(aShow);
                            Controller.updateShowField(aShow, true);
                        });
                        ProgramSettingsController.setMainDirectoryVersion(ProgramSettingsController.getMainDirectoryVersion() + 1);
                    }
                    recheckShowFilePercentage += (percentagePerShow * 2);
                }
            });
            if (!stopRunning) {
                recheckShowFilePercentage = 100;
                if (hasChanged[0] && Main.programFullyRunning) {
                    ShowInfoController.loadShowsFile();
                    findChangedShows.findShowFileDifferences(ShowInfoController.getShowsFile());
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
        }
    }

    // This compares the given showsFile HashMap episodes for any new episodes found in the shows folder. Adds anything new it finds to a ArrayList and returns.
    @SuppressWarnings("unchecked")
    private ArrayList<String> hasEpisodesChanged(String aShow, Integer aSeason, File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile) {
        Set<String> oldEpisodeList = showsFile.get(aShow).get(aSeason).keySet();
        ArrayList<String> newEpisodesList = FindShows.findEpisodes(folderLocation, aShow, aSeason);
        ArrayList<String> newEpisodesListFixed = new ArrayList<>(0);
        ArrayList<String> changedEpisodes = new ArrayList<>();
        if ((oldEpisodeList.isEmpty()) && newEpisodesList.isEmpty()) {
            return changedEpisodes;
        }
        if (newEpisodesList != null) {
            newEpisodesList.forEach(aNewEpisode -> {
                ArrayList<Integer> EpisodeInfo = ShowInfoController.getEpisodeSeasonInfo(aNewEpisode);
                if (!EpisodeInfo.isEmpty()) {
                    if (EpisodeInfo.size() == 2) {
                        String episodeNumber = String.valueOf(EpisodeInfo.get(1));
                        newEpisodesListFixed.add(episodeNumber);
                    } else if (EpisodeInfo.size() == 3) {
                        String episodeNumber = String.valueOf(EpisodeInfo.get(1) + "+" + EpisodeInfo.get(2));
                        newEpisodesListFixed.add(episodeNumber);
                    }
                }
            });
            changedEpisodes.addAll(oldEpisodeList.stream().filter(aOldEpisode -> !newEpisodesListFixed.contains(aOldEpisode)).collect(Collectors.toList()));
            changedEpisodes.addAll(newEpisodesListFixed.stream().filter(newEpisode -> !oldEpisodeList.contains(newEpisode)).collect(Collectors.toList()));
        } else return (ArrayList<String>) oldEpisodeList;
        return changedEpisodes;
    }

    // Same as above, but instead scans the shows folder for new seasons.
    private ArrayList<Integer> hasSeasonsChanged(String aShow, File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile) {
        Set<Integer> oldSeasons = showsFile.get(aShow).keySet();
        ArrayList<Integer> newSeasons = FindShows.findSeasons(folderLocation, aShow);
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

    // Scans the folder for any changes between it and the given showsFile, and for new shows it finds (that aren't empty) it gets all seasons & episodes for it, adds them to a HashMap and returns that.
    private HashMap<String, HashMap<Integer, HashMap<String, String>>> hasShowsChanged(File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile, boolean forceRun) {
        HashMap<String, HashMap<Integer, HashMap<String, String>>> newShows = new HashMap<>(0);
        Set<String> oldShows = showsFile.keySet();
        FindShows.findShows(folderLocation).forEach(aShow -> {
            if (forceRun || runNumber == 5) {
                emptyShows = new ArrayList<>();
                runNumber = 0;
            }
            if (Main.programFullyRunning && !oldShows.contains(aShow) && !emptyShows.contains(aShow)) {
                log.info("Currently checking if new & valid: " + aShow);
                // Integer = Season Number -- HashMap = Episodes in that season from episodeNumEpisode
                HashMap<Integer, HashMap<String, String>> seasonEpisode = new HashMap<>(0);
                FindShows.findSeasons(folderLocation, aShow).forEach(aSeason -> {
                    // First String = Episode Number -- Second String = Episode Location
                    HashMap<String, String> episodeNumEpisode = new HashMap<>(0);
                    ArrayList<String> episodesFull = FindShows.findEpisodes(folderLocation, aShow, aSeason);
                    if (!episodesFull.isEmpty()) {
                        episodesFull.forEach(aEpisode -> {
                            ArrayList<Integer> episode = ShowInfoController.getEpisodeSeasonInfo(aEpisode);
                            if (!episode.isEmpty()) {
                                if (episode.size() == 2) {
                                    String episodeNumber = String.valueOf(episode.get(1));
                                    episodeNumEpisode.put(episodeNumber, (folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode));
                                } else if (episode.size() == 3) {
                                    String episodeNumber = String.valueOf(episode.get(1) + "+" + episode.get(2));
                                    episodeNumEpisode.put(episodeNumber, (folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode));
                                } else {
                                    log.warning("Error 1 if at this point!" + " + " + episode);
                                }
                            }
                        });
                        if (!episodeNumEpisode.isEmpty()) {
                            seasonEpisode.put(aSeason, episodeNumEpisode);
                        }
                    }
                });
                if (seasonEpisode.keySet().isEmpty()) emptyShows.add(aShow);
                else newShows.put(aShow, seasonEpisode);
            }
        });
        runNumber++;
        return newShows;
    }

    // Checks if the season folder episodes of the show, and if none are found, returns true. Returns false for first episode found.
    private boolean isSeasonEmpty(String aShow, Integer aSeason, File folderLocation) {
        ArrayList<String> episodesFull = FindShows.findEpisodes(folderLocation, aShow, aSeason);
        final boolean[] answer = {true};
        if (!episodesFull.isEmpty()) {
            episodesFull.forEach(aEpisode -> {
                if (!ShowInfoController.getEpisodeSeasonInfo(aEpisode).isEmpty()) {
                    answer[0] = false;
                }
            });
        }
        return answer[0];
    }
}
