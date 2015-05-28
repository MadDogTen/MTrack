package program.io;

import program.information.ShowInfoController;
import program.util.FindLocation;
import program.util.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class UpdateShowFiles {
    private static final Logger log = Logger.getLogger(UpdateShowFiles.class.getName());

    public static void checkForNewOrRemovedSeasons(File folderLocation, String aShow, ArrayList<Integer> changedSeasons, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile, int hashMapIndex) {
        HashMap<Integer, HashMap<String, String>> seasonEpisode = showsFile.get(aShow);
        changedSeasons.forEach(aSeason -> {
            HashMap<String, String> episodeNum = new HashMap<>(0);
            ArrayList<String> episodesFullList = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
            if (episodesFullList.isEmpty()) seasonEpisode.remove(aSeason);
            else {
                episodesFullList.forEach(aEpisode -> {
                    ArrayList<Integer> episode = ShowInfoController.getEpisodeSeasonInfo(aEpisode);
                    if (!episode.isEmpty()) {
                        if (episode.size() == 2) {
                            String episodeNumber = String.valueOf(episode.get(1));
                            episodeNum.put(episodeNumber, aEpisode);
                        } else if (episode.size() == 3) {
                            String episodeNumber = String.valueOf(episode.get(1) + "+" + episode.get(2));
                            episodeNum.put(episodeNumber, aEpisode);
                        } else {
                            log.warning("Error 1 if at this point!" + " + " + episode);
                        }
                    }
                });
                if (!episodeNum.isEmpty() && seasonEpisode.containsKey(aSeason)) {
                    seasonEpisode.replace(aSeason, episodeNum);
                } else if (!episodeNum.isEmpty()) {
                    seasonEpisode.put(aSeason, episodeNum);
                }
            }
        });
        showsFile.replace(aShow, seasonEpisode);
        ShowInfoController.saveShowsHashMapFile(showsFile, hashMapIndex);
    }

    public static void checkForNewOrRemovedEpisodes(File folderLocation, String aShow, Integer aSeason, HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile, int hashMapIndex) {
        HashMap<Integer, HashMap<String, String>> seasonEpisode = showsFile.get(aShow);
        HashMap<String, String> episodeNum = new HashMap<>();
        ArrayList<String> episodesFullList = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
        if (!episodesFullList.isEmpty()) {
            episodesFullList.forEach(aEpisode -> {
                ArrayList<Integer> episode = ShowInfoController.getEpisodeSeasonInfo(aEpisode);
                if (!episode.isEmpty()) {
                    if (episode.size() == 2) {
                        String episodeNumber = String.valueOf(episode.get(1));
                        episodeNum.put(episodeNumber, (folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode));
                    } else if (episode.size() == 3) {
                        String episodeNumber = String.valueOf(episode.get(1) + "+" + episode.get(2));
                        episodeNum.put(episodeNumber, (folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode));
                    } else {
                        log.warning("Error 2 if at this point!" + " + " + episode);
                    }
                }
            });
        }
        seasonEpisode.replace(aSeason, episodeNum);
        showsFile.replace(aShow, seasonEpisode);
        ShowInfoController.saveShowsHashMapFile(showsFile, hashMapIndex);
    }
}
