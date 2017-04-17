package com.maddogten.mtrack.information;

import com.maddogten.mtrack.Controller;
import com.maddogten.mtrack.Database.DBUserManager;
import com.maddogten.mtrack.Database.DBUserSettingsManager;
import com.maddogten.mtrack.util.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

/*
      UserInfoController loads and stores all the UserSettings.
 */

public class UserInfoController {
    private final Logger log = Logger.getLogger(UserInfoController.class.getName());
    private DBUserManager dbUserManager;
    private DBUserSettingsManager dbUserSettingsManager;

    public void initDatabase(Connection connection) throws SQLException {
        dbUserManager = new DBUserManager(connection);
        dbUserSettingsManager = new DBUserSettingsManager(connection);
    }

    public String getUserNameFromID(int userID) {
        return dbUserManager.getUsername(userID);
    }

    // Returns all users found in the programs user folder (If any). Username's are not saved anywhere in the program (Other then the current default), So you can remove and add as wanted.
    public ArrayList<Integer> getAllUsers() {
        return dbUserManager.getAllUsers();
    }

    // Sets a show to Ignored, Which means the show is long longer found in any of the folders. Keep the information just in case it is found again later.
    public void setIgnoredStatus(int userID, final int showID, final boolean ignored) {
        log.fine(showID + " ignore status is: " + ignored);
        dbUserSettingsManager.changeBooleanSetting(userID, showID, ignored, StringDB.COLUMN_IGNORED, StringDB.TABLE_USERSHOWSETTINGS);
    }

    public boolean doesUserExist(String userName) {
        return dbUserManager.doesUserExist(userName);
    }

    public boolean addUser(String userName) {
        return dbUserManager.addUser(userName);
    }

    public Set<Integer> getIgnoredShows(int userID) {
        return dbUserSettingsManager.getShows(userID, StringDB.COLUMN_IGNORED, true);
    }

    // Saves whether or not a show is currently active. If a show is Active, it means the user is actively watching it, and it is being searched for in rechecks.
    public void setActiveStatus(int userID, final int showID, final boolean active) {
        dbUserSettingsManager.changeBooleanSetting(userID, showID, active, StringDB.COLUMN_ACTIVE, StringDB.TABLE_USERSHOWSETTINGS);
    }

    public boolean isShowActive(int userID, final int showID) {
        return dbUserSettingsManager.getBooleanSetting(userID, showID, StringDB.COLUMN_ACTIVE, StringDB.TABLE_USERSHOWSETTINGS);
    }

    public Set<Integer> getShowsWithActiveStatus(int userID, boolean getActiveShows) {
        return dbUserSettingsManager.getShows(userID, StringDB.COLUMN_ACTIVE, getActiveShows);
    }

    // Returns all the shows applicable to the type requested
    public Set<Integer> getUsersShows(int userID) {
        return dbUserSettingsManager.getShows(userID);
    }

    // Returns all the shows the program currently has being tracked.
    public Set<Integer> getAllNonIgnoredShows(int userID) {
        return dbUserSettingsManager.getShows(userID, StringDB.COLUMN_IGNORED, false);
    }

    // If a user doesn't want a show clogging up any lists, then it can be set hidden. It shouldn't be able to be found anywhere at that point.
    public void setHiddenStatus(int userID, final int showID, final boolean isHidden) {
        log.fine(showID + " hidden status is: " + isHidden);
        dbUserSettingsManager.changeBooleanSetting(userID, showID, isHidden, StringDB.COLUMN_HIDDEN, StringDB.TABLE_USERSHOWSETTINGS);
    }

    public Set<Integer> getHiddenShows(int userID) {
        return dbUserSettingsManager.getShows(userID, StringDB.COLUMN_HIDDEN, true);
    }

    // Attempts to play the file using the default program for the extension.
    public boolean playAnyEpisode(int userID, final int episodeID) {
        log.info("Attempting to play " + ClassHandler.showInfoController().getShowNameFromEpisodeID(episodeID) + " EpisodeID: " + episodeID);
        File episode = ClassHandler.showInfoController().getEpisode(episodeID);
        if (episode == null) log.warning("Episode wasn't found in database!");
        else {
            log.finer("Known show location: " + episode);
            if (episode.exists())
                return OperatingSystem.openVideo(episode, dbUserSettingsManager.getEpisodePosition(userID, episodeID), getVideoPlayer(userID));
            else log.warning("File \"" + episode + "\" doesn't exists!");
        }
        return false;
    }

    // Changes which episode the user is currently on. If -2 is returned as the episode, it increases it to the next episode found.
    // If no episode is found, then it checks if there is another season, and if there is, checks if it contains the first episode in the season.
    public void changeEpisode(int userID, int showID, final int episode) {
        if (episode == -2) {
            int currentSeason = dbUserSettingsManager.getIntegerSetting(userID, showID, StringDB.COLUMN_SEASON, StringDB.TABLE_USERSHOWSETTINGS);
            int currentEpisode = dbUserSettingsManager.getIntegerSetting(userID, showID, StringDB.COLUMN_EPISODE, StringDB.TABLE_USERSHOWSETTINGS);
            if (ClassHandler.showInfoController().isDoubleEpisode(showID, currentSeason, currentEpisode))
                ++currentEpisode;
            boolean[] isAnotherEpisodeResult = isAnotherEpisode(userID, showID, currentSeason, currentEpisode);
            String showName = ClassHandler.showInfoController().getShowNameFromShowID(showID);
            if (isAnotherEpisodeResult[0]) {
                this.setSeasonEpisode(userID, showID, getCurrentUserSeason(userID, showID), ++currentEpisode);
                log.info(showName + " is now on episode " + currentEpisode);
            } else if (isAnotherEpisodeResult[1] && isAnotherSeason(userID, showID, currentSeason)) {
                this.setSeasonEpisode(userID, showID, ++currentSeason, 1);
                log.info(showName + " is now on season " + currentSeason + " episode " + 1);
            } else {
                dbUserSettingsManager.changeIntegerSetting(userID, showID, ++currentEpisode, StringDB.COLUMN_EPISODE, StringDB.TABLE_USERSHOWSETTINGS);
                log.info(showName + " is now on episode " + currentEpisode);
            }
        } else {
            if (ClassHandler.showInfoController().doesEpisodeExist(showID, getCurrentUserSeason(userID, showID), getCurrentUserEpisode(userID, showID)))
                this.setSeasonEpisode(userID, showID, getCurrentUserSeason(userID, showID), episode);
        }
    }

    public int getCurrentUserSeason(int userID, int showID) {
        return dbUserSettingsManager.getIntegerSetting(userID, showID, StringDB.COLUMN_SEASON, StringDB.TABLE_USERSHOWSETTINGS);
    }

    public int getCurrentUserEpisode(int userID, int showID) {
        return dbUserSettingsManager.getIntegerSetting(userID, showID, StringDB.COLUMN_EPISODE, StringDB.TABLE_USERSHOWSETTINGS);
    }

    // Directly sets the Season & Episode for a show.
    public void setSeasonEpisode(int userID, int showID, final int season, final int episode) {
        dbUserSettingsManager.changeIntegerSetting(userID, showID, season, StringDB.COLUMN_SEASON, StringDB.TABLE_USERSHOWSETTINGS);
        dbUserSettingsManager.changeIntegerSetting(userID, showID, episode, StringDB.COLUMN_EPISODE, StringDB.TABLE_USERSHOWSETTINGS);
        log.info(ClassHandler.showInfoController().getShowNameFromShowID(showID) + " is now set to Season: " + season + " - Episode: " + episode);
    }

    // Checks if there is an episode directly following the current episode.
    private boolean[] isAnotherEpisode(int userID, final int showID, final int aSeason, final int aEpisode) {
        boolean[] result = {false, false};
        Set<Integer> episodes = new HashSet<>();
        ClassHandler.showInfoController().getEpisodesList(showID, aSeason).stream().filter(episodeInt -> episodeInt >= aEpisode).forEach(episodes::add);
        if (episodes.contains(aEpisode)) result[0] = true;
        if (!result[0] && episodes.isEmpty()) result[1] = true;
        return result;
    }

    private boolean shouldSwitchSeasons(int userID, int showID, final int aSeason, final int aEpisode) {
        return isAnotherEpisode(userID, showID, aSeason, aEpisode)[1] && isAnotherSeason(userID, showID, aSeason);
    }

    // Checks if there is another season directly following the current season.
    private boolean isAnotherSeason(int userID, int showID, final int season) {
        return ClassHandler.showInfoController().getSeasonsList(showID).contains(season + 1);
    }

