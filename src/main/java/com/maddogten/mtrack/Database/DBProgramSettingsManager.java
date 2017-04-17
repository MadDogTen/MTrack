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
        ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.TABLE_PROGRAMSETTINGS, null);
        if (!resultSet.next()) {
            log.fine("Program Settings table doesn't exist, creating...");
            try (Statement statement = connection.createStatement()) {
                createProgramSettingsTable(statement);
                addSettingsRow(statement);
            }
        }
        resultSet.close();

        getDefaultUser = connection.prepareStatement("SELECT " + StringDB.COLUMN_USER_ID + " FROM " + StringDB.TABLE_PROGRAMSETTINGS);
        changeDefaultUser = connection.prepareStatement("UPDATE " + StringDB.TABLE_PROGRAMSETTINGS + " SET " + StringDB.COLUMN_USER_ID + "=?");
    }

    private void createProgramSettingsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.TABLE_PROGRAMSETTINGS + "(" + StringDB.COLUMN_PROGRAMSETTINGSTABLEVERSION + " INTEGER NOT NULL, " + StringDB.COLUMN_SETTINGSTABLEVERSION + " INTEGER NOT NULL, " + StringDB.COLUMN_SHOWSTABLEVERSION + " INTEGER NOT NULL, " + StringDB.COLUMN_SEASONSTABLEVERSION + " INTEGER NOT NULL, " + StringDB.COLUMN_EPISODESTABLEVERSION + " INTEGER NOT NULL, " + StringDB.COLUMN_EPISODEFILESTABLEVERSION + " INTEGER NOT NULL, " + StringDB.COLUMN_DIRECTORIESTABLEVERSION + " INTEGER NOT NULL, " + StringDB.COLUMN_USERSTABLEVERSION + " INTEGER NOT NULL, " + StringDB.COLUMN_DEFAULTUSER + " INTEGER NOT NULL " + ")");
    }

    private void addSettingsRow(Statement statement) throws SQLException {
        statement.execute("INSERT INTO " + StringDB.TABLE_PROGRAMSETTINGS + " VALUES (" + Variables.programSettingsTableVersion + Variables.seasonsTableVersion + Variables.showsTableVersion + Variables.seasonsTableVersion + Variables.episodesTableVersion + Variables.episodeFilesTableVersion + Variables.directoriesTableVersion + Variables.usersTableVersion + ")");
    }

    public boolean useDefaultUser() {
        boolean result = false;
        try (ResultSet resultSet = getDefaultUser.executeQuery()) {
            result = resultSet.next() && resultSet.getInt(StringDB.COLUMN_USER_ID) != -2;
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public int getDefaultUser() {
        int result = -2;
        try (ResultSet resultSet = getDefaultUser.executeQuery()) {
            if (resultSet.next()) result = resultSet.getInt(StringDB.COLUMN_USER_ID);
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public void setDefaultUser(int userID) {
        try {
            changeDefaultUser.setInt(1, userID);
            changeDefaultUser.execute();
            changeDefaultUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public void clearDefaultUser() {
        try {
            changeDefaultUser.setInt(1, -2);
            changeDefaultUser.execute();
            changeDefaultUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }
}
