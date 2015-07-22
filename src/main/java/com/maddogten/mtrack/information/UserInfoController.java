package com.maddogten.mtrack.information;

import com.maddogten.mtrack.information.settings.UserSettings;
import com.maddogten.mtrack.information.settings.UserShowSettings;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

public class UserInfoController {
    private final Logger log = Logger.getLogger(UserInfoController.class.getName());
    private final ShowInfoController showInfoController;
    private UserSettings userSettings;

    public UserInfoController(ShowInfoController showInfoController) {
        this.showInfoController = showInfoController;
    }

    @SuppressWarnings("unchecked")
    public void loadUserInfo() {
        this.userSettings = (UserSettings) new FileManager().loadFile(Variables.UsersFolder, Strings.UserName, Variables.UsersExtension);
    }

    public ArrayList<String> getAllUsers() {
        File folder = new File(Variables.dataFolder + Variables.UsersFolder);
        ArrayList<String> users = new ArrayList<>();
        if (new FileManager().checkFolderExists(folder)) {
            Collections.addAll(users, folder.list((dir, name) -> (name.toLowerCase().endsWith(Variables.UsersExtension) && !name.toLowerCase().matches("Program"))));
        }
        ArrayList<String> usersCleaned = new ArrayList<>();
        users.forEach(aUser -> usersCleaned.add(aUser.replace(Variables.UsersExtension, Strings.EmptyString)));
        return usersCleaned;
    }

    public void setIgnoredStatus(String aShow, boolean ignored) {
        log.info(aShow + " ignore status is: " + ignored);
        userSettings.getAShowSettings(aShow).setIgnored(ignored);
    }

