package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.information.settings.UserSettings;
import com.maddogten.mtrack.information.settings.UserShowSettings;
import com.maddogten.mtrack.io.FileManager;
import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/*
      UserInfoController loads and stores all the UserSettings.
 */

public class UserInfoController {
    private final Logger log = Logger.getLogger(UserInfoController.class.getName());
    private UserSettings userSettings;

    public void loadUserInfo() {
        this.userSettings = (UserSettings) new FileManager().loadFile(Variables.UsersFolder, Strings.UserName.getValue(), Variables.UserFileExtension);
    }

    // Returns all users found in the programs user folder (If any). Username's are not saved anywhere in the program (Other then the current default), So you can remove and add as wanted.
    public ArrayList<String> getAllUsers() {
        File folder = new File(Variables.dataFolder + Variables.UsersFolder);
        if (folder.exists()) {
            ArrayList<String> users = new ArrayList<>(folder.list().length);
            if (new FileManager().checkFolderExistsAndReadable(folder))
                Collections.addAll(users, folder.list((dir, name) -> (name.toLowerCase().endsWith(Variables.UserFileExtension) && !name.toLowerCase().matches("Program"))));
            ArrayList<String> usersCleaned = new ArrayList<>(users.size());
            users.forEach(aUser -> usersCleaned.add(aUser.replace(Variables.UserFileExtension, Strings.EmptyString)));
            if (usersCleaned.isEmpty()) log.info("Users folder was empty.");
            return usersCleaned;
        } else log.fine("Users folder doesn't exists.");
        return new ArrayList<>();
    }

    // Sets a show to Ignored, Which means the show is long longer found in any of the folders. Keep the information just in case it is found again later.
    public void setIgnoredStatus(String aShow, boolean ignored) {
        log.fine(aShow + " ignore status is: " + ignored);
        userSettings.getAShowSettings(aShow).setIgnored(ignored);
    }

