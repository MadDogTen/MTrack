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
        dbUserSettingsManager.getUserShowIgnoredStatus(userID, showID);
    }

    public void setIgnoredStatusAllUsers(int showID, boolean ignored) {
        getAllUsers().forEach(userID -> setIgnoredStatus(userID, showID, ignored));
    }

    public int doesUserExist(String userName) {
        return dbUserManager.doesUserExist(userName);
    }

    public int addUser(String userName) {
        int userID = dbUserManager.addUser(userName);
        dbUserSettingsManager.addUserSettings(userID);
        if (userID != -2) {
            ClassHandler.showInfoController().getShows().forEach(showID -> addNewShow(userID, showID));
        }
        log.fine("Generated all show data for " + userName + ".");
        return dbUserManager.addUser(userName);
    }

    public void addShowForUsers(int showID) {
        getAllUsers().forEach(userID -> {
            if (!dbUserSettingsManager.doesShowSettingExistForUser(userID, showID)) {
                this.addNewShow(userID, showID);
                log.fine("\"" + ClassHandler.showInfoController().getShowNameFromShowID(showID) + "\" was added for user \"" + getUserNameFromID(userID) + "\".");
            } else if (getIgnoredStatus(userID, showID)) {
                this.setIgnoredStatus(userID, showID, false);
                log.fine("\"" + ClassHandler.showInfoController().getShowNameFromShowID(showID) + "\" was un-ignored for user \"" + getUserNameFromID(userID) + "\".");
            }
        });
    }

    // Adds a new show to the shows file.
    public void addNewShow(int userID, int showID) {
        log.fine("Adding " + ClassHandler.showInfoController().getShowNameFromShowID(showID) + " to " + dbUserManager.getUsername(userID) + "'s settings file.");
        int season = (Variables.genUserShowInfoAtFirstFound) ? ClassHandler.showInfoController().findLowestInt(ClassHandler.showInfoController().getSeasonsList(showID)) : 1;
        int episode = (Variables.genUserShowInfoAtFirstFound) ? ClassHandler.showInfoController().findLowestInt(ClassHandler.showInfoController().getEpisodesList(showID, season)) : (ClassHandler.showInfoController().doesEpisodeExist(showID, season, 0)) ? 0 : 1;
        dbUserSettingsManager.addShowSettings(userID, showID, season, episode);
    }

    public boolean getIgnoredStatus(int userID, int showID) {
        return dbUserSettingsManager.getUserShowIgnoredStatus(userID, showID);
    }

    public Set<Integer> getIgnoredShows(int userID) {
        return dbUserSettingsManager.getShows(userID, StringDB.COLUMN_IGNORED, true);
    }

    // Saves whether or not a show is currently active. If a show is Active, it means the user is actively watching it, and it is being searched for in rechecks.
    public void setActiveStatus(int userID, final int showID, final boolean active) {
        dbUserSettingsManager.setUserShowActiveStatus(userID, showID, active);
    }

    public boolean isShowActive(int userID, final int showID) {
        return dbUserSettingsManager.getUserShowActiveStatus(userID, showID);
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
        dbUserSettingsManager.setUserShowHiddenStatus(userID, showID, isHidden);
    }

    public Set<Integer> getHiddenShows(int userID) {
        return dbUserSettingsManager.getShows(userID, StringDB.COLUMN_HIDDEN, true);
    }

    // Attempts to play the file using the default program for the extension.
    public boolean playAnyEpisode(final int userID, final int episodeID) {
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
            int currentSeason = getCurrentUserSeason(userID, showID);
            int currentEpisode = getCurrentUserEpisode(userID, showID);
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
                dbUserSettingsManager.setUserShowEpisode(userID, showID, ++currentEpisode);
                log.info(showName + " is now on episode " + currentEpisode);
            }
        } else {
            if (ClassHandler.showInfoController().doesEpisodeExist(showID, getCurrentUserSeason(userID, showID), getCurrentUserEpisode(userID, showID)))
                this.setSeasonEpisode(userID, showID, getCurrentUserSeason(userID, showID), episode);
        }
    }

    public int getCurrentUserSeason(int userID, int showID) {
        return dbUserSettingsManager.getUserShowSeason(userID, showID);
    }

    public int getCurrentUserEpisode(int userID, int showID) {
        return dbUserSettingsManager.getUserShowEpisode(userID, showID);
    }

    // Directly sets the Season & Episode for a show.
    public void setSeasonEpisode(int userID, int showID, final int season, final int episode) {
        dbUserSettingsManager.setUserShowSeason(userID, showID, season);
        dbUserSettingsManager.setUserShowEpisode(userID, showID, episode);
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
        final int[] currentInfo = {getCurrentUserSeason(userID, showID), getCurrentUserEpisode(userID, showID)};
        SortedSet<Integer> allSeasons = new TreeSet<>();
        ClassHandler.showInfoController().getSeasonsList(showID).stream().filter(integer -> (integer >= currentInfo[0])).forEach(allSeasons::add);
        if (!allSeasons.isEmpty()) {
            if (ClassHandler.showInfoController().isDoubleEpisode(showID, currentInfo[0], currentInfo[1]))
                currentInfo[1]++;
            final boolean[] isCurrentSeason = {true};
            int lastSeason = -2;
            for (int aSeason : allSeasons) {
                if (lastSeason != -2 && lastSeason != aSeason - 1) return remaining;
                int episode = 1;
                if (isCurrentSeason[0]) {
                    if (aSeason != currentInfo[0]) return remaining;
                    episode = currentInfo[1];
                }
                SortedSet<Integer> episodes = new TreeSet<>();
                ClassHandler.showInfoController().getEpisodesList(showID, aSeason).stream().filter(integer -> (isCurrentSeason[0] && integer >= currentInfo[1]) || !isCurrentSeason[0] && integer > 0).forEach(episodes::add);
                if (!episodes.isEmpty()) {
                    Iterator<Integer> episodesIterator = episodes.iterator();
                    while (episodesIterator.hasNext()) {
                        if (episodesIterator.next() == episode) {
                            remaining++;
                            episode++;
                            episodesIterator.remove();
                        } else return remaining;
                    }
                }
                if (isCurrentSeason[0]) isCurrentSeason[0] = false;
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

    public ArrayList<String> getAllUsersString() {
        return dbUserManager.getAllUserStrings();
    }

    public void setLanguage(int userID, String language) {
        dbUserSettingsManager.setUserLanguage(userID, language);
        log.info("Default language was set to " + language + '.');
    }

    public String getLanguage(int userID) {
        return dbUserSettingsManager.getUserLanguage(userID);
    }

    public boolean doShowUpdating(int userID) {
        return dbUserSettingsManager.getUserDoShowUpdating(userID);
    }

    public void setShowUpdating(int userID, boolean updateShows) {
        dbUserSettingsManager.setUserDoShowUpdating(userID, updateShows);
    }

    public void setUpdateSpeed(int userID, final int updateSpeed) {
        dbUserSettingsManager.setUserUpdateSpeed(userID, updateSpeed);
        log.info("Update speed is now set to: " + updateSpeed);
    }

    public int getUpdateSpeed(int userID) {
        return dbUserSettingsManager.getUserUpdateSpeed(userID);
    }

    public void setTimeToWaitForDirectory(int userID, final int timeToWaitForDirectory) {
        dbUserSettingsManager.setUserTimeToWaitForDirectory(userID, timeToWaitForDirectory);
        log.info("Time to wait for directory is now set to: " + timeToWaitForDirectory);
    }

    public int getTimeToWaitForDirectory(int userID) {
        return dbUserSettingsManager.getUserTimeToWaitForDirectory(userID);
    }

    public boolean doFileLogging(int userID) {
        return dbUserSettingsManager.getUserDoFileLogging(userID);
    }

    public void setFileLogging(int userID, final boolean enableFileLogging) {
        dbUserSettingsManager.setUserDoFileLogging(userID, enableFileLogging);
        if (enableFileLogging && !GenericMethods.isFileLoggingStarted()) {
            try {
                GenericMethods.initFileLogging(log);
            } catch (IOException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        } else if (GenericMethods.isFileLoggingStarted()) GenericMethods.stopFileLogging(log);
    }

    public boolean doSpecialEffects(int userID) {
        return dbUserSettingsManager.getUserDoSpecialEffects(userID);
    }

    public boolean show0Remaining(int userID) {
        return dbUserSettingsManager.getUserShow0Remaining(userID);
    }

    public boolean showActiveShows(int userID) {
        return dbUserSettingsManager.getUserShowActiveShows(userID);
    }

    public boolean getRecordChangesForNonActiveShows(int userID) {
        return dbUserSettingsManager.getUserRecordChangesForNonActiveShows(userID);
    }

    public boolean getRecordChangedSeasonsLowerThanCurrent(int userID) {
        return dbUserSettingsManager.getUserRecordChangesSeasonsLowerThanCurrent(userID);
    }

    public boolean getMoveStageWithParent(int userID) {
        return dbUserSettingsManager.getUserMoveStageWithParent(userID);
    }

    public boolean getHaveStageBlockParentStage(int userID) {
        return dbUserSettingsManager.getUserHaveStageBlockParentStage(userID);
    }

    public boolean getEnableFileLogging(int userID) {
        return dbUserSettingsManager.getUserEnableFileLogging(userID);
    }

    public boolean getColumnVisibilityStatus(int userID, String column) { // TODO Fix this tomorrow
        return true; //dbUserSettingsManager.getBooleanSetting(userID, -2, column, StringDB.TABLE_USERSETTINGS);
    }

    public float getColumnWidth(int userID, String column) { // TODO Fix this tomorrow
        return 20; //dbUserSettingsManager.getFloatSetting(userID, column, StringDB.TABLE_USERSETTINGS);
    }

    public VideoPlayer getVideoPlayer(int userID) {
        VideoPlayer videoPlayer = new VideoPlayer();
        videoPlayer.setVideoPlayerEnum(VideoPlayer.VideoPlayerEnum.getVideoPlayerFromID(dbUserSettingsManager.getUserVideoPlayerType(userID)));
        if (videoPlayer.getVideoPlayerEnum() != VideoPlayer.VideoPlayerEnum.OTHER)
            videoPlayer.setVideoPlayerLocation(new File(dbUserSettingsManager.getUserVideoPlayerLocation(userID)));
        return videoPlayer;
    }

    public void setEpisodePosition(int userID, int episodeID, int position) {
        dbUserSettingsManager.setEpisodePosition(userID, episodeID, position);
    }

    public void clearEpisodeSettings(int userID, int episodeID) {
        dbUserSettingsManager.removeEpisode(userID, episodeID);
    }

    public String getMostUsedLanguage() {
        Map<String, Integer> languages = new HashMap<>();
        getAllUsers().forEach(userID -> {
            String language = getLanguage(userID);
            if (!languages.containsKey(language)) languages.put(language, 1);
            else languages.replace(language, languages.get(language) + 1);
        });
        final String[] mostUsedLanguage = {Strings.EmptyString};
        final int[] mostUsedAmount = {0};
        languages.forEach((language, timesUsed) -> {
            if (timesUsed > mostUsedAmount[0]) {
                mostUsedLanguage[0] = language;
                mostUsedAmount[0] = timesUsed;
            }
        });
        if (mostUsedLanguage[0].matches(Strings.EmptyString)) mostUsedLanguage[0] = Variables.DefaultLanguage[0];
        return mostUsedLanguage[0];
    }

    public void setDoSpecialEffects(int userID, boolean doSpecialEffects) {
        dbUserSettingsManager.setUserDoSpecialEffects(userID, doSpecialEffects);
    }

    public void setShow0Remaining(int userID, boolean show0Remaining) {
        dbUserSettingsManager.setUserShow0Remaining(userID, show0Remaining);
    }

    public void setShowActiveShows(int userID, boolean showActiveShows) {
        dbUserSettingsManager.setUserShowActiveShows(userID, showActiveShows);
    }

    public void setRecordChangesForNonActiveShows(int userID, boolean recordChangesForNonActiveShows) {
        dbUserSettingsManager.setUserRecordChangesForNonActiveShows(userID, recordChangesForNonActiveShows);
    }

    public void setRecordChangesSeasonsLowerThanCurrent(int userID, boolean recordChangesSeasonsLowerThanCurrent) {
        dbUserSettingsManager.setUserRecordChangesSeasonsLowerThanCurrent(userID, recordChangesSeasonsLowerThanCurrent);
    }

    public void setMoveStageWithParent(int userID, boolean moveStageWithParent) {
        dbUserSettingsManager.setUserMoveStageWithParent(userID, moveStageWithParent);
    }

    public void setHaveStageBlockParentStage(int userID, boolean haveStageBlockParentStage) {
        dbUserSettingsManager.setUserHaveStageBlockParentStage(userID, haveStageBlockParentStage);
    }

    public synchronized void setEnableFileLogging(int userID, boolean enableFileLogging) {
        dbUserSettingsManager.setUserEnableFileLogging(userID, enableFileLogging);
    }

    public void setVideoPlayerType(int userID, int videoPlayerType) {
        dbUserSettingsManager.setUserVideoPlayerType(userID, videoPlayerType);
    }

    public void setVideoPlayerLocation(int userID, String videoPlayerLocation) {
        dbUserSettingsManager.setUserVideoPlayerLocation(userID, videoPlayerLocation);
    }

    public boolean showUsername(int userID) {
        return dbUserSettingsManager.getUserShowUsername(userID);
    }

    public void setShowUsername(int userID, boolean showUsername) {
        dbUserSettingsManager.setUserShowUsername(userID, showUsername);
    }
}
