package program.io;

import program.Main;
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
            ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = ShowInfoController.getShowsFileArray();
            ArrayList<String> activeShows = UserInfoController.getActiveShows();
            for (HashMap<String, HashMap<Integer, HashMap<String, String>>> aHashMap : showsFileArray) {
                log.info("Rechecking shows...");
                int hashMapIndex = showsFileArray.indexOf(aHashMap);
                File folderLocation = ProgramSettingsController.getDirectory(hashMapIndex);
                if (ProgramSettingsController.isDirectoryCurrentlyActive(folderLocation)) {
                    for (String aShow : activeShows) {
                        log.info("Currently rechecking " + aShow);
                        if (aHashMap.containsKey(aShow)) {
                            Object[] seasons = aHashMap.get(aShow).keySet().toArray();
                            for (Object aSeason : seasons) {
                                ArrayList<String> changedEpisodes = hasEpisodesChanged(aShow, (Integer) aSeason, folderLocation, aHashMap);
                                if (!changedEpisodes.isEmpty()) {
                                    hasChanged = true;
                                    UpdateShowFiles.checkForNewOrRemovedEpisodes(folderLocation, aShow, (Integer) aSeason, aHashMap, hashMapIndex);
                                }
                            }
                            ArrayList<Integer> changedSeasons = hasSeasonsChanged(aShow, folderLocation, aHashMap);
                            if (!changedSeasons.isEmpty()) {
                                log.info(aShow + " has changed!");
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
                        for (String aNewShow : changedShows.keySet()) {
                            aHashMap.put(aNewShow, changedShows.get(aNewShow));
                        }
                        ShowInfoController.saveShowsHashMapFile(aHashMap, hashMapIndex);
                        for (String aNewShow : changedShows.keySet()) {
                            UserInfoController.addNewShow(aNewShow);
                        }
                    }
                }
            }
        }
        if (hasChanged && Main.running) {
            log.info("Some shows have been updated.");
            log.info("Finished Rechecking Shows! - It took " + Clock.timeTakenSeconds(timer) + " seconds to finish.");
        } else if (Main.running) {
            log.info("All shows were the same.");
            log.info("Finished Rechecking Shows! - It took " + Clock.timeTakenSeconds(timer) + " seconds to finish.");
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
                if (EpisodeInfo != null) {
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
        HashMap<String, HashMap<Integer, HashMap<String, String>>> showSeasons = new HashMap<>(0);
        Set<String> oldShows = showsFile.keySet();
        String[] newShows = FindLocation.findShows(folderLocation);
        ArrayList<String> newShowsFixed = new ArrayList<>();
        Collections.addAll(newShowsFixed, newShows);
        for (String aShow : newShowsFixed) {
            if (forceRun || runNumber == 5) {
                emptyShows = new ArrayList<>();
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
                            if (episode != null) {
                                if (episode.size() == 2) {
                                    String episodeNumber = String.valueOf(episode.get(1));
                                    episodeNumEpisode.put(episodeNumber, (folderLocation + "\\" + aShow + "\\" + "Season " + aSeason + "\\" + aEpisode));
                                } else if (episode.size() == 3) {
                                    String episodeNumber = String.valueOf(episode.get(1) + "+" + episode.get(2));
                                    episodeNumEpisode.put(episodeNumber, (folderLocation + "\\" + aShow + "\\" + "Season " + aSeason + "\\" + aEpisode));
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
                    showSeasons.put(aShow, seasonEpisode);
                } else emptyShows.add(aShow);
            }
        }
        runNumber++;
        return showSeasons;
    }

    public static ArrayList<String> getEmptyShows() {
        return emptyShows;
    }

    private static Boolean isSeasonEmpty(String aShow, Integer aSeason, File folderLocation) {
        String[] episodesFull = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
        if (episodesFull != null) {
            for (String aEpisode : episodesFull) {
                if (ShowInfoController.getEpisodeSeasonInfo(aEpisode) != null) {
                    return false;
                }
            }
        }
        return true;
    }
}