    public ArrayList<String> getIgnoredShows() {
        ArrayList<String> ignoredShows = new ArrayList<>(userSettings.getShowSettings().size());
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (showSettings.isIgnored()) ignoredShows.add(showSettings.getShowName());
        });
        return ignoredShows;
    }

    // Saves whether or not a show is currently active. If a show is Active, it means the user is actively watching it, and it is being searched for in rechecks.
    public void setActiveStatus(String aShow, boolean active) {
        userSettings.getAShowSettings(aShow).setActive(active);
    }

    public boolean isShowActive(String aShow) {
        return userSettings.getAShowSettings(aShow).isActive();
    }

    public ArrayList<String> getActiveShows() {
        ArrayList<String> activeShows = new ArrayList<>(userSettings.getShowSettings().size());
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (showSettings.isActive() && !showSettings.isIgnored() && !showSettings.isHidden())
                activeShows.add(showSettings.getShowName());
        });
        return activeShows;
    }

    // Returns all the shows that the user isn't currently watching. (Other than ignored or hidden shows)
    public ArrayList<String> getInactiveShows() {
        ArrayList<String> inActiveShows = new ArrayList<>(userSettings.getShowSettings().size());
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (!showSettings.isActive() && !showSettings.isIgnored() && !showSettings.isHidden())
                inActiveShows.add(showSettings.getShowName());
        });
        return inActiveShows;
    }

    public ArrayList<String> getUsersShows() {
        ArrayList<String> shows = new ArrayList<>(userSettings.getShowSettings().size());
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (!showSettings.isIgnored() && !showSettings.isHidden())
                shows.add(showSettings.getShowName());
        });
        return shows;
    }

    // Returns all the shows the program currently has being tracked.
    public ArrayList<String> getAllNonIgnoredShows() {
        ArrayList<String> nonIgnoredShows = new ArrayList<>(userSettings.getShowSettings().size());
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (!showSettings.isIgnored()) nonIgnoredShows.add(showSettings.getShowName());
        });
        return nonIgnoredShows;
    }

    // If a user doesn't want a show clogging up any lists, then it can be set hidden. It shouldn't be able to be found anywhere at that point.
    public void setHiddenStatus(String aShow, boolean isHidden) {
        log.fine(aShow + " hidden status is: " + isHidden);
        userSettings.getAShowSettings(aShow).setHidden(isHidden);
    }

    public ArrayList<String> getHiddenShows() {
        ArrayList<String> hiddenShows = new ArrayList<>(userSettings.getShowSettings().size());
        userSettings.getShowSettings().forEach((showName, showSettings) -> {
            if (showSettings.isHidden() && !showSettings.isIgnored()) hiddenShows.add(showSettings.getShowName());
        });
        return hiddenShows;
    }

    // Attempts to play the file using the default program for the extension.
    public boolean playAnyEpisode(String aShow, int aSeason, int aEpisode) {
        log.info("Attempting to play " + aShow + " Season: " + aSeason + " - Episode: " + aEpisode);
        String showLocation = ClassHandler.showInfoController().getEpisode(aShow, aSeason, aEpisode);
        log.finer("Known show location: " + showLocation);
        if (showLocation.isEmpty()) log.warning("showLocation is empty!");
        else {
            File file = new File(showLocation);
            if (file.exists()) return new FileManager().open(file);
            else log.warning("File \"" + file + "\" doesn't exists!");
        }
        return false;
    }

    // Changes which episode the user is currently on. If -2 is returned as the episode, it increases it to the next episode found.
    // If no episode is found, then it checks if there is another season, and if there is, checks if it contains the first episode in the season.
    public void changeEpisode(String aShow, int episode) {
        if (episode == -2) {
            int currentSeason = userSettings.getAShowSettings(aShow).getCurrentSeason();
            int currentEpisode = userSettings.getAShowSettings(aShow).getCurrentEpisode();
            if (ClassHandler.showInfoController().isDoubleEpisode(aShow, currentSeason, currentEpisode))
                ++currentEpisode;
            boolean[] isAnotherEpisodeResult = isAnotherEpisode(aShow, currentSeason, currentEpisode);
            if (isAnotherEpisodeResult[0]) {
                userSettings.getAShowSettings(aShow).setCurrentEpisode(++currentEpisode);
                log.info(aShow + " is now on episode " + userSettings.getAShowSettings(aShow).getCurrentEpisode());
            } else if (isAnotherEpisodeResult[1] && isAnotherSeason(aShow, currentSeason)) {
                userSettings.getAShowSettings(aShow).setCurrentSeason(++currentSeason);
                userSettings.getAShowSettings(aShow).setCurrentEpisode(1);
                log.info(aShow + " is now on episode " + userSettings.getAShowSettings(aShow).getCurrentEpisode());
            } else {
                userSettings.getAShowSettings(aShow).setCurrentEpisode(++currentEpisode);
                log.info(aShow + " is now on episode " + userSettings.getAShowSettings(aShow).getCurrentEpisode());
            }
        } else {
            if (doesEpisodeExistInShowFile(aShow, userSettings.getAShowSettings(aShow).getCurrentSeason(), episode))
                userSettings.getAShowSettings(aShow).setCurrentEpisode(episode);
        }
    }

    // Directly sets the Season & Episode for a show.
    public void setSeasonEpisode(String aShow, int season, int episode) {
        userSettings.getAShowSettings(aShow).setCurrentSeason(season);
        userSettings.getAShowSettings(aShow).setCurrentEpisode(episode);
        log.info(aShow + " is now set to Season: " + season + " - Episode: " + episode);
    }

    // Checks if there is an episode directly following the current episode.
    private boolean[] isAnotherEpisode(String aShow, int aSeason, int aEpisode) {
        boolean[] result = {false, false};
        Set<Integer> episodes = new HashSet<>();
        ClassHandler.showInfoController().getEpisodesList(aShow, aSeason).stream().filter(episodeInt -> episodeInt >= aEpisode).forEach(episodes::add);
        if (episodes.contains(aEpisode)) result[0] = true;
        if (!result[0] && episodes.isEmpty()) result[1] = true;
        return result;
    }

    private boolean shouldSwitchSeasons(String aShow, int aSeason, int aEpisode) {
        return isAnotherEpisode(aShow, aSeason, aEpisode)[1] && isAnotherSeason(aShow, aSeason);
    }

    // Checks if there is another season directly following the current season.
    private boolean isAnotherSeason(String aShow, int season) {
        return ClassHandler.showInfoController().getSeasonsList(aShow).contains(++season);
    }

    // Sets a show to the very first season & episode.
    public void setToBeginning(String aShow) {
        userSettings.getAShowSettings(aShow).setCurrentSeason(ClassHandler.showInfoController().findLowestSeason(aShow));
        userSettings.getAShowSettings(aShow).setCurrentEpisode(ClassHandler.showInfoController().findLowestEpisode(ClassHandler.showInfoController().getEpisodesList(aShow, userSettings.getAShowSettings(aShow).getCurrentSeason())));
        log.info(aShow + " is reset to Season " + userSettings.getAShowSettings(aShow).getCurrentSeason() + " episode " + userSettings.getAShowSettings(aShow).getCurrentEpisode());
    }

    // Sets a show to the last season and to the last found episode + 1.
    public void setToEnd(String aShow) {
        userSettings.getAShowSettings(aShow).setCurrentSeason(ClassHandler.showInfoController().findHighestSeason(aShow));
        userSettings.getAShowSettings(aShow).setCurrentEpisode(ClassHandler.showInfoController().findHighestEpisode(ClassHandler.showInfoController().getEpisodesList(aShow, userSettings.getAShowSettings(aShow).getCurrentSeason())) + 1);
        log.info(aShow + " is reset to Season " + userSettings.getAShowSettings(aShow).getCurrentSeason() + " episode " + userSettings.getAShowSettings(aShow).getCurrentEpisode());
    }

    // Checks if the given episode has been found for a show.
    private boolean doesEpisodeExistInShowFile(String aShow, int aSeason, int aEpisode) {
        Set<Integer> episodes = ClassHandler.showInfoController().getEpisodesList(aShow, aSeason);
        return !episodes.isEmpty() && episodes.contains(aEpisode);
    }

    // Does same as above, but checks against the current episode & season.
    public boolean doesEpisodeExistInShowFile(String aShow) {
        Set<Integer> episodes = ClassHandler.showInfoController().getEpisodesList(aShow, getCurrentSeason(aShow));
        return !episodes.isEmpty() && episodes.contains(getCurrentEpisode(aShow));
    }

    // Attempts to get the episode directly before the current one. If the current one is the first of a season,
    // Then it checks if there is a season before it, and plays the last found episode in it.
    public int[] getPreviousEpisodeIfExists(String aShow) {
        int[] seasonEpisodeReturn = new int[2];
        int currentSeason = userSettings.getAShowSettings(aShow).getCurrentSeason();
        seasonEpisodeReturn[0] = currentSeason;
        int episode = userSettings.getAShowSettings(aShow).getCurrentEpisode();
        episode -= 1;
        for (int aEpisode : ClassHandler.showInfoController().getEpisodesList(aShow, currentSeason)) {
            seasonEpisodeReturn[1] = aEpisode;
            if (aEpisode == episode) return seasonEpisodeReturn;
        }
        if (episode == 0) {
            Set<Integer> seasons = ClassHandler.showInfoController().getSeasonsList(aShow);
            int season = currentSeason;
            season -= 1;
            if (seasons.contains(season)) {
                seasonEpisodeReturn[0] = season;
                if (!ClassHandler.showInfoController().getEpisodesList(aShow, season).isEmpty()) {
                    Set<Integer> episodesPreviousSeason = ClassHandler.showInfoController().getEpisodesList(aShow, season);
                    int episode1 = ClassHandler.showInfoController().findHighestEpisode(episodesPreviousSeason);
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

    // Finds out how many episodes you have following the currently one. If it finds incrementing episodes all the way from the current one,
    // Then checks for a following season that contains episode 1.
    public int getRemainingNumberOfEpisodes(String aShow) {
        int remaining = 0, currentSeason = userSettings.getAShowSettings(aShow).getCurrentSeason(), currentEpisode = userSettings.getAShowSettings(aShow).getCurrentEpisode();
        if (ClassHandler.showInfoController().getShowsFile().containsKey(aShow)) {
            Set<Integer> allSeasons = ClassHandler.showInfoController().getSeasonsList(aShow);
            ArrayList<Integer> allSeasonAllowed = new ArrayList<>(allSeasons.size());
            allSeasons.forEach(aSeason -> {
                if (aSeason >= currentSeason) allSeasonAllowed.add(aSeason);
            });
            if (!allSeasonAllowed.isEmpty()) {
                if (ClassHandler.showInfoController().isDoubleEpisode(aShow, currentSeason, currentEpisode))
                    currentEpisode++;
                boolean isCurrentSeason = true;
                Collections.sort(allSeasonAllowed);
                int lastSeason = -2;
                for (int aSeason : allSeasonAllowed) {
                    if (lastSeason != -2 && lastSeason != aSeason - 1) return remaining;
                    int episode = 1;
                    if (isCurrentSeason) {
                        if (aSeason != currentSeason) return remaining;
                        episode = currentEpisode;
                    }
                    Set<Integer> episodes = ClassHandler.showInfoController().getEpisodesList(aShow, Integer.parseInt(String.valueOf(aSeason)));
                    if (!episodes.isEmpty()) {
                        ArrayList<Integer> episodesArray = new ArrayList<>(episodes.size());
                        episodes.forEach(episodesArray::add);
                        Collections.sort(episodesArray);
                        Iterator<Integer> episodesIterator = episodesArray.iterator();
                        ArrayList<Integer> episodesAllowed = new ArrayList<>(episodesArray.size());
                        if (isCurrentSeason) {
                            while (episodesIterator.hasNext()) {
                                int next = episodesIterator.next();
                                if (next >= currentEpisode) episodesAllowed.add(next);
                            }
                        } else episodesArray.forEach(episodesAllowed::add);
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
                        if (!episodesAllowed.isEmpty()) return remaining;
                    }
                    if (isCurrentSeason) isCurrentSeason = false;
                    lastSeason = aSeason;
                }
            }
        }
        return remaining;
    }

    public boolean isProperEpisodeInNextSeason(String aShow) {
        // This is being done because I set episodes 1 further when watched, which works when the season is ongoing. However, when moving onto a new season it breaks. This is a simple check to move
        // it into a new season if one is found, and no further episodes are found in the current season.
        if (getRemainingNumberOfEpisodes(aShow) > 0 && shouldSwitchSeasons(aShow, getCurrentSeason(aShow), getCurrentEpisode(aShow))) {
            log.info("No further episodes found for current season, further episodes found in next season, switching to new season.");
            changeEpisode(aShow, -2);
            Controller.updateShowField(aShow, true);
            return true;
        } else return false;
    }

    // Adds a new show to the shows file.
    public void addNewShow(String aShow) {
        if (!userSettings.getShowSettings().containsKey(aShow)) {
            log.fine("Adding " + aShow + " to user settings file.");
            if (Variables.genUserShowInfoAtFirstFound)
                userSettings.addShowSettings(new UserShowSettings(aShow, ClassHandler.showInfoController().findLowestSeason(aShow), ClassHandler.showInfoController().findLowestEpisode(ClassHandler.showInfoController().getEpisodesList(aShow, ClassHandler.showInfoController().findLowestSeason(aShow)))));
            else userSettings.addShowSettings(new UserShowSettings(aShow, 1, 1));
        }
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    public void saveUserSettingsFile() {
        new FileManager().save(userSettings, Variables.UsersFolder, Strings.UserName.getValue(), Variables.UserFileExtension, true);
        log.fine("userSettingsFile has been saved!");
    }
}
