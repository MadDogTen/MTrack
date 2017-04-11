package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.StringDB;
import com.maddogten.mtrack.util.Strings;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

public class DBUserManager {
    private final Logger log = Logger.getLogger(DBUserManager.class.getName());

    private final PreparedStatement insertUser;
    private final PreparedStatement changeUsername;
    private final PreparedStatement getShowUsername;
    private final PreparedStatement checkUserID;
    private final PreparedStatement getUserID;
    private final PreparedStatement getUsername;
    private final PreparedStatement checkForUser;

    public DBUserManager(Connection connection) throws SQLException {
        ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.users, null);
        if (!resultSet.next()) {
            log.fine("User table doesn't exist, creating...");
            try (Statement statement = connection.createStatement()) {
                createUserTables(statement);
            }
        }
        resultSet.close();

        insertUser = connection.prepareStatement("INSERT INTO " + StringDB.users + " VALUES (?, ?, ?)");
        changeUsername = connection.prepareStatement("UPDATE " + StringDB.users + " SET " + StringDB.username + "=? WHERE " + StringDB.username + " =?");
        getShowUsername = connection.prepareStatement("SELECT " + StringDB.showUsername + " FROM " + StringDB.users + " WHERE " + StringDB.username + "=?");
        checkUserID = connection.prepareStatement("SELECT " + StringDB.username + " FROM " + StringDB.users + " WHERE " + StringDB.userID + "=?");
        getUserID = connection.prepareStatement("SELECT " + StringDB.userID + " FROM " + StringDB.users + " WHERE " + StringDB.username + " =?");
        getUsername = connection.prepareStatement("SELECT " + StringDB.username + " FROM " + StringDB.users + " WHERE " + StringDB.userID + "=?");
        checkForUser = connection.prepareStatement("SELECT * FROM " + StringDB.users + " WHERE " + StringDB.username + "=?");
    }

    public boolean addUser(String username) {
        return this.addUser(username, true);
    }

    public boolean addUser(String userName, boolean showUsername) {
        boolean result = false;
        try {
            insertUser.setInt(1, generateUserID());
            insertUser.setString(2, userName);
            insertUser.setBoolean(3, showUsername);
            insertUser.executeUpdate();
            result = true; // TODO Generate other tables information
            log.info("User \"" + userName + "\" was successfully added with ID \"" + getUserID(userName) + "\".");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public boolean doesUserExist(String userName) {
        boolean result = false;
        try {
            checkForUser.setString(1, userName);
            try (ResultSet resultSet = checkForUser.executeQuery()) {
                result = resultSet.next();
            }
            checkForUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public void deleteUser(String userName) {
        try {
            int userID = getUserID(userName);
            if (userID != -2) {
                try (Statement statement = ClassHandler.getDBManager().getStatement()) { // TODO Make sure it deletes all associated information.
                    statement.execute("DELETE FROM " + StringDB.users + " WHERE " + StringDB.userID + " = " + userID);
                    //statement.executeQuery("DELETE FROM " + StringDB.settings + " WHERE " + StringDB.userID + " = " + userID);

                    log.info("User \"" + userName + "\" was successfully deleted.");
                }
            } else log.warning("Invalid userID, User wasn't deleted.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public void changeUsername(String oldUsername, String newUsername) {
        try {
            changeUsername.setString(1, newUsername);
            changeUsername.setString(2, oldUsername);
            changeUsername.executeUpdate();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        log.info("Username for \"" + oldUsername + "\" was successfully changed to \"" + newUsername + "\".");
    }

    public ArrayList<Integer> getAllUsers() {
        ArrayList<Integer> users = new ArrayList<>();
        try (Statement statement = ClassHandler.getDBManager().executeQuery("SELECT " + StringDB.userID + " FROM " + StringDB.users);
             ResultSet resultSet = statement.getResultSet()) {
            while (resultSet.next()) {
                users.add(resultSet.getInt(StringDB.userID));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return users;
    }

    public ArrayList<String> getAllUserStrings() { // TODO Remove, Temp Value
        ArrayList<String> allUsers = new ArrayList<>();
        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT " + StringDB.username + " FROM " + StringDB.users)) {
                while (resultSet.next()) allUsers.add(resultSet.getString(StringDB.username));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return allUsers;
    }

    private int generateUserID() throws SQLException {
        Random random = new Random();
        int userID;
        ResultSet resultSet;
        do {
            userID = random.nextInt(Integer.MAX_VALUE - 100000000) + 100000000;
            checkUserID.setInt(1, userID);
            resultSet = checkUserID.executeQuery();
        } while (resultSet.next());
        resultSet.close();
        checkUserID.clearParameters();
        return userID;
    }

    public int getUserID(String username) throws SQLException {
        int result = -2;
        getUserID.setString(1, username);
        try (ResultSet resultSet = getUserID.executeQuery()) {
            if (resultSet.next()) {
                result = resultSet.getInt(StringDB.userID);
                if (resultSet.next()) {
                    log.warning("Duplicate found for \"" + username + "\", canceling deletion.");
                    result = -2;
                }
            } else log.warning("Couldn't find UserID for \"" + username + "\".");
        }
        getUserID.clearParameters();
        return result;
    }

    public boolean getShowUsername(String user) {
        boolean result = true;
        try {
            getShowUsername.setString(1, user);
            ResultSet resultSet = getShowUsername.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getBoolean(StringDB.showUsername);
                resultSet.close();
                getShowUsername.clearParameters();
            } else log.warning("Couldn't find '" + StringDB.showUsername + "' Field under \"" + user + "\".");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    private void createUserTables(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.users + "(" + StringDB.userID + " INTEGER UNIQUE NOT NULL, " + StringDB.username + " VARCHAR(20) NOT NULL," + StringDB.showUsername + " BOOLEAN NOT NULL )");
        log.info("User database tables have been created.");
    }

    public String getUsername(int userID) {
        String result = Strings.EmptyString;
        try {
            getUsername.setInt(1, userID);
            try (ResultSet resultSet = getUsername.executeQuery()) {
                if (resultSet.next()) result = resultSet.getString(StringDB.username);
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }
}
