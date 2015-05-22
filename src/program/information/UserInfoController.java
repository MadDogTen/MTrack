package program.information;

import program.io.FileManager;
import program.util.Strings;
import program.util.Variables;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.logging.Logger;

public class UserInfoController {
    private static final Logger log = Logger.getLogger(UserInfoController.class.getName());

    private static HashMap<String, HashMap<String, HashMap<String, String>>> userSettingsFile;

    private static void loadUserInfo() {
        if (userSettingsFile == null) {
            userSettingsFile = new FileManager().loadUserInfo(Variables.UsersFolder, Strings.UserName, Variables.UsersExtension);
        }
    }

    public static ArrayList<String> getAllUsers() {
        File folder = new File(Variables.dataFolder + Variables.UsersFolder);
        String[] userFile = folder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(Variables.UsersExtension));
            }
        });
        ArrayList<String> users = new ArrayList<>();
        if (userFile != null) {
            for (String aUser : userFile) {
                String user = aUser.replace(Variables.UsersExtension, Variables.EmptyString);
                users.add(user);

            }
        }
        if (!users.isEmpty()) {
            users.remove("Program");
        }
        return users;
    }

    public static void setIgnoredStatus(String aShow, Boolean ignored) {
        loadUserInfo();
        log.info(aShow + " ignore status is: " + ignored);
        HashMap<String, String> aShowSettings = userSettingsFile.get("ShowSettings").get(aShow);
        log.info(String.valueOf(userSettingsFile.get("ShowSettings").get(aShow)));
        aShowSettings.replace("isIgnored", String.valueOf(ignored));
        userSettingsFile.get("ShowSettings").put(aShow, aShowSettings);
        log.info(String.valueOf(userSettingsFile.get("ShowSettings").get(aShow)));
    }

    public static ArrayList<String> getIgnoredShows() {
        loadUserInfo();
        ArrayList<String> ignoredShows = new ArrayList<>();
        if (userSettingsFile.containsKey("ShowSettings")) {
            for (String aShow : userSettingsFile.get("ShowSettings").keySet()) {
                Boolean isActive = Boolean.valueOf(userSettingsFile.get("ShowSettings").get(aShow).get("isIgnored"));
                if (isActive) {
                    ignoredShows.add(aShow);
                }
            }
        }
        return ignoredShows;
    }

    public static void setActiveStatus(String aShow, Boolean active) {
        loadUserInfo();
        userSettingsFile.get("ShowSettings").get(aShow).replace("isActive", String.valueOf(active));
    }

    public static ArrayList<String> getActiveShows() {
        loadUserInfo();
        ArrayList<String> activeShows = new ArrayList<>();
        for (String aShow : userSettingsFile.get("ShowSettings").keySet()) {
            Boolean isActive = Boolean.valueOf(userSettingsFile.get("ShowSettings").get(aShow).get("isActive"));
            Boolean isIgnored = Boolean.valueOf(userSettingsFile.get("ShowSettings").get(aShow).get("isIgnored"));
            Boolean isHidden = Boolean.valueOf(userSettingsFile.get("ShowSettings").get(aShow).get("isHidden"));
            if (isActive && !isIgnored & !isHidden) {
                activeShows.add(aShow);
            }
        }
        return activeShows;
    }

    public static ArrayList<String> getInactiveShows() {
        loadUserInfo();
        ArrayList<String> inActiveShows = new ArrayList<>();
        for (String aShow : userSettingsFile.get("ShowSettings").keySet()) {
            Boolean isActive = Boolean.valueOf(userSettingsFile.get("ShowSettings").get(aShow).get("isActive"));
            Boolean isIgnored = Boolean.valueOf(userSettingsFile.get("ShowSettings").get(aShow).get("isIgnored"));
            Boolean isHidden = Boolean.valueOf(userSettingsFile.get("ShowSettings").get(aShow).get("isHidden"));
            if (!isActive && !isIgnored && !isHidden) {
                inActiveShows.add(aShow);
            }
        }
        return inActiveShows;
    }

    public static void setHiddenStatus(String aShow, Boolean isHidden) {
        loadUserInfo();
        log.info(aShow + " hidden status is: " + isHidden);
        userSettingsFile.get("ShowSettings").get(aShow).replace("isHidden", String.valueOf(isHidden));
    }

    public static ArrayList<String> getHiddenShows() {
        loadUserInfo();
        ArrayList<String> hiddenShows = new ArrayList<>();
        for (String aShow : userSettingsFile.get("ShowSettings").keySet()) {
            Boolean isIgnored = Boolean.valueOf(userSettingsFile.get("ShowSettings").get(aShow).get("isIgnored"));
            Boolean isHidden = Boolean.valueOf(userSettingsFile.get("ShowSettings").get(aShow).get("isHidden"));
            if (isHidden & !isIgnored) {
                hiddenShows.add(aShow);
            }
        }
        return hiddenShows;
    }

    public static int getUserSettingsVersion() { //TODO Remove -2 return when program is at Version 0.9
        loadUserInfo();
        if (userSettingsFile.containsKey("UserSettings") && userSettingsFile.get("UserSettings").containsKey("UserVersions")) {
            return Integer.parseInt(userSettingsFile.get("UserSettings").get("UserVersions").get("0"));
        } else return -2;
    }

    public static void playAnyEpisode(String aShow, int season, String episode) {
        loadUserInfo();
        log.info("Attempting to play " + aShow + " Season: " + season + " - Episode: " + episode);
        String showLocation = ShowInfoController.getEpisode(aShow, String.valueOf(season), episode);
        log.info(showLocation);
        if (showLocation != null) {
            File file = new File(showLocation);
            if (file.exists()) {
                new FileManager().open(file);
            } else log.warning("File doesn't exists!");
        } else log.warning("File doesn't exists!");
    }

    public static void changeEpisode(String aShow, int episode, Boolean fileExists, int episodeType) {
        loadUserInfo();
        HashMap<String, String> aShowSettings = userSettingsFile.get("ShowSettings").get(aShow);
        if (fileExists && episode == -2) {
            int currentEpisode = Integer.parseInt(aShowSettings.get("CurrentEpisode"));
            if (episodeType == 2) {
                currentEpisode++;
            }
            if (isAnotherEpisode(aShow, Integer.parseInt(aShowSettings.get("CurrentSeason")), currentEpisode)) {
                currentEpisode++;
                aShowSettings.replace("CurrentEpisode", String.valueOf(currentEpisode));
                log.info("UserInfoController- " + aShow + " is now on episode " + aShowSettings.get("CurrentEpisode"));
            } else if (isAnotherSeason(aShow, Integer.parseInt(aShowSettings.get("CurrentSeason")))) {
                int newSeason = Integer.parseInt(aShowSettings.get("CurrentSeason"));
                newSeason += 1;
                aShowSettings.replace("CurrentSeason", String.valueOf(newSeason));
                if (isAnotherEpisode(aShow, Integer.parseInt(aShowSettings.get("CurrentSeason")), 0)) {
                    aShowSettings.replace("CurrentEpisode", String.valueOf(1));
                    log.info("UserInfoController- " + aShow + " is now on episode " + aShowSettings.get("CurrentEpisode"));
                } else {
                    aShowSettings.replace("CurrentEpisode", String.valueOf(0));
                }
            } else {
                currentEpisode++;
                aShowSettings.replace("CurrentEpisode", String.valueOf(currentEpisode));
            }
        } else if (!fileExists && episode == -2) {
            log.info("UserInfoController- No further files!");
        } else {
            if (doesEpisodeExist(aShow, Integer.parseInt(aShowSettings.get("CurrentSeason")), episode)) {
                aShowSettings.replace("CurrentEpisode", String.valueOf(episode));
            }
        }
        userSettingsFile.get("ShowSettings").put(aShow, aShowSettings);
    }

    public static void setSeasonEpisode(String aShow, int season, String episode) {
        loadUserInfo();
        userSettingsFile.get("ShowSettings").get(aShow).replace("CurrentSeason", String.valueOf(season));
        userSettingsFile.get("ShowSettings").get(aShow).replace("CurrentEpisode", episode);
        log.info(aShow + " is now set to Season: " + season + " - Episode: " + episode);
    }

    private static boolean isAnotherSeason(String aShow, int season) {
        Set<Integer> seasons = ShowInfoController.getSeasonsList(aShow);
        season++;
        return seasons.contains(season);
    }

    private static boolean isAnotherEpisode(String aShow, int aSeason, int episode) {
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, Integer.parseInt(String.valueOf(aSeason)));
        if (!episodes.isEmpty()) {
            Set<Integer> episodesInt = episodesToInt(episodes);
            if (!episodesInt.isEmpty() && episodesInt.contains(episode + 1)) {
                return true;
            }
        }
        return false;
    }

    public static void setToBeginning(String aShow) {
        HashMap<String, String> aShowSettings = userSettingsFile.get("ShowSettings").get(aShow);
        aShowSettings.replace("CurrentSeason", String.valueOf(ShowInfoController.findLowestSeason(aShow)));
        aShowSettings.replace("CurrentEpisode", String.valueOf(ShowInfoController.findLowestEpisode(ShowInfoController.getEpisodesList(aShow, Integer.parseInt(aShowSettings.get("CurrentSeason"))))));
        userSettingsFile.get("ShowSettings").put(aShow, aShowSettings);
        log.info("UserInfoController- " + aShow + " is reset to Season " + aShowSettings.get("CurrentSeason") + " episode " + aShowSettings.get("CurrentEpisode"));

    }

    private static boolean doesEpisodeExist(String aShow, int season, int episode) {
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, Integer.parseInt(String.valueOf(season)));
        if (!episodes.isEmpty()) {
            Set<Integer> episodesInt = episodesToInt(episodes);
            if (!episodesInt.isEmpty()) {
                return episodesInt.contains(episode);
            }
        }
        return false;
    }

    public static int doesEpisodeExists(String aShow) {
        HashMap<String, String> aShowSettings = userSettingsFile.get("ShowSettings").get(aShow);
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, Integer.parseInt(aShowSettings.get("CurrentSeason")));
        if (!episodes.isEmpty()) {
            if (episodes.contains(aShowSettings.get("CurrentEpisode"))) {
                return 1;
            } else {
                for (String aEpisode : episodes) {
                    if (aEpisode.contains(aShowSettings.get("CurrentEpisode"))) {
                        return 2;
                    }
                }
            }
        }
        return 0;
    }

    private static Set<Integer> episodesToInt(Set<String> oldEpisodes) {
        Set<Integer> episodes = new HashSet<>();
        if (oldEpisodes != null) {
            for (String aEpisode : oldEpisodes) {
                if (aEpisode.contains("+")) {
                    String[] temp = aEpisode.split("\\+");
                    int temp1 = Integer.parseInt(temp[0]);
                    int temp2 = Integer.parseInt(temp[1]);

                    episodes.add(temp1);
                    episodes.add(temp2);
                } else episodes.add(Integer.valueOf(aEpisode));
            }
        }
        return episodes;
    }

    public static int getCurrentSeason(String aShow) {
        return Integer.parseInt(userSettingsFile.get("ShowSettings").get(aShow).get("CurrentSeason"));
    }

    public static String getCurrentEpisode(String aShow) {
        return userSettingsFile.get("ShowSettings").get(aShow).get("CurrentEpisode");
    }

    public static int getRemainingNumberOfEpisodes(String aShow) {
        HashMap<String, String> aShowSettings = userSettingsFile.get("ShowSettings").get(aShow);
        int remaining = 0;
        Set<Integer> allSeasons = ShowInfoController.getSeasonsList(aShow);
        if (!allSeasons.isEmpty()) {
            Iterator<Integer> seasonsIterator = allSeasons.iterator();
            while (seasonsIterator.hasNext()) {
                if (seasonsIterator.next() < Integer.parseInt(aShowSettings.get("CurrentSeason"))) {
                    seasonsIterator.remove();
                }
            }
            int currentEpisodeInt;
            if (aShowSettings.get("CurrentEpisode").contains("+")) {
                String[] temp = aShowSettings.get("CurrentEpisode").split("\\+");
                currentEpisodeInt = Integer.parseInt(temp[1]);
            } else currentEpisodeInt = Integer.parseInt(aShowSettings.get("CurrentEpisode"));
            for (int aSeason : allSeasons) {
                int episode = 1;
                if (aSeason == Integer.parseInt(aShowSettings.get("CurrentSeason"))) {
                    episode = currentEpisodeInt;
                }
                Set<String> episodes = ShowInfoController.getEpisodesList(aShow, Integer.parseInt(String.valueOf(aSeason)));
                if (!episodes.isEmpty()) {
                    ArrayList<Integer> episodesArray = new ArrayList<>();
                    for (String aEpisode : episodes) {
                        if (aEpisode.contains("+")) {
                            String[] Temp = aEpisode.split("\\+");
                            episodesArray.add(Integer.parseInt(Temp[0]));
                            episodesArray.add(Integer.parseInt(Temp[1]));
                        } else episodesArray.add(Integer.valueOf(aEpisode));
                    }
                    Collections.sort(episodesArray);
                    Iterator<Integer> episodesIterator = episodesArray.iterator();
                    while (aSeason == Integer.parseInt(aShowSettings.get("CurrentSeason")) && episodesIterator.hasNext()) {
                        if (episodesIterator.next() < currentEpisodeInt) {
                            episodesIterator.remove();
                        }
                    }
                    Collections.sort(episodesArray);
                    episodesIterator = episodesArray.iterator();
                    while (episodesIterator.hasNext()) {
                        int e = episodesIterator.next();
                        if (e == episode) {
                            remaining++;
                            episode++;
                            episodesIterator.remove();
                        } else return remaining;
                    }
                    if (!episodesArray.isEmpty()) {
                        return remaining;
                    }
                }
            }
        }
        return remaining;
    }

    public static void addNewShow(String aShow) {
        if (!userSettingsFile.containsKey("ShowSettings")) {
            userSettingsFile.put("ShowSettings", new HashMap<>());
        }
        if (!userSettingsFile.get("ShowSettings").containsKey(aShow)) {
            log.info("Adding " + aShow + " to user settings file.");
            HashMap<String, String> temp = new HashMap<>();
            temp.put("isActive", "false");
            temp.put("isIgnored", "false");
            temp.put("isHidden", "false");
            temp.put("CurrentSeason", String.valueOf(ShowInfoController.findLowestSeason(String.valueOf(aShow))));
            Set<String> episodes = ShowInfoController.getEpisodesList(String.valueOf(aShow), Integer.parseInt(temp.get("CurrentSeason")));
            temp.put("CurrentEpisode", String.valueOf(ShowInfoController.findLowestEpisode(episodes)));
            userSettingsFile.get("ShowSettings").put(aShow, temp);
        }
    }

    public static HashMap<String, HashMap<String, HashMap<String, String>>> getUserSettingsFile() {
        return userSettingsFile;
    }

    public static void setUserSettingsFile(HashMap<String, HashMap<String, HashMap<String, String>>> userSettingsFile) {
        UserInfoController.userSettingsFile = userSettingsFile;
    }

    public static void printAllUserInfo() {
        log.info("Printing all user info for " + Strings.UserName + "...");
        for (String aString : userSettingsFile.keySet()) {
            log.info(aString);
            HashMap<String, HashMap<String, String>> aHashMap = userSettingsFile.get(aString);
            for (String aString1 : aHashMap.keySet()) {
                log.info(aString1 + " - " + String.valueOf(aHashMap.get(aString1)));
            }
        }
        log.info("Finished printing all user info.");
    }

    public static void saveUserSettingsFile() {
        if (userSettingsFile != null) {
            new FileManager().save(userSettingsFile, Variables.UsersFolder, Strings.UserName, Variables.UsersExtension, true);
            log.info("UserInfoController- userSettingsFile has been saved!");
        }
    }
}
