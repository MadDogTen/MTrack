package com.maddogten.mtrack.information;

import com.maddogten.mtrack.information.show.Directory;
import com.maddogten.mtrack.information.show.Episode;
import com.maddogten.mtrack.information.show.Season;
import com.maddogten.mtrack.information.show.Show;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
      ShowInfoController stores the showsFile. It loads all of the show files
      found, and combines them into one master shows file.
 */

public class ShowInfoController {
    private final Logger log = Logger.getLogger(ShowInfoController.class.getName());
    private final DirectoryController directoryController;
    private Map<String, Show> showsFile;

    @SuppressWarnings("SameParameterValue")
    public ShowInfoController(DirectoryController directoryController) {
        this.directoryController = directoryController;
    }

    // This first checks if there are more than 1 saved directory, and if there is, then combines them into a single Map that contains all the shows. If only 1 is found, then just directly uses it.
    public void loadShowsFile() {
        if (directoryController.isReloadShowsFile()) directoryController.setReloadShowsFile(false);
        if (directoryController.getDirectories().isEmpty()) {
            showsFile = new HashMap<>();
            log.info("showsFile was loaded blank, No directories found.");
        } else if (directoryController.getDirectories().size() == 1) {
            //noinspection unchecked
            showsFile = directoryController.getDirectories().get(0).getShows();
            log.info("showsFile was loaded, Only one Directory.");
        } else if (directoryController.getDirectories().size() > 1) {
            long timer = GenericMethods.getTimeMilliSeconds();
            showsFile = new HashMap<>();
            ArrayList<Directory> showsFileArray = directoryController.getDirectories(-2);
            HashSet<String> allShows = new HashSet<>();
            showsFileArray.stream().filter(aMap -> aMap != null).forEach(showsHashSet -> showsHashSet.getShows().forEach((aShow, Show) -> allShows.add(aShow)));
            allShows.forEach(aShow -> {
                Map<Integer, Season> fullSeasons = new HashMap<>();
                HashSet<Integer> seasons = new HashSet<>();
                showsFileArray.stream().filter(aDirectory -> aDirectory.getShows().containsKey(aShow)).forEach(aHashMap -> aHashMap.getShows().get(aShow).getSeasons().forEach((aSeason, seasonMap) -> seasons.add(aSeason)));
                seasons.forEach(aSeason -> {
                    Map<Integer, Episode> episodes = new HashMap<>();
                    showsFileArray.stream().filter(aDirectory -> (aDirectory.getShows().containsKey(aShow) && aDirectory.getShows().get(aShow).getSeasons().containsKey(aSeason))).forEach(aDirectory -> aDirectory.getShows().get(aShow).getSeasons().get(aSeason).getEpisodes().forEach(episodes::put));
                    fullSeasons.put(aSeason, new Season(aSeason, episodes));
                });
                showsFile.put(aShow, new Show(aShow, fullSeasons));
            });
            log.info("showsFile was loaded, It took " + GenericMethods.timeTakenMilli(timer) + " nanoseconds to combine all files");
        }
    }

    public Map<String, Show> getShowsFile() {
        return showsFile;
    }

    // Returns an arrayList of all shows.
    public ArrayList<String> getShowsList() {
        ArrayList<String> showsList = new ArrayList<>();
        if (showsFile != null) showsList.addAll(showsFile.keySet().stream().collect(Collectors.toList()));
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
        if (showsFile.get(show).getSeason(season).getEpisodes().containsKey(episode))
            return showsFile.get(show).getSeason(season).getEpisode(episode).getEpisodeFilename();
        else {
            log.warning("Error for: " + show + " - Season " + season + " - Episode " + episode + ", Please report.");
            return Strings.EmptyString;
        }
    }

