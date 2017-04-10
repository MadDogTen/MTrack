package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.StringDB;
import com.maddogten.mtrack.util.Variables;

import java.sql.*;
import java.util.logging.Logger;

public class DBUserSettingsManager {
    private final Logger log = Logger.getLogger(DBUserSettingsManager.class.getName());
    private final PreparedStatement insertSettings;

    public DBUserSettingsManager(Connection connection) throws SQLException {
        initTables(connection);

        insertSettings = connection.prepareStatement("INSERT INTO " + StringDB.settings + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        addUserSettings(0); // Insert default program settings
    }

    private void initTables(Connection connection) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.settings, null)) {
            if (!resultSet.next()) {
                log.fine("Settings table doesn't exist, creating...");
                try (Statement statement = connection.createStatement()) {
                    createSettingsTable(statement);
                }
            }
        }
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.userShowSettings, null)) {
            if (!resultSet.next()) {
                log.fine("User show settings table doesn't exist, creating...");
                try (Statement statement = connection.createStatement()) {
                    createShowSettingsTable(statement);
                }
            }
        }
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.userEpisodeSettings, null)) {
            if (!resultSet.next()) {
                log.fine("User episode settings table doesn't exist, creating...");
                try (Statement statement = connection.createStatement()) {
                    createEpisodeSettingsTable(statement);
                }
            }
        }
    }

    private void createEpisodeSettingsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.userEpisodeSettings + "(" + StringDB.userID + " INTEGER NOT NULL, " + StringDB.episodeID + " INTEGER NOT NULL, " + StringDB.episodeTimePosition + " INTEGER NOT NULL)");
    }

    private void createShowSettingsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.userShowSettings + "(" + StringDB.userID + " INTEGER NOT NULL, " + StringDB.showID + " INTEGER NOT NULL, " + StringDB.currentSeason + " INTEGER NOT NULL, " + StringDB.currentEpisode + " INTEGER NOT NULL, " + StringDB.showStatus + " VARCHAR(8) NOT NULL)");
    }

    private void createSettingsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.settings + "(" + StringDB.userID + " INTEGER UNIQUE NOT NULL, " + StringDB.updateSpeed + " INTEGER NOT NULL," +
                StringDB.automaticShowUpdating + " BOOLEAN NOT NULL, " + StringDB.timeToWaitForDirectory + " INTEGER NOT NULL, " +
                StringDB.show0Remaining + " BOOLEAN NOT NULL, " + StringDB.showActiveShows + " BOOLEAN NOT NULL, " + StringDB.language + " VARCHAR(20) NOT NULL, " +
                StringDB.recordChangesForNonActiveShows + " BOOLEAN NOT NULL, " + StringDB.recordChangedSeasonsLowerThanCurrent + " BOOLEAN NOT NULL, " +
                StringDB.moveStageWithParent + " BOOLEAN NOT NULL, " + StringDB.haveStageBlockParentStage + " BOOLEAN NOT NULL, " + StringDB.enableSpecialEffects + " BOOLEAN NOT NULL, " +
                StringDB.enableAutomaticSaving + " BOOLEAN NOT NULL, " + StringDB.saveSpeed + " INTEGER NOT NULL, " + StringDB.enableFileLogging + " BOOLEAN NOT NULL, " +
                StringDB.showColumnWidth + " FLOAT NOT NULL, " + StringDB.remainingColumnWidth + " FLOAT NOT NULL, " +
                StringDB.seasonColumnWidth + " FLOAT NOT NULL, " + StringDB.episodeColumnWidth + " FLOAT NOT NULL, " + StringDB.showColumnVisibility + " BOOLEAN NOT NULL, " +
                StringDB.remainingColumnVisibility + " BOOLEAN NOT NULL, " + StringDB.seasonColumnVisibility + " BOOLEAN NOT NULL, " + StringDB.episodeColumnVisibility + " BOOLEAN NOT NULL)");
    }

    public void addUserSettings(int userID, int updateSpeed, boolean automaticShowUpdating, int timeToWaitForDirectory, boolean show0Remaining, boolean showActiveShows, String language,
                                boolean recordChangesForNonActiveShows, boolean recordChangedSeasonsLowerThanCurrent, boolean moveStageWithParent, boolean haveStageBlockParentStage,
                                boolean enableSpecialEffects, boolean enableAutomaticSaving, int saveSpeed, boolean enableFileLogging, float showColumnWidth,
                                float remainingColumnWidth, float seasonColumnWidth, float episodeColumnWidth, boolean showColumnVisibility, boolean remainingColumnVisibility,
                                boolean seasonColumnVisibility, boolean episodeColumnVisibility) {
        try {
            insertSettings.setInt(1, userID);
            insertSettings.setInt(2, updateSpeed);
            insertSettings.setBoolean(3, automaticShowUpdating);
            insertSettings.setInt(4, timeToWaitForDirectory);
            insertSettings.setBoolean(5, show0Remaining);
            insertSettings.setBoolean(6, showActiveShows);
            insertSettings.setString(7, language);
            insertSettings.setBoolean(8, recordChangesForNonActiveShows);
            insertSettings.setBoolean(9, recordChangedSeasonsLowerThanCurrent);
            insertSettings.setBoolean(10, moveStageWithParent);
            insertSettings.setBoolean(11, haveStageBlockParentStage);
            insertSettings.setBoolean(12, enableSpecialEffects);
            insertSettings.setBoolean(13, enableAutomaticSaving);
            insertSettings.setInt(14, saveSpeed);
            insertSettings.setBoolean(15, enableFileLogging);
            insertSettings.setFloat(16, showColumnWidth);
            insertSettings.setFloat(17, remainingColumnWidth);
            insertSettings.setFloat(18, seasonColumnWidth);
            insertSettings.setFloat(19, episodeColumnWidth);
            insertSettings.setBoolean(20, showColumnVisibility);
            insertSettings.setBoolean(21, remainingColumnVisibility);
            insertSettings.setBoolean(22, seasonColumnVisibility);
            insertSettings.setBoolean(23, episodeColumnVisibility);
            insertSettings.execute();
            insertSettings.clearParameters();
            log.info("Settings were successfully added with ID \"" + userID + "\".");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public void addUserSettings(int userID) {
        this.addUserSettings(userID, Variables.defaultUpdateSpeed, true, Variables.defaultTimeToWaitForDirectory, false, false, "None", false, false, true, true, true, true, 600, true,
                Variables.SHOWS_COLUMN_WIDTH, Variables.REMAINING_COLUMN_WIDTH, Variables.SEASONS_COLUMN_WIDTH, Variables.EPISODE_COLUMN_WIDTH, true, true, false, false);
    }

    public int getIntegerSetting(int userID, String settingType) {
        int setting = -2;
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + settingType + " FROM " + StringDB.settings + " WHERE " + StringDB.userID + "=" + userID)) {
            if (resultSet.next()) setting = resultSet.getInt(settingType);
            else log.warning("Unable to load \"" + settingType + "\" for user \"" + userID + "\", using default.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return setting;
    }

    public void changeIntegerSetting(int userID, int newSetting, String settingType) {
        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            int oldSetting = getIntegerSetting(userID, settingType);
            statement.execute("UPDATE " + StringDB.settings + " SET " + settingType + "=" + newSetting + " WHERE " + StringDB.userID + "=" + userID);
            if (getIntegerSetting(userID, settingType) == newSetting)
                log.info(settingType + " for UserID \"" + userID + "\" was changed to \"" + newSetting + "\" from \"" + oldSetting + "\".");
            else log.info(settingType + " for UserID \"" + userID + "\" was unable to be changed.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public float getFloatSetting(int userID, String settingType) {
        float setting = -2;
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + settingType + " FROM " + StringDB.settings + " WHERE " + StringDB.userID + "=" + userID)) {
            if (resultSet.next()) setting = resultSet.getFloat(settingType);
            else log.warning("Unable to load \"" + settingType + "\" for user \"" + userID + "\", using default.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return setting;
    }

    public void changeFloatSetting(int userID, float newSetting, String settingType) {
        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            float oldSetting = getFloatSetting(userID, settingType);
            statement.execute("UPDATE " + StringDB.settings + " SET " + settingType + "=" + newSetting + " WHERE " + StringDB.userID + "=" + userID);
            if (getFloatSetting(userID, settingType) == newSetting)
                log.info(settingType + " for UserID \"" + userID + "\" was changed to \"" + newSetting + "\" from \"" + oldSetting + "\".");
            else log.info(settingType + " for UserID \"" + userID + "\" was unable to be changed.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public Boolean getBooleanSetting(int userID, String settingType) {
        Boolean setting = null;
        try (Statement statement = ClassHandler.getDBManager().getStatement();
             ResultSet resultSet = statement.executeQuery("SELECT " + settingType + " FROM " + StringDB.settings + " WHERE " + StringDB.userID + "=" + userID)) {
            if (resultSet.next()) setting = resultSet.getBoolean(settingType);
            else log.warning("Unable to load \"" + settingType + "\" for user \"" + userID + "\", using default.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return setting;
    }

    public void changeBooleanSetting(int userID, boolean newSetting, String settingType) {
        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            boolean oldSetting = getBooleanSetting(userID, settingType);
            statement.execute("UPDATE " + StringDB.settings + " SET " + settingType + "=" + newSetting + " WHERE " + StringDB.userID + "=" + userID);
            if (getBooleanSetting(userID, settingType) == newSetting)
                log.info(settingType + " for UserID \"" + userID + "\" was changed to \"" + newSetting + "\" from \"" + oldSetting + "\".");
            else log.info(settingType + " for UserID \"" + userID + "\" was unable to be changed.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }
}
