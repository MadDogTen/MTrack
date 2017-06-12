package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.Variables;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class DBUpdateManager {
    private final Logger log = Logger.getLogger(DBUpdateManager.class.getName());

    public synchronized void updateDatabase(DBManager dbManager) throws SQLException {
        log.info("Updating Database...");
        if (updateProgramSettings(dbManager)) {
            updateUsersTable(dbManager);
            updateUserSettingsTable(dbManager);
            updateShowsTable(dbManager);
            updateSeasonsTable(dbManager);
            updateEpisodesTable(dbManager);
            updateEpisodeFilesTable(dbManager);
            updateDirectoriesTable(dbManager);
            updateUserShowSettingsTable(dbManager);
            updateUserEpisodeSettingsTable(dbManager);
            updateShowChangesTable(dbManager);
            updateUserChangeTrackingTable(dbManager);
        }
        log.info("Finished updating Database!");
    }

    private synchronized boolean updateProgramSettings(DBManager dbManager) throws SQLException {
        if (dbManager.createTable(DBStrings.CREATE_PROGRAMSETTINGSTABLE)) {
            dbManager.dropTable(DBStrings.TABLE_PROGRAMSETTINGS);
            return false;
        } else {
            try (Statement statement = dbManager.getStatement()) {
                final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_PROGRAMSETTINGSTABLEVERSION);
                if (oldVersion != -2) {
                    if (oldVersion == Variables.programSettingsTableVersion)
                        log.fine("Program Settings Table already update to date.");
                    else {
                        switch (oldVersion) {
                            case 0:
                                statement.execute("ALTER TABLE " + DBStrings.TABLE_PROGRAMSETTINGS + " ADD COLUMN " + DBStrings.COLUMN_USERSHOWSETTINGSTABLEVERSION + " INT NOT NULL DEFAULT " + 1000);
                                statement.execute("ALTER TABLE " + DBStrings.TABLE_PROGRAMSETTINGS + " ADD COLUMN " + DBStrings.COLUMN_USEREPISODESETTINGSTABLEVERSION + " INT NOT NULL DEFAULT " + 1000);
                                statement.execute("ALTER TABLE " + DBStrings.TABLE_PROGRAMSETTINGS + " ADD COLUMN " + DBStrings.COLUMN_SHOWCHANGESTABLEVERSION + " INT NOT NULL DEFAULT " + 1000);
                                statement.execute("ALTER TABLE " + DBStrings.TABLE_PROGRAMSETTINGS + " ADD COLUMN " + DBStrings.COLUMN_USERCHANGETRACKINGTABLEVERSION + " INT NOT NULL DEFAULT " + 1000);
                                updateTableVersion(statement, DBStrings.COLUMN_PROGRAMSETTINGSTABLEVERSION, oldVersion, Variables.programSettingsTableVersion);
                        }
                    }
                }
            }
            return true;
        }
    }

    private synchronized void updateUsersTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_USERSTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.usersTableVersion) log.fine("Users Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                            updateTableVersion(statement, DBStrings.COLUMN_USERSTABLEVERSION, oldVersion, Variables.usersTableVersion);
                    }
                }
            }
        }
    }

    private synchronized void updateUserSettingsTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_USERSETTINGSTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.userSettingsTableVersion)
                    log.fine("User Settings Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                            updateTableVersion(statement, DBStrings.COLUMN_USERSETTINGSTABLEVERSION, oldVersion, Variables.userSettingsTableVersion);
                    }
                }
            }
        }
    }

    private synchronized void updateShowsTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_SHOWSTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.showsTableVersion) log.fine("Shows Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                            updateTableVersion(statement, DBStrings.COLUMN_SHOWSTABLEVERSION, oldVersion, Variables.showsTableVersion);
                    }
                }
            }
        }
    }

    private synchronized void updateSeasonsTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_SEASONSTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.seasonsTableVersion) log.fine("Seasons Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                            updateTableVersion(statement, DBStrings.COLUMN_SEASONSTABLEVERSION, oldVersion, Variables.seasonsTableVersion);
                    }
                }
            }
        }
    }

    private synchronized void updateEpisodesTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_EPISODESTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.episodesTableVersion) log.fine("Episode Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                            updateTableVersion(statement, DBStrings.COLUMN_EPISODESTABLEVERSION, oldVersion, Variables.episodesTableVersion);
                    }
                }
            }
        }
    }

    private synchronized void updateEpisodeFilesTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_EPISODEFILESTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.episodeFilesTableVersion)
                    log.fine("Episode Files Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                            updateTableVersion(statement, DBStrings.COLUMN_EPISODEFILESTABLEVERSION, oldVersion, Variables.episodeFilesTableVersion);
                    }
                }
            }
        }
    }

    private synchronized void updateDirectoriesTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_DIRECTORIESTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.directoriesTableVersion)
                    log.fine("Directories Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                            updateTableVersion(statement, DBStrings.COLUMN_DIRECTORIESTABLEVERSION, oldVersion, Variables.directoriesTableVersion);
                    }
                }
            }
        }
    }

    private synchronized void updateUserShowSettingsTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_USERSHOWSETTINGSTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.userShowSettingsTableVersion)
                    log.fine("User Show Settings Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                        case 1000:
                            statement.execute("ALTER TABLE " + DBStrings.TABLE_USERSHOWSETTINGS + " ADD COLUMN " + DBStrings.COLUMN_USERALTERED + " BOOLEAN NOT NULL DEFAULT FALSE");
                            for (Integer userID : ClassHandler.userInfoController().getAllUsers()) {
                                for (Integer showID : ClassHandler.userInfoController().getUsersShows(userID)) {
                                    int defaultSeason = ClassHandler.userInfoController().getShowDefaultSeason(showID);
                                    boolean userAltered =
                                            ClassHandler.userInfoController().getCurrentUserSeason(userID, showID) != defaultSeason ||
                                                    ClassHandler.userInfoController().getCurrentUserEpisode(userID, showID) != ClassHandler.userInfoController().getShowDefaultEpisode(showID, defaultSeason) ||
                                                    ClassHandler.userInfoController().isShowActive(userID, showID) ||
                                                    ClassHandler.userInfoController().isShowHidden(userID, showID);
                                    statement.execute("UPDATE " + DBStrings.TABLE_USERSHOWSETTINGS + " SET " + DBStrings.COLUMN_USERALTERED + "=" + userAltered + " WHERE " + DBStrings.COLUMN_USER_ID + "=" + userID + " AND " + DBStrings.COLUMN_SHOW_ID + "=" + showID);
                                }
                            }
                            updateTableVersion(statement, DBStrings.COLUMN_USERSHOWSETTINGSTABLEVERSION, oldVersion, Variables.userShowSettingsTableVersion);
                    }
                }
            }
        }
    }

    private synchronized void updateUserEpisodeSettingsTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_USEREPISODESETTINGSTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.userEpisodeSettingsTableVersion)
                    log.fine("User Episode Settings Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                            updateTableVersion(statement, DBStrings.COLUMN_USEREPISODESETTINGSTABLEVERSION, oldVersion, Variables.userEpisodeSettingsTableVersion);
                    }
                }
            }
        }
    }

    private synchronized void updateShowChangesTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_SHOWCHANGESTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.showChangesTableVersion)
                    log.fine("Show Changes Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                            updateTableVersion(statement, DBStrings.COLUMN_SHOWCHANGESTABLEVERSION, oldVersion, Variables.showChangesTableVersion);
                    }
                }
            }
        }
    }

    private synchronized void updateUserChangeTrackingTable(DBManager dbManager) throws SQLException {
        try (Statement statement = dbManager.getStatement()) {
            final int oldVersion = getTableVersion(statement, DBStrings.COLUMN_USERCHANGETRACKINGTABLEVERSION);
            if (oldVersion != -2) {
                if (oldVersion == Variables.userChangeTrackingTableVersion)
                    log.fine("Users Change Tracking Table already update to date.");
                else {
                    switch (oldVersion) {
                        case 0:
                            updateTableVersion(statement, DBStrings.COLUMN_USERCHANGETRACKINGTABLEVERSION, oldVersion, Variables.userChangeTrackingTableVersion);
                    }
                }
            }
        }
    }

    private void updateTableVersion(Statement statement, String tableToUpdate, int oldVersion, int newVersion) throws SQLException {
        statement.execute("UPDATE " + DBStrings.TABLE_PROGRAMSETTINGS + " SET " + tableToUpdate + "=" + newVersion);
        log.info(tableToUpdate + " was updated from " + oldVersion + " -> " + newVersion);
    }

    private int getTableVersion(Statement statement, String tableToGet) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery("SELECT " + tableToGet + " FROM " + DBStrings.TABLE_PROGRAMSETTINGS)) {
            if (resultSet.next()) return resultSet.getInt(tableToGet);
        }
        log.warning("Unable to get version info for: \"" + tableToGet + "\".");
        return -2;
    }

    private Set<Integer> getUsers(Statement statement) throws SQLException {
        Set<Integer> users = new HashSet<>();
        try (ResultSet resultSet = statement.executeQuery("SELECT " + DBStrings.COLUMN_USER_ID + " FROM " + DBStrings.TABLE_USERS)) {
            while (resultSet.next()) users.add(resultSet.getInt(DBStrings.COLUMN_USER_ID));
        }
        return users;
    }
}
