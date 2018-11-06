package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Variables;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DBProgramSettingsManager {
    private final Logger log = Logger.getLogger(DBProgramSettingsManager.class.getName());

    private final DBManager dbManager;

    private PreparedStatement getDefaultUser;
    private PreparedStatement changeDefaultUser;

    public DBProgramSettingsManager(DBManager dbManager) throws SQLException {
        this.dbManager = dbManager;
        try (Statement statement = this.dbManager.getStatement()) {
            if (this.dbManager.createTable(DBStrings.CREATE_PROGRAMSETTINGSTABLE)) addSettingsRow(statement);
        }
    }

    private void addSettingsRow(Statement statement) throws SQLException { // TODO Set versions to -2 by default, and have each table update it as they are created.
        statement.execute("INSERT INTO " + DBStrings.TABLE_PROGRAMSETTINGS + " VALUES (" +
                Variables.programSettingsTableVersion + ", " +
                Variables.userSettingsTableVersion + ", " +
                Variables.userShowSettingsTableVersion + ", " +
                Variables.userEpisodeSettingsTableVersion + ", " +
                Variables.showsTableVersion + ", " +
                Variables.seasonsTableVersion + ", " +
                Variables.episodesTableVersion + ", " +
                Variables.episodeFilesTableVersion + ", " +
                Variables.directoriesTableVersion + ", " +
                Variables.usersTableVersion + ", " +
                Variables.showChangesTableVersion + ", " +
                Variables.userChangeTrackingTableVersion + ", " +
                -2 + ")");
    }

    public synchronized boolean useDefaultUser() {
        return getDefaultUser() != -2;
    }

    public synchronized int getDefaultUser() {
        if (isNull(getDefaultUser))
            getDefaultUser = dbManager.prepareStatement(DBStrings.DBProgramSettingsManager_getDefaultUserSQL);
        int result = -2;
        try (ResultSet resultSet = getDefaultUser.executeQuery()) {
            if (resultSet.next()) result = resultSet.getInt(DBStrings.COLUMN_DEFAULTUSER);
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void setDefaultUser(int userID) {
        if (isNull(changeDefaultUser))
            changeDefaultUser = dbManager.prepareStatement(DBStrings.DBProgramSettingsManager_changeDefaultUserSQL);
        try {
            changeDefaultUser.setInt(1, userID);
            changeDefaultUser.execute();
            changeDefaultUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void clearDefaultUser() {
        if (isNull(changeDefaultUser))
            changeDefaultUser = dbManager.prepareStatement(DBStrings.DBProgramSettingsManager_changeDefaultUserSQL);
        try {
            changeDefaultUser.setInt(1, -2);
            changeDefaultUser.execute();
            changeDefaultUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    private boolean isNull(Object object) {
        return object == null;
    }
}
