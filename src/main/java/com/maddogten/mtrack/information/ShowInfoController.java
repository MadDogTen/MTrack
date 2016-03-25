package com.maddogten.mtrack.information;

import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.Episode;
import com.maddogten.mtrack.information.show.Season;
import com.maddogten.mtrack.information.show.Show;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/*
      ShowInfoController stores the showsFile. It loads all of the show files
      found, and combines them into one master shows file.
 */

public class ShowInfoController {
    private final Logger log = Logger.getLogger(ShowInfoController.class.getName());
    private Map<String, Show> showsFile;

    // This first checks if there are more than 1 saved directory, and if there is, then combines them into a single Map that contains all the shows. If only 1 is found, then just directly uses it.
    public void loadShowsFile(ArrayList<Directory> directories) {
        if (directories.size() == 1) {
            showsFile = directories.get(0).getShows();
            log.info("showsFile was loaded, Only one Directory.");
        } else if (directories.size() > 1) {
            long timer = GenericMethods.getTimeMilliSeconds();
            showsFile = new HashMap<>();
            HashSet<String> allShows = new HashSet<>();
            directories.stream().filter(aMap -> aMap != null).forEach(showsHashSet -> showsHashSet.getShows().forEach((aShow, Show) -> allShows.add(aShow)));
            allShows.forEach(aShow -> {
                Map<Integer, Season> fullSeasons = new HashMap<>();
                HashSet<Integer> seasons = new HashSet<>();
                directories.stream().filter(aDirectory -> aDirectory.getShows().containsKey(aShow)).forEach(aHashMap -> aHashMap.getShows().get(aShow).getSeasons().forEach((aSeason, seasonMap) -> seasons.add(aSeason)));
                seasons.forEach(aSeason -> {
                    Map<Integer, Episode> episodes = new HashMap<>();
                    directories.stream().filter(aDirectory -> (aDirectory.getShows().containsKey(aShow) && aDirectory.getShows().get(aShow).getSeasons().containsKey(aSeason))).forEach(aDirectory -> aDirectory.getShows().get(aShow).getSeasons().get(aSeason).getEpisodes().forEach(episodes::put));
                    fullSeasons.put(aSeason, new Season(aSeason, episodes));
                });
                showsFile.put(aShow, new Show(aShow, fullSeasons));
            });
            log.info("showsFile was loaded, It took " + GenericMethods.timeTakenMilli(timer) + " nanoseconds to combine all files");
        } else {
            if (directories.isEmpty())
                log.info("showsFile was loaded empty, No directories found.");
            else log.info("showsFile loaded empty, Unknown reason.");
            showsFile = new HashMap<>();
        }
    }

    public Map<String, Show> getShowsFile() {
        return showsFile;
    }

    // Returns an arrayList of all shows.
    public ArrayList<String> getShowsList() {
        ArrayList<String> showsList = new ArrayList<>();
        if (showsFile != null) showsList.addAll(showsFile.keySet());
        return showsList;
    }

    // Returns a Set of all season in a given show.
    public Set<Integer> getSeasonsList(String show) {
        return showsFile.get(show).getSeasons().keySet();
    }

    // Returns a Set of all episodes in a given shows season.
    public Set<Integer> getEpisodesList(String show, int season) {
        if (showsFile.containsKey(show) && showsFile.get(show).getSeasons().containsKey(season)) {
            return showsFile.get(show).getSeason(season).getEpisodes().keySet();
        } else return new HashSet<>();
    }

    // Returns a given episode from a given season in a given show.
    public String getEpisode(String show, int season, int episode) {
        return showsFile.get(show).getSeason(season).getEpisodes().containsKey(episode) ? showsFile.get(show).getSeason(season).getEpisode(episode).getEpisodeFilename() : Strings.EmptyString;
    }

    // Returns whether or not an episode is part of a double episode.
    public boolean isDoubleEpisode(String show, int season, int episode) {
        return showsFile.get(show).containsSeason(season) && showsFile.get(show).getSeason(season).containsEpisode(episode) && showsFile.get(show).getSeason(season).getEpisode(episode).isPartOfDoubleEpisode();
    }

    // Returns the lowest found season in a show.
    public int findLowestSeason(String aShow) {
        final int[] lowestSeason = {-1};
        getSeasonsList(aShow).forEach(aSeason -> {
            if (lowestSeason[0] == -1 || aSeason < lowestSeason[0]) lowestSeason[0] = aSeason;
        });
        return lowestSeason[0];
    }

    // Returns the highest found season in a show.
    public int findHighestSeason(String aShow) {
        final int[] highestSeason = {-1};
        Set<Integer> seasons = getSeasonsList(aShow);
        seasons.forEach(aSeason -> {
            if (highestSeason[0] == -1 || aSeason > highestSeason[0]) highestSeason[0] = aSeason;
        });
        return highestSeason[0];
    }

    // Returns the lowest episode in the given set.
    public int findLowestEpisode(Set<Integer> episodes) {
        final int[] lowestEpisodeString = new int[1];
        final int[] lowestEpisodeInt = {-1};
        episodes.forEach(aEpisode -> {
            if (lowestEpisodeInt[0] == -1 || aEpisode < lowestEpisodeInt[0]) {
                lowestEpisodeInt[0] = aEpisode;
                lowestEpisodeString[0] = aEpisode;
            }
        });
        return lowestEpisodeString[0];
    }

    // Returns the highest episode in the given set.
    public int findHighestEpisode(Set<Integer> episodes) {
        final int[] highestEpisode = {-1};
        episodes.forEach(aEpisode -> {
            if (highestEpisode[0] == -1 || aEpisode > highestEpisode[0]) highestEpisode[0] = aEpisode;
        });
        return highestEpisode[0];
    }

    public Map<Integer, Set<Integer>> getMissingEpisodes(String aShow) {
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
    }

    // Checks if the show is found in the given showsFileArray. If it is found, returns true, otherwise returns false.
    public boolean doesShowExistElsewhere(String aShow, ArrayList<Directory> showsFileArray) {
        final boolean[] showExistsElsewhere = {false};
        if (!showsFileArray.isEmpty()) {
            showsFileArray.forEach(aDirectory -> {
                if (aDirectory.getShows().containsKey(aShow)) showExistsElsewhere[0] = true;
            });
        }
        if (showExistsElsewhere[0]) log.info(aShow + " exists elsewhere.");
        else log.info(aShow + " doesn't exists elsewhere.");
        return showExistsElsewhere[0];
    }

    // Uses the given string (The full filename of an episode of a show) and returns the episode or episodes if its a double episode.
    public int[] getEpisodeInfo(String aEpisode) {
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
}
