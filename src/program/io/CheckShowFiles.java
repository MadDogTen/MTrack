package program.io;

import program.Controller;
import program.Main;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.util.Clock;
import program.util.FindChangedShows;
import program.util.FindLocation;
import program.util.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CheckShowFiles {
    private static boolean recheckShowFileRunning = false, keepRunning = false;
    private static ArrayList<String> emptyShows = new ArrayList<>();
    private static int runNumber = 0;
    private final Logger log = Logger.getLogger(CheckShowFiles.class.getName());

    public static ArrayList<String> getEmptyShows() {
        return emptyShows;
    }

    public void recheckShowFile(Boolean forceRun) {
        final Boolean[] hasChanged = {false};
        int timer = Clock.getTimeSeconds();
        FindChangedShows findChangedShows = new FindChangedShows();
        findChangedShows.findChangedShows(ShowInfoController.getShowsFile());
        if (!recheckShowFileRunning || (forceRun && keepRunning)) {
            log.info("Started rechecking shows...");
            recheckShowFileRunning = true;
            keepRunning = !forceRun;
            FileManager fileManager = new FileManager();
            ProgramSettingsController.getDirectoriesIndexes().forEach(aIndex -> {
                HashMap<String, HashMap<Integer, HashMap<String, String>>> hashMap = ShowInfoController.getDirectoryHashMap(aIndex);
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
                            Boolean doesShowExistElsewhere = ShowInfoController.doesShowExistElsewhere(aShow, ShowInfoController.getDirectoriesHashMaps(aIndex));
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
                            if (!Main.running || (!forceRun && !keepRunning)) {
                                break;
                            }
                        }
                    }
                }
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
            });
        }
        if (hasChanged[0] && Main.running) {
            ShowInfoController.loadShowsFile();
            findChangedShows.findShowFileDifferences(ShowInfoController.getShowsFile());
            log.info("Some shows have been updated.");
            log.info("Finished Rechecking Shows! - It took " + Clock.timeTakenSeconds(timer) + " seconds.");
        } else if (Main.running) {
            log.info("All shows were the same.");
            log.info("Finished Rechecking Shows! - It took " + Clock.timeTakenSeconds(timer) + " seconds.");
        }
        recheckShowFileRunning = false;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<String> hasEpisodesChanged(String aShow, Integer aSeason, File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile) {
        Set<String> oldEpisodeList = showsFile.get(aShow).get(aSeason).keySet();
        ArrayList<String> newEpisodesList = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
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

    private ArrayList<Integer> hasSeasonsChanged(String aShow, File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile) {
        Set<Integer> oldSeasons = showsFile.get(aShow).keySet();
        ArrayList<Integer> newSeasons = FindLocation.findSeasons(folderLocation, aShow);
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

    private HashMap<String, HashMap<Integer, HashMap<String, String>>> hasShowsChanged(File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile, Boolean forceRun) {
        HashMap<String, HashMap<Integer, HashMap<String, String>>> newShows = new HashMap<>(0);
        Set<String> oldShows = showsFile.keySet();
        FindLocation.findShows(folderLocation).forEach(aShow -> {
            if (forceRun || runNumber == 5) {
                emptyShows = new ArrayList<>();
                runNumber = 0;
            }
            if (Main.running && !oldShows.contains(aShow) && !emptyShows.contains(aShow)) {
                log.info("Currently checking if new & valid: " + aShow);
                // Integer = Season Number -- HashMap = Episodes in that season from episodeNumEpisode
                HashMap<Integer, HashMap<String, String>> seasonEpisode = new HashMap<>(0);
                FindLocation.findSeasons(folderLocation, aShow).forEach(aSeason -> {
                    // First String = Episode Number -- Second String = Episode Location
                    HashMap<String, String> episodeNumEpisode = new HashMap<>(0);
                    ArrayList<String> episodesFull = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
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

    private Boolean isSeasonEmpty(String aShow, Integer aSeason, File folderLocation) {
        ArrayList<String> episodesFull = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
        final Boolean[] answer = {true};
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
