package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.StringDB;
import com.maddogten.mtrack.util.Variables;

import java.sql.*;
import java.util.logging.Logger;

public class DBProgramSettingsManager {
    private final Logger log = Logger.getLogger(DBProgramSettingsManager.class.getName());

    private final PreparedStatement getDefaultUser;
    private final PreparedStatement changeDefaultUser;

    public DBProgramSettingsManager(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            createProgramSettingsTable(statement);
        }

        getDefaultUser = connection.prepareStatement("SELECT " + StringDB.COLUMN_DEFAULTUSER + " FROM " + StringDB.TABLE_PROGRAMSETTINGS);
        changeDefaultUser = connection.prepareStatement("UPDATE " + StringDB.TABLE_PROGRAMSETTINGS + " SET " + StringDB.COLUMN_DEFAULTUSER + "=?");
    }

    private void createProgramSettingsTable(Statement statement) throws SQLException {
        boolean doesNotExist = true;
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_PROGRAMSETTINGS + "(" +
                    StringDB.COLUMN_PROGRAMSETTINGSTABLEVERSION + " INTEGER NOT NULL, " +
                    StringDB.COLUMN_USERSETTINGSTABLEVERSION + " INTEGER NOT NULL, " +
                    StringDB.COLUMN_SHOWSTABLEVERSION + " INTEGER NOT NULL, " +
                    StringDB.COLUMN_SEASONSTABLEVERSION + " INTEGER NOT NULL, " +
                    StringDB.COLUMN_EPISODESTABLEVERSION + " INTEGER NOT NULL, " +
                    StringDB.COLUMN_EPISODEFILESTABLEVERSION + " INTEGER NOT NULL, " +
                    StringDB.COLUMN_DIRECTORIESTABLEVERSION + " INTEGER NOT NULL, " +
                    StringDB.COLUMN_USERSTABLEVERSION + " INTEGER NOT NULL, " +
                    StringDB.COLUMN_DEFAULTUSER + " INTEGER NOT NULL " + ")");
        } catch (SQLException e) {
            if (GenericMethods.doesTableExistsFromError(e)) doesNotExist = false;
            else GenericMethods.printStackTrace(log, e, this.getClass());
        }
        if (doesNotExist) addSettingsRow(statement);
    }

    private void addSettingsRow(Statement statement) throws SQLException {
        statement.execute("INSERT INTO " + StringDB.TABLE_PROGRAMSETTINGS + " VALUES (" +
                Variables.programSettingsTableVersion + ", " +
                Variables.seasonsTableVersion + ", " +
                Variables.showsTableVersion + ", " +
                Variables.seasonsTableVersion + ", " +
                Variables.episodesTableVersion + ", " +
                Variables.episodeFilesTableVersion + ", " +
                Variables.directoriesTableVersion + ", " +
                Variables.usersTableVersion + ", " +
                -2 + ")");
    }

    public synchronized boolean useDefaultUser() {
        boolean result = false;
        try (ResultSet resultSet = getDefaultUser.executeQuery()) {
            result = resultSet.next() && resultSet.getInt(StringDB.COLUMN_USER_ID) != -2;
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized int getDefaultUser() {
        int result = -2;
        try (ResultSet resultSet = getDefaultUser.executeQuery()) {
            if (resultSet.next()) result = resultSet.getInt(StringDB.COLUMN_DEFAULTUSER);
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setDefaultUser(int userID) {
        try {
            changeDefaultUser.setInt(1, userID);
            changeDefaultUser.execute();
            changeDefaultUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void clearDefaultUser() {
        try {
            changeDefaultUser.setInt(1, -2);
            changeDefaultUser.execute();
            changeDefaultUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }
}
