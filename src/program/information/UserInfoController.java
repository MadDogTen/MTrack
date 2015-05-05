package program.information;

import program.io.FileManager;
import program.util.Variables;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class UserInfoController {
    private static HashMap<String, HashMap<String, String[]>> userSettingsFile;
    private static HashMap<String, String[]> userSettings;
    private static HashMap<String, String[]> showSettings;

    private static void loadUserInfo(String userName) {
        if (userSettingsFile == null) {
            userSettingsFile = FileManager.loadUserInfo(Variables.settingsFolder, userName, Variables.settingsExtension);
        }
        if (userSettingsFile != null && userSettings == null) {
            userSettings = userSettingsFile.get("ProgramSettings");
        }
        if (userSettingsFile != null && showSettings == null) {
            showSettings = userSettingsFile.get("ShowSettings");
        }
    }

    public static String[] getDirectories(String userName) {
        if (userSettings == null) {
            loadUserInfo(userName);
        }
        return userSettings.get("Directory");
    }

    public static String[] getShowSettings(String userName, String show) {
        if (userSettings == null) {
            loadUserInfo(userName);
        }
        return showSettings.get(show);
    }

    public static ArrayList<String> getAllUsers() {
        File folder = new File(FileManager.getDataFolder() + Variables.settingsFolder);
        String[] userFile = folder.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.toLowerCase().endsWith(Variables.settingsExtension));
            }
        });
        ArrayList<String> users = new ArrayList<>();
        if (userFile != null) {
            for (String aUser : userFile) {
                String user = aUser.replace(Variables.settingsExtension, "");
                users.add(user);

            }
        }
        if (!users.isEmpty()) {
            users.remove("Program");
        }
        return users;
    }

    public static Integer getNumberOfUsers(String settingsFolder, String extension, Boolean overRide) {
        if (getAllUsers() != null) {
            return getAllUsers().size();
        }
        return 0;
    }

    public static ArrayList<String> getActiveShows(String userName) {
        if (showSettings == null) {
            loadUserInfo(userName);
        }
        ArrayList<String> activeShows = new ArrayList<>();
        for (String aShow : showSettings.keySet()) {
            Boolean showInfo = Boolean.valueOf(showSettings.get(aShow)[0]);
            if (showInfo) {
                activeShows.add(aShow);
            }
        }
        return activeShows;
    }

    public static void playAnyEpisode(String aShow, int season, String episode, Boolean fileExists, int episodeType) {
        String[] aShowSettings = showSettings.get(aShow);
        if (season == -1) {
            season = Integer.parseInt(aShowSettings[3]);
        }
        if (episode.isEmpty()) {
            if (episodeType == 1) {
                episode = aShowSettings[6];
            } else if (episodeType == 2) {
                int temp = Integer.parseInt(aShowSettings[6]) + 1;
                episode = (aShowSettings[6] + "+" + temp);
            } else {
                episode = aShowSettings[6];
            }
        }
        String showLocation = ShowInfoController.getEpisode(aShow, season, episode);
        if (fileExists) {
            if (showLocation != null) {
                File file = new File(showLocation);
                if (file.exists()) {
                    FileManager.open(file);
                } else {
                    System.out.println("File does not exists!");
                }
            } else System.out.println("File does not exists!");
        }
    }

    public static void changeEpisode(String aShow, int episode, Boolean fileExists, int episodeType) {
        String[] aShowSettings = showSettings.get(aShow);
        if (fileExists && episode == -2) {
            int currentEpisode = Integer.parseInt(aShowSettings[6]);
            if (episodeType == 2) {
                currentEpisode++;
            }
            if (isAnotherEpisode(aShow, Integer.parseInt(aShowSettings[3]), currentEpisode)) {
                currentEpisode++;
                aShowSettings[6] = String.valueOf(currentEpisode);
                System.out.println(aShow + " is now on episode " + aShowSettings[6]);
            } else if (isAnotherSeason(aShow, Integer.parseInt(aShowSettings[3]))) {
                int newSeason = Integer.parseInt(aShowSettings[3]);
                newSeason += 1;
                aShowSettings[3] = String.valueOf(newSeason);
                if (isAnotherEpisode(aShow, Integer.parseInt(aShowSettings[3]), 0)) {
                    aShowSettings[6] = String.valueOf(1);
                    System.out.println(aShow + " is now on episode " + aShowSettings[6]);
                } else {
                    aShowSettings[6] = String.valueOf(0);
                }
            } else {
                currentEpisode++;
                aShowSettings[6] = String.valueOf(currentEpisode);
            }
        } else if (!fileExists && episode == -2) {
            System.out.println("No further files!");
        } else {
            if (doesEpisodeExist(aShow, Integer.parseInt(aShowSettings[3]), episode)) {
                aShowSettings[6] = String.valueOf(episode);
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
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, aSeason);
        if (episodes != null) {
            Set<Integer> episodesInt = episodesToInt(episodes);
            if (episodesInt.contains(episode + 1)) {
                return true;
            }
        }
        return false;
    }

    public static void setToBeginning(String aShow) {
        String[] aShowSettings = showSettings.get(aShow);

        aShowSettings[3] = String.valueOf(ShowInfoController.getLowestSeason(aShow, ShowInfoController.getHighestSeason(aShow)));
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, aShowSettings[3]);
        aShowSettings[6] = String.valueOf(ShowInfoController.getLowestEpisode(aShow, episodes, Integer.parseInt(aShowSettings[5])));

        showSettings.put(aShow, aShowSettings);
        userSettingsFile.put("ShowSettings", showSettings);
        System.out.println(aShow + " is reset to Season " + aShowSettings[3] + " episode " + aShowSettings[6]);
    }

    public static boolean doesEpisodeExist(String aShow, int season, int episode) {
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, season);
        if (episodes != null) {
            Set<Integer> episodesInt = episodesToInt(episodes);
            return episodesInt.contains(episode);
        } else return false;
    }

    public static int doesEpisodeExists(String aShow) {
        String[] aShowSettings = showSettings.get(aShow);
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, aShowSettings[3]);
        if (episodes != null) {
            if (episodes.contains(aShowSettings[6])) {
                return 1;
            } else {
                for (String aEpisode : episodes) {
                    if (aEpisode.contains(aShowSettings[6])) {
                        return 2;
                    }
                }
            }
        }
        return 0;
    }

    public static int doesSeasonEpisodeExists(String aShow, int season, String episode) {
        Set<String> episodes = ShowInfoController.getEpisodesList(aShow, season);
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
        String[] aShowSettings = showSettings.get(aShow);
        int remaining = 0;
        Set<Integer> allSeasons = ShowInfoController.getSeasonsListSet(aShow);
        if (!allSeasons.isEmpty()) {
            Iterator<Integer> seasonsIterator = allSeasons.iterator();
            while (seasonsIterator.hasNext()) {
                if (seasonsIterator.next() < Integer.parseInt(aShowSettings[3])) {
                    seasonsIterator.remove();
                }
            }
            int currentEpisodeInt;
            if (aShowSettings[6].contains("+")) {
                String[] temp = aShowSettings[6].split("\\+");
                currentEpisodeInt = Integer.parseInt(temp[1]);
            } else currentEpisodeInt = Integer.parseInt(aShowSettings[6]);

            for (int aSeason : allSeasons) {
                int episode = 1;
                if (aSeason == Integer.parseInt(aShowSettings[3])) {
                    episode = Integer.parseInt(aShowSettings[6]);
                }
                Set<String> episodes = ShowInfoController.getEpisodesList(aShow, aSeason);
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
                    while (aSeason == Integer.parseInt(aShowSettings[3]) && episodesIterator.hasNext()) {
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

    public static void saveUserSettingsFile(String userName) {
        if (userSettingsFile != null) {
            FileManager.save(userSettingsFile, Variables.settingsFolder, userName, Variables.settingsExtension, true);
            System.out.println("userSettingsFile has been saved!");
        }
    }

    // Getters
    public static int getSeason(String aShow) {
        String[] aShowSettings = showSettings.get(aShow);

        return Integer.parseInt(aShowSettings[3]);
    }

    public static String getEpisode(String aShow) {
        String[] aShowSettings = showSettings.get(aShow);

        return aShowSettings[6];
    }
}
