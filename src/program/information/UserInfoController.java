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
    private static HashMap<String, HashMap<String, String>> showSettings;

    private static void loadUserInfo() {
        if (userSettingsFile == null) {
            userSettingsFile = FileManager.loadUserInfo(Variables.UsersFolder, Strings.UserName, Variables.UsersExtension);
        }
        if (userSettingsFile != null && showSettings == null) {
            showSettings = userSettingsFile.get("ShowSettings");
        }
    }

    public static HashMap<String, String> getShowSettings(String show) {
        if (showSettings == null) {
            loadUserInfo();
        }
        return showSettings.get(show);
    }

    public static ArrayList<String> getAllUsers() {
        File folder = new File(FileManager.getDataFolder() + Variables.UsersFolder);
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

    public static Integer getNumberOfUsers() {
        if (getAllUsers() != null) {
            return getAllUsers().size();
        }
        return 0;
    }

    public static void setActiveStatus(String aShow, Boolean active) {
        HashMap<String, String> aShowSettings = showSettings.get(aShow);
        aShowSettings.replace("isActive", String.valueOf(active));
        showSettings.put(aShow, aShowSettings);
    }

    public static ArrayList<String> getActiveShows() {
        if (showSettings == null) {
            loadUserInfo();
        }
        ArrayList<String> activeShows = new ArrayList<>();
        for (String aShow : showSettings.keySet()) {
            Boolean isActive = Boolean.valueOf(showSettings.get(aShow).get("isActive"));
            if (isActive) {
                activeShows.add(aShow);
            }
        }
        return activeShows;
    }

    public static ArrayList<String> getInactiveShows() {
        if (showSettings == null) {
            loadUserInfo();
        }
        ArrayList<String> inActiveShows = new ArrayList<>();
        for (String aShow : showSettings.keySet()) {
            Boolean isActive = Boolean.valueOf(showSettings.get(aShow).get("isActive"));
            if (!isActive) {
                inActiveShows.add(aShow);
            }
        }
        return inActiveShows;
    }

    public static void playAnyEpisode(String aShow, int season, String episode, Boolean fileExists, int episodeType) {
        HashMap<String, String> aShowSettings = showSettings.get(aShow);
        if (season == -1) {
            season = Integer.parseInt(aShowSettings.get("CurrentSeason"));
        }
        if (episode.isEmpty()) {
            if (episodeType == 1) {
                episode = aShowSettings.get("CurrentEpisode");
            } else if (episodeType == 2) {
                int temp = Integer.parseInt(aShowSettings.get("CurrentEpisode")) + 1;
                episode = (aShowSettings.get("CurrentEpisode") + "+" + temp);
            } else {
                episode = aShowSettings.get("CurrentEpisode");
            }
        }
        String showLocation = ShowInfoController.getEpisode(aShow, String.valueOf(season), episode);
        if (fileExists) {
            if (showLocation != null) {
                File file = new File(showLocation);
                if (file.exists()) {
                    FileManager.open(file);
                } else {
                    log.warning("UserInfoController- File does not exists!");
                }
            } else log.warning("UserInfoController- File does not exists!");
        }
    }

    public static void changeEpisode(String aShow, int episode, Boolean fileExists, int episodeType) {
        HashMap<String, String> aShowSettings = showSettings.get(aShow);
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
        showSettings.put(aShow, aShowSettings);
        userSettingsFile.put("ShowSettings", showSettings);
    }

    public static boolean isAnotherSeason(String aShow, int season) {
        Set<Integer> seasons = ShowInfoController.getSeasonsListSet(aShow);
        season++;
        return seasons.contains(season);
    }

    public static boolean isAnotherEpisode(String aShow, int aSeason, int episode) {
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, String.valueOf(aSeason));
        if (episodes != null) {
            Set<Integer> episodesInt = episodesToInt(episodes);
            if (episodesInt.contains(episode + 1)) {
                return true;
            }
        }
        return false;
    }

    public static void setToBeginning(String aShow) {
        HashMap<String, String> aShowSettings = showSettings.get(aShow);

        aShowSettings.replace("CurrentSeason", String.valueOf(ShowInfoController.getLowestSeason(aShow, ShowInfoController.getHighestSeason(aShow))));
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, aShowSettings.get("CurrentSeason"));
        aShowSettings.replace("CurrentEpisode", String.valueOf(ShowInfoController.getLowestEpisode(episodes, ShowInfoController.getHighestEpisode(ShowInfoController.getEpisodesList(aShow, aShowSettings.get("CurrentSeason"))))));

        showSettings.put(aShow, aShowSettings);
        userSettingsFile.put("ShowSettings", showSettings);
        log.info("UserInfoController- " + aShow + " is reset to Season " + aShowSettings.get("CurrentSeason") + " episode " + aShowSettings.get("CurrentEpisode"));
    }

    public static boolean doesEpisodeExist(String aShow, int season, int episode) {
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, String.valueOf(season));
        if (episodes != null) {
            Set<Integer> episodesInt = episodesToInt(episodes);
            return episodesInt.contains(episode);
        } else return false;
    }

    public static int doesEpisodeExists(String aShow) {
        HashMap<String, String> aShowSettings = showSettings.get(aShow);
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, aShowSettings.get("CurrentSeason"));
        if (episodes != null) {
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

    public static int doesSeasonEpisodeExists(String aShow, int season, String episode) {
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, String.valueOf(season));
        if (episodes != null) {
            if (episodes.contains(episode)) {
                return 1;
            } else {
                for (String aEpisode : episodes) {
                    if (aEpisode.contains(episode)) {
                        return 2;
                    }
                }
            }
        }
        return 0;
    }

    public static Set<Integer> episodesToInt(Set<String> oldEpisodes) {
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
        } else return null;
        return episodes;
    }

    public static int getRemainingNumberOfEpisodes(String aShow) {
        HashMap<String, String> aShowSettings = showSettings.get(aShow);
        int remaining = 0;
        Set<Integer> allSeasons = ShowInfoController.getSeasonsListSet(aShow);
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
                    episode = Integer.parseInt(aShowSettings.get("CurrentEpisode"));
                }
                Set<String> episodes = ShowInfoController.getEpisodesList(aShow, String.valueOf(aSeason));
                if (episodes != null) {
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
        if (!showSettings.containsKey(aShow)) {
            HashMap<String, String> temp = new HashMap<>();
            temp.put("isActive", "false");
            temp.put("isHidden", "false");
            temp.put("CurrentSeason", String.valueOf(ShowInfoController.getLowestSeason(String.valueOf(aShow), ShowInfoController.getHighestSeason(String.valueOf(aShow)))));
            Set<String> episodes = ShowInfoController.getEpisodesList(String.valueOf(aShow), temp.get("CurrentSeason"));
            temp.put("CurrentEpisode", String.valueOf(ShowInfoController.getLowestEpisode(episodes, ShowInfoController.getHighestEpisode(episodes))));
            showSettings.put(aShow, temp);
        }
    }

    public static void saveUserSettingsFile() {
        if (userSettingsFile != null) {
            userSettingsFile.replace("ShowSettings", showSettings);
            FileManager.save(userSettingsFile, Variables.UsersFolder, Strings.UserName, Variables.UsersExtension, true);
            log.info("UserInfoController- userSettingsFile has been saved!");
        }
    }
}
