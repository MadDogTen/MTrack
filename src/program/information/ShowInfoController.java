package program.information;

import program.information.show.Episode;
import program.information.show.Season;
import program.information.show.Show;
import program.io.FileManager;
import program.util.Clock;
import program.util.Strings;
import program.util.Variables;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ShowInfoController {
    private static final Logger log = Logger.getLogger(ShowInfoController.class.getName());

    private static Map<String, Show> showsFile;

    public static void loadShowsFile() {
        if (ProgramSettingsController.getDirectoriesNames().size() > 1) {
            long timer = Clock.getTimeMilliSeconds();
            showsFile = new HashMap<>();
            ArrayList<Map<String, Show>> showsFileArray = getDirectoriesMaps(-1);
            HashSet<String> allShows = new HashSet<>();
            showsFileArray.stream().filter(aMap -> aMap != null).forEach(showsHashSet -> showsHashSet.forEach((aShow, Show) -> allShows.add(aShow)));
            allShows.forEach(aShow -> {
                Map<Integer, Season> fullSeasons = new HashMap<>();
                HashSet<Integer> seasons = new HashSet<>();
                showsFileArray.stream().filter(aHashMap -> aHashMap.containsKey(aShow)).forEach(aHashMap -> aHashMap.get(aShow).getSeasons().forEach((aSeason, seasonMap) -> seasons.add(aSeason)));
                seasons.forEach(aSeason -> {
                    Map<Integer, Episode> episodes = new HashMap<>();
                    showsFileArray.stream().filter(aHashMap -> (aHashMap.containsKey(aShow) && aHashMap.get(aShow).getSeasons().containsKey(aSeason))).forEach(aHashMap -> aHashMap.get(aShow).getSeasons().get(aSeason).getEpisodes().forEach(episodes::put));
                    fullSeasons.put(aSeason, new Season(aSeason, episodes));
                });
                showsFile.put(aShow, new Show(aShow, fullSeasons));
            });
            log.info("ShowInfoController- It took " + Clock.timeTakenMilli(timer) + " nanoseconds to combine all files");
        } else {
            FileManager fileManager = new FileManager();
            //noinspection unchecked
            ProgramSettingsController.getDirectoriesNames().forEach(aString -> showsFile = (Map<String, Show>) fileManager.loadFile(Variables.DirectoriesFolder, aString, Strings.EmptyString));
        }
    }

    public static Map<String, Show> getShowsFile() {
        return showsFile;
    }

    public static ArrayList<Map<String, Show>> getDirectoriesMaps(int skip) {
        // ArrayList = Shows list from all added Directories
        ArrayList<Map<String, Show>> showsFileArray = new ArrayList<>();
        ArrayList<String> files = ProgramSettingsController.getDirectoriesNames();
        FileManager fileManager = new FileManager();
        files.forEach(aString -> {
            int place = Integer.parseInt(aString.split("\\-|\\.")[1]);
            if (skip != place) {
                //noinspection unchecked
                showsFileArray.add((Map<String, Show>) fileManager.loadFile(Variables.DirectoriesFolder, aString, Strings.EmptyString));
            }
        });
        return showsFileArray;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Show> getDirectoryMap(int index) {
        Map<String, Show> showsFile = new HashMap<>();
        ArrayList<String> files = ProgramSettingsController.getDirectoriesNames();
        FileManager fileManager = new FileManager();
        for (String aFile : files) {
            if (aFile.split("\\-|\\.")[1].matches(String.valueOf(index))) {
                showsFile = (Map<String, Show>) fileManager.loadFile(Variables.DirectoriesFolder, aFile, Strings.EmptyString);
                break;
            }
        }
        return showsFile;
    }

    public static ArrayList<String> getShowsList() {
        ArrayList<String> showsList = new ArrayList<>();
        if (showsFile != null) {
            showsList.addAll(showsFile.keySet().stream().collect(Collectors.toList()));
        }
        return showsList;
    }

    public static Set<Integer> getSeasonsList(String show) {
        return showsFile.get(show).getSeasons().keySet();
    }

    public static Set<Integer> getEpisodesList(String show, int season) {
        return showsFile.get(show).getSeason(season).getEpisodes().keySet();
    }

    public static String getEpisode(String show, int season, int episode) {
        if (showsFile.get(show).getSeason(season).getEpisodes().containsKey(episode)) {
            return showsFile.get(show).getSeason(season).getEpisodes().get(episode).getEpisodeFilename();
        } else {
            log.warning("Error, Please report.");
            return null;
        }
    }

    public static boolean isDoubleEpisode(String show, int season, int episode) {
        if (showsFile.get(show).getSeason(season).containsEpisode(episode)) {
            return showsFile.get(show).getSeason(season).getEpisode(episode).isPartOfDoubleEpisode();
        } else return false;
    }

    public static int findLowestSeason(String aShow) {
        final int[] lowestSeason = {-1};
        ShowInfoController.getSeasonsList(aShow).forEach(aSeason -> {
            if (lowestSeason[0] == -1 || aSeason < lowestSeason[0]) {
                lowestSeason[0] = aSeason;
            }
        });
        return lowestSeason[0];
    }

    public static int findHighestSeason(String aShow) {
        final int[] highestSeason = {-1};
        Set<Integer> seasons = ShowInfoController.getSeasonsList(aShow);
        seasons.forEach(aSeason -> {
            if (highestSeason[0] == -1 || aSeason > highestSeason[0]) {
                highestSeason[0] = aSeason;
            }
        });
        return highestSeason[0];
    }

    public static int findLowestEpisode(Set<Integer> episodes) {
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

    public static int findHighestEpisode(Set<Integer> episodes) {
        final int[] highestEpisode = {-1};
        if (episodes != null) {
            episodes.forEach(aEpisode -> {
                if (highestEpisode[0] == -1 || aEpisode > highestEpisode[0]) {
                    highestEpisode[0] = aEpisode;
                }
            });
        }
        return highestEpisode[0];
    }

    public static boolean doesShowExistElsewhere(String aShow, ArrayList<Map<String, Show>> showsFileArray) {
        final boolean[] showExistsElsewhere = {false};
        if (!showsFileArray.isEmpty()) {
            showsFileArray.forEach(aHashMap -> {
                if (aHashMap.containsKey(aShow)) {
                    showExistsElsewhere[0] = true;
                }
            });
        }
        if (showExistsElsewhere[0]) {
            log.info(aShow + " exists elsewhere.");
        } else {
            log.info(aShow + " doesn't exists elsewhere.");
        }
        return showExistsElsewhere[0];
    }


    public static void printOutAllShowsAndEpisodes() {
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
        } else {
            log.info("No shows.");
        }
        log.info("Finished printing out all Shows and Episodes.");
    }

    // To get the Season and Episode Number
    public static int[] getEpisodeInfo(String aEpisode) {
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
            } else {
                info = MainM.group();
            }
            String splitResult = info.toLowerCase().replaceFirst("s", Strings.EmptyString);
            splitResult = splitResult.toLowerCase().replaceFirst("e", " ");

            if (isDouble) {
                bothInt = new int[2];
                if (splitResult.contains("-")) {
                    splitResult = splitResult.replaceFirst("-", " ");
                } else if (splitResult.contains("e")) {
                    splitResult = splitResult.replaceFirst("e", " ");
                }
            }
            String[] bothString = splitResult.split(" ");
            bothInt[0] = Integer.valueOf(bothString[1]);
            if (isDouble) {
                bothInt[1] = Integer.valueOf(bothString[2]);
            }
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

    public static void saveShowsMapFile(Map<String, Show> arrayList, int mapIndex) {
        new FileManager().save((Serializable) arrayList, Variables.DirectoriesFolder, ("Directory-" + String.valueOf(mapIndex)), Variables.ShowsExtension, true);
    }
}