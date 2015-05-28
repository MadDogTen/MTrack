package program.io;

import program.information.ProgramSettingsController;
import program.information.ShowInfoController;
import program.information.UserInfoController;
import program.util.FindLocation;
import program.util.Strings;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class GenerateNewShowFiles {
    private static final Logger log = Logger.getLogger(GenerateNewShowFiles.class.getName());

    public static void generateShowsFile(int index, File folderLocation, Boolean forceGen, Boolean firstRun) {
        FileManager fileManager = new FileManager();
        if (forceGen || !fileManager.checkFileExists(Variables.DirectoriesFolder, ("Directory-" + String.valueOf(index)), Variables.ShowsExtension)) {
            log.info("Generating ShowsFile for: " + folderLocation);
            // String = Show Name -- HashMap == Seasons in show from seasonEpisode
            HashMap<String, HashMap<Integer, HashMap<String, String>>> showSeasons = new HashMap<>(0);
            final ArrayList<String> ignoredShows;
            if (UserInfoController.getAllUsers().isEmpty()) ignoredShows = new ArrayList<>();
            else ignoredShows = UserInfoController.getIgnoredShows();
            FindLocation.findShows(folderLocation).forEach(aShow -> {
                log.info("Currently Processing: " + aShow);
                // Integer = Season Number -- HashMap = Episodes in that season from episodeNumEpisode
                HashMap<Integer, HashMap<String, String>> seasonEpisode = new HashMap<>(0);
                FindLocation.findSeasons(folderLocation, aShow).forEach(aSeason -> {
                    log.info("Season: " + aSeason);
                    // First String = Episode Number -- Second String = Episode Location
                    HashMap<String, String> episodeNumEpisode = new HashMap<>(0);
                    ArrayList<String> episodesFull = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
                    episodesFull.forEach(aEpisode -> {
                            log.info("Episode: " + aEpisode);
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
                });
                if (!seasonEpisode.keySet().isEmpty()) {
                    showSeasons.put(aShow, seasonEpisode);
                    if (ignoredShows.contains(aShow)) {
                        UserInfoController.setIgnoredStatus(aShow, false);
                    }
                }
            });
            log.info("0");
            ProgramSettingsController.setMainDirectoryVersion(ProgramSettingsController.getMainDirectoryVersion() + 1, !firstRun);
            fileManager.save(showSeasons, Variables.DirectoriesFolder, ("Directory-" + String.valueOf(index)), Variables.ShowsExtension, forceGen);
        }
    }
}