package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.*;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class DBUserSettingsManager {
    private final Logger log = Logger.getLogger(DBUserSettingsManager.class.getName());
    private final PreparedStatement insertSettings;
    //private final PreparedStatement getShowsMultiConditional;
    private final PreparedStatement getEpisodePosition;
    private final PreparedStatement setEpisodePosition;
    private final PreparedStatement addShowSettings;
    private final PreparedStatement addEpisodeSettings;
    private final PreparedStatement removeEpisode;
    private final PreparedStatement getShowsForUser;
    private final PreparedStatement getUserShowSeason;
    private final PreparedStatement getUserShowEpisode;
    private final PreparedStatement checkIfShowSettingsExistForUser;
    private final PreparedStatement checkUserHasShowID;

    // Settings
    private final PreparedStatement getUserLanguage;
    private final PreparedStatement setUserLanguage;
    private final PreparedStatement getUserDoShowUpdating;
    private final PreparedStatement setUserDoShowUpdating;
    private final PreparedStatement getUserUpdateSpeed;
    private final PreparedStatement setUserUpdateSpeed;
    private final PreparedStatement getUserTimeToWaitForDirectory;
    private final PreparedStatement setUserTimeToWaitForDirectory;
    private final PreparedStatement getUserDoFileLogging;
    private final PreparedStatement setUserDoFileLogging;
    private final PreparedStatement getUserDoSpecialEffects;
    private final PreparedStatement setUserDoSpecialEffects;
    private final PreparedStatement getUserShow0Remaining;
    private final PreparedStatement setUserShow0Remaining;
    private final PreparedStatement getUserShowActiveShows;
    private final PreparedStatement setUserShowActiveShows;
    private final PreparedStatement getUserRecordChangesForNonActiveShows;
    private final PreparedStatement setUserRecordChangesForNonActiveShows;
    private final PreparedStatement getUserRecordChangesSeasonsLowerThanCurrent;
    private final PreparedStatement setUserRecordChangesSeasonsLowerThanCurrent;
    private final PreparedStatement getUserMoveStageWithParent;
    private final PreparedStatement setUserMoveStageWithParent;
    private final PreparedStatement getUserHaveStageBlockParentStage;
    private final PreparedStatement setUserHaveStageBlockParentStage;
    private final PreparedStatement getUserEnableFileLogging;
    private final PreparedStatement getUserVideoPlayerType;
    private final PreparedStatement setUserVideoPlayerType;
    private final PreparedStatement getUserVideoPlayerLocation;
    private final PreparedStatement setUserVideoPlayerLocation;
    private final PreparedStatement setUserShowSeason;
    private final PreparedStatement setUserShowEpisode;
    private final PreparedStatement getUserShowIgnoredStatus;
    private final PreparedStatement setUserShowIgnoredStatus;
    private final PreparedStatement getUserShowActiveStatus;
    private final PreparedStatement setUserShowActiveStatus;
    private final PreparedStatement getUserShowHiddenStatus;
    private final PreparedStatement setUserShowHiddenStatus;
    private final PreparedStatement getUserShowUsername;
    private final PreparedStatement setUserShowUsername;
    private final PreparedStatement getUserShowColumnVisibility;
    private final PreparedStatement setUserShowColumnVisibility;
    private final PreparedStatement getUserShowColumnWidth;
    private final PreparedStatement setUserShowColumnWidth;
    private final PreparedStatement getUserSeasonColumnVisibility;
    private final PreparedStatement setUserSeasonColumnVisibility;
    private final PreparedStatement getUserSeasonColumnWidth;
    private final PreparedStatement setUserSeasonColumnWidth;
    private final PreparedStatement getUserEpisodeColumnVisibility;
    private final PreparedStatement setUserEpisodeColumnVisibility;
    private final PreparedStatement getUserEpisodeColumnWidth;
    private final PreparedStatement setUserEpisodeColumnWidth;
    private final PreparedStatement getUserRemainingColumnVisibility;
    private final PreparedStatement setUserRemainingColumnVisibility;
    private final PreparedStatement getUserRemainingColumnWidth;
    private final PreparedStatement setUserRemainingColumnWidth;


    public DBUserSettingsManager(Connection connection) throws SQLException {
        boolean doesNotExist;
        try (Statement statement = connection.createStatement()) {
            doesNotExist = !createSettingsTable(statement);
            createShowSettingsTable(statement);
            createEpisodeSettingsTable(statement);
        }

        insertSettings = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_USERSETTINGS + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        //getShowsMultiConditional = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_ACTIVE + "=? AND " + StringDB.COLUMN_IGNORED + "=? AND " + StringDB.COLUMN_HIDDEN + "=?");
        getEpisodePosition = connection.prepareStatement("SELECT " + StringDB.COLUMN_EPISODETIMEPOSITION + " FROM " + StringDB.TABLE_USEREPISODESETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_EPISODE_ID + "=?");
        setEpisodePosition = connection.prepareStatement("UPDATE " + StringDB.TABLE_USEREPISODESETTINGS + " SET " + StringDB.COLUMN_EPISODETIMEPOSITION + "=? WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_EPISODE_ID + "=?");
        addShowSettings = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_USERSHOWSETTINGS + " VALUES (?, ?, ?, ?, ?, ?, ?)");
        addEpisodeSettings = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_USEREPISODESETTINGS + " VALUES (?, ?, ?)");
        removeEpisode = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_USEREPISODESETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_EPISODE_ID + "=?");
        getShowsForUser = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserShowSeason = connection.prepareStatement("SELECT " + StringDB.COLUMN_CURRENTSEASON + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        getUserShowEpisode = connection.prepareStatement("SELECT " + StringDB.COLUMN_CURRENTEPISODE + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        getUserLanguage = connection.prepareStatement("SELECT " + StringDB.COLUMN_LANGUAGE + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserLanguage = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_LANGUAGE + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserDoShowUpdating = connection.prepareStatement("SELECT " + StringDB.COLUMN_AUTOMATICSHOWUPDATING + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserDoShowUpdating = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_AUTOMATICSHOWUPDATING + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserUpdateSpeed = connection.prepareStatement("SELECT " + StringDB.COLUMN_UPDATESPEED + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserUpdateSpeed = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_UPDATESPEED + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserTimeToWaitForDirectory = connection.prepareStatement("SELECT " + StringDB.COLUMN_TIMETOWAITFORDIRECTORY + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserTimeToWaitForDirectory = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_TIMETOWAITFORDIRECTORY + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserDoFileLogging = connection.prepareStatement("SELECT " + StringDB.COLUMN_ENABLEFILELOGGING + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserDoFileLogging = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_ENABLEFILELOGGING + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserDoSpecialEffects = connection.prepareStatement("SELECT " + StringDB.COLUMN_ENABLESPECIALEFFECTS + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserDoSpecialEffects = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_ENABLESPECIALEFFECTS + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserShow0Remaining = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOW0REMAINING + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserShow0Remaining = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_SHOW0REMAINING + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserShowActiveShows = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOWACTIVESHOWS + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserShowActiveShows = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_SHOWACTIVESHOWS + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserRecordChangesForNonActiveShows = connection.prepareStatement("SELECT " + StringDB.COLUMN_RECORDCHANGESFORNONACTIVESHOWS + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserRecordChangesForNonActiveShows = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_RECORDCHANGESFORNONACTIVESHOWS + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserRecordChangesSeasonsLowerThanCurrent = connection.prepareStatement("SELECT " + StringDB.COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserRecordChangesSeasonsLowerThanCurrent = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserMoveStageWithParent = connection.prepareStatement("SELECT " + StringDB.COLUMN_MOVESTAGEWITHPARENT + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserMoveStageWithParent = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_MOVESTAGEWITHPARENT + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserHaveStageBlockParentStage = connection.prepareStatement("SELECT " + StringDB.COLUMN_HAVESTAGEBLOCKPARENTSTAGE + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserHaveStageBlockParentStage = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_HAVESTAGEBLOCKPARENTSTAGE + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserEnableFileLogging = connection.prepareStatement("SELECT " + StringDB.COLUMN_ENABLEFILELOGGING + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserVideoPlayerType = connection.prepareStatement("SELECT " + StringDB.COLUMN_VIDEOPLAYERTYPE + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserVideoPlayerType = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_VIDEOPLAYERTYPE + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserVideoPlayerLocation = connection.prepareStatement("SELECT " + StringDB.COLUMN_VIDEOPLAYERLOCATION + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserVideoPlayerLocation = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_VIDEOPLAYERLOCATION + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
         /*= connection.prepareStatement("SELECT " +  + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
         = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " +  + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");*/
        setUserShowSeason = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSHOWSETTINGS + " SET " + StringDB.COLUMN_CURRENTSEASON + "=?" + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        setUserShowEpisode = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSHOWSETTINGS + " SET " + StringDB.COLUMN_CURRENTEPISODE + "=?" + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        getUserShowIgnoredStatus = connection.prepareStatement("SELECT " + StringDB.COLUMN_IGNORED + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        setUserShowIgnoredStatus = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSHOWSETTINGS + " SET " + StringDB.COLUMN_IGNORED + "=? WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        getUserShowActiveStatus = connection.prepareStatement("SELECT " + StringDB.COLUMN_ACTIVE + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        setUserShowActiveStatus = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSHOWSETTINGS + " SET " + StringDB.COLUMN_ACTIVE + "=? WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        getUserShowHiddenStatus = connection.prepareStatement("SELECT " + StringDB.COLUMN_HIDDEN + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        setUserShowHiddenStatus = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSHOWSETTINGS + " SET " + StringDB.COLUMN_HIDDEN + "=? WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        checkIfShowSettingsExistForUser = connection.prepareStatement("SELECT  " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");
        getUserShowUsername = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOWUSERNAME + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserShowUsername = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_SHOWUSERNAME + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserShowColumnVisibility = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOWCOLUMNVISIBILITY + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserShowColumnVisibility = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_SHOWCOLUMNVISIBILITY + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserShowColumnWidth = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOWCOLUMNWIDTH + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserShowColumnWidth = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_SHOWCOLUMNWIDTH + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserSeasonColumnVisibility = connection.prepareStatement("SELECT " + StringDB.COLUMN_SEASONCOLUMNVISIBILITY + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserSeasonColumnVisibility = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_SEASONCOLUMNVISIBILITY + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserSeasonColumnWidth = connection.prepareStatement("SELECT " + StringDB.COLUMN_SEASONCOLUMNWIDTH + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserSeasonColumnWidth = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_SEASONCOLUMNWIDTH + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserEpisodeColumnVisibility = connection.prepareStatement("SELECT " + StringDB.COLUMN_EPISODECOLUMNVISIBILITY + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserEpisodeColumnVisibility = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_EPISODECOLUMNVISIBILITY + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserEpisodeColumnWidth = connection.prepareStatement("SELECT " + StringDB.COLUMN_EPISODECOLUMNWIDTH + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserEpisodeColumnWidth = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_EPISODECOLUMNWIDTH + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserRemainingColumnVisibility = connection.prepareStatement("SELECT " + StringDB.COLUMN_REMAININGCOLUMNVISIBILITY + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserRemainingColumnVisibility = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_REMAININGCOLUMNVISIBILITY + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserRemainingColumnWidth = connection.prepareStatement("SELECT " + StringDB.COLUMN_REMAININGCOLUMNWIDTH + " FROM " + StringDB.TABLE_USERSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setUserRemainingColumnWidth = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERSETTINGS + " SET " + StringDB.COLUMN_REMAININGCOLUMNWIDTH + "=? WHERE " + StringDB.COLUMN_USER_ID + "=?");
        checkUserHasShowID = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_SHOW_ID + "=?");

        if (doesNotExist) addUserSettings(0); // Insert default program settings
    }


    private void createEpisodeSettingsTable(Statement statement) {
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_USEREPISODESETTINGS + "(" + StringDB.COLUMN_USER_ID + " INTEGER NOT NULL, " + StringDB.COLUMN_EPISODE_ID + " INTEGER NOT NULL, " + StringDB.COLUMN_EPISODETIMEPOSITION + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    private void createShowSettingsTable(Statement statement) {
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_USERSHOWSETTINGS + "(" + StringDB.COLUMN_USER_ID + " INTEGER NOT NULL, " + StringDB.COLUMN_SHOW_ID + " INTEGER NOT NULL, " + StringDB.COLUMN_CURRENTSEASON + " INTEGER NOT NULL, " + StringDB.COLUMN_CURRENTEPISODE + " INTEGER NOT NULL, " + StringDB.COLUMN_ACTIVE + " BOOLEAN NOT NULL, " + StringDB.COLUMN_IGNORED + " BOOLEAN NOT NULL, " + StringDB.COLUMN_HIDDEN + " BOOLEAN NOT NULL)");
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    private boolean createSettingsTable(Statement statement) {
        boolean alreadyExists = false;
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_USERSETTINGS + "(" + StringDB.COLUMN_USER_ID + " INTEGER UNIQUE NOT NULL, " + StringDB.COLUMN_SHOWUSERNAME + " BOOLEAN NOT NULL, " + StringDB.COLUMN_UPDATESPEED + " INTEGER NOT NULL," +
                    StringDB.COLUMN_AUTOMATICSHOWUPDATING + " BOOLEAN NOT NULL, " + StringDB.COLUMN_TIMETOWAITFORDIRECTORY + " INTEGER NOT NULL, " +
                    StringDB.COLUMN_SHOW0REMAINING + " BOOLEAN NOT NULL, " + StringDB.COLUMN_SHOWACTIVESHOWS + " BOOLEAN NOT NULL, " + StringDB.COLUMN_LANGUAGE + " VARCHAR(20) NOT NULL, " +
                    StringDB.COLUMN_RECORDCHANGESFORNONACTIVESHOWS + " BOOLEAN NOT NULL, " + StringDB.COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT + " BOOLEAN NOT NULL, " +
                    StringDB.COLUMN_MOVESTAGEWITHPARENT + " BOOLEAN NOT NULL, " + StringDB.COLUMN_HAVESTAGEBLOCKPARENTSTAGE + " BOOLEAN NOT NULL, " + StringDB.COLUMN_ENABLESPECIALEFFECTS + " BOOLEAN NOT NULL, " +
                    StringDB.COLUMN_ENABLEFILELOGGING + " BOOLEAN NOT NULL, " +
                    StringDB.COLUMN_SHOWCOLUMNWIDTH + " FLOAT NOT NULL, " + StringDB.COLUMN_REMAININGCOLUMNWIDTH + " FLOAT NOT NULL, " +
                    StringDB.COLUMN_SEASONCOLUMNWIDTH + " FLOAT NOT NULL, " + StringDB.COLUMN_EPISODECOLUMNWIDTH + " FLOAT NOT NULL, " + StringDB.COLUMN_SHOWCOLUMNVISIBILITY + " BOOLEAN NOT NULL, " +
                    StringDB.COLUMN_REMAININGCOLUMNVISIBILITY + " BOOLEAN NOT NULL, " + StringDB.COLUMN_SEASONCOLUMNVISIBILITY + " BOOLEAN NOT NULL, " + StringDB.COLUMN_EPISODECOLUMNVISIBILITY + " BOOLEAN NOT NULL, " +
                    StringDB.COLUMN_VIDEOPLAYERTYPE + " INTEGER NOT NULL, " + StringDB.COLUMN_VIDEOPLAYERLOCATION + " VARCHAR(" + StringDB.directoryLength + ") NOT NULL)");
        } catch (SQLException e) {
            if (GenericMethods.doesTableExistsFromError(e)) alreadyExists = true;
            else GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return alreadyExists;
    }

    public synchronized void addShowSettings(int userID, int showID, int currentSeason, int currentEpisode, boolean active, boolean ignored, boolean hidden) {
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
        Set<Integer> result = new HashSet<>();
        try {
            getShowsForUser.setInt(1, userID);
            try (ResultSet resultSet = getShowsForUser.executeQuery()) {
                while (resultSet.next()) {
                    int showID = resultSet.getInt(StringDB.COLUMN_SHOW_ID);
                    if (ClassHandler.showInfoController().doesShowExist(showID)) result.add(showID);
                }
            }
            getShowsForUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized Set<Integer> getShows(int userID, String settingType, boolean setting) { // TODO Remove?
        Set<Integer> result = new HashSet<>();
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID + " AND " + settingType + "=" + setting)) {
            while (resultSet.next()) result.add(resultSet.getInt(StringDB.COLUMN_SHOW_ID));
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized Set<Integer> getShows(int userID, boolean active, boolean ignored, boolean hidden) { // TODO Remove?
        Set<Integer> result = new HashSet<>();
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID + " AND " + StringDB.COLUMN_ACTIVE + "=" + active + " AND " + StringDB.COLUMN_IGNORED + "=" + ignored + " AND " + StringDB.COLUMN_HIDDEN + "=" + hidden)) {
            while (resultSet.next()) result.add(resultSet.getInt(StringDB.COLUMN_SHOW_ID));
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized int getEpisodePosition(final int userID, int episodeID) {
        int episodePosition = -2;
        try {
            getEpisodePosition.setInt(1, userID);
            getEpisodePosition.setInt(2, episodeID);
            try (ResultSet resultSet = getEpisodePosition.executeQuery()) {
                if (resultSet.next()) episodePosition = resultSet.getInt(StringDB.COLUMN_EPISODETIMEPOSITION);
            }
            getEpisodePosition.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episodePosition;
    }

    public synchronized void setEpisodePosition(final int userID, int episodeID, int position) {
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
        int season = -2;
        try {
            getUserShowSeason.setInt(1, userID);
            getUserShowSeason.setInt(2, showID);
            try (ResultSet resultSet = getUserShowSeason.executeQuery()) {
                if (resultSet.next()) season = resultSet.getInt(StringDB.COLUMN_CURRENTSEASON);
            }
            getUserShowSeason.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return season;
    }

    public synchronized int getUserShowEpisode(int userID, int showID) {
        int episode = -2;
        try {
            getUserShowEpisode.setInt(1, userID);
            getUserShowEpisode.setInt(2, showID);
            try (ResultSet resultSet = getUserShowEpisode.executeQuery()) {
                if (resultSet.next()) episode = resultSet.getInt(StringDB.COLUMN_CURRENTEPISODE);
            }
            getUserShowEpisode.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return episode;
    }

    public synchronized String getUserLanguage(int userID) {
        String result = Strings.EmptyString;
        try {
            getUserLanguage.setInt(1, userID);
            try (ResultSet resultSet = getUserLanguage.executeQuery()) {
                if (resultSet.next()) result = resultSet.getString(StringDB.COLUMN_LANGUAGE);
            }
            getUserLanguage.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserLanguage(int userID, String language) {
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
        boolean result = false;
        try {
            getUserDoShowUpdating.setInt(1, userID);
            try (ResultSet resultSet = getUserDoShowUpdating.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_AUTOMATICSHOWUPDATING);
            }
            getUserDoShowUpdating.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserDoShowUpdating(int userID, boolean doShowUpdating) {
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
        int result = Variables.defaultUpdateSpeed;
        try {
            getUserUpdateSpeed.setInt(1, userID);
            try (ResultSet resultSet = getUserUpdateSpeed.executeQuery()) {
                if (resultSet.next()) result = resultSet.getInt(StringDB.COLUMN_UPDATESPEED);
            }
            getUserUpdateSpeed.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserUpdateSpeed(int userID, int updateSpeed) {
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
        int result = Variables.defaultTimeToWaitForDirectory;
        try {
            getUserTimeToWaitForDirectory.setInt(1, userID);
            try (ResultSet resultSet = getUserTimeToWaitForDirectory.executeQuery()) {
                if (resultSet.next()) result = resultSet.getInt(StringDB.COLUMN_TIMETOWAITFORDIRECTORY);
            }
            getUserTimeToWaitForDirectory.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserTimeToWaitForDirectory(int userID, int timeToWaitForDirectory) {
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
        boolean result = false;
        try {
            getUserDoFileLogging.setInt(1, userID);
            try (ResultSet resultSet = getUserDoFileLogging.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_ENABLEFILELOGGING);
            }
            getUserDoFileLogging.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserDoFileLogging(int userID, boolean doFileLogging) {
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
        boolean result = false;
        try {
            getUserDoSpecialEffects.setInt(1, userID);
            try (ResultSet resultSet = getUserDoSpecialEffects.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_ENABLESPECIALEFFECTS);
            }
            getUserDoSpecialEffects.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserDoSpecialEffects(int userID, boolean doSpecialEffects) {
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
        boolean result = false;
        try {
            getUserShow0Remaining.setInt(1, userID);
            try (ResultSet resultSet = getUserShow0Remaining.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_SHOW0REMAINING);
            }
            getUserShow0Remaining.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShow0Remaining(int userID, boolean show0Remaining) {
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
        boolean result = false;
        try {
            getUserShowActiveShows.setInt(1, userID);
            try (ResultSet resultSet = getUserShowActiveShows.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_SHOWACTIVESHOWS);
            }
            getUserShowActiveShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowActiveShows(int userID, boolean showActiveShows) {
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
        boolean result = false;
        try {
            getUserRecordChangesForNonActiveShows.setInt(1, userID);
            try (ResultSet resultSet = getUserRecordChangesForNonActiveShows.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_RECORDCHANGESFORNONACTIVESHOWS);
            }
            getUserRecordChangesForNonActiveShows.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserRecordChangesForNonActiveShows(int userID, boolean recordChangesForNonActiveShows) {
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
        boolean result = false;
        try {
            getUserRecordChangesSeasonsLowerThanCurrent.setInt(1, userID);
            try (ResultSet resultSet = getUserRecordChangesSeasonsLowerThanCurrent.executeQuery()) {
                if (resultSet.next())
                    result = resultSet.getBoolean(StringDB.COLUMN_RECORDCHANGEDSEASONSLOWERTHANCURRENT);
            }
            getUserRecordChangesSeasonsLowerThanCurrent.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserRecordChangesSeasonsLowerThanCurrent(int userID, boolean recordChangesSeasonsLowerThanCurrent) {
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
        boolean result = false;
        try {
            getUserMoveStageWithParent.setInt(1, userID);
            try (ResultSet resultSet = getUserMoveStageWithParent.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_MOVESTAGEWITHPARENT);
            }
            getUserMoveStageWithParent.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserMoveStageWithParent(int userID, boolean moveStageWithParent) {
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
        boolean result = false;
        try {
            getUserHaveStageBlockParentStage.setInt(1, userID);
            try (ResultSet resultSet = getUserHaveStageBlockParentStage.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_HAVESTAGEBLOCKPARENTSTAGE);
            }
            getUserHaveStageBlockParentStage.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserHaveStageBlockParentStage(int userID, boolean haveStageBlockParentStage) {
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
        boolean result = false;
        try {
            getUserEnableFileLogging.setInt(1, userID);
            try (ResultSet resultSet = getUserEnableFileLogging.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_ENABLEFILELOGGING);
            }
            getUserEnableFileLogging.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized int getUserVideoPlayerType(int userID) {
        int result = -2;
        try {
            getUserVideoPlayerType.setInt(1, userID);
            try (ResultSet resultSet = getUserVideoPlayerType.executeQuery()) {
                if (resultSet.next()) result = resultSet.getInt(StringDB.COLUMN_VIDEOPLAYERTYPE);
            }
            getUserVideoPlayerType.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserVideoPlayerType(int userID, int videoPlayerType) {
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
        String result = Strings.EmptyString;
        try {
            getUserVideoPlayerLocation.setInt(1, userID);
            try (ResultSet resultSet = getUserVideoPlayerLocation.executeQuery()) {
                if (resultSet.next()) result = resultSet.getString(StringDB.COLUMN_VIDEOPLAYERLOCATION);
            }
            getUserVideoPlayerLocation.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserVideoPlayerLocation(int userID, String videoPlayerLocation) {
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
        boolean result = false;
        try {
            getUserShowIgnoredStatus.setInt(1, userID);
            getUserShowIgnoredStatus.setInt(2, showID);
            try (ResultSet resultSet = getUserShowIgnoredStatus.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_IGNORED);
            }
            getUserShowIgnoredStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowIgnoredStatus(int userID, int showID, boolean showIgnoredStatus) {
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
        boolean result = false;
        try {
            getUserShowActiveStatus.setInt(1, userID);
            getUserShowActiveStatus.setInt(2, showID);
            try (ResultSet resultSet = getUserShowActiveStatus.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_ACTIVE);
            }
            getUserShowActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowActiveStatus(int userID, int showID, boolean showActiveStatus) {
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
        boolean result = false;
        try {
            getUserShowHiddenStatus.setInt(1, userID);
            getUserShowHiddenStatus.setInt(2, showID);
            try (ResultSet resultSet = getUserShowHiddenStatus.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_HIDDEN);
            }
            getUserShowHiddenStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }


    public synchronized void setUserShowHiddenStatus(int userID, int showID, boolean showHiddenStatus) {
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
        boolean result = false;
        try {
            getUserShowUsername.setInt(1, userID);
            try (ResultSet resultSet = getUserShowUsername.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_SHOWUSERNAME);
            }
            getUserShowUsername.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowUsername(int userID, boolean showUsername) {
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
        boolean result = false;
        try {
            getUserShowColumnVisibility.setInt(1, userID);
            try (ResultSet resultSet = getUserShowColumnVisibility.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_SHOWCOLUMNVISIBILITY);
            }
            getUserShowColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowColumnVisibility(int userID, boolean showColumn) {
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
        float result = -2.0f;
        try {
            getUserShowColumnWidth.setInt(1, userID);
            try (ResultSet resultSet = getUserShowColumnWidth.executeQuery()) {
                if (resultSet.next()) result = resultSet.getFloat(StringDB.COLUMN_SHOWCOLUMNWIDTH);
            }
            getUserShowColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserShowColumnWidth(int userID, float columnWidth) {
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
        boolean result = false;
        try {
            getUserSeasonColumnVisibility.setInt(1, userID);
            try (ResultSet resultSet = getUserSeasonColumnVisibility.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_SEASONCOLUMNVISIBILITY);
            }
            getUserSeasonColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserSeasonColumnVisibility(int userID, boolean showColumn) {
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
        float result = -2.0f;
        try {
            getUserSeasonColumnWidth.setInt(1, userID);
            try (ResultSet resultSet = getUserSeasonColumnWidth.executeQuery()) {
                if (resultSet.next()) result = resultSet.getFloat(StringDB.COLUMN_SEASONCOLUMNWIDTH);
            }
            getUserSeasonColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserSeasonColumnWidth(int userID, float columnWidth) {
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
        boolean result = false;
        try {
            getUserEpisodeColumnVisibility.setInt(1, userID);
            try (ResultSet resultSet = getUserEpisodeColumnVisibility.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_EPISODECOLUMNVISIBILITY);
            }
            getUserEpisodeColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserEpisodeColumnVisibility(int userID, boolean showColumn) {
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
        float result = -2.0f;
        try {
            getUserEpisodeColumnWidth.setInt(1, userID);
            try (ResultSet resultSet = getUserEpisodeColumnWidth.executeQuery()) {
                if (resultSet.next()) result = resultSet.getFloat(StringDB.COLUMN_EPISODECOLUMNWIDTH);
            }
            getUserEpisodeColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserEpisodeColumnWidth(int userID, float columnWidth) {
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
        boolean result = false;
        try {
            getUserRemainingColumnVisibility.setInt(1, userID);
            try (ResultSet resultSet = getUserRemainingColumnVisibility.executeQuery()) {
                if (resultSet.next()) result = resultSet.getBoolean(StringDB.COLUMN_REMAININGCOLUMNVISIBILITY);
            }
            getUserRemainingColumnVisibility.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserRemainingColumnVisibility(int userID, boolean showColumn) {
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
        float result = -2.0f;
        try {
            getUserRemainingColumnWidth.setInt(1, userID);
            try (ResultSet resultSet = getUserRemainingColumnWidth.executeQuery()) {
                if (resultSet.next()) result = resultSet.getFloat(StringDB.COLUMN_REMAININGCOLUMNWIDTH);
            }
            getUserRemainingColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setUserRemainingColumnWidth(int userID, float columnWidth) {
        try {
            setUserRemainingColumnWidth.setFloat(1, columnWidth);
            setUserRemainingColumnWidth.setInt(2, userID);
            setUserRemainingColumnWidth.execute();
            setUserRemainingColumnWidth.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }
}
