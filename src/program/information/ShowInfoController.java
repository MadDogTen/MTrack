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
                for (HashMap<String, HashMap<Integer, HashMap<String, String>>> aHashMap : showsFileArray) {
                    if (aHashMap != null) {
                        Set<String> showsSet = aHashMap.keySet();
                        for (String aShow : showsSet) {
                            if (!allShows.contains(aShow)) {
                                allShows.add(aShow);
                            }
                        }
                    }
                }
                for (String aShow : allShows) {
                    HashMap<Integer, HashMap<String, String>> seasonEpisode = new HashMap<>();
                    ArrayList<Integer> allShowSeasons = new ArrayList<>();
                    for (HashMap<String, HashMap<Integer, HashMap<String, String>>> aHashMap : showsFileArray) {
                        if (aHashMap.containsKey(aShow)) {
                            Set<Integer> seasonSet = aHashMap.get(aShow).keySet();
                            for (int aSeason : seasonSet) {
                                if (!allShowSeasons.contains(aSeason)) {
                                    allShowSeasons.add(aSeason);
                                }
                            }
                        }
                    }
                    for (Integer aSeason : allShowSeasons) {
                        HashMap<String, String> episodeNumEpisode = new HashMap<>();
                        for (HashMap<String, HashMap<Integer, HashMap<String, String>>> aHashMap : showsFileArray) {
                            if (aHashMap.containsKey(aShow) && aHashMap.get(aShow).containsKey(aSeason)) {
                                Set<String> allShowSeasonEpisodes = aHashMap.get(aShow).get(aSeason).keySet();
                                for (String aEpisode : allShowSeasonEpisodes) {
                                    episodeNumEpisode.put(aEpisode, aHashMap.get(aShow).get(aSeason).get(aEpisode));
                                }
                            }
                        }
                        seasonEpisode.put(aSeason, episodeNumEpisode);
                    }
                    showsFile.put(aShow, seasonEpisode);
                }
                log.info("ShowInfoController- It took " + Clock.timeTakenMilli(timer) + " nanoseconds to combine all files");
            } else {
                FileManager fileManager = new FileManager();
                for (String aString : ProgramSettingsController.getDirectoriesNames()) {
                    showsFile = fileManager.loadShows(aString);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> getDirectoriesHashMaps(int skip) {
        // ArrayList = Shows list from all added Directories
        ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = new ArrayList<>();
        ArrayList<String> files = ProgramSettingsController.getDirectoriesNames();
        FileManager fileManager = new FileManager();
        for (String aString : files) {
            int place = Integer.parseInt(aString.split("[-]|[.]")[1]);
            if (skip != place) {
                showsFileArray.add(fileManager.loadShows(aString));
            }
        }
        return showsFileArray;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, HashMap<Integer, HashMap<String, String>>> getDirectoryHashMap(int index) {
        HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile = new HashMap<>();
        ArrayList<String> files = ProgramSettingsController.getDirectoriesNames();
        FileManager fileManager = new FileManager();
        for (String aFile : files) {
            if (aFile.split("[-]|[.]")[1].matches(String.valueOf(index))) {
                showsFile = fileManager.loadShows(aFile);
                break;
            }
        }
        return showsFile;
    }

    public static Object[] getShowsList() {
        loadShowsFile(false);
        if (showsFile != null) {
            return showsFile.keySet().toArray();
        } else return null;
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
        int lowestSeason = -1;
        Set<Integer> seasons = ShowInfoController.getSeasonsList(aShow);
        for (int aSeason : seasons) {
            if (lowestSeason == -1) {
                lowestSeason = aSeason;
            } else if (aSeason < lowestSeason) {
                lowestSeason = aSeason;
            }
        }
        return lowestSeason;
    }

    public static int findLowestEpisode(Set<String> episodes) {
        int lowestEpisode = -1;
        if (episodes != null) {
            for (String aEpisode : episodes) {
                if (aEpisode.contains("+")) {
                    String[] temp = aEpisode.split("[+]");
                    int temp1 = Integer.parseInt(temp[0]),
                            temp2 = Integer.parseInt(temp[1]);
                    if (lowestEpisode == -1) {
                        lowestEpisode = temp1;
                    } else if (temp2 < lowestEpisode) {
                        lowestEpisode = temp1;
                    }
                } else if (lowestEpisode == -1) {
                    lowestEpisode = Integer.parseInt(aEpisode);
                } else if (Integer.parseInt(aEpisode) < lowestEpisode) {
                    lowestEpisode = Integer.parseInt(aEpisode);
                }
            }
        }
        return lowestEpisode;
    }

    public static boolean doesShowExistElsewhere(String aShow, ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray) {
        Boolean showExistsElsewhere = false;
        if (!showsFileArray.isEmpty()) {
            for (HashMap<String, HashMap<Integer, HashMap<String, String>>> aHashMap : showsFileArray) {
                if (aHashMap.containsKey(aShow)) {
                    showExistsElsewhere = true;
                }
            }
        }
        if (showExistsElsewhere) {
            log.info(aShow + " exists elsewhere.");
        } else {
            log.info(aShow + " doesn't exists elsewhere.");
        }
        return showExistsElsewhere;
    }


    public static void printOutAllShowsAndEpisodes() {
        loadShowsFile(false);
        log.info("Printing out all Shows and Episodes:");
        if (showsFile != null) {
            Set<String> Show = showsFile.keySet();
            int numberOfShows = 0;
            for (String aShow : Show) {
                log.info(aShow);
                HashMap<Integer, HashMap<String, String>> seasons = showsFile.get(aShow);
                Set<Integer> season = seasons.keySet();
                for (int aSeason : season) {
                    log.info("Season: " + aSeason);
                    HashMap<String, String> episodes = seasons.get(aSeason);
                    Set<String> episode = episodes.keySet();
                    for (String aEpisode : episode) {
                        log.info(episodes.get(aEpisode));
                    }
                }
                numberOfShows++;
            }
            log.info("Total Number of Shows: " + numberOfShows);
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