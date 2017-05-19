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

    public synchronized int getIntegerSetting(int userID, int showID, String settingType, String table) {
        int setting = -2;
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + settingType + " FROM " + table + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID + ((showID != -2) ? (" AND " + StringDB.COLUMN_SHOW_ID + "=" + showID) : ""))) {
            if (resultSet.next()) setting = resultSet.getInt(settingType);
            else log.warning("Unable to load \"" + settingType + "\" for user \"" + userID + "\".");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return setting;
    }

    public synchronized void changeIntegerSetting(int userID, int showID, int newSetting, String settingType, String table) {
        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            int oldSetting = getIntegerSetting(userID, showID, settingType, table);
            statement.execute("UPDATE " + table + " SET " + settingType + "=" + newSetting + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID + ((showID != -2) ? (" AND " + StringDB.COLUMN_SHOW_ID + "=" + showID) : ""));
            if (getIntegerSetting(userID, showID, settingType, table) == newSetting)
                log.info(settingType + " for UserID \"" + userID + "\" was changed to \"" + newSetting + "\" from \"" + oldSetting + "\".");
            else log.info(settingType + " for UserID \"" + userID + "\" was unable to be changed.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized String getStringSetting(int userID, int showID, String settingType, String table) {
        String setting = Strings.EmptyString;
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + settingType + " FROM " + table + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID + ((showID != -2) ? (" AND " + StringDB.COLUMN_SHOW_ID + "=" + showID) : ""))) {
            if (resultSet.next()) setting = resultSet.getString(settingType);
            else log.warning("Unable to load \"" + settingType + "\" for user \"" + userID + "\".");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return setting;
    }

    public synchronized void changeStringSetting(int userID, int showID, String newSetting, String settingType, String table) {
        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            String oldSetting = getStringSetting(userID, showID, settingType, table);
            statement.execute("UPDATE " + table + " SET " + settingType + "='" + newSetting + "' WHERE " + StringDB.COLUMN_USER_ID + "=" + userID + ((showID != -2) ? (" AND " + StringDB.COLUMN_SHOW_ID + "=" + showID) : ""));
            if (oldSetting.matches(newSetting))
                log.info(settingType + " for UserID \"" + userID + "\" was changed to \"" + newSetting + "\" from \"" + oldSetting + "\".");
            else log.info(settingType + " for UserID \"" + userID + "\" was unable to be changed.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
            System.exit(0);
        }
    }

    public synchronized float getFloatSetting(int userID, String settingType, String table) {
        float setting = -2;
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + settingType + " FROM " + table + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID)) {
            if (resultSet.next()) setting = resultSet.getFloat(settingType);
            else log.warning("Unable to load \"" + settingType + "\" for user \"" + userID + "\".");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return setting;
    }

    public synchronized void changeFloatSetting(int userID, float newSetting, String settingType, String table) {
        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            float oldSetting = getFloatSetting(userID, settingType, table);
            statement.execute("UPDATE " + table + " SET " + settingType + "=" + newSetting + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID);
            if (getFloatSetting(userID, settingType, table) == newSetting)
                log.info(settingType + " for UserID \"" + userID + "\" was changed to \"" + newSetting + "\" from \"" + oldSetting + "\".");
            else log.info(settingType + " for UserID \"" + userID + "\" was unable to be changed.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean getBooleanSetting(int userID, int showID, String settingType, String table) {
        Boolean setting = false;
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + settingType + " FROM " + table + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID + ((showID != -2) ? (" AND " + StringDB.COLUMN_SHOW_ID + "=" + showID) : ""))) {
            if (resultSet.next()) setting = resultSet.getBoolean(settingType);
            else log.warning("Unable to load \"" + settingType + "\" for user \"" + userID + "\".");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return setting;
    }

    public synchronized void changeBooleanSetting(int userID, int showID, boolean newSetting, String settingType, String table) {
        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            boolean oldSetting = getBooleanSetting(userID, showID, settingType, table);
            statement.execute("UPDATE " + table + " SET " + settingType + "=" + newSetting + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID + ((showID != -2) ? (" AND " + StringDB.COLUMN_SHOW_ID + "=" + showID) : ""));
            if (getBooleanSetting(userID, showID, settingType, table) == newSetting)
                log.info(settingType + " for UserID \"" + userID + "\" was changed to \"" + newSetting + "\" from \"" + oldSetting + "\".");
            else log.info(settingType + " for UserID \"" + userID + "\" was unable to be changed.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized Set<Integer> getShows(int userID) {
        Set<Integer> result = new HashSet<>();
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID)) {
            while (resultSet.next()) {
                int showID = resultSet.getInt(StringDB.COLUMN_SHOW_ID);
                if (ClassHandler.showInfoController().doesShowExist(showID)) result.add(showID);
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized Set<Integer> getShows(int userID, String settingType, boolean setting) {
        Set<Integer> result = new HashSet<>();
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + StringDB.COLUMN_SHOW_ID + " FROM " + StringDB.TABLE_USERSHOWSETTINGS + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID + " AND " + settingType + "=" + setting)) {
            while (resultSet.next()) result.add(resultSet.getInt(StringDB.COLUMN_SHOW_ID));
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized Set<Integer> getShows(int userID, boolean active, boolean ignored, boolean hidden) {
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
}
