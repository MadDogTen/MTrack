package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.StringDB;
import com.maddogten.mtrack.util.Variables;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class DBProgramSettingsManager {
    private final Logger log = Logger.getLogger(DBProgramSettingsManager.class.getName());

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
    }

    private void createProgramSettingsTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.programSettings + "(" + StringDB.programSettingsTableVersion + " INTEGER NOT NULL, " + StringDB.settingsTableVersion + " INTEGER NOT NULL, " + StringDB.showsTableVersion + " INTEGER NOT NULL, " + StringDB.seasonsTableVersion + " INTEGER NOT NULL, " + StringDB.episodesTableVersion + " INTEGER NOT NULL, " + StringDB.episodeFilesTableVersion + " INTEGER NOT NULL, " + StringDB.directoriesTableVersion + " INTEGER NOT NULL, " + StringDB.usersTableVersion + " INTEGER NOT NULL, " + StringDB.defaultUser + " INTEGER NOT NULL " + ")");
    }

    private void addSettingsRow(Statement statement) throws SQLException {
        statement.execute("INSERT INTO " + StringDB.programSettings + " VALUES (" + Variables.programSettingsTableVersion + Variables.seasonsTableVersion + Variables.showsTableVersion + Variables.seasonsTableVersion + Variables.episodesTableVersion + Variables.episodeFilesTableVersion + Variables.directoriesTableVersion + Variables.usersTableVersion + ")");
    }


}
