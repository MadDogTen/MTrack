package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Database.DBShowManager;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.Variables;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/*
      ShowInfoController stores the showsFile. It loads all of the show files
      found, and combines them into one master shows file.
 */

public class ShowInfoController {
    private final Logger log = Logger.getLogger(ShowInfoController.class.getName());
    private DBShowManager dbShowManager;

    public void initDBManager(Connection connection) throws SQLException {
        dbShowManager = new DBShowManager(connection);
    }

    // Returns an arrayList of all shows.
    public ArrayList<Integer> getShows() {
        return dbShowManager.getAllShows();
    }

    public int addShow(String show) {
        return dbShowManager.addShow(show);
    } // TODO Have this report a added Show

    public void removeShow(int showID) {
        dbShowManager.removeShow(showID);
    }

    // Returns a Set of all season in a given show.
    public Set<Integer> getSeasonsList(int showID) {
        return dbShowManager.getSeasons(showID);
    }

    public String getShowNameFromShowID(int showID) {
        return dbShowManager.getShowName(showID);
    }

    public String getShowNameFromEpisodeID(int episodeID) {
        return dbShowManager.getShowNameFromEpisodeID(episodeID);
    }

    // Returns a Set of all episodes in a given shows season.
    public Set<Integer> getEpisodesList(int showID, final int season) {
        return dbShowManager.getSeasonEpisodes(showID, season);
    }

    // Returns a given episode from a given season in a given show.
    public File getEpisode(int episodeID) {
        return dbShowManager.getEpisode(episodeID);
    }

    // Returns whether or not an episode is part of a double episode.
    boolean isDoubleEpisode(int episodeID) {
        return dbShowManager.isEpisodePartOfDoubleEpisode(episodeID);
    }

    boolean isDoubleEpisode(int showID, int season, int episode) {
        return dbShowManager.isEpisodePartOfDoubleEpisode(showID, season, episode);
    }

    public boolean doesEpisodeExist(int showID, int season, int episode) {
        return dbShowManager.doesEpisodeExist(showID, season, episode);
    }

    public boolean doesSeasonExist(int showID, int season) {
        return dbShowManager.doesSeasonExist(showID, season);
    }

    public void addSeason(int showID, int season) { // TODO Have this report a added season
        dbShowManager.addSeason(showID, season);
    }

    public int addEpisode(int showID, int season, int episode, boolean partOfDoubleEpisode) { // TODO Have this report a added episode
        return dbShowManager.addEpisode(showID, season, episode, partOfDoubleEpisode);
    }

    public void addEpisodeFile(int episodeID, int directoryID, String episodeFile) {
        dbShowManager.addEpisodeFile(episodeID, directoryID, episodeFile);
    }

    public int containsShow(String showName) {
        return dbShowManager.doesAlreadyContainShow(showName);
    }

    // Returns the lowest found integer in a set.
    public int findLowestInt(final Set<Integer> integers) {
        final int[] lowestSeason = {-1};
        integers.forEach(aSeason -> {
            if (lowestSeason[0] == -1 || aSeason < lowestSeason[0]) lowestSeason[0] = aSeason;
        });
        return lowestSeason[0];
    }

    // Returns the highest found integer in a set.
    public int findHighestInt(final Set<Integer> integers) {
        final int[] highestInteger = {-1};
        integers.forEach(integer -> {
            if (highestInteger[0] == -1 || integer > highestInteger[0]) highestInteger[0] = integer;
        });
        return highestInteger[0];
    }

    // Checks if the show is found in the given showsFileArray. If it is found, returns true, otherwise returns false.
    public boolean doesShowExistInOtherDirectory(int showID, int... directoriesIgnored) {
        return ClassHandler.directoryController().doesShowExistsInOtherDirectories(showID, directoriesIgnored);
    }

    // Uses the given string (The full filename of an episode of a show) and returns the episode or episodes if its a double episode.
    public int[] getEpisodeInfo(final String aEpisode) {
        final int[] bothInt = new int[]{-2, -2};
        Arrays.asList(Variables.doubleEpisodePatterns, Variables.singleEpisodePatterns).forEach(aPatternArray -> aPatternArray.forEach(aPattern -> {
            if (bothInt[0] == -2) {
                Matcher matcher = aPattern.matcher(aEpisode.toLowerCase());
                if (matcher.find())
                    for (int i = 1; i <= matcher.groupCount(); i++) bothInt[i - 1] = Integer.parseInt(matcher.group(i));
            }
        }));
        if (bothInt[0] == -2) log.info("Couldn't find episode information in: \"" + aEpisode + '\"');
        return bothInt;
    }

    public int getEpisodeID(int showID, int season, int episode) {
        return dbShowManager.getEpisodeID(showID, season, episode);
    }

    /*public Map<Integer, Set<Integer>> getMissingEpisodes(final int aShow) { // TODO Work on later
        if (showsFile.containsKey(aShow) && showsFile.get(aShow).isShowData()) {
            Show show = ClassHandler.showInfoController().getShowsFile().get(aShow);
            int currentSeason = ClassHandler.userInfoController().getCurrentSeason(aShow);
            int currentEpisode = ClassHandler.userInfoController().getCurrentEpisode(aShow);
            Map<Integer, Set<Integer>> missingEpisodes = new HashMap<>();
            log.info("Finding missing episodes for: " + aShow);
            Set<Integer> seasons = show.getSeasons().keySet();
            Iterator<Integer> integerIterator = seasons.iterator();
            while (integerIterator.hasNext()) {
                int seasonInt = integerIterator.next();
                if (seasonInt < currentSeason) integerIterator.remove();
                else break;
            }
            for (int seasonInt : show.getSeasons().keySet()) {
                Season season = show.getSeason(seasonInt);
                log.info("Season: " + seasonInt + " - Number of episodes: " + season.getNumberOfEpisodes());
                if (season.getNumberOfEpisodes() != -1) {
                    Set<Integer> episodes = new HashSet<>();
                    season.getEpisodes().keySet().stream().filter(episodeInt -> episodeInt >= currentEpisode).forEach(episodes::add);
                    for (int episodeInt = seasonInt == currentSeason ? currentEpisode : 1; episodeInt <= season.getNumberOfEpisodes(); episodeInt++) {
                        log.info("Episode: " + episodeInt);
                        if (!episodes.contains(episodeInt)) {
                            if (!missingEpisodes.containsKey(seasonInt))
                                missingEpisodes.put(seasonInt, new HashSet<>());
                            missingEpisodes.get(seasonInt).add(episodeInt);
                        } else episodes.remove(episodeInt);
                        if (episodes.isEmpty() && episodeInt != season.getNumberOfEpisodes()) break;
                    }
                    if (!show.containsSeason(++seasonInt)) break;
                }
            }
            return missingEpisodes;
        }
        return new HashMap<>();
    }*/
}
