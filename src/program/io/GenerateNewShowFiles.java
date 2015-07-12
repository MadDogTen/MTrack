package program.io;

import program.information.*;
import program.util.FindShows;
import program.util.Variables;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GenerateNewShowFiles {
    private final Logger log = Logger.getLogger(GenerateNewShowFiles.class.getName());

    public void generateShowsFile(int index, File folderLocation) {
        FileManager fileManager = new FileManager();
        if (!fileManager.checkFileExists(Variables.DirectoriesFolder, ("Directory-" + String.valueOf(index)), Variables.ShowsExtension)) {
            log.info("Generating ShowsFile for: " + folderLocation);
            // String = Show Name -- HashMap == Seasons in show from seasonEpisode
            Map<String, Show> shows = new HashMap<>();
            final ArrayList<String> ignoredShows;
            if (UserInfoController.getAllUsers().isEmpty()) ignoredShows = new ArrayList<>();
            else ignoredShows = UserInfoController.getIgnoredShows();
            FindShows.findShows(folderLocation).forEach(aShow -> {
                log.info("Currently Processing: " + aShow);
                // Integer = Season Number -- HashMap = Episodes in that season from episodeNumEpisode
                Map<Integer, Season> seasons = new HashMap<>();
                FindShows.findSeasons(folderLocation, aShow).forEach(aSeason -> {
                    log.info("Season: " + aSeason);
                    // First String = Episode Number -- Second String = Episode Location
                    Map<Integer, Episode> episodes = new HashMap<>();
                    ArrayList<String> episodesFull = FindShows.findEpisodes(folderLocation, aShow, aSeason);
                    episodesFull.forEach(aEpisode -> {
                        log.info("Episode: " + aEpisode);
                        int[] episode = ShowInfoController.getEpisodeSeasonInfo(aEpisode);
                        if (episode != null && episode.length > 0) {
                            episodes.put(episode[0], new Episode(episode[0], aEpisode));
                            if (episode.length == 2) {
                                episodes.put(episode[1], new Episode(episode[0], aEpisode));
                            } else if (episode.length >= 3) {
                                log.warning("Error 1 if at this point!" + " + " + Arrays.toString(episode));
                            }
                        }
                    });
                    if (!episodes.isEmpty()) {
                        seasons.put(aSeason, new Season(aSeason, episodes));
                    }
                });
                if (!seasons.isEmpty()) {
                    shows.put(aShow, new Show(aShow, seasons));
                    if (ignoredShows.contains(aShow)) {
                        UserInfoController.setIgnoredStatus(aShow, false);
                    }
                }
            });
            ProgramSettingsController.setMainDirectoryVersion(ProgramSettingsController.getMainDirectoryVersion() + 1);
            fileManager.save((Serializable) shows, Variables.DirectoriesFolder, ("Directory-" + String.valueOf(index)), Variables.ShowsExtension, false);
        }
    }
}