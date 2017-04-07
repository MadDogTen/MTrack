package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.DatabaseStrings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseUserManager {
    private final Logger log = Logger.getLogger(DatabaseUserManager.class.getName());

    private final PreparedStatement insetUser;
    private final PreparedStatement changeUsername;
    private final PreparedStatement getShowUsername;

    public DatabaseUserManager() throws SQLException {
        Connection connection = ClassHandler.getDatabaseManager().getDatabaseConnection();

        ResultSet resultSet = connection.getMetaData().getTables(null, null, "USERS", null);
        if (!resultSet.next()) {
            log.fine("User tables don't exist, creating...");
            createUserTables();
        }
        resultSet.close();

        insetUser = connection.prepareStatement("INSERT INTO " + DatabaseStrings.UsersTable + " VALUES (?, ?)");
        changeUsername = connection.prepareStatement("UPDATE " + DatabaseStrings.UsersTable + " SET USERNAME=? WHERE USERNAME=?");
        getShowUsername = connection.prepareStatement("SELECT " + DatabaseStrings.ShowUsernameField + " FROM " + DatabaseStrings.UsersTable + " WHERE " + DatabaseStrings.UsernameField + "=?");
    }

    public void addUser(String username) throws SQLException {
        insetUser.setString(1, username);
        insetUser.setBoolean(2, true);
        insetUser.executeUpdate();
    }

    public void addUser(String userName, boolean showUsername) throws SQLException {
        insetUser.setString(1, userName);
        insetUser.setBoolean(2, showUsername);
        insetUser.executeUpdate();
    }

    public void changeUsername(String oldUsername, String newUsername) throws SQLException {
        changeUsername.setString(1, newUsername);
        changeUsername.setString(2, oldUsername);
        changeUsername.executeUpdate();
    }

    public ResultSet getAllUsers() throws SQLException {
        return ClassHandler.getDatabaseManager().executeQuery("SELECT " + DatabaseStrings.UsernameField + " FROM USERS");
    }

    public boolean getShowUsername(String user) throws SQLException {
        getShowUsername.setString(1, user);
        ResultSet resultSet = getShowUsername.executeQuery();
        if (resultSet.next()) {
            boolean result = resultSet.getBoolean(DatabaseStrings.ShowUsernameField);
            resultSet.close();
            return result;
        } else {
            log.warning("Couldn't find '" + DatabaseStrings.ShowUsernameField + "' Field under \"" + user + "\".");
            return true;
        }
    }

    private void createUserTables() throws SQLException {
        ClassHandler.getDatabaseManager().execute("CREATE TABLE " + DatabaseStrings.UsersTable + "(" + DatabaseStrings.UsernameField + " VARCHAR(20)," + DatabaseStrings.ShowUsernameField + " BOOLEAN DEFAULT FALSE)");
        log.info("User database tables have been created.");
    }
}
