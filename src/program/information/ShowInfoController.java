package program.information;

import program.io.FileManager;
import program.util.Clock;
import program.util.Variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ShowInfoController {
    private static final Logger log = Logger.getLogger(ShowInfoController.class.getName());

    private static HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile;

    @SuppressWarnings("unchecked")
    public static void loadShowsFile(Boolean forceRegen) {
        if (forceRegen || showsFile == null) {
            if (ProgramSettingsController.getDirectoriesNames().size() > 1) {
                ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = getDirectoriesHashMaps(-1);
                // This crazy thing is to combine all found Shows/Seasons/Episodes from all directory's into one HashMap.
                long timer = Clock.getTimeMilliSeconds();
                showsFile = new HashMap<>();
                ArrayList<String> allShows = new ArrayList<>();
                showsFileArray.stream().filter(aHashMap -> aHashMap != null).forEach(aHashMap -> aHashMap.keySet().stream().filter(aShow -> !allShows.contains(aShow)).forEach(allShows::add));
                allShows.forEach(aShow -> {
                    HashMap<Integer, HashMap<String, String>> seasonEpisode = new HashMap<>();
                    ArrayList<Integer> allShowSeasons = new ArrayList<>();
                    showsFileArray.stream().filter(aHashMap -> aHashMap.containsKey(aShow)).forEach(aHashMap -> aHashMap.get(aShow).keySet().stream().filter(aSeason -> !allShowSeasons.contains(aSeason)).forEach(allShowSeasons::add));
                    allShowSeasons.forEach(aSeason -> {
                        HashMap<String, String> episodeNumEpisode = new HashMap<>();
                        showsFileArray.stream().filter(aHashMap -> aHashMap.containsKey(aShow) && aHashMap.get(aShow).containsKey(aSeason)).forEach(aHashMap -> aHashMap.get(aShow).get(aSeason).keySet().forEach(aEpisode -> episodeNumEpisode.put(aEpisode, aHashMap.get(aShow).get(aSeason).get(aEpisode))));
                        seasonEpisode.put(aSeason, episodeNumEpisode);
                    });
                    showsFile.put(aShow, seasonEpisode);
                });
                log.info("ShowInfoController- It took " + Clock.timeTakenMilli(timer) + " nanoseconds to combine all files");
            } else {
                FileManager fileManager = new FileManager();
                ProgramSettingsController.getDirectoriesNames().forEach(aString -> showsFile = (HashMap<String, HashMap<Integer, HashMap<String, String>>>) fileManager.loadFile(Variables.DirectoriesFolder, aString, Variables.EmptyString));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> getDirectoriesHashMaps(int skip) {
        // ArrayList = Shows list from all added Directories
        ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = new ArrayList<>();
        ArrayList<String> files = ProgramSettingsController.getDirectoriesNames();
        FileManager fileManager = new FileManager();
        files.forEach(aString -> {
            int place = Integer.parseInt(aString.split("\\-|\\.")[1]);
            if (skip != place) {
                showsFileArray.add((HashMap<String, HashMap<Integer, HashMap<String, String>>>) fileManager.loadFile(Variables.DirectoriesFolder, aString, Variables.EmptyString));
            }
        });
        return showsFileArray;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, HashMap<Integer, HashMap<String, String>>> getDirectoryHashMap(int index) {
        HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile = new HashMap<>();
        ArrayList<String> files = ProgramSettingsController.getDirectoriesNames();
        FileManager fileManager = new FileManager();
        for (String aFile : files) {
            if (aFile.split("\\-|\\.")[1].matches(String.valueOf(index))) {
                showsFile = (HashMap<String, HashMap<Integer, HashMap<String, String>>>) fileManager.loadFile(Variables.DirectoriesFolder, aFile, Variables.EmptyString);
                break;
            }
        }
        return showsFile;
    }

    public static ArrayList<String> getShowsList() {
        loadShowsFile(false);
        ArrayList<String> showsList = new ArrayList<>();
        if (showsFile != null) {
            showsList.addAll(showsFile.keySet().stream().collect(Collectors.toList()));
        }
        return showsList;
    }

    public static Set<Integer> getSeasonsList(String show) {
        loadShowsFile(false);
        return showsFile.get(show).keySet();
    }

    public static Set<String> getEpisodesList(String show, int season) {
        loadShowsFile(false);
        return showsFile.get(show).get(season).keySet();
    }

    public static String getEpisode(String show, String season, String episode) {
        loadShowsFile(false);
        HashMap<Integer, HashMap<String, String>> seasonEpisode = showsFile.get(show);
        int aSeason = Integer.parseInt(String.valueOf(season));
        HashMap<String, String> episodeNumEpisode = seasonEpisode.get(aSeason);
        if (episodeNumEpisode != null) {
            return episodeNumEpisode.get(episode);
        } else {
            log.warning("Error 1");
            return null;
        }
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

    public static String findLowestEpisode(Set<String> episodes) {
        final String[] lowestEpisodeString = new String[1];
        final int[] lowestEpisodeInt = {-1};
        if (episodes != null) {
            episodes.forEach(aEpisode -> {
                if (aEpisode.contains("+")) {
                    String[] temp = aEpisode.split("[+]");
                    int temp1 = Integer.parseInt(temp[0]),
                            temp2 = Integer.parseInt(temp[1]);
                    if (lowestEpisodeInt[0] == -1 || temp2 < lowestEpisodeInt[0]) {
                        lowestEpisodeInt[0] = temp1;
                        lowestEpisodeString[0] = aEpisode;
                    }
                } else if (lowestEpisodeInt[0] == -1 || Integer.parseInt(aEpisode) < lowestEpisodeInt[0]) {
                    lowestEpisodeInt[0] = Integer.parseInt(aEpisode);
                    lowestEpisodeString[0] = aEpisode;
                }
            });
        }
        return lowestEpisodeString[0];
    }

    public static int findHighestEpisode(Set<String> episodes) {
        final int[] highestEpisode = {-1};
        if (episodes != null) {
            episodes.forEach(aEpisode -> {
                int episode;
                if (aEpisode.contains("+")) {
                    episode = Integer.parseInt(aEpisode.split("[+]")[1]);
                } else episode = Integer.parseInt(aEpisode);
                if (highestEpisode[0] == -1 || episode > highestEpisode[0]) {
                    highestEpisode[0] = episode;
                }
            });
        }
        return highestEpisode[0];
    }

    public static boolean doesShowExist(String aShow) {
        loadShowsFile(false);
        return showsFile.containsKey(aShow);
    }

    public static boolean doesShowExistElsewhere(String aShow, ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray) {
        final Boolean[] showExistsElsewhere = {false};
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
        loadShowsFile(false);
        log.info("Printing out all Shows and Episodes:");
        if (showsFile != null) {
            final int[] numberOfShows = {0};
            showsFile.keySet().forEach(aShow -> {
                log.info(aShow);
                showsFile.get(aShow).keySet().forEach(aSeason -> {
                    log.info("Season: " + aSeason);
                    HashMap<String, String> episodes = showsFile.get(aShow).get(aSeason);
                    episodes.keySet().forEach(aEpisode -> log.info(episodes.get(aEpisode)));
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
    public static ArrayList<Integer> getEpisodeSeasonInfo(String aEpisode) {
        ArrayList<Integer> bothInt = new ArrayList<>();
        Pattern MainP = Pattern.compile("s\\d{1,4}e\\d{1,4}");
        Matcher MainM = MainP.matcher(aEpisode.toLowerCase());
        if (MainM.find()) {
            Pattern pattern = Pattern.compile("s\\d{1,4}e\\d{1,4}[e|-]\\d{1,4}");
            Matcher match = pattern.matcher(aEpisode.toLowerCase());
            String info;
            Boolean isDouble = false;
            if (match.find()) {
                info = match.group();
                isDouble = true;
            } else {
                info = MainM.group();
            }
            String splitResult = info.toLowerCase().replaceFirst("s", Variables.EmptyString);
            splitResult = splitResult.toLowerCase().replaceFirst("e", " ");

            if (isDouble) {
                if (splitResult.contains("-")) {
                    splitResult = splitResult.replaceFirst("-", " ");
                } else if (splitResult.contains("e")) {
                    splitResult = splitResult.replaceFirst("e", " ");
                }
            }
            String[] bothString = splitResult.split(" ");
            bothInt.add(Integer.valueOf(bothString[0]));
            bothInt.add(Integer.valueOf(bothString[1]));
            if (isDouble) {
                bothInt.add(Integer.valueOf(bothString[2]));
            }
            return bothInt;
        }
        MainP = Pattern.compile("\\d{1,4}x\\d{1,4}");
        MainM = MainP.matcher(aEpisode.toLowerCase());
        if (MainM.find()) {
            String info = MainM.group();
            String splitResult = info.toLowerCase().replaceFirst("x", " ");
            String[] bothString = splitResult.split(" ");
            bothInt.add(Integer.valueOf(bothString[0]));
            bothInt.add(Integer.valueOf(bothString[1]));
            return bothInt;
        }
        return bothInt;
    }

    public static void saveShowsHashMapFile(HashMap<String, HashMap<Integer, HashMap<String, String>>> hashMap, int hashMapIndex) {
        new FileManager().save(hashMap, Variables.DirectoriesFolder, ("Directory-" + String.valueOf(hashMapIndex)), Variables.ShowsExtension, true);
        loadShowsFile(true);
    }
}