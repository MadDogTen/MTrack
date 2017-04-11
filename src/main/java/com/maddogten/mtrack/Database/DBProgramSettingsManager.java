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
        ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.programSettings, null);
        if (!resultSet.next()) {
            log.fine("Program Settings table doesn't exist, creating...");
            try (Statement statement = connection.createStatement()) {
                createProgramSettingsTable(statement);
                addSettingsRow(statement);
            }
        }
        resultSet.close();

        getDefaultUser = connection.prepareStatement("SELECT " + StringDB.userID + " FROM " + StringDB.programSettings);
        changeDefaultUser = connection.prepareStatement("CREATE " + StringDB.programSettings + " SET " + StringDB.userID + "=?");
    }

    private void createProgramSettingsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.programSettings + "(" + StringDB.programSettingsTableVersion + " INTEGER NOT NULL, " + StringDB.settingsTableVersion + " INTEGER NOT NULL, " + StringDB.showsTableVersion + " INTEGER NOT NULL, " + StringDB.seasonsTableVersion + " INTEGER NOT NULL, " + StringDB.episodesTableVersion + " INTEGER NOT NULL, " + StringDB.episodeFilesTableVersion + " INTEGER NOT NULL, " + StringDB.directoriesTableVersion + " INTEGER NOT NULL, " + StringDB.usersTableVersion + " INTEGER NOT NULL, " + StringDB.defaultUser + " INTEGER NOT NULL " + ")");
    }

    private void addSettingsRow(Statement statement) throws SQLException {
        statement.execute("INSERT INTO " + StringDB.programSettings + " VALUES (" + Variables.programSettingsTableVersion + Variables.seasonsTableVersion + Variables.showsTableVersion + Variables.seasonsTableVersion + Variables.episodesTableVersion + Variables.episodeFilesTableVersion + Variables.directoriesTableVersion + Variables.usersTableVersion + ")");
    }

    public boolean useDefaultUser() {
        boolean result = false;
        try (ResultSet resultSet = getDefaultUser.executeQuery()) {
            result = resultSet.next() && resultSet.getInt(StringDB.userID) != -2;
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public int getDefaultUser() {
        int result = -2;
        try (ResultSet resultSet = getDefaultUser.executeQuery()) {
            if (resultSet.next()) result = resultSet.getInt(StringDB.userID);
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
