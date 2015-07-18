package program.information;

import program.Main;
import program.information.show.Episode;
import program.information.show.Season;
import program.information.show.Show;
import program.io.FileManager;
import program.util.Strings;
import program.util.Variables;

import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ShowInfoController {
    private final Logger log = Logger.getLogger(ShowInfoController.class.getName());
    private final ProgramSettingsController programSettingsController;
    private Map<String, Show> showsFile;

    public ShowInfoController(ProgramSettingsController programSettingsController) {
        this.programSettingsController = programSettingsController;
    }

    public void loadShowsFile() {
        if (programSettingsController.getDirectoriesNames().size() > 1) {
            long timer = Main.clock.getTimeMilliSeconds();
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
            log.info("ShowInfoController- It took " + Main.clock.timeTakenMilli(timer) + " nanoseconds to combine all files");
        } else {
            FileManager fileManager = new FileManager();
            //noinspection unchecked
            programSettingsController.getDirectoriesNames().forEach(aString -> showsFile = (Map<String, Show>) fileManager.loadFile(Variables.DirectoriesFolder, aString, Strings.EmptyString));
        }
    }

    public Map<String, Show> getShowsFile() {
        return showsFile;
    }

    public ArrayList<Map<String, Show>> getDirectoriesMaps(int skip) {
        // ArrayList = Shows list from all added Directories
        ArrayList<Map<String, Show>> showsFileArray = new ArrayList<>();
        ArrayList<String> files = programSettingsController.getDirectoriesNames();
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
    public Map<String, Show> getDirectoryMap(int index) {
        Map<String, Show> showsFile = new HashMap<>();
        ArrayList<String> files = programSettingsController.getDirectoriesNames();
        FileManager fileManager = new FileManager();
        for (String aFile : files) {
            if (aFile.split("\\-|\\.")[1].matches(String.valueOf(index))) {
                showsFile = (Map<String, Show>) fileManager.loadFile(Variables.DirectoriesFolder, aFile, Strings.EmptyString);
                break;
            }
        }
        return showsFile;
    }

    public ArrayList<String> getShowsList() {
        ArrayList<String> showsList = new ArrayList<>();
        if (showsFile != null) {
            showsList.addAll(showsFile.keySet().stream().collect(Collectors.toList()));
        }
        return showsList;
    }

    public Set<Integer> getSeasonsList(String show) {
        return showsFile.get(show).getSeasons().keySet();
    }

    public Set<Integer> getEpisodesList(String show, int season) {
        if (showsFile.containsKey(show)) {
            return showsFile.get(show).getSeason(season).getEpisodes().keySet();
        } else return new HashSet<>();
    }

    public String getEpisode(String show, int season, int episode) {
        if (showsFile.get(show).getSeason(season).getEpisodes().containsKey(episode)) {
            return showsFile.get(show).getSeason(season).getEpisodes().get(episode).getEpisodeFilename();
        } else {
            log.warning("Error, Please report.");
            //noinspection ReturnOfNull
            return null;
        }
    }

    public boolean isDoubleEpisode(String show, int season, int episode) {
        if (showsFile.get(show).containsSeason(season) && showsFile.get(show).getSeason(season).containsEpisode(episode)) {
            return showsFile.get(show).getSeason(season).getEpisode(episode).isPartOfDoubleEpisode();
        } else return false;
    }

    public int findLowestSeason(String aShow) {
        final int[] lowestSeason = {-1};
        getSeasonsList(aShow).forEach(aSeason -> {
            if (lowestSeason[0] == -1 || aSeason < lowestSeason[0]) {
                lowestSeason[0] = aSeason;
            }
        });
        return lowestSeason[0];
    }

    public int findHighestSeason(String aShow) {
        final int[] highestSeason = {-1};
        Set<Integer> seasons = getSeasonsList(aShow);
        seasons.forEach(aSeason -> {
            if (highestSeason[0] == -1 || aSeason > highestSeason[0]) {
                highestSeason[0] = aSeason;
            }
        });
        return highestSeason[0];
    }

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

    public int findHighestEpisode(Set<Integer> episodes) {
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

    public boolean doesShowExistElsewhere(String aShow, ArrayList<Map<String, Show>> showsFileArray) {
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
        } else {
            log.info("No shows.");
        }
        log.info("Finished printing out all Shows and Episodes.");
    }

    // To get the Season and Episode Number
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

    public void saveShowsMapFile(Map<String, Show> arrayList, int mapIndex, Boolean loadMap) {
        new FileManager().save((Serializable) arrayList, Variables.DirectoriesFolder, ("Directory-" + String.valueOf(mapIndex)), Variables.ShowsExtension, true);
        if (loadMap) {
            loadShowsFile();
        }
    }
}