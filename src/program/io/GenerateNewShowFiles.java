package program.io;

import program.information.ShowInfoController;
import program.util.FindLocation;
import program.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class GenerateNewShowFiles {
    public static void generateShowsFile(int fileName, File folderLocation, Boolean forceGen) {
        if (forceGen || !FileManager.checkFileExists("", "Shows", Variables.ShowsExtension)) {
            // String = Show Name -- HashMap == Seasons in show from seasonEpisode
            HashMap<String, HashMap<Integer, HashMap<String, String>>> showSeasons = new HashMap<>(0);
            String[] shows = FindLocation.findShows(folderLocation);
            for (String aShow : shows) {
                System.out.println("Currently Processing: " + aShow);
                // Integer = Season Number -- HashMap = Episodes in that season from episodeNumEpisode
                HashMap<Integer, HashMap<String, String>> seasonEpisode = new HashMap<>(0);
                ArrayList<Integer> seasons = FindLocation.findSeasons(folderLocation, aShow);
                for (Integer aSeason : seasons) {
                    System.out.println("Season: " + aSeason);
                    // First String = Episode Number -- Second String = Episode Location
                    HashMap<String, String> episodeNumEpisode = new HashMap<>(0);
                    String[] episodesFull = FindLocation.findEpisodes(folderLocation, aShow, aSeason);
                    if (episodesFull != null) {
                        for (String aEpisode : episodesFull) {
                            System.out.println("Episode: " + aEpisode);
                            ArrayList<Integer> episode = ShowInfoController.getEpisodeSeasonInfo(aEpisode);
                            if (episode != null) {
                                if (episode.size() == 2) {
                                    String episodeNumber = String.valueOf(episode.get(1));
                                    episodeNumEpisode.put(episodeNumber, (folderLocation + "\\" + aShow + "\\" + "Season " + aSeason + "\\" + aEpisode));
                                } else if (episode.size() == 3) {
                                    String episodeNumber = String.valueOf(episode.get(1) + "+" + episode.get(2));
                                    episodeNumEpisode.put(episodeNumber, (folderLocation + "\\" + aShow + "\\" + "Season " + aSeason + "\\" + aEpisode));
                                } else {
                                    System.out.println("GenerateNewShowFile - Error 1 if at this point!" + " + " + episode);
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
                }
            }
            FileManager.save(showSeasons, "", ("Directory-" + String.valueOf(fileName)), Variables.ShowsExtension, forceGen);
        }
    }
}