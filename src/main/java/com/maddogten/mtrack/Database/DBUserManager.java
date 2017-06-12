package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

public class DBUserManager {
    private final Logger log = Logger.getLogger(DBUserManager.class.getName());
    private final DBManager dbManager;

    private PreparedStatement insertUser = null;
    private PreparedStatement changeUsername = null;
    private PreparedStatement getShowUsername = null;
    private PreparedStatement checkUserID = null;
    private PreparedStatement getUserID = null;
    private PreparedStatement getUsername = null;
    private PreparedStatement getAllUsers = null;

    public DBUserManager(DBManager dbManager) {
        this.dbManager = dbManager;
        this.dbManager.createTable(DBStrings.CREATE_USERSTABLE);
    }

    public synchronized int addUser(String username) {
        return this.addUser(username, true);
    }

    public synchronized int addUser(String userName, boolean showUsername) {
        if (isNull(insertUser)) insertUser = dbManager.prepareStatement(DBStrings.DBUserManager_insertUserSQL);
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
        if (isNull(getUserID)) getUserID = dbManager.prepareStatement(DBStrings.DBUserManager_getUserIDSQL);
        int result = -2;
        try {
            getUserID.setString(1, userName);
            try (ResultSet resultSet = getUserID.executeQuery()) {
                if (resultSet.next()) result = resultSet.getInt(DBStrings.COLUMN_USER_ID);
            }
            getUserID.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    /*public synchronized void deleteUser(String userName) { // TODO Finish
        try {
            int userID = getUserID(userName);
            if (userID != -2) {
                try (Statement statement = ClassHandler.getDBManager().getStatement()) { // TODO Make sure it deletes all associated information.
                    statement.execute("DELETE FROM " + DBStrings.TABLE_USERS + " WHERE " + DBStrings.COLUMN_USER_ID + "=" + userID);
                    //statement.executeQuery("DELETE FROM " + DBStrings.settings + " WHERE " + DBStrings.userID + " = " + userID);

                    log.info("User \"" + userName + "\" was successfully deleted.");
                }
            } else log.warning("Invalid userID, User wasn't deleted.");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }*/

    public synchronized void changeUsername(String oldUsername, String newUsername) {
        if (isNull(changeUsername))
            changeUsername = dbManager.prepareStatement(DBStrings.DBUserManager_changeUsernameSQL);
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
        if (isNull(getAllUsers)) getAllUsers = dbManager.prepareStatement(DBStrings.DBUserManager_getAllUsersSQL);
        ArrayList<Integer> users = new ArrayList<>();
        try (ResultSet resultSet = getAllUsers.executeQuery()) {
            while (resultSet.next()) {
                users.add(resultSet.getInt(DBStrings.COLUMN_USER_ID));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return users;
    }

    private synchronized int generateUserID() throws SQLException {
        if (isNull(checkUserID)) checkUserID = dbManager.prepareStatement(DBStrings.DBUserManager_checkUserIDSQL);
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

    public synchronized int getUserID(String username) {
        if (isNull(getUserID)) getUserID = dbManager.prepareStatement(DBStrings.DBUserManager_getUserIDSQL);
        int result = -2;
        try {
            getUserID.setString(1, username);
            try (ResultSet resultSet = getUserID.executeQuery()) {
                if (resultSet.next()) {
                    result = resultSet.getInt(DBStrings.COLUMN_USER_ID);
                } else log.warning("Couldn't find UserID for \"" + username + "\".");
            }
            getUserID.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized boolean getShowUsername(String user) {
        if (isNull(getShowUsername))
            getShowUsername = dbManager.prepareStatement(DBStrings.DBUserManager_getShowUsernameSQL);
        boolean result = true;
        try {
            getShowUsername.setString(1, user);
            ResultSet resultSet = getShowUsername.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getBoolean(DBStrings.COLUMN_SHOWUSERNAME);
                resultSet.close();
                getShowUsername.clearParameters();
            } else log.warning("Couldn't find '" + DBStrings.COLUMN_SHOWUSERNAME + "' Field under \"" + user + "\".");
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized String getUsername(int userID) {
        if (isNull(getUsername)) getUsername = dbManager.prepareStatement(DBStrings.DBUserManager_getUsernameSQL);
        String result = Strings.EmptyString;
        try {
            getUsername.setInt(1, userID);
            try (ResultSet resultSet = getUsername.executeQuery()) {
                if (resultSet.next()) result = resultSet.getString(DBStrings.COLUMN_USERNAME);
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    private boolean isNull(Object object) {
        return object == null;
    }
}
