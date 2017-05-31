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
    private final PreparedStatement getAllUsers;

    public DBUserManager(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            createUserTables(statement);
        }

        insertUser = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_USERS + " VALUES (?, ?, ?)");
        changeUsername = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERS + " SET " + StringDB.COLUMN_USERNAME + "=? WHERE " + StringDB.COLUMN_USERNAME + " =?");
        getShowUsername = connection.prepareStatement("SELECT " + StringDB.COLUMN_SHOWUSERNAME + " FROM " + StringDB.TABLE_USERS + " WHERE " + StringDB.COLUMN_USERNAME + "=?");
        checkUserID = connection.prepareStatement("SELECT " + StringDB.COLUMN_USERNAME + " FROM " + StringDB.TABLE_USERS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getUserID = connection.prepareStatement("SELECT " + StringDB.COLUMN_USER_ID + " FROM " + StringDB.TABLE_USERS + " WHERE " + StringDB.COLUMN_USERNAME + " =?");
        getUsername = connection.prepareStatement("SELECT " + StringDB.COLUMN_USERNAME + " FROM " + StringDB.TABLE_USERS + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        getAllUsers = connection.prepareStatement("SELECT " + StringDB.COLUMN_USER_ID + " FROM " + StringDB.TABLE_USERS);
    }

    public synchronized int addUser(String username) {
        return this.addUser(username, true);
    }

    public synchronized int addUser(String userName, boolean showUsername) {
        int userID = doesUserExist(userName);
        if (userID != -2) {
            // Set show username here
            return userID;
        } else {
            try {
                insertUser.setInt(1, generateUserID());
                insertUser.setString(2, userName);
                insertUser.setBoolean(3, showUsername);
                insertUser.executeUpdate();
                userID = getUserID(userName);
                log.info("User \"" + userName + "\" was successfully added with ID \"" + userID + "\".");
            } catch (SQLException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
        return userID;
    }

    public synchronized int doesUserExist(String userName) {
        int result = -2;
        try {
            getUserID.setString(1, userName);
            try (ResultSet resultSet = getUserID.executeQuery()) {
                if (resultSet.next()) result = resultSet.getInt(StringDB.COLUMN_USER_ID);
            }
            getUserID.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized void deleteUser(String userName) { // TODO Finish
        try {
            int userID = getUserID(userName);
            if (userID != -2) {
                try (Statement statement = ClassHandler.getDBManager().getStatement()) { // TODO Make sure it deletes all associated information.
                    statement.execute("DELETE FROM " + StringDB.TABLE_USERS + " WHERE " + StringDB.COLUMN_USER_ID + "=" + userID);
                    //statement.executeQuery("DELETE FROM " + StringDB.settings + " WHERE " + StringDB.userID + " = " + userID);

                    log.info("User \"" + userName + "\" was successfully deleted.");
                }
            } else log.warning("Invalid userID, User wasn't deleted.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void changeUsername(String oldUsername, String newUsername) {
        try {
            changeUsername.setString(1, newUsername);
            changeUsername.setString(2, oldUsername);
            changeUsername.executeUpdate();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        log.info("Username for \"" + oldUsername + "\" was successfully changed to \"" + newUsername + "\".");
    }

    public synchronized ArrayList<Integer> getAllUsers() {
        ArrayList<Integer> users = new ArrayList<>();
        try (ResultSet resultSet = getAllUsers.executeQuery()) {
            while (resultSet.next()) {
                users.add(resultSet.getInt(StringDB.COLUMN_USER_ID));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return users;
    }

    private synchronized int generateUserID() throws SQLException {
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

    public synchronized int getUserID(String username) throws SQLException {
        int result = -2;
        getUserID.setString(1, username);
        try (ResultSet resultSet = getUserID.executeQuery()) {
            if (resultSet.next()) {
                result = resultSet.getInt(StringDB.COLUMN_USER_ID);
            } else log.warning("Couldn't find UserID for \"" + username + "\".");
        }
        getUserID.clearParameters();
        return result;
    }

    public synchronized boolean getShowUsername(String user) {
        boolean result = true;
        try {
            getShowUsername.setString(1, user);
            ResultSet resultSet = getShowUsername.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getBoolean(StringDB.COLUMN_SHOWUSERNAME);
                resultSet.close();
                getShowUsername.clearParameters();
            } else log.warning("Couldn't find '" + StringDB.COLUMN_SHOWUSERNAME + "' Field under \"" + user + "\".");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    private void createUserTables(Statement statement) {
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_USERS + "(" + StringDB.COLUMN_USER_ID + " INTEGER UNIQUE NOT NULL, " + StringDB.COLUMN_USERNAME + " VARCHAR(20) NOT NULL," + StringDB.COLUMN_SHOWUSERNAME + " BOOLEAN NOT NULL )");
            log.info("User database tables have been created.");
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized String getUsername(int userID) {
        String result = Strings.EmptyString;
        try {
            getUsername.setInt(1, userID);
            try (ResultSet resultSet = getUsername.executeQuery()) {
                if (resultSet.next()) result = resultSet.getString(StringDB.COLUMN_USERNAME);
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }
}