    // Attempts to get the episode directly before the current one. If the current one is the first of a season,
    // Then it checks if there is a season before it, and plays the last found episode in it.
    public int[] getPreviousEpisodeIfExists(int userID, int showID) {
        int[] seasonEpisodeReturn = new int[2];
        int currentSeason = getCurrentUserSeason(userID, showID);
        seasonEpisodeReturn[0] = currentSeason;
        int episode = getCurrentUserEpisode(userID, showID);
        episode -= 1;
        for (int aEpisode : ClassHandler.showInfoController().getEpisodesList(showID, currentSeason)) {
            seasonEpisodeReturn[1] = aEpisode;
            if (aEpisode == episode) return seasonEpisodeReturn;
        }
        if (episode == 0) {
            Set<Integer> seasons = ClassHandler.showInfoController().getSeasonsList(showID);
            int season = currentSeason;
            season -= 1;
            if (seasons.contains(season)) {
                seasonEpisodeReturn[0] = season;
                if (!ClassHandler.showInfoController().getEpisodesList(showID, season).isEmpty()) {
                    Set<Integer> episodesPreviousSeason = ClassHandler.showInfoController().getEpisodesList(showID, season);
                    int episode1 = ClassHandler.showInfoController().findHighestInt(episodesPreviousSeason);
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

    // Finds out how many episodes you have following the currently one. If it finds incrementing episodes all the way from the current one,
    // Then checks for a following season that contains episode 1.
    public int getRemainingNumberOfEpisodes(int userID, int showID) {
        int remaining = 0;
        int currentSeason = getCurrentUserSeason(userID, showID), currentEpisode = getCurrentUserEpisode(userID, showID);
        Set<Integer> allSeasons = ClassHandler.showInfoController().getSeasonsList(showID);
        ArrayList<Integer> allSeasonAllowed = new ArrayList<>(allSeasons.size());
        allSeasons.forEach(aSeason -> {
            if (aSeason >= currentSeason) allSeasonAllowed.add(aSeason);
        });
        if (!allSeasonAllowed.isEmpty()) {
            if (ClassHandler.showInfoController().isDoubleEpisode(showID, currentSeason, currentEpisode))
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
                Set<Integer> episodes = ClassHandler.showInfoController().getEpisodesList(showID, aSeason);
                if (!episodes.isEmpty()) {
                    ArrayList<Integer> episodesArray = new ArrayList<>(episodes.size());
                    episodesArray.addAll(episodes);
                    Collections.sort(episodesArray);
                    Iterator<Integer> episodesIterator = episodesArray.iterator();
                    ArrayList<Integer> episodesAllowed = new ArrayList<>(episodesArray.size());
                    while (episodesIterator.hasNext()) {
                        int next = episodesIterator.next();
                        if (isCurrentSeason && next >= currentEpisode) episodesAllowed.add(next);
                        else if (!isCurrentSeason && next > 0) episodesAllowed.add(next);
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
                    if (!episodesAllowed.isEmpty()) return remaining;
                }
                if (isCurrentSeason) isCurrentSeason = false;
                lastSeason = aSeason;
            }
        }
        return remaining;
    }

    public boolean isProperEpisodeInNextSeason(int userID, int showID) {
        // This is being done because I set episodes 1 further when watched, which works when the season is ongoing. However, when moving onto a new season it breaks. This is a simple check to move
        // it into a new season if one is found, and no further episodes are found in the current season.
        if (getRemainingNumberOfEpisodes(userID, showID) > 0 && shouldSwitchSeasons(userID, showID, getCurrentUserSeason(userID, showID), getCurrentUserEpisode(userID, showID))) {
            log.info("No further episodes found for current season, further episodes found in next season, switching to new season.");
            changeEpisode(userID, showID, -2);
            Controller.updateShowField(showID, true);
            return true;
        } else return false;
    }

    // Adds a new show to the shows file.
    public void addNewShow(int userID, int showID) {
        log.fine("Adding " + ClassHandler.showInfoController().getShowNameFromShowID(showID) + " to user settings file.");
        int season = (Variables.genUserShowInfoAtFirstFound) ? ClassHandler.showInfoController().findLowestInt(ClassHandler.showInfoController().getSeasonsList(showID)) : 1;
        int episode = (Variables.genUserShowInfoAtFirstFound) ? ClassHandler.showInfoController().findLowestInt(ClassHandler.showInfoController().getEpisodesList(showID, season)) : (ClassHandler.showInfoController().doesEpisodeExist(showID, season, 0)) ? 0 : 1;
        dbUserSettingsManager.addShowSettings(userID, showID, season, episode);
    }

    public ArrayList<String> getAllUsersString() {
        return dbUserManager.getAllUserStrings();
    }

    public void setLanguage(int userID, String language) {
        dbUserSettingsManager.changeStringSetting(userID, -2, language, StringDB.COLUMN_LANGUAGE, StringDB.TABLE_USERSETTINGS);
        Variables.language = language;
        log.info("Default language was set to " + language + '.');
    }

    public String getLanguage(int userID) {
        return dbUserSettingsManager.getStringSetting(userID, -2, StringDB.COLUMN_LANGUAGE, StringDB.TABLE_USERSETTINGS);
    }

    public void setUpdateSpeed(int userID, final int updateSpeed) {
        dbUserSettingsManager.changeIntegerSetting(userID, -2, updateSpeed, StringDB.COLUMN_UPDATESPEED, StringDB.TABLE_USERSETTINGS);
        log.info("Update speed is now set to: " + updateSpeed);
    }

    public int getUpdateSpeed(int userID) {
        return dbUserSettingsManager.getIntegerSetting(userID, -2, StringDB.COLUMN_UPDATESPEED, StringDB.TABLE_USERSETTINGS);
    }

    public void setSavingSpeed(int userID, final int savingSpeed) {
        dbUserSettingsManager.changeIntegerSetting(userID, -2, savingSpeed, StringDB.COLUMN_SAVESPEED, StringDB.TABLE_USERSETTINGS);
        log.info("Save speed is now set to: " + savingSpeed);
    }

    public int getSavingSpeed(int userID) {
        return dbUserSettingsManager.getIntegerSetting(userID, -2, StringDB.COLUMN_SAVESPEED, StringDB.TABLE_USERSETTINGS);
    }

    public void setTimeToWaitForDirectory(int userID, final int timeToWaitForDirectory) {
        dbUserSettingsManager.changeIntegerSetting(userID, -2, timeToWaitForDirectory, StringDB.COLUMN_TIMETOWAITFORDIRECTORY, StringDB.TABLE_USERSETTINGS);
        log.info("Time to wait for directory is now set to: " + timeToWaitForDirectory);
    }

    public int getTimeToWaitForDirectory(int userID) {
        return dbUserSettingsManager.getIntegerSetting(userID, -2, StringDB.COLUMN_TIMETOWAITFORDIRECTORY, StringDB.TABLE_USERSETTINGS);
    }

    public void setFileLogging(int userID, final boolean enableFileLogging) {
        dbUserSettingsManager.changeBooleanSetting(userID, -2, enableFileLogging, StringDB.COLUMN_ENABLEFILELOGGING, StringDB.TABLE_USERSETTINGS);
        if (enableFileLogging && !GenericMethods.isFileLoggingStarted()) {
            try {
                GenericMethods.initFileLogging(log);
            } catch (IOException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        } else if (GenericMethods.isFileLoggingStarted()) GenericMethods.stopFileLogging(log);
    }

    public boolean getColumnVisibilityStatus(int userID, String column) {
        return dbUserSettingsManager.getBooleanSetting(userID, -2, column, StringDB.TABLE_USERSETTINGS);
    }

    public float getColumnWidth(int userID, String column) {
        return dbUserSettingsManager.getFloatSetting(userID, column, StringDB.TABLE_USERSETTINGS);
    }

    public boolean getUserBooleanSetting(int userID, String setting) {
        return dbUserSettingsManager.getBooleanSetting(userID, -2, setting, StringDB.TABLE_USERSETTINGS);
    }

    public void setUserBooleanSetting(int userID, String setting, boolean newSetting) {
        dbUserSettingsManager.changeBooleanSetting(userID, -2, newSetting, setting, StringDB.TABLE_USERSETTINGS);
    }

    public VideoPlayer getVideoPlayer(int userID) {
        VideoPlayer videoPlayer = new VideoPlayer();
        videoPlayer.setVideoPlayerEnum(VideoPlayer.VideoPlayerEnum.getVideoPlayerFromID(dbUserSettingsManager.getIntegerSetting(userID, -2, StringDB.COLUMN_VIDEOPLAYERTYPE, StringDB.TABLE_USERSETTINGS)));
        if (videoPlayer.getVideoPlayerEnum() != VideoPlayer.VideoPlayerEnum.OTHER)
            videoPlayer.setVideoPlayerLocation(new File(dbUserSettingsManager.getStringSetting(userID, -2, StringDB.COLUMN_VIDEOPLAYERLOCATION, StringDB.TABLE_USERSETTINGS)));
        return videoPlayer;
    }

    public void setEpisodePostion(int userID, int episodeID, int position) {
        dbUserSettingsManager.setEpisodePosition(userID, episodeID, position);
    }

    public void clearEpisodeSettings(int userID, int episodeID) {
        dbUserSettingsManager.removeEpisode(userID, episodeID);
    }
}
