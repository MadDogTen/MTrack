package program.io;

import program.Main;
import program.information.ChangeReporter;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.util.Clock;
import program.util.FindLocation;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class CheckShowFiles {
    private static final Logger log = Logger.getLogger(CheckShowFiles.class.getName());

    private static boolean recheckShowFileRunning = false, keepRunning = false;
    private static ArrayList<String> emptyShows = new ArrayList<>();
    private static int runNumber = 0;

    public static void recheckShowFile(Boolean forceRun) {
        Boolean hasChanged = false;
        int timer = Clock.getTimeSeconds();
        if (!recheckShowFileRunning || (forceRun && keepRunning)) {
            recheckShowFileRunning = true;
            keepRunning = !forceRun;
            ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = ShowInfoController.getDirectoriesHashMaps(-1);
            ArrayList<String> activeShows = UserInfoController.getActiveShows();
            FileManager fileManager = new FileManager();
            for (HashMap<String, HashMap<Integer, HashMap<String, String>>> aHashMap : showsFileArray) {
                log.info("Started rechecking shows...");
                int hashMapIndex = showsFileArray.indexOf(aHashMap);
                File folderLocation = ProgramSettingsController.getDirectory(hashMapIndex);
                if (ProgramSettingsController.isDirectoryCurrentlyActive(folderLocation)) {
                    for (String aShow : activeShows) {
                        log.info("Currently rechecking " + aShow);
                        int currentSeason = UserInfoController.getCurrentSeason(aShow);
                        if (aHashMap.containsKey(aShow)) {
                            Set<Integer> seasons = aHashMap.get(aShow).keySet();
                            Iterator<Integer> seasonsIterator = seasons.iterator();
                            while (seasonsIterator.hasNext()) {
                                int aSeason = seasonsIterator.next();
                                if (aSeason < currentSeason) {
                                    seasonsIterator.remove();
                                    log.finest("Season " + aSeason + " was skipped in rechecking as the current user is past it.");
                                }
                            }
                            for (Object aSeason : seasons) {
                                if (fileManager.checkFolderExists(String.valueOf(folderLocation) + '\\' + aShow + "\\Season " + aSeason + '\\')) {
                                    log.info("Checking for new episodes for " + aShow + " - Season: " + aSeason);
                                    ArrayList<String> changedEpisodes = hasEpisodesChanged(aShow, (Integer) aSeason, folderLocation, aHashMap);
                                    if (!changedEpisodes.isEmpty()) {
                                        ChangeReporter.addChange(aShow + "- Season: " + aSeason + " Episode(s): " + changedEpisodes + " has changed");
                                        hasChanged = true;
                                        UpdateShowFiles.checkForNewOrRemovedEpisodes(folderLocation, aShow, (Integer) aSeason, aHashMap, hashMapIndex);
                                    }
                                }
                            }
                            ArrayList<Integer> changedSeasons = hasSeasonsChanged(aShow, folderLocation, aHashMap);
                            Iterator<Integer> changedSeasonIterator = changedSeasons.iterator();
                            while (changedSeasonIterator.hasNext()) {
                                int aSeason = changedSeasonIterator.next();
                                if (aSeason < currentSeason) {
                                    changedSeasonIterator.remove();
                                    log.info("Season " + aSeason + " wasn't added as the current user is past it.");
                                }
                            }
                            if (!changedSeasons.isEmpty()) {
                                ChangeReporter.addChange(aShow + "- Season(s): " + changedSeasons + " has changed");
                                hasChanged = true;
                                UpdateShowFiles.checkForNewOrRemovedSeasons(folderLocation, aShow, changedSeasons, aHashMap, hashMapIndex);
                            }
                            if (!Main.running || (!forceRun && !keepRunning)) {
                                break;
                            }
                        }
                    }
                    HashMap<String, HashMap<Integer, HashMap<String, String>>> changedShows = hasShowsChanged(folderLocation, aHashMap, forceRun);
                    if (!changedShows.isEmpty()) {
                        log.info("Current Shows have changed.");
                        hasChanged = true;
                        ArrayList<String> ignoredShows = UserInfoController.getIgnoredShows();
                        for (String aNewShow : changedShows.keySet()) {
                            ChangeReporter.addChange(aNewShow + " has been added!");
                            aHashMap.put(aNewShow, changedShows.get(aNewShow));
                            if (ignoredShows.contains(aNewShow)) {
                                UserInfoController.setIgnoredStatus(aNewShow, false);
                            }
                        }
                        ShowInfoController.saveShowsHashMapFile(aHashMap, hashMapIndex);
                        changedShows.keySet().forEach(UserInfoController::addNewShow);
                        ProgramSettingsController.setMainDirectoryVersion(ProgramSettingsController.getMainDirectoryVersion() + 1, true);
                    }
                }
            }
        }
        if (hasChanged && Main.running) {
            log.info("Some shows have been updated.");

            // Change Writer - Temporary
            ChangeReporter.printChanges();

            log.info("Finished Rechecking Shows! - It took " + Clock.timeTakenSeconds(timer) + " seconds.");
        } else if (Main.running) {
            log.info("All shows were the same.");
            log.info("Finished Rechecking Shows! - It took " + Clock.timeTakenSeconds(timer) + " seconds.");
        }
        recheckShowFileRunning = false;
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<String> hasEpisodesChanged(String aShow, Integer aSeason, File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile) {
        Set<String> oldEpisodeList = showsFile.get(aShow).get(aSeason).keySet();
        String[] newEpisodesList = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
        ArrayList<String> newEpisodesListFixed = new ArrayList<>(0);
        ArrayList<String> changedEpisodes = new ArrayList<>();
        if ((oldEpisodeList.isEmpty()) && newEpisodesList == null) {
            return changedEpisodes;
        }
        if (newEpisodesList != null) {
            for (String aNewEpisode : newEpisodesList) {
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
            }
            for (String aOldEpisode : oldEpisodeList) {
                if (!newEpisodesListFixed.contains(aOldEpisode)) {
                    changedEpisodes.add(aOldEpisode);
                }
            }
            for (String newEpisode : newEpisodesListFixed) {
                if (!oldEpisodeList.contains(newEpisode)) {
                    changedEpisodes.add(newEpisode);
                }
            }
        } else return (ArrayList<String>) oldEpisodeList;
        return changedEpisodes;
    }

    private static ArrayList<Integer> hasSeasonsChanged(String aShow, File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile) {
        Set<Integer> oldSeasons = showsFile.get(aShow).keySet();
        ArrayList<Integer> newSeasons = FindLocation.findSeasons(folderLocation, aShow);
        Iterator<Integer> newSeasonsIterator = newSeasons.iterator();
        while (newSeasonsIterator.hasNext()) {
            if (isSeasonEmpty(aShow, newSeasonsIterator.next(), folderLocation)) {
                newSeasonsIterator.remove();
            }
        }
        ArrayList<Integer> ChangedSeasons = new ArrayList<>();
        for (Integer aOldSeason : oldSeasons) {
            if (!newSeasons.contains(aOldSeason)) {
                ChangedSeasons.add(aOldSeason);
            }
        }
        for (Integer aNewSeason : newSeasons) {
            if (!oldSeasons.contains(aNewSeason)) {
                ChangedSeasons.add(aNewSeason);
            }
        }
        return ChangedSeasons;
    }

    private static HashMap<String, HashMap<Integer, HashMap<String, String>>> hasShowsChanged(File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile, Boolean forceRun) {
        HashMap<String, HashMap<Integer, HashMap<String, String>>> newShows = new HashMap<>(0);
        Set<String> oldShows = showsFile.keySet();
        String[] currentShows = FindLocation.findShows(folderLocation);
        ArrayList<String> newShowsFixed = new ArrayList<>();
        Collections.addAll(newShowsFixed, currentShows);
        for (String aShow : newShowsFixed) {
            if (forceRun || runNumber == 5) {
                emptyShows = new ArrayList<>();
                runNumber = 0;
            }
            if (Main.running && !oldShows.contains(aShow) && !emptyShows.contains(aShow)) {
                log.info("Currently checking if new & valid: " + aShow);
                // Integer = Season Number -- HashMap = Episodes in that season from episodeNumEpisode
                HashMap<Integer, HashMap<String, String>> seasonEpisode = new HashMap<>(0);
                ArrayList<Integer> seasons = FindLocation.findSeasons(folderLocation, aShow);
                for (Integer aSeason : seasons) {
                    // First String = Episode Number -- Second String = Episode Location
                    HashMap<String, String> episodeNumEpisode = new HashMap<>(0);
                    String[] episodesFull = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
                    if (episodesFull != null) {
                        for (String aEpisode : episodesFull) {
                            ArrayList<Integer> episode = ShowInfoController.getEpisodeSeasonInfo(aEpisode);
                            if (!episode.isEmpty()) {
                                if (episode.size() == 2) {
                                    String episodeNumber = String.valueOf(episode.get(1));
                                    episodeNumEpisode.put(episodeNumber, (folderLocation + "\\" + aShow + '\\' + "Season " + aSeason + '\\' + aEpisode));
                                } else if (episode.size() == 3) {
                                    String episodeNumber = String.valueOf(episode.get(1) + "+" + episode.get(2));
                                    episodeNumEpisode.put(episodeNumber, (folderLocation + "\\" + aShow + '\\' + "Season " + aSeason + '\\' + aEpisode));
                                } else {
                                    log.warning("Error 1 if at this point!" + " + " + episode);
                                }
                            }
                        }
                        if (!episodeNumEpisode.isEmpty()) {
                            seasonEpisode.put(aSeason, episodeNumEpisode);
                        }
                    }
                }
                if (!seasonEpisode.keySet().isEmpty()) {
                    newShows.put(aShow, seasonEpisode);
                } else emptyShows.add(aShow);
            }
        }
        runNumber++;
        return newShows;
    }

    public static ArrayList<String> getEmptyShows() {
        return emptyShows;
    }

    private static Boolean isSeasonEmpty(String aShow, Integer aSeason, File folderLocation) {
        String[] episodesFull = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
        if (episodesFull != null) {
            for (String aEpisode : episodesFull) {
                if (!ShowInfoController.getEpisodeSeasonInfo(aEpisode).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