    // Returns whether or not an episode is part of a double episode.
    public boolean isDoubleEpisode(String show, int season, int episode) {
        if (showsFile.get(show).containsSeason(season) && showsFile.get(show).getSeason(season).containsEpisode(episode))
            return showsFile.get(show).getSeason(season).getEpisode(episode).isPartOfDoubleEpisode();
        else return false;
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
        if (episodes != null) {
            episodes.forEach(aEpisode -> {
                if (lowestEpisodeInt[0] == -1 || aEpisode < lowestEpisodeInt[0]) {
                    lowestEpisodeInt[0] = aEpisode;
                    lowestEpisodeString[0] = aEpisode;
                }
            });
        }
        return lowestEpisodeString[0];
    }

    // Returns the highest episode in the given set.
    public int findHighestEpisode(Set<Integer> episodes) {
        final int[] highestEpisode = {-1};
        if (episodes != null) {
            episodes.forEach(aEpisode -> {
                if (highestEpisode[0] == -1 || aEpisode > highestEpisode[0]) highestEpisode[0] = aEpisode;
            });
        }
        return highestEpisode[0];
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

    // Debug tool to find out all found shows, the seasons in the shows, and the episodes in the seasons.
    public void printOutAllShowsAndEpisodes() {
        log.info("Printing out all Shows and Episodes:");
        if (showsFile != null) {
            final int[] numberOfShows = {0};
            showsFile.keySet().forEach(aShow -> {
                Show show = showsFile.get(aShow);
                log.info(show.getName());
                show.getSeasons().keySet().forEach(aSeason -> {
                    Season season = show.getSeasons().get(aSeason);
                    log.info("Season: " + season.getSeason());
                    int[] episodes = new int[season.getEpisodes().size()];
                    final int[] iterator = {0};
                    season.getEpisodes().keySet().forEach(aEpisode -> {
                        Episode episode = season.getEpisodes().get(aEpisode);
                        int episodeNum = episode.getEpisode();
                        episodes[iterator[0]] = episodeNum;
                        iterator[0]++;
                    });
                    log.info(Arrays.toString(episodes));
                });
                numberOfShows[0]++;
            });
            log.info("Total Number of Shows: " + numberOfShows[0]);
        } else log.info("No shows.");
        log.info("Finished printing out all Shows and Episodes.");
    }

    // Uses the given string (The full filename of an episode of a show) and returns the episode number.
    public int[] getEpisodeInfo(String aEpisode) {
        int[] bothInt = new int[1];
        Pattern MainP = Pattern.compile("s\\d{1,4}e\\d{1,4}");
        Matcher MainM = MainP.matcher(aEpisode.toLowerCase());
        if (MainM.find()) {
            Pattern pattern = Pattern.compile("s\\d{1,4}e\\d{1,4}[e|-]\\d{1,4}");
            Matcher match = pattern.matcher(aEpisode.toLowerCase());
            String info;
            boolean isDouble = false;
            if (match.find()) {
                info = match.group();
                isDouble = true;
            } else info = MainM.group();
            String splitResult = info.toLowerCase().replaceFirst("s", Strings.EmptyString);
            splitResult = splitResult.toLowerCase().replaceFirst("e", " ");
            if (isDouble) {
                bothInt = new int[2];
                if (splitResult.contains("-")) splitResult = splitResult.replaceFirst("-", " ");
                else if (splitResult.contains("e")) splitResult = splitResult.replaceFirst("e", " ");
            }
            String[] bothString = splitResult.split(" ");
            bothInt[0] = Integer.valueOf(bothString[1]);
            if (isDouble) bothInt[1] = Integer.valueOf(bothString[2]);
            return bothInt;
        }
        Pattern MainP2 = Pattern.compile("\\d{1,4}x\\d{1,4}");
        Matcher MainM2 = MainP2.matcher(aEpisode.toLowerCase());
        if (MainM2.find()) {
            bothInt = new int[2];
            String info = MainM2.group();
            String splitResult = info.toLowerCase().replaceFirst("x", " ");
            String[] bothString = splitResult.split(" ");
            bothInt[0] = Integer.valueOf(bothString[0]);
            bothInt[1] = Integer.valueOf(bothString[1]);
            return bothInt;
        }
        return bothInt;
    }
}