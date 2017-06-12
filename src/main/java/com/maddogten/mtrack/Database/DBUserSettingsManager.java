package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class DBUserSettingsManager {
    private final Logger log = Logger.getLogger(DBUserSettingsManager.class.getName());
    private final DBManager dbManager;

    private PreparedStatement insertSettings = null;
    private PreparedStatement getEpisodePosition = null;
    private PreparedStatement setEpisodePosition = null;
    private PreparedStatement addShowSettings = null;
    private PreparedStatement addEpisodeSettings = null;
    private PreparedStatement removeEpisode = null;
    private PreparedStatement getShowsForUser = null;
    private PreparedStatement getUserShowSeason = null;
    private PreparedStatement getUserShowEpisode = null;
    private PreparedStatement checkIfShowSettingsExistForUser = null;
    private PreparedStatement checkUserHasShowID = null;
    private PreparedStatement getUserIgnoredShows = null;
    private PreparedStatement getUserHiddenShows = null;
    private PreparedStatement getUserActiveShows = null;
    private PreparedStatement getUserNonIgnoredShows = null;
    private PreparedStatement getUserInactiveShows = null;

    // Settings
    private PreparedStatement getUserLanguage = null;
    private PreparedStatement setUserLanguage = null;
    private PreparedStatement getUserDoShowUpdating = null;
    private PreparedStatement setUserDoShowUpdating = null;
    private PreparedStatement getUserUpdateSpeed = null;
    private PreparedStatement setUserUpdateSpeed = null;
    private PreparedStatement getUserTimeToWaitForDirectory = null;
    private PreparedStatement setUserTimeToWaitForDirectory = null;
    private PreparedStatement getUserDoFileLogging = null;
    private PreparedStatement setUserDoFileLogging = null;
    private PreparedStatement getUserDoSpecialEffects = null;
    private PreparedStatement setUserDoSpecialEffects = null;
    private PreparedStatement getUserShow0Remaining = null;
    private PreparedStatement setUserShow0Remaining = null;
    private PreparedStatement getUserShowActiveShows = null;
    private PreparedStatement setUserShowActiveShows = null;
    private PreparedStatement getUserRecordChangesForNonActiveShows = null;
    private PreparedStatement setUserRecordChangesForNonActiveShows = null;
    private PreparedStatement getUserRecordChangesSeasonsLowerThanCurrent = null;
    private PreparedStatement setUserRecordChangesSeasonsLowerThanCurrent = null;
    private PreparedStatement getUserMoveStageWithParent = null;
    private PreparedStatement setUserMoveStageWithParent = null;
    private PreparedStatement getUserHaveStageBlockParentStage = null;
    private PreparedStatement setUserHaveStageBlockParentStage = null;
    private PreparedStatement getUserEnableFileLogging = null;
    private PreparedStatement getUserVideoPlayerType = null;
    private PreparedStatement setUserVideoPlayerType = null;
    private PreparedStatement getUserVideoPlayerLocation = null;
    private PreparedStatement setUserVideoPlayerLocation = null;
    private PreparedStatement setUserShowSeason = null;
    private PreparedStatement setUserShowEpisode = null;
    private PreparedStatement getUserShowIgnoredStatus = null;
    private PreparedStatement setUserShowIgnoredStatus = null;
    private PreparedStatement getUserShowActiveStatus = null;
    private PreparedStatement setUserShowActiveStatus = null;
    private PreparedStatement getUserShowHiddenStatus = null;
    private PreparedStatement setUserShowHiddenStatus = null;
    private PreparedStatement getUserShowUsername = null;
    private PreparedStatement setUserShowUsername = null;
    private PreparedStatement getUserShowColumnVisibility = null;
    private PreparedStatement setUserShowColumnVisibility = null;
    private PreparedStatement getUserShowColumnWidth = null;
    private PreparedStatement setUserShowColumnWidth = null;
    private PreparedStatement getUserSeasonColumnVisibility = null;
    private PreparedStatement setUserSeasonColumnVisibility = null;
    private PreparedStatement getUserSeasonColumnWidth = null;
    private PreparedStatement setUserSeasonColumnWidth = null;
    private PreparedStatement getUserEpisodeColumnVisibility = null;
    private PreparedStatement setUserEpisodeColumnVisibility = null;
    private PreparedStatement getUserEpisodeColumnWidth = null;
    private PreparedStatement setUserEpisodeColumnWidth = null;
    private PreparedStatement getUserRemainingColumnVisibility = null;
    private PreparedStatement setUserRemainingColumnVisibility = null;
    private PreparedStatement getUserRemainingColumnWidth = null;
    private PreparedStatement setUserRemainingColumnWidth = null;


    public DBUserSettingsManager(DBManager dbManager) throws SQLException {
        this.dbManager = dbManager;
        boolean tableCreated = this.dbManager.createTable(DBStrings.CREATE_USERSETTINGSTABLE);
        this.dbManager.createTable(DBStrings.CREATE_USERSHOWSETTINGSTABLE);
        this.dbManager.createTable(DBStrings.CREATE_USEREPISODESETTINGSTABLE);

        if (tableCreated) addUserSettings(0); // Insert default program settings
    }

    public synchronized void addShowSettings(int userID, int showID, int currentSeason, int currentEpisode, boolean active, boolean ignored, boolean hidden) {
        if (isNull(addShowSettings))
            addShowSettings = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_addShowSettingsSQL);
        try {
            addShowSettings.setInt(1, userID);
            addShowSettings.setInt(2, showID);
            addShowSettings.setInt(3, currentSeason);
            addShowSettings.setInt(4, currentEpisode);
            addShowSettings.setBoolean(5, active);
            addShowSettings.setBoolean(6, ignored);
            addShowSettings.setBoolean(7, hidden);
            addShowSettings.execute();
            addShowSettings.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void addShowSettings(int userID, int showID, int currentSeason, int currentEpisode) {
        this.addShowSettings(userID, showID, currentSeason, currentEpisode, false, false, false);
    }

    public synchronized void addEpisodeSettings(int userID, int episodeID, int showTimePosition) {
        if (isNull(addEpisodeSettings))
            addEpisodeSettings = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_addEpisodeSettingsSQL);
        try {
            addEpisodeSettings.setInt(1, userID);
            addEpisodeSettings.setInt(2, episodeID);
            addEpisodeSettings.setInt(3, showTimePosition);
            addEpisodeSettings.execute();
            addEpisodeSettings.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void addEpisodeSettings(int userID, int episodeID) {
        this.addEpisodeSettings(userID, episodeID, 0);
    }

    public synchronized void addUserSettings(int userID, boolean showUsername, int updateSpeed, boolean automaticShowUpdating, int timeToWaitForDirectory, boolean show0Remaining, boolean showActiveShows, String language,
                                             boolean recordChangesForNonActiveShows, boolean recordChangedSeasonsLowerThanCurrent, boolean moveStageWithParent, boolean haveStageBlockParentStage,
                                             boolean enableSpecialEffects, boolean enableFileLogging, float showColumnWidth,
                                             float remainingColumnWidth, float seasonColumnWidth, float episodeColumnWidth, boolean showColumnVisibility, boolean remainingColumnVisibility,
                                             boolean seasonColumnVisibility, boolean episodeColumnVisibility, int videoPlayerType, String videoPlayerLocation) {
        if (isNull(insertSettings))
            insertSettings = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_insertSettingsSQL);
        try {
            insertSettings.setInt(1, userID);
            insertSettings.setBoolean(2, showUsername);
            insertSettings.setInt(3, updateSpeed);
            insertSettings.setBoolean(4, automaticShowUpdating);
            insertSettings.setInt(5, timeToWaitForDirectory);
            insertSettings.setBoolean(6, show0Remaining); //
            insertSettings.setBoolean(7, showActiveShows); //
            insertSettings.setString(8, language); //
            insertSettings.setBoolean(9, recordChangesForNonActiveShows);
            insertSettings.setBoolean(10, recordChangedSeasonsLowerThanCurrent);
            insertSettings.setBoolean(11, moveStageWithParent);
            insertSettings.setBoolean(12, haveStageBlockParentStage);
            insertSettings.setBoolean(13, enableSpecialEffects);
            insertSettings.setBoolean(14, enableFileLogging);
            insertSettings.setFloat(15, showColumnWidth);
            insertSettings.setFloat(16, remainingColumnWidth);
            insertSettings.setFloat(17, seasonColumnWidth);
            insertSettings.setFloat(18, episodeColumnWidth);
            insertSettings.setBoolean(19, showColumnVisibility);
            insertSettings.setBoolean(20, remainingColumnVisibility);
            insertSettings.setBoolean(21, seasonColumnVisibility);
            insertSettings.setBoolean(22, episodeColumnVisibility);
            insertSettings.setInt(23, videoPlayerType);
            insertSettings.setString(24, videoPlayerLocation);
            insertSettings.execute();
            insertSettings.clearParameters();
            log.info("Settings were successfully added with ID \"" + userID + "\".");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void addUserSettings(int userID) {
        this.addUserSettings(userID, true, Variables.defaultUpdateSpeed, true, Variables.defaultTimeToWaitForDirectory, false, false, "None", false, false, true, true, true, true,
                Variables.SHOWS_COLUMN_WIDTH, Variables.REMAINING_COLUMN_WIDTH, Variables.SEASONS_COLUMN_WIDTH, Variables.EPISODE_COLUMN_WIDTH, true, true, false, false, 0, "");
    }

    public synchronized Set<Integer> getShows(int userID) {
        if (isNull(getShowsForUser))
            getShowsForUser = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getShowsForUserSQL);
        Set<Integer> result = new HashSet<>();
        try {
            getShowsForUser.setInt(1, userID);
            try (ResultSet resultSet = getShowsForUser.executeQuery()) {
                while (resultSet.next()) {
                    int showID = resultSet.getInt(DBStrings.COLUMN_SHOW_ID);
                    if (ClassHandler.showInfoController().doesShowExist(showID)) result.add(showID);
                }
            }
            getShowsForUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized Set<Integer> getUserActiveShows(int userID) {
        if (isNull(getUserActiveShows))
            getUserActiveShows = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserActiveShows);
        Set<Integer> ignoredShows = new HashSet<>();
        try {
            getUserActiveShows.setInt(1, userID);
            try (ResultSet resultSet = getUserActiveShows.executeQuery()) {
                while (resultSet.next()) ignoredShows.add(resultSet.getInt(DBStrings.COLUMN_SHOW_ID));
            }
            getUserActiveShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return ignoredShows;
    }

    public synchronized Set<Integer> getUserInactiveShows(int userID) {
        if (isNull(getUserInactiveShows))
            getUserInactiveShows = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserInactiveShows);
        Set<Integer> ignoredShows = new HashSet<>();
        try {
            getUserInactiveShows.setInt(1, userID);
            try (ResultSet resultSet = getUserInactiveShows.executeQuery()) {
                while (resultSet.next()) ignoredShows.add(resultSet.getInt(DBStrings.COLUMN_SHOW_ID));
            }
            getUserInactiveShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return ignoredShows;
    }


    public synchronized Set<Integer> getUserNonIgnoredShows(int userID) {
        if (isNull(getUserNonIgnoredShows))
            getUserNonIgnoredShows = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserNonIgnoredShows);
        Set<Integer> ignoredShows = new HashSet<>();
        try {
            getUserNonIgnoredShows.setInt(1, userID);
            try (ResultSet resultSet = getUserNonIgnoredShows.executeQuery()) {
                while (resultSet.next()) ignoredShows.add(resultSet.getInt(DBStrings.COLUMN_SHOW_ID));
            }
            getUserNonIgnoredShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return ignoredShows;
    }

    public synchronized Set<Integer> getUserIgnoredShows(int userID) {
        if (isNull(getUserIgnoredShows))
            getUserIgnoredShows = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserIgnoredShows);
        Set<Integer> ignoredShows = new HashSet<>();
        try {
            getUserIgnoredShows.setInt(1, userID);
            try (ResultSet resultSet = getUserIgnoredShows.executeQuery()) {
                while (resultSet.next()) ignoredShows.add(resultSet.getInt(DBStrings.COLUMN_SHOW_ID));
            }
            getUserIgnoredShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return ignoredShows;
    }

    public synchronized Set<Integer> getUserHiddenShows(int userID) {
        if (isNull(getUserHiddenShows))
            getUserHiddenShows = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserHiddenShows);
        Set<Integer> ignoredShows = new HashSet<>();
        try {
            getUserHiddenShows.setInt(1, userID);
            try (ResultSet resultSet = getUserHiddenShows.executeQuery()) {
                while (resultSet.next()) ignoredShows.add(resultSet.getInt(DBStrings.COLUMN_SHOW_ID));
            }
            getUserHiddenShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return ignoredShows;
    }

    public synchronized int getEpisodePosition(final int userID, int episodeID) {
        if (isNull(getEpisodePosition))
            getEpisodePosition = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getEpisodePositionSQL);
        int episodePosition = -2;
        try {
            getEpisodePosition.setInt(1, userID);
            getEpisodePosition.setInt(2, episodeID);
            try (ResultSet resultSet = getEpisodePosition.executeQuery()) {
                if (resultSet.next()) episodePosition = resultSet.getInt(DBStrings.COLUMN_EPISODETIMEPOSITION);
            }
            getEpisodePosition.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episodePosition;
    }

    public synchronized void setEpisodePosition(final int userID, int episodeID, int position) {
        if (isNull(setEpisodePosition))
            setEpisodePosition = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setEpisodePositionSQL);
        try {
            if (getEpisodePosition(userID, episodeID) == -2) addEpisodeSettings(userID, episodeID, position);
            else {
                setEpisodePosition.setInt(1, position);
                setEpisodePosition.setInt(2, userID);
                setEpisodePosition.setInt(3, episodeID);
                setEpisodePosition.execute();
                setEpisodePosition.clearParameters();
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void removeEpisode(int userID, int episodeID) {
        if (isNull(removeEpisode))
            removeEpisode = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_removeEpisodeSQL);
        try {
            removeEpisode.setInt(1, userID);
            removeEpisode.setInt(2, episodeID);
            removeEpisode.execute();
            removeEpisode.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean doesUserHaveShowSettings(int userID, int showID) {
        if (isNull(checkUserHasShowID))
            checkUserHasShowID = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_checkUserHasShowIDSQL);
        boolean result = false;
        try {
            checkUserHasShowID.setInt(1, userID);
            checkUserHasShowID.setInt(2, showID);
            try (ResultSet resultSet = checkUserHasShowID.executeQuery()) {
                result = resultSet.next();
            }
            checkUserHasShowID.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized int getUserShowSeason(int userID, int showID) {
        if (isNull(getUserShowSeason))
            getUserShowSeason = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserShowSeasonSQL);
        int season = -2;
        try {
            getUserShowSeason.setInt(1, userID);
            getUserShowSeason.setInt(2, showID);
            try (ResultSet resultSet = getUserShowSeason.executeQuery()) {
                if (resultSet.next()) season = resultSet.getInt(DBStrings.COLUMN_CURRENTSEASON);
            }
            getUserShowSeason.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return season;
    }

    public synchronized int getUserShowEpisode(int userID, int showID) {
        if (isNull(getUserShowEpisode))
            getUserShowEpisode = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserShowEpisodeSQL);
        int episode = -2;
        try {
            getUserShowEpisode.setInt(1, userID);
            getUserShowEpisode.setInt(2, showID);
            try (ResultSet resultSet = getUserShowEpisode.executeQuery()) {
                if (resultSet.next()) episode = resultSet.getInt(DBStrings.COLUMN_CURRENTEPISODE);
            }
            getUserShowEpisode.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episode;
    }

    public synchronized String getUserLanguage(int userID) {
        if (isNull(getUserLanguage))
            getUserLanguage = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserLanguageSQL);
        String result = Strings.EmptyString;
        try {
            getUserLanguage.setInt(1, userID);
            try (ResultSet resultSet = getUserLanguage.executeQuery()) {
                if (resultSet.next()) result = resultSet.getString(DBStrings.COLUMN_LANGUAGE);
            }
            getUserLanguage.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserLanguage(int userID, String language) {
        if (isNull(setUserLanguage))
            setUserLanguage = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserLanguageSQL);
        try {
            setUserLanguage.setString(1, language);
            setUserLanguage.setInt(2, userID);
            setUserLanguage.execute();
            setUserLanguage.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserDoShowUpdating(int userID) {
        if (isNull(getUserDoShowUpdating))
            getUserDoShowUpdating = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserDoShowUpdatingSQL);
        boolean result = false;
        try {
            getUserDoShowUpdating.setInt(1, userID);
            try (ResultSet resultSet = getUserDoShowUpdating.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_AUTOMATICSHOWUPDATING);
            }
            getUserDoShowUpdating.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserDoShowUpdating(int userID, boolean doShowUpdating) {
        if (isNull(setUserDoShowUpdating))
            setUserDoShowUpdating = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserDoShowUpdatingSQL);
        try {
            setUserDoShowUpdating.setBoolean(1, doShowUpdating);
            setUserDoShowUpdating.setInt(2, userID);
            setUserDoShowUpdating.execute();
            setUserDoShowUpdating.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized int getUserUpdateSpeed(int userID) {
        if (isNull(getUserUpdateSpeed))
            getUserUpdateSpeed = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserUpdateSpeedSQL);
        int result = Variables.defaultUpdateSpeed;
        try {
            getUserUpdateSpeed.setInt(1, userID);
            try (ResultSet resultSet = getUserUpdateSpeed.executeQuery()) {
                if (resultSet.next()) result = resultSet.getInt(DBStrings.COLUMN_UPDATESPEED);
            }
            getUserUpdateSpeed.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserUpdateSpeed(int userID, int updateSpeed) {
        if (isNull(setUserUpdateSpeed))
            setUserUpdateSpeed = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserUpdateSpeedSQL);
        try {
            setUserUpdateSpeed.setInt(1, updateSpeed);
            setUserUpdateSpeed.setInt(2, userID);
            setUserUpdateSpeed.execute();
            setUserUpdateSpeed.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized int getUserTimeToWaitForDirectory(int userID) {
        if (isNull(getUserTimeToWaitForDirectory))
            getUserTimeToWaitForDirectory = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserTimeToWaitForDirectorySQL);
        int result = Variables.defaultTimeToWaitForDirectory;
        try {
            getUserTimeToWaitForDirectory.setInt(1, userID);
            try (ResultSet resultSet = getUserTimeToWaitForDirectory.executeQuery()) {
                if (resultSet.next()) result = resultSet.getInt(DBStrings.COLUMN_TIMETOWAITFORDIRECTORY);
            }
            getUserTimeToWaitForDirectory.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserTimeToWaitForDirectory(int userID, int timeToWaitForDirectory) {
        if (isNull(setUserTimeToWaitForDirectory))
            setUserTimeToWaitForDirectory = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserTimeToWaitForDirectorySQL);
        try {
            setUserTimeToWaitForDirectory.setInt(1, timeToWaitForDirectory);
            setUserTimeToWaitForDirectory.setInt(2, userID);
            setUserTimeToWaitForDirectory.execute();
            setUserTimeToWaitForDirectory.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserDoFileLogging(int userID) {
        if (isNull(getUserDoFileLogging))
            getUserDoFileLogging = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserDoFileLoggingSQL);
        boolean result = false;
        try {
            getUserDoFileLogging.setInt(1, userID);
            try (ResultSet resultSet = getUserDoFileLogging.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_ENABLEFILELOGGING);
            }
            getUserDoFileLogging.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserDoFileLogging(int userID, boolean doFileLogging) {
        if (isNull(setUserDoFileLogging))
            setUserDoFileLogging = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserDoFileLoggingSQL);
        try {
            setUserDoFileLogging.setBoolean(1, doFileLogging);
            setUserDoFileLogging.setInt(2, userID);
            setUserDoFileLogging.execute();
            setUserDoFileLogging.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserDoSpecialEffects(int userID) {
        if (isNull(getUserDoSpecialEffects))
            getUserDoSpecialEffects = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserDoSpecialEffectsSQL);
        boolean result = false;
        try {
            getUserDoSpecialEffects.setInt(1, userID);
            try (ResultSet resultSet = getUserDoSpecialEffects.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_ENABLESPECIALEFFECTS);
            }
            getUserDoSpecialEffects.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserDoSpecialEffects(int userID, boolean doSpecialEffects) {
        if (isNull(setUserDoSpecialEffects))
            setUserDoSpecialEffects = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserDoSpecialEffectsSQL);
        try {
            setUserDoSpecialEffects.setBoolean(1, doSpecialEffects);
            setUserDoSpecialEffects.setInt(2, userID);
            setUserDoSpecialEffects.execute();
            setUserDoSpecialEffects.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserShow0Remaining(int userID) {
        if (isNull(getUserShow0Remaining))
            getUserShow0Remaining = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserShow0RemainingSQL);
        boolean result = false;
        try {
            getUserShow0Remaining.setInt(1, userID);
            try (ResultSet resultSet = getUserShow0Remaining.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_SHOW0REMAINING);
            }
            getUserShow0Remaining.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShow0Remaining(int userID, boolean show0Remaining) {
        if (isNull(setUserShow0Remaining))
            setUserShow0Remaining = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserShow0RemainingSQL);
        try {
            setUserShow0Remaining.setBoolean(1, show0Remaining);
            setUserShow0Remaining.setInt(2, userID);
            setUserShow0Remaining.execute();
            setUserShow0Remaining.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserShowActiveShows(int userID) {
        if (isNull(getUserShowActiveShows))
            getUserShowActiveShows = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserShowActiveShowsSQL);
        boolean result = false;
        try {
            getUserShowActiveShows.setInt(1, userID);
            try (ResultSet resultSet = getUserShowActiveShows.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_SHOWACTIVESHOWS);
            }
            getUserShowActiveShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowActiveShows(int userID, boolean showActiveShows) {
        if (isNull(setUserShowActiveShows))
            setUserShowActiveShows = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserShowActiveShowsSQL);
        try {
            setUserShowActiveShows.setBoolean(1, showActiveShows);
            setUserShowActiveShows.setInt(2, userID);
            setUserShowActiveShows.execute();
            setUserShowActiveShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserRecordChangesForNonActiveShows(int userID) {
        if (isNull(getUserRecordChangesForNonActiveShows))
            getUserRecordChangesForNonActiveShows = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserRecordChangesForNonActiveShowsSQL);
        boolean result = false;
        try {
            getUserRecordChangesForNonActiveShows.setInt(1, userID);
            try (ResultSet resultSet = getUserRecordChangesForNonActiveShows.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_RECORDCHANGESFORNONACTIVESHOWS);
            }
            getUserRecordChangesForNonActiveShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserRecordChangesForNonActiveShows(int userID, boolean recordChangesForNonActiveShows) {
        if (isNull(setUserRecordChangesForNonActiveShows))
            setUserRecordChangesForNonActiveShows = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserRecordChangesForNonActiveShowsSQL);
        try {
            setUserRecordChangesForNonActiveShows.setBoolean(1, recordChangesForNonActiveShows);
            setUserRecordChangesForNonActiveShows.setInt(2, userID);
            setUserRecordChangesForNonActiveShows.execute();
            setUserRecordChangesForNonActiveShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserRecordChangesSeasonsLowerThanCurrent(int userID) {
        if (isNull(getUserRecordChangesSeasonsLowerThanCurrent))
            getUserRecordChangesSeasonsLowerThanCurrent = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserRecordChangesSeasonsLowerThanCurrentSQL);
        boolean result = false;
        try {
            getUserRecordChangesSeasonsLowerThanCurrent.setInt(1, userID);
            try (ResultSet resultSet = getUserRecordChangesSeasonsLowerThanCurrent.executeQuery()) {
                if (resultSet.next())
                    result = resultSet.getBoolean(DBStrings.COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT);
            }
            getUserRecordChangesSeasonsLowerThanCurrent.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserRecordChangesSeasonsLowerThanCurrent(int userID, boolean recordChangesSeasonsLowerThanCurrent) {
        if (isNull(setUserRecordChangesSeasonsLowerThanCurrent))
            setUserRecordChangesSeasonsLowerThanCurrent = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserRecordChangesSeasonsLowerThanCurrentSQL);
        try {
            setUserRecordChangesSeasonsLowerThanCurrent.setBoolean(1, recordChangesSeasonsLowerThanCurrent);
            setUserRecordChangesSeasonsLowerThanCurrent.setInt(2, userID);
            setUserRecordChangesSeasonsLowerThanCurrent.execute();
            setUserRecordChangesSeasonsLowerThanCurrent.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserMoveStageWithParent(int userID) {
        if (isNull(getUserMoveStageWithParent))
            getUserMoveStageWithParent = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserMoveStageWithParentSQL);
        boolean result = false;
        try {
            getUserMoveStageWithParent.setInt(1, userID);
            try (ResultSet resultSet = getUserMoveStageWithParent.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_MOVESTAGEWITHPARENT);
            }
            getUserMoveStageWithParent.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserMoveStageWithParent(int userID, boolean moveStageWithParent) {
        if (isNull(setUserMoveStageWithParent))
            setUserMoveStageWithParent = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserMoveStageWithParentSQL);
        try {
            setUserMoveStageWithParent.setBoolean(1, moveStageWithParent);
            setUserMoveStageWithParent.setInt(2, userID);
            setUserMoveStageWithParent.execute();
            setUserMoveStageWithParent.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserHaveStageBlockParentStage(int userID) {
        if (isNull(getUserHaveStageBlockParentStage))
            getUserHaveStageBlockParentStage = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserHaveStageBlockParentStageSQL);
        boolean result = false;
        try {
            getUserHaveStageBlockParentStage.setInt(1, userID);
            try (ResultSet resultSet = getUserHaveStageBlockParentStage.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_HAVESTAGEBLOCKPARENTSTAGE);
            }
            getUserHaveStageBlockParentStage.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserHaveStageBlockParentStage(int userID, boolean haveStageBlockParentStage) {
        if (isNull(setUserHaveStageBlockParentStage))
            setUserHaveStageBlockParentStage = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserHaveStageBlockParentStageSQL);
        try {
            setUserHaveStageBlockParentStage.setBoolean(1, haveStageBlockParentStage);
            setUserHaveStageBlockParentStage.setInt(2, userID);
            setUserHaveStageBlockParentStage.execute();
            setUserHaveStageBlockParentStage.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserEnableFileLogging(int userID) {
        if (isNull(getUserEnableFileLogging))
            getUserEnableFileLogging = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserEnableFileLoggingSQL);
        boolean result = false;
        try {
            getUserEnableFileLogging.setInt(1, userID);
            try (ResultSet resultSet = getUserEnableFileLogging.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_ENABLEFILELOGGING);
            }
            getUserEnableFileLogging.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized int getUserVideoPlayerType(int userID) {
        if (isNull(getUserVideoPlayerType))
            getUserVideoPlayerType = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserVideoPlayerTypeSQL);
        int result = -2;
        try {
            getUserVideoPlayerType.setInt(1, userID);
            try (ResultSet resultSet = getUserVideoPlayerType.executeQuery()) {
                if (resultSet.next()) result = resultSet.getInt(DBStrings.COLUMN_VIDEOPLAYERTYPE);
            }
            getUserVideoPlayerType.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserVideoPlayerType(int userID, int videoPlayerType) {
        if (isNull(setUserVideoPlayerType))
            setUserVideoPlayerType = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserVideoPlayerTypeSQL);
        try {
            setUserVideoPlayerType.setInt(1, videoPlayerType);
            setUserVideoPlayerType.setInt(2, userID);
            setUserVideoPlayerType.execute();
            setUserVideoPlayerType.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized String getUserVideoPlayerLocation(int userID) {
        if (isNull(getUserVideoPlayerLocation))
            getUserVideoPlayerLocation = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserVideoPlayerLocationSQL);
        String result = Strings.EmptyString;
        try {
            getUserVideoPlayerLocation.setInt(1, userID);
            try (ResultSet resultSet = getUserVideoPlayerLocation.executeQuery()) {
                if (resultSet.next()) result = resultSet.getString(DBStrings.COLUMN_VIDEOPLAYERLOCATION);
            }
            getUserVideoPlayerLocation.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserVideoPlayerLocation(int userID, String videoPlayerLocation) {
        if (isNull(setUserVideoPlayerLocation))
            setUserVideoPlayerLocation = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserVideoPlayerLocationSQL);
        try {
            setUserVideoPlayerLocation.setString(1, videoPlayerLocation);
            setUserVideoPlayerLocation.setInt(2, userID);
            setUserVideoPlayerLocation.execute();
            setUserVideoPlayerLocation.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void setUserShowSeason(int userID, int showID, int showSeason) {
        if (isNull(setUserShowSeason))
            setUserShowSeason = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserShowSeasonSQL);
        try {
            setUserShowSeason.setInt(1, showSeason);
            setUserShowSeason.setInt(2, userID);
            setUserShowSeason.setInt(3, showID);
            setUserShowSeason.execute();
            setUserShowSeason.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void setUserShowEpisode(int userID, int showID, int showEpisode) {
        if (isNull(setUserShowEpisode))
            setUserShowEpisode = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserShowEpisodeSQL);
        try {
            setUserShowEpisode.setInt(1, showEpisode);
            setUserShowEpisode.setInt(2, userID);
            setUserShowEpisode.setInt(3, showID);
            setUserShowEpisode.execute();
            setUserShowEpisode.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserShowIgnoredStatus(int userID, int showID) {
        if (isNull(getUserShowIgnoredStatus))
            getUserShowIgnoredStatus = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserShowIgnoredStatusSQL);
        boolean result = false;
        try {
            getUserShowIgnoredStatus.setInt(1, userID);
            getUserShowIgnoredStatus.setInt(2, showID);
            try (ResultSet resultSet = getUserShowIgnoredStatus.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_IGNORED);
            }
            getUserShowIgnoredStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowIgnoredStatus(int userID, int showID, boolean showIgnoredStatus) {
        if (isNull(setUserShowIgnoredStatus))
            setUserShowIgnoredStatus = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserShowIgnoredStatusSQL);
        try {
            setUserShowIgnoredStatus.setBoolean(1, showIgnoredStatus);
            setUserShowIgnoredStatus.setInt(2, userID);
            setUserShowIgnoredStatus.setInt(3, showID);
            setUserShowIgnoredStatus.execute();
            setUserShowIgnoredStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserShowActiveStatus(int userID, int showID) {
        if (isNull(getUserShowActiveStatus))
            getUserShowActiveStatus = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserShowActiveStatusSQL);
        boolean result = false;
        try {
            getUserShowActiveStatus.setInt(1, userID);
            getUserShowActiveStatus.setInt(2, showID);
            try (ResultSet resultSet = getUserShowActiveStatus.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_ACTIVE);
            }
            getUserShowActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowActiveStatus(int userID, int showID, boolean showActiveStatus) {
        if (isNull(setUserShowActiveStatus))
            setUserShowActiveStatus = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserShowActiveStatusSQL);
        try {
            setUserShowActiveStatus.setBoolean(1, showActiveStatus);
            setUserShowActiveStatus.setInt(2, userID);
            setUserShowActiveStatus.setInt(3, showID);
            setUserShowActiveStatus.execute();
            setUserShowActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserShowHiddenStatus(int userID, int showID) {
        if (isNull(getUserShowHiddenStatus))
            getUserShowHiddenStatus = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserShowHiddenStatusSQL);
        boolean result = false;
        try {
            getUserShowHiddenStatus.setInt(1, userID);
            getUserShowHiddenStatus.setInt(2, showID);
            try (ResultSet resultSet = getUserShowHiddenStatus.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_HIDDEN);
            }
            getUserShowHiddenStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }


    public synchronized void setUserShowHiddenStatus(int userID, int showID, boolean showHiddenStatus) {
        if (isNull(setUserShowHiddenStatus))
            setUserShowHiddenStatus = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserShowHiddenStatusSQL);
        try {
            setUserShowHiddenStatus.setBoolean(1, showHiddenStatus);
            setUserShowHiddenStatus.setInt(2, userID);
            setUserShowHiddenStatus.setInt(3, showID);
            setUserShowHiddenStatus.execute();
            setUserShowHiddenStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean doesShowSettingExistForUser(int userID, int showID) {
        if (isNull(checkIfShowSettingsExistForUser))
            checkIfShowSettingsExistForUser = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_checkIfShowSettingsExistForUserSQL);
        boolean result = false;
        try {
            checkIfShowSettingsExistForUser.setInt(1, userID);
            checkIfShowSettingsExistForUser.setInt(2, showID);
            try (ResultSet resultSet = checkIfShowSettingsExistForUser.executeQuery()) {
                result = resultSet.next();
            }
            checkIfShowSettingsExistForUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized boolean getUserShowUsername(int userID) {
        if (isNull(getUserShowUsername))
            getUserShowUsername = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserShowUsernameSQL);
        boolean result = false;
        try {
            getUserShowUsername.setInt(1, userID);
            try (ResultSet resultSet = getUserShowUsername.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_SHOWUSERNAME);
            }
            getUserShowUsername.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowUsername(int userID, boolean showUsername) {
        if (isNull(setUserShowUsername))
            setUserShowUsername = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserShowUsernameSQL);
        try {
            setUserShowUsername.setBoolean(1, showUsername);
            setUserShowUsername.setInt(2, userID);
            setUserShowUsername.execute();
            setUserShowUsername.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserShowColumnVisibility(int userID) {
        if (isNull(getUserShowColumnVisibility))
            getUserShowColumnVisibility = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserShowColumnVisibilitySQL);
        boolean result = false;
        try {
            getUserShowColumnVisibility.setInt(1, userID);
            try (ResultSet resultSet = getUserShowColumnVisibility.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_SHOWCOLUMNVISIBILITY);
            }
            getUserShowColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowColumnVisibility(int userID, boolean showColumn) {
        if (isNull(setUserShowColumnVisibility))
            setUserShowColumnVisibility = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserShowColumnVisibilitySQL);
        try {
            setUserShowColumnVisibility.setBoolean(1, showColumn);
            setUserShowColumnVisibility.setInt(2, userID);
            setUserShowColumnVisibility.execute();
            setUserShowColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized float getUserShowColumnWidth(int userID) {
        if (isNull(getUserShowColumnWidth))
            getUserShowColumnWidth = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserShowColumnWidthSQL);
        float result = -2.0f;
        try {
            getUserShowColumnWidth.setInt(1, userID);
            try (ResultSet resultSet = getUserShowColumnWidth.executeQuery()) {
                if (resultSet.next()) result = resultSet.getFloat(DBStrings.COLUMN_SHOWCOLUMNWIDTH);
            }
            getUserShowColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowColumnWidth(int userID, float columnWidth) {
        if (isNull(setUserShowColumnWidth))
            setUserShowColumnWidth = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserShowColumnWidthSQL);
        try {
            setUserShowColumnWidth.setFloat(1, columnWidth);
            setUserShowColumnWidth.setInt(2, userID);
            setUserShowColumnWidth.execute();
            setUserShowColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserSeasonColumnVisibility(int userID) {
        if (isNull(getUserSeasonColumnVisibility))
            getUserSeasonColumnVisibility = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserSeasonColumnVisibilitySQL);
        boolean result = false;
        try {
            getUserSeasonColumnVisibility.setInt(1, userID);
            try (ResultSet resultSet = getUserSeasonColumnVisibility.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_SEASONCOLUMNVISIBILITY);
            }
            getUserSeasonColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserSeasonColumnVisibility(int userID, boolean showColumn) {
        if (isNull(setUserSeasonColumnVisibility))
            setUserSeasonColumnVisibility = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserSeasonColumnVisibilitySQL);
        try {
            setUserSeasonColumnVisibility.setBoolean(1, showColumn);
            setUserSeasonColumnVisibility.setInt(2, userID);
            setUserSeasonColumnVisibility.execute();
            setUserSeasonColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized float getUserSeasonColumnWidth(int userID) {
        if (isNull(getUserSeasonColumnWidth))
            getUserSeasonColumnWidth = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserSeasonColumnWidthSQL);
        float result = -2.0f;
        try {
            getUserSeasonColumnWidth.setInt(1, userID);
            try (ResultSet resultSet = getUserSeasonColumnWidth.executeQuery()) {
                if (resultSet.next()) result = resultSet.getFloat(DBStrings.COLUMN_SEASONCOLUMNWIDTH);
            }
            getUserSeasonColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserSeasonColumnWidth(int userID, float columnWidth) {
        if (isNull(setUserSeasonColumnWidth))
            setUserSeasonColumnWidth = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserSeasonColumnWidthSQL);
        try {
            setUserSeasonColumnWidth.setFloat(1, columnWidth);
            setUserSeasonColumnWidth.setInt(2, userID);
            setUserSeasonColumnWidth.execute();
            setUserSeasonColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserEpisodeColumnVisibility(int userID) {
        if (isNull(getUserEpisodeColumnVisibility))
            getUserEpisodeColumnVisibility = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserEpisodeColumnVisibilitySQL);
        boolean result = false;
        try {
            getUserEpisodeColumnVisibility.setInt(1, userID);
            try (ResultSet resultSet = getUserEpisodeColumnVisibility.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_EPISODECOLUMNVISIBILITY);
            }
            getUserEpisodeColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserEpisodeColumnVisibility(int userID, boolean showColumn) {
        if (isNull(setUserEpisodeColumnVisibility))
            setUserEpisodeColumnVisibility = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserEpisodeColumnVisibilitySQL);
        try {
            setUserEpisodeColumnVisibility.setBoolean(1, showColumn);
            setUserEpisodeColumnVisibility.setInt(2, userID);
            setUserEpisodeColumnVisibility.execute();
            setUserEpisodeColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized float getUserEpisodeColumnWidth(int userID) {
        if (isNull(getUserEpisodeColumnWidth))
            getUserEpisodeColumnWidth = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserEpisodeColumnWidthSQL);
        float result = -2.0f;
        try {
            getUserEpisodeColumnWidth.setInt(1, userID);
            try (ResultSet resultSet = getUserEpisodeColumnWidth.executeQuery()) {
                if (resultSet.next()) result = resultSet.getFloat(DBStrings.COLUMN_EPISODECOLUMNWIDTH);
            }
            getUserEpisodeColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserEpisodeColumnWidth(int userID, float columnWidth) {
        if (isNull(setUserEpisodeColumnWidth))
            setUserEpisodeColumnWidth = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserEpisodeColumnWidthSQL);
        try {
            setUserEpisodeColumnWidth.setFloat(1, columnWidth);
            setUserEpisodeColumnWidth.setInt(2, userID);
            setUserEpisodeColumnWidth.execute();
            setUserEpisodeColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getUserRemainingColumnVisibility(int userID) {
        if (isNull(getUserRemainingColumnVisibility))
            getUserRemainingColumnVisibility = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserRemainingColumnVisibilitySQL);
        boolean result = false;
        try {
            getUserRemainingColumnVisibility.setInt(1, userID);
            try (ResultSet resultSet = getUserRemainingColumnVisibility.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(DBStrings.COLUMN_REMAININGCOLUMNVISIBILITY);
            }
            getUserRemainingColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserRemainingColumnVisibility(int userID, boolean showColumn) {
        if (isNull(setUserRemainingColumnVisibility))
            setUserRemainingColumnVisibility = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserRemainingColumnVisibilitySQL);
        try {
            setUserRemainingColumnVisibility.setBoolean(1, showColumn);
            setUserRemainingColumnVisibility.setInt(2, userID);
            setUserRemainingColumnVisibility.execute();
            setUserRemainingColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized float getUserRemainingColumnWidth(int userID) {
        if (isNull(getUserRemainingColumnWidth))
            getUserRemainingColumnWidth = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_getUserRemainingColumnWidthSQL);
        float result = -2.0f;
        try {
            getUserRemainingColumnWidth.setInt(1, userID);
            try (ResultSet resultSet = getUserRemainingColumnWidth.executeQuery()) {
                if (resultSet.next()) result = resultSet.getFloat(DBStrings.COLUMN_REMAININGCOLUMNWIDTH);
            }
            getUserRemainingColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserRemainingColumnWidth(int userID, float columnWidth) {
        if (isNull(setUserRemainingColumnWidth))
            setUserRemainingColumnWidth = dbManager.prepareStatement(DBStrings.DBUserSettingsManager_setUserRemainingColumnWidthSQL);
        try {
            setUserRemainingColumnWidth.setFloat(1, columnWidth);
            setUserRemainingColumnWidth.setInt(2, userID);
            setUserRemainingColumnWidth.execute();
            setUserRemainingColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    private boolean isNull(Object object) {
        return object == null;
    }
}
