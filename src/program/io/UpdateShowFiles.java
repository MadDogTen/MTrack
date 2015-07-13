package program.io;

import program.information.Episode;
import program.information.Season;
import program.information.Show;
import program.information.ShowInfoController;
import program.util.FindShows;
import program.util.Strings;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class UpdateShowFiles {
    private final Logger log = Logger.getLogger(UpdateShowFiles.class.getName());

    public void checkForNewOrRemovedSeasons(File folderLocation, String aShow, ArrayList<Integer> changedSeasons, Map<String, Show> showsFile, int hashMapIndex) {
        Show seasonEpisode = showsFile.get(aShow);
        changedSeasons.forEach(aSeason -> {
            Map<Integer, Episode> episodeNum = new HashMap<>(0);
            ArrayList<String> episodesFullList = FindShows.findEpisodes(folderLocation, aShow, aSeason);
            if (episodesFullList.isEmpty()) seasonEpisode.removeSeason(aSeason);
            else {
                episodesFullList.forEach(aEpisode -> {
                    int[] episode = ShowInfoController.getEpisodeInfo(aEpisode);
                    if (episode != null) {
                        if (episode.length == 1) {
                            episodeNum.put(episode[0], new Episode(episode[0], aEpisode));
                        } else if (episode.length == 2) {
                            episodeNum.put(episode[0], new Episode(episode[0], aEpisode));
                            episodeNum.put(episode[1], new Episode(episode[1], aEpisode));
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
        showsFile.replace(aShow, seasonEpisode);
        ShowInfoController.saveShowsArrayListFile(showsFile, hashMapIndex);
    }

    public void checkForNewOrRemovedEpisodes(File folderLocation, String aShow, Integer aSeason, Map<String, Show> showsFile, int hashMapIndex) {
        Show seasonEpisode = showsFile.get(aShow);
        Map<Integer, Episode> episodeNum = new HashMap<>();
        ArrayList<String> episodesFullList = FindShows.findEpisodes(folderLocation, aShow, aSeason);
        if (!episodesFullList.isEmpty()) {
            episodesFullList.forEach(aEpisode -> {
                int[] episode = ShowInfoController.getEpisodeInfo(aEpisode);
                if (episode != null) {
                    if (episode.length == 1) {
                        episodeNum.put(episode[0], new Episode(episode[0], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode));
                    } else if (episode.length == 2) {
                        episodeNum.put(episode[0], new Episode(episode[0], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode));
                        episodeNum.put(episode[1], new Episode(episode[1], folderLocation + Strings.FileSeparator + aShow + Strings.FileSeparator + "Season " + aSeason + Strings.FileSeparator + aEpisode));
                    } else {
                        log.warning("Error 2 if at this point!" + " + " + Arrays.toString(episode));
                    }
                }
            });
        }
        seasonEpisode.addOrReplaceSeason(aSeason, new Season(aSeason, episodeNum));
        showsFile.replace(aShow, seasonEpisode);
        ShowInfoController.saveShowsArrayListFile(showsFile, hashMapIndex);
    }
}
