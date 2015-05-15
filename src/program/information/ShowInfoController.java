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
    // String = Show Name -- HashMap == Seasons in show from seasonEpisode
    private static HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile;


    // To pull Show Information

    @SuppressWarnings("unchecked")
    public static void loadShowsFile() {
        if (showsFile == null) {
            if (ProgramSettingsController.getDirectoriesNames().size() > 1) {
                ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = getShowsFileArray();

                // This crazy thing is to combine all found Shows/Seasons/Episodes from all directory's into one HashMap.
                long timer = Clock.getTimeMilliSeconds();
                showsFile = new HashMap<>();
                ArrayList<String> allShows = new ArrayList<>();
                for (HashMap<String, HashMap<Integer, HashMap<String, String>>> aHashMap : showsFileArray) {
                    Set<String> showsSet = aHashMap.keySet();
                    for (String aShow : showsSet) {
                        if (!allShows.contains(aShow)) {
                            allShows.add(aShow);
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
                for (String aString : ProgramSettingsController.getDirectoriesNames()) {
                    showsFile = FileManager.loadShows(aString);
                    System.out.println(showsFile);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> getShowsFileArray() {
        // ArrayList = Shows list from all added Directories
        ArrayList<HashMap<String, HashMap<Integer, HashMap<String, String>>>> showsFileArray = new ArrayList<>();
        ArrayList<String> files = ProgramSettingsController.getDirectoriesNames();
        for (String aString : files) {
            int place = files.indexOf(aString);
            showsFileArray.add(place, FileManager.loadShows(aString));
            log.info(aString);
        }
        return showsFileArray;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, HashMap<Integer, HashMap<String, String>>> getShowsFile(int index) {
        HashMap<String, HashMap<Integer, HashMap<String, String>>> showsFile = new HashMap<>();
        ArrayList<String> files = ProgramSettingsController.getDirectoriesNames();
        for (String afile : files) {
            if (afile.contains(String.valueOf(index))) {
                showsFile = FileManager.loadShows(afile);
                break;
            }
        }
        return showsFile;
    }

    public static Object[] getShowsList() {
        loadShowsFile();
        return showsFile.keySet().toArray();
    }

    public static Set<Integer> getSeasonsListSet(String show) {
        loadShowsFile();
        return showsFile.get(show).keySet();
    }

    public static Set<String> getEpisodesList(String show, String season) {
        loadShowsFile();
        int aSeason = Integer.parseInt(String.valueOf(season));
        HashMap<Integer, HashMap<String, String>> seasonEpisode = showsFile.get(show);
        HashMap<String, String> episodeNumEpisode = seasonEpisode.get(aSeason);
        if (episodeNumEpisode != null) {
            return episodeNumEpisode.keySet();
        } else return null;
    }

    public static String getEpisode(String show, String season, String episode) {
        loadShowsFile();
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

    // To get the Season and Episode Number
    public static ArrayList<Integer> getEpisodeSeasonInfo(String aEpisode) {
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
            ArrayList<Integer> bothInt = new ArrayList<>();
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
            ArrayList<Integer> bothInt = new ArrayList<>();
            bothInt.add(Integer.valueOf(bothString[0]));
            bothInt.add(Integer.valueOf(bothString[1]));
            return bothInt;
        }
        return null;
    }

    public static int getLowestSeason(String aShow, int highestSeason) {
        int lowestSeason = highestSeason;
        Set<Integer> seasons = ShowInfoController.getSeasonsListSet(aShow);
        for (int aSeason : seasons) {
            if (aSeason < lowestSeason) {
                lowestSeason = aSeason;
            }
        }
        return lowestSeason;
    }

    public static int getHighestSeason(String aShow) {
        int highestSeason = 0;
        Set<Integer> seasons = ShowInfoController.getSeasonsListSet(aShow);
        for (int aSeason : seasons) {
            if (aSeason > highestSeason) {
                highestSeason = aSeason;
            }
        }
        return highestSeason;
    }

    public static int getLowestEpisode(Set<String> episodes, int highestEpisode) {
        int lowestEpisode = highestEpisode;
        if (episodes != null) {
            for (String aEpisode : episodes) {
                if (aEpisode.contains("+")) {
                    String[] temp = aEpisode.split("[+]");
                    int temp1 = Integer.parseInt(temp[0]),
                            temp2 = Integer.parseInt(temp[1]);
                    if (temp2 < lowestEpisode) {
                        lowestEpisode = temp1;
                    }
                } else if (Integer.parseInt(aEpisode) < lowestEpisode) {
                    lowestEpisode = Integer.parseInt(aEpisode);
                }
            }
        } else return -1;
        return lowestEpisode;
    }

    public static int getHighestEpisode(Set<String> episodes) {
        int highestEpisode = 0;
        if (episodes != null) {
            for (String aEpisode : episodes) {
                if (aEpisode.contains("+")) {
                    String[] temp = aEpisode.split("[+]");
                    int temp1 = Integer.parseInt(temp[0]),
                            temp2 = Integer.parseInt(temp[1]);
                    if (temp1 > highestEpisode) {
                        highestEpisode = temp2;
                    }
                } else if (Integer.parseInt(aEpisode) > highestEpisode) {
                    highestEpisode = Integer.parseInt(aEpisode);
                }
            }
        } else return -1;
        return highestEpisode;
    }

    public static void saveShowsHashMapFile(HashMap<String, HashMap<Integer, HashMap<String, String>>> hashMap, int hashMapIndex) {
        FileManager.save(hashMap, Variables.DirectoriesFolder, ("Directory-" + String.valueOf(hashMapIndex)), Variables.ShowsExtension, true);
        loadShowsFile();
    }

    public static void printOutAllShowsAndEpisodes() {
        loadShowsFile();
        Set<String> Show = showsFile.keySet();
        log.info("Printing out all Shows and Episodes");
        for (String aShow : Show) {
            log.info("\n\n ShowInfoController- " + aShow);
            HashMap<Integer, HashMap<String, String>> seasons = showsFile.get(aShow);
            Set<Integer> season = seasons.keySet();
            for (int aSeason : season) {
                log.info("\n ShowInfoController- " + "Season: " + aSeason);
                HashMap<String, String> episodes = seasons.get(aSeason);
                Set<String> episode = episodes.keySet();
                for (String aEpisode : episode) {
                    log.info("ShowInfoController- " + episodes.get(aEpisode));
                }
            }
        }
    }
}