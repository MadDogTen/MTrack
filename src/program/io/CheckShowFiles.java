package program.io;

import program.Main;
import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.util.Clock;
import program.util.FindLocation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class CheckShowFiles {
    private static final Logger log = Logger.getLogger(CheckShowFiles.class.getName());

    public static void recheckShowFile() {
        ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = ShowInfoController.getShowsFileArray();
        ArrayList<String> activeShows = UserInfoController.getActiveShows();
        for (HashMap<String, HashMap<Integer, HashMap<String, String>>> aHashMap : showsFileArray) {
            int timer = Clock.getTimeSeconds();
            log.finest("Rechecking shows...");
            int hashMapIndex = showsFileArray.indexOf(aHashMap);
            Boolean hasChanged = false;
            for (String aShow : activeShows) {
                log.finest("Currently rechecking " + aShow);
                if (aHashMap.containsKey(aShow)) {
                    File folderLocation = ProgramSettingsController.getDirectory(hashMapIndex);
                    Object[] seasons = aHashMap.get(aShow).keySet().toArray();
                    for (Object aSeason : seasons) {
                        if (CheckShowFiles.hasEpisodesChanged(aShow, (Integer) aSeason, folderLocation, aHashMap)) {
                            hasChanged = true;
                            UpdateShowFiles.checkForNewOrRemovedEpisodes(folderLocation, aShow, (Integer) aSeason, aHashMap, hashMapIndex);
                        }
                    }
                    if (!CheckShowFiles.hasSeasonsChanged(aShow, folderLocation, aHashMap).isEmpty()) {
                        log.finest(aShow + " has changed!");
                        hasChanged = true;
                        UpdateShowFiles.checkForNewOrRemovedSeasons(folderLocation, aShow, aHashMap, hashMapIndex);
                    }
                    if (!Main.running) {
                        break;
                    }
                }
            }
            if (hasChanged && Main.running) {
                log.finest("Some shows have been updated.");
                //ShowInfoController.saveShowsFile();
                log.finest("Finished Rechecking Shows! - It took " + Clock.timeTakenSeconds(timer) + " seconds to finish.");
            } else if (Main.running) {
                log.finest("All shows were the same.");
                log.finest("Finished Rechecking Shows! - It took " + Clock.timeTakenSeconds(timer) + " seconds to finish.");
            }
        }
    }

    public static boolean hasEpisodesChanged(String aShow, Integer aSeason, File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile) {
        Set<String> oldEpisodeList = showsFile.get(aShow).get(aSeason).keySet();
        String[] newEpisodesList = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
        ArrayList<String> newEpisodesListFixed = new ArrayList<>(0);
        if ((oldEpisodeList.isEmpty()) && newEpisodesList == null) {
            return false;
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
                if (!newEpisodesListFixed.contains(aOldEpisode)) return true;
            }
            for (String newEpisode : newEpisodesListFixed) {
                if (!oldEpisodeList.contains(newEpisode)) return true;
            }
        } else return true;
        return false;
    }

    public static ArrayList<Integer> hasSeasonsChanged(String aShow, File folderLocation, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile) {
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