    public ArrayList<String> getIgnoredShows() {
        ArrayList<String> ignoredShows = new ArrayList<>();
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (showSettings.isIgnored()) {
                ignoredShows.add(showSettings.getShowName());
            }
        });
        return ignoredShows;
    }

    public void setActiveStatus(String aShow, boolean active) {
        userSettings.getAShowSettings(aShow).setActive(active);
    }

    public boolean isShowActive(String aShow) {
        return userSettings.getAShowSettings(aShow).isActive();
    }

    public ArrayList<String> getActiveShows() {
        ArrayList<String> activeShows = new ArrayList<>();
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (showSettings.isActive() && !showSettings.isIgnored() && !showSettings.isHidden()) {
                activeShows.add(showSettings.getShowName());
            }
        });
        return activeShows;
    }

    public ArrayList<String> getInactiveShows() {
        ArrayList<String> inActiveShows = new ArrayList<>();
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (!showSettings.isActive() && !showSettings.isIgnored() && !showSettings.isHidden()) {
                inActiveShows.add(showSettings.getShowName());
            }
        });
        return inActiveShows;
    }

    public ArrayList<String> getAllNonIgnoredShows() {
        ArrayList<String> nonIgnoredShows = new ArrayList<>();
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (!showSettings.isIgnored()) {
                nonIgnoredShows.add(showSettings.getShowName());
            }
        });
        return nonIgnoredShows;
    }

    public void setHiddenStatus(String aShow, boolean isHidden) {
        log.info(aShow + " hidden status is: " + isHidden);
        userSettings.getAShowSettings(aShow).setHidden(isHidden);
    }

    public ArrayList<String> getHiddenShows() {
        ArrayList<String> hiddenShows = new ArrayList<>();
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (showSettings.isHidden() && !showSettings.isIgnored()) {
                hiddenShows.add(showSettings.getShowName());
            }
        });
        return hiddenShows;
    }

    public int getUserSettingsVersion() {
        return userSettings.getUserSettingsFileVersion();
    }

    public int getUserDirectoryVersion() {
        return userSettings.getUserDirectoryVersion();
    }

    public void setUserDirectoryVersion(int version) {
        userSettings.setUserDirectoryVersion(version);
    }

    public void playAnyEpisode(String aShow, int season, int episode) { //Todo Find proper place for this
        log.info("Attempting to play " + aShow + " Season: " + season + " - Episode: " + episode);
        String showLocation = showInfoController.getEpisode(aShow, season, episode);
        log.info(showLocation);
        if (showLocation != null) {
            File file = new File(showLocation);
            if (file.exists()) {
                new FileManager().open(file);
            } else log.warning("File doesn't exists!");
        } else log.warning("File doesn't exists!");
    }

    public void changeEpisode(String aShow, int episode, boolean fileExists) {
        if (fileExists && episode == -2) {
            int currentSeason = userSettings.getAShowSettings(aShow).getCurrentSeason();
            int currentEpisode = userSettings.getAShowSettings(aShow).getCurrentEpisode();
            Boolean isDoubleEpisode = showInfoController.isDoubleEpisode(aShow, userSettings.getAShowSettings(aShow).getCurrentSeason(), currentEpisode);
            if (isDoubleEpisode) {
                currentEpisode++;
            }
            if (isAnotherEpisode(aShow, currentSeason, currentEpisode)) {
                userSettings.getAShowSettings(aShow).setCurrentEpisode(currentEpisode);
                log.info(aShow + " is now on episode " + userSettings.getAShowSettings(aShow).getCurrentEpisode());
            } else if (isAnotherSeason(aShow, currentSeason)) {
                userSettings.getAShowSettings(aShow).setCurrentSeason(currentSeason++);
                if (isAnotherEpisode(aShow, currentSeason, 0))
                    userSettings.getAShowSettings(aShow).setCurrentEpisode(0);
                else {
                    userSettings.getAShowSettings(aShow).setCurrentEpisode(0);
                    log.info(aShow + " is now on episode " + userSettings.getAShowSettings(aShow).getCurrentEpisode());
                }
            } else {
                currentEpisode++;
                userSettings.getAShowSettings(aShow).setCurrentEpisode(currentEpisode);
            }
        } else if (!fileExists && episode == -2) {
            log.info("No further files!");
        } else {
            if (doesEpisodeExist(aShow, userSettings.getAShowSettings(aShow).getCurrentSeason(), episode)) {
                userSettings.getAShowSettings(aShow).setCurrentEpisode(episode);
            }
        }
    }

    public void setSeasonEpisode(String aShow, int season, int episode) {
        userSettings.getAShowSettings(aShow).setCurrentSeason(season);
        userSettings.getAShowSettings(aShow).setCurrentEpisode(episode);
        log.info(aShow + " is now set to Season: " + season + " - Episode: " + episode);
    }

    private boolean isAnotherSeason(String aShow, int season) {
        Set<Integer> seasons = showInfoController.getSeasonsList(aShow);
        season++;
        return seasons.contains(season);
    }

    private boolean isAnotherEpisode(String aShow, int aSeason, int episode) {
        Set<Integer> episodes = showInfoController.getEpisodesList(aShow, aSeason);
        int newEpisode = episode + 1;
        if (!episodes.isEmpty()) {
            if (episodes.contains(newEpisode)) {
                return true;
            }
        }
        return false;
    }

    public void setToBeginning(String aShow) {
        userSettings.getAShowSettings(aShow).setCurrentSeason(showInfoController.findLowestSeason(aShow));
        userSettings.getAShowSettings(aShow).setCurrentEpisode(showInfoController.findLowestEpisode(showInfoController.getEpisodesList(aShow, userSettings.getAShowSettings(aShow).getCurrentSeason())));
        log.info(aShow + " is reset to Season " + userSettings.getAShowSettings(aShow).getCurrentSeason() + " episode " + userSettings.getAShowSettings(aShow).getCurrentEpisode());
    }

    public void setToEnd(String aShow) {
        userSettings.getAShowSettings(aShow).setCurrentSeason(showInfoController.findHighestSeason(aShow));
        userSettings.getAShowSettings(aShow).setCurrentEpisode(showInfoController.findHighestEpisode(showInfoController.getEpisodesList(aShow, userSettings.getAShowSettings(aShow).getCurrentSeason())) + 1);
        log.info(aShow + " is reset to Season " + userSettings.getAShowSettings(aShow).getCurrentSeason() + " episode " + userSettings.getAShowSettings(aShow).getCurrentEpisode());
    }

    private boolean doesEpisodeExist(String aShow, int season, int episode) {
        Set<Integer> episodes = showInfoController.getEpisodesList(aShow, Integer.parseInt(String.valueOf(season)));
        return !episodes.isEmpty() && episodes.contains(episode);
    }

    public boolean doesEpisodeExists(String aShow) {
        return userSettings.getShowSettings().containsKey(aShow);
    }

    public int[] getPreviousEpisodeIfExists(String aShow) {
        int[] seasonEpisodeReturn = new int[2];
        int currentSeason = userSettings.getAShowSettings(aShow).getCurrentSeason();
        seasonEpisodeReturn[0] = currentSeason;
        Set<Integer> episodes = showInfoController.getEpisodesList(aShow, currentSeason);
        int episode = userSettings.getAShowSettings(aShow).getCurrentEpisode();
        episode -= 1;
        for (int aEpisode : episodes) {
            seasonEpisodeReturn[1] = aEpisode;
            if (aEpisode == episode) {
                return seasonEpisodeReturn;
            }
        }
        if (episode == 0) {
            log.info(String.valueOf(episode));
            Set<Integer> seasons = showInfoController.getSeasonsList(aShow);
            int season = currentSeason;
            season -= 1;
            if (seasons.contains(season)) {
                seasonEpisodeReturn[0] = season;
                if (!showInfoController.getEpisodesList(aShow, season).isEmpty()) {
                    Set<Integer> episodesPreviousSeason = showInfoController.getEpisodesList(aShow, season);
                    log.info(String.valueOf(episodesPreviousSeason));
                    int episode1 = showInfoController.findHighestEpisode(episodesPreviousSeason);
                    if (episodesPreviousSeason.contains(episode1)) {
                        seasonEpisodeReturn[1] = episode1;
                        return seasonEpisodeReturn;
                    }
                    return seasonEpisodeReturn;
                }
            }
        } else {
            seasonEpisodeReturn[0] = -3;
            return seasonEpisodeReturn;
        }
        seasonEpisodeReturn[0] = -2;
        return seasonEpisodeReturn;
    }

    public int getCurrentSeason(String aShow) {
        return userSettings.getAShowSettings(aShow).getCurrentSeason();
    }

    public int getCurrentEpisode(String aShow) {
        return userSettings.getAShowSettings(aShow).getCurrentEpisode();
    }

    public int getRemainingNumberOfEpisodes(String aShow, ShowInfoController showInfoController) {
        int remaining = 0, currentSeason = userSettings.getAShowSettings(aShow).getCurrentSeason(), currentEpisode = userSettings.getAShowSettings(aShow).getCurrentEpisode();
        Set<Integer> allSeasons = showInfoController.getSeasonsList(aShow);
        ArrayList<Integer> allSeasonAllowed = new ArrayList<>();
        allSeasons.forEach(aSeason -> {
            if (aSeason >= currentSeason) {
                allSeasonAllowed.add(aSeason);
            }
        });
        if (!allSeasonAllowed.isEmpty()) {
            if (showInfoController.isDoubleEpisode(aShow, currentSeason, currentEpisode)) {
                currentEpisode++;
            }
            for (int aSeason : allSeasonAllowed) {
                int episode = 1;
                if (aSeason == currentSeason) {
                    episode = currentEpisode;
                }
                Set<Integer> episodes = showInfoController.getEpisodesList(aShow, Integer.parseInt(String.valueOf(aSeason)));
                if (!episodes.isEmpty()) {
                    ArrayList<Integer> episodesArray = new ArrayList<>();
                    episodes.forEach(episodesArray::add);
                    Collections.sort(episodesArray);
                    Iterator<Integer> episodesIterator = episodesArray.iterator();
                    ArrayList<Integer> episodesAllowed = new ArrayList<>();
                    if (aSeason == currentSeason) {
                        while (episodesIterator.hasNext()) {
                            int next = episodesIterator.next();
                            if (next >= currentEpisode) {
                                episodesAllowed.add(next);
                            }
                        }
                    } else {
                        episodesArray.forEach(episodesAllowed::add);
                    }
                    Collections.sort(episodesAllowed);
                    Iterator<Integer> episodesIterator2 = episodesAllowed.iterator();
                    while (episodesIterator2.hasNext()) {
                        int e = episodesIterator2.next();
                        if (e == episode) {
                            remaining++;
                            episode++;
                            episodesIterator2.remove();
                        } else return remaining;
                    }
                    if (!episodesAllowed.isEmpty()) {
                        return remaining;
                    }
                }
            }
        }
        return remaining;
    }

    public void addNewShow(String aShow) {
        if (!userSettings.getShowSettings().containsKey(aShow)) {
            log.info("Adding " + aShow + " to user settings file.");
            int lowestSeason = showInfoController.findLowestSeason(aShow);
            userSettings.addShowSettings(new UserShowSettings(aShow, showInfoController.findLowestSeason(aShow), showInfoController.findLowestEpisode(showInfoController.getEpisodesList(aShow, lowestSeason))));
        }
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    public void printAllUserInfo() {
        log.info("Printing all user info for " + Strings.UserName + "...");
        log.info(String.valueOf(userSettings.getUserSettingsFileVersion()) + " - " + String.valueOf(userSettings.getUserDirectoryVersion()));
        userSettings.getShowSettings().values().forEach(aShowSettings -> log.info(aShowSettings.getShowName() + " - " + aShowSettings.isActive() + ", " + aShowSettings.isIgnored() + ", " + aShowSettings.isHidden() + " - Season: " + aShowSettings.getCurrentSeason() + " | Episode: " + aShowSettings.getCurrentEpisode()));
        log.info("Finished printing all user info.");
    }

    public void saveUserSettingsFile() {
        if (userSettings != null) {
            new FileManager().save(userSettings, Variables.UsersFolder, Strings.UserName, Variables.UsersExtension, true);
            log.info("userSettingsFile has been saved!");
        }
    }
}
