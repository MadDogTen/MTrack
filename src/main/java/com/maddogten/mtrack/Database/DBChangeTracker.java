package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.StringDB;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class DBChangeTracker {
    private final Logger log = Logger.getLogger(DBChangeTracker.class.getName());

    private final PreparedStatement addChange;
    private final PreparedStatement getChangeInfo;
    private final PreparedStatement removeChange;
    private final PreparedStatement addUserChange;
    private final PreparedStatement getUserChanges;
    private final PreparedStatement removeUserChange;
    private final PreparedStatement removeChangeForUsers;
    private final PreparedStatement setUserChangeSeen;
    private final PreparedStatement findUsersSeenChange;
    private final PreparedStatement changeFoundStatus;
    private final PreparedStatement getUsersWithChange;
    private final PreparedStatement getChangesWithUsers;
    //private final PreparedStatement getAllChanges;
    private final PreparedStatement deleteAllChangesForUser;
    private final PreparedStatement setAllSeenForUser;

    public DBChangeTracker(DBManager dbManager) throws SQLException {
        Connection connection = dbManager.getConnection();
        try (Statement statement = connection.createStatement()) {
            createChangesTable(statement);
            createUserChangeTrackTable(statement);
        }

        addChange = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_SHOWCHANGES + " VALUES(?,?,?,?,?,?)");
        getChangeInfo = connection.prepareStatement("SELECT * FROM " + StringDB.TABLE_SHOWCHANGES + " WHERE " + StringDB.COLUMN_CHANGE_ID + "=?");
        removeChange = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_SHOWCHANGES + " WHERE " + StringDB.COLUMN_CHANGE_ID + "=?");
        addUserChange = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_USERCHANGETRACKING + " VALUES(?,?,?)");
        getUserChanges = connection.prepareStatement("SELECT " + StringDB.COLUMN_CHANGE_ID + " FROM " + StringDB.TABLE_USERCHANGETRACKING + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        removeUserChange = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_USERCHANGETRACKING + " WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_CHANGE_ID + "=?");
        removeChangeForUsers = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_USERCHANGETRACKING + " WHERE " + StringDB.COLUMN_CHANGE_ID + "=?");
        setUserChangeSeen = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERCHANGETRACKING + " SET " + StringDB.COLUMN_USERSEEN + "=? WHERE " + StringDB.COLUMN_USER_ID + "=? AND " + StringDB.COLUMN_CHANGE_ID + "=?");
        findUsersSeenChange = connection.prepareStatement("SELECT " + StringDB.COLUMN_USER_ID + " FROM " + StringDB.TABLE_USERCHANGETRACKING + " WHERE " + StringDB.COLUMN_USERSEEN + "=" + Boolean.TRUE + " AND " + StringDB.COLUMN_CHANGE_ID + "=?");
        changeFoundStatus = connection.prepareStatement("UPDATE " + StringDB.TABLE_SHOWCHANGES + " SET " + StringDB.COLUMN_SHOWFOUND + "=? WHERE " + StringDB.COLUMN_CHANGE_ID + "=?");
        getUsersWithChange = connection.prepareStatement("SELECT " + StringDB.COLUMN_USER_ID + " FROM " + StringDB.TABLE_USERCHANGETRACKING + " WHERE " + StringDB.COLUMN_CHANGE_ID + "=?");
        getChangesWithUsers = connection.prepareStatement("SELECT " + StringDB.COLUMN_CHANGE_ID + " FROM " + StringDB.TABLE_USERCHANGETRACKING);
        //getAllChanges = connection.prepareStatement("SELECT " + StringDB.COLUMN_CHANGE_ID + " FROM " + StringDB.TABLE_SHOWCHANGES);
        deleteAllChangesForUser = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_USERCHANGETRACKING + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
        setAllSeenForUser = connection.prepareStatement("UPDATE " + StringDB.TABLE_USERCHANGETRACKING + " SET " + StringDB.COLUMN_USERSEEN + "=" + Boolean.TRUE + " WHERE " + StringDB.COLUMN_USER_ID + "=?");
    }

    private void createUserChangeTrackTable(Statement statement) {
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_USERCHANGETRACKING + "(" + StringDB.COLUMN_USER_ID + " INTEGER NOT NULL, " + StringDB.COLUMN_CHANGE_ID + " INTEGER NOT NULL, " + StringDB.COLUMN_USERSEEN + " BOOLEAN NOT NULL)");
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    private void createChangesTable(Statement statement) {
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_SHOWCHANGES + "(" + StringDB.COLUMN_CHANGE_ID + " INTEGER UNIQUE NOT NULL, " + StringDB.COLUMN_SHOW_ID + " INTEGER NOT NULL , " + StringDB.COLUMN_SEASON + " INTEGER NOT NULL, " + StringDB.COLUMN_EPISODE + " INTEGER NOT NULL, " + StringDB.COLUMN_SHOWFOUND + " BOOLEAN NOT NULL, " + StringDB.COLUMN_TIMEADDED + " INTEGER NOT NULL)");
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized int addChange(int showID, int season, int episode, boolean found) {
        int changeID = getChangeID(showID, season, episode);
        int[] changeInfo = getChangeInfo(changeID);
        if (changeInfo != null) {
            Set<Integer> usersSeenChange = findUsersSeenChange(changeID);
            if (usersSeenChange.isEmpty() && changeInfo[5] != ((found) ? 1 : 0)) removeChange(changeID);
            else {
                changeFoundStatus(changeID, found);
                removeChangeForUsers(changeID);
                usersSeenChange.forEach(userID -> addUserChange(userID, changeID));
            }
        } else {
            try {
                boolean[] changeAddedForUser = {false};
                ClassHandler.userInfoController().getAllUsers().forEach(userID -> {
                    boolean isSeasonValid = season == -2 || ClassHandler.userInfoController().getRecordChangedSeasonsLowerThanCurrent(userID) || !(ClassHandler.userInfoController().getCurrentUserSeason(userID, showID) < season);
                    if ((ClassHandler.userInfoController().getRecordChangesForNonActiveShows(userID) && isSeasonValid || (ClassHandler.userInfoController().isShowActive(userID, showID) && isSeasonValid))) {
                        addUserChange(userID, changeID);
                        changeAddedForUser[0] = true;
                    }
                });
                if (changeAddedForUser[0]) {
                    addChange.setInt(1, changeID);
                    addChange.setInt(2, showID);
                    addChange.setInt(3, season);
                    addChange.setInt(4, episode);
                    addChange.setBoolean(5, found);
                    addChange.setInt(6, GenericMethods.getTimeSeconds());
                    addChange.execute();
                    addChange.clearParameters();
                }
            } catch (SQLException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
        return changeID;
    }

    private synchronized void changeFoundStatus(int changeID, boolean status) {
        try {
            changeFoundStatus.setBoolean(1, status);
            changeFoundStatus.setInt(2, changeID);
            changeFoundStatus.execute();
            changeFoundStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void removeChange(int changeID) {
        try {
            removeChangeForUsers(changeID);
            removeChange.setInt(1, changeID);
            removeChange.execute();
            removeChange.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void removeChangeForUsers(int changeID) {
        try {
            removeChangeForUsers.setInt(1, changeID);
            removeChangeForUsers.execute();
            removeChangeForUsers.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized int[] getChangeInfo(int changeID) {
        int[] result = null;
        try {
            getChangeInfo.setInt(1, changeID);
            try (ResultSet resultSet = getChangeInfo.executeQuery()) {
                if (resultSet.next()) {
                    result = new int[6];
                    result[0] = resultSet.getInt(StringDB.COLUMN_CHANGE_ID);
                    result[1] = resultSet.getInt(StringDB.COLUMN_SHOW_ID);
                    result[2] = resultSet.getInt(StringDB.COLUMN_SEASON);
                    result[3] = resultSet.getInt(StringDB.COLUMN_EPISODE);
                    result[4] = (resultSet.getBoolean(StringDB.COLUMN_SHOWFOUND) ? 1 : 0);
                    result[5] = resultSet.getInt(StringDB.COLUMN_TIMEADDED);
                }
            }
            getChangeInfo.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    private int getChangeID(int showID, int season, int episode) {
        return (season == -2) ? showID : (episode == -2) ? GenericMethods.concatenateDigits(showID, season) : GenericMethods.concatenateDigits(showID, season, episode);
    }

    public synchronized void addUserChange(int userID, int changeID) {
        try {
            addUserChange.setInt(1, userID);
            addUserChange.setInt(2, changeID);
            addUserChange.setBoolean(3, false);
            addUserChange.execute();
            addUserChange.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void setUserChangeSeen(int userID, int changeID) {
        try {
            setUserChangeSeen.setBoolean(1, true);
            setUserChangeSeen.setInt(2, userID);
            setUserChangeSeen.setInt(3, changeID);
            setUserChangeSeen.execute();
            setUserChangeSeen.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized Set<Integer> findUsersSeenChange(int changeID) {
        Set<Integer> seenBy = new HashSet<>();
        try {
            findUsersSeenChange.setInt(1, changeID);
            try (ResultSet resultSet = findUsersSeenChange.executeQuery()) {
                while (resultSet.next()) seenBy.add(resultSet.getInt(StringDB.COLUMN_USER_ID));
            }
            findUsersSeenChange.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return seenBy;
    }

    public synchronized Set<Integer> getUserChanges(int userID) {
        Set<Integer> userChanges = new HashSet<>();
        try {
            getUserChanges.setInt(1, userID);
            try (ResultSet resultSet = getUserChanges.executeQuery()) {
                while (resultSet.next()) userChanges.add(resultSet.getInt(StringDB.COLUMN_CHANGE_ID));
            }
            getUserChanges.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return userChanges;
    }

    public synchronized void removeUserChange(int userID, int changeID) {
        try {
            removeUserChange.setInt(1, userID);
            removeUserChange.setInt(2, changeID);
            removeUserChange.clearParameters();
            if (getUsersWithChange(changeID).isEmpty()) removeChange(changeID);
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized Set<Integer> getUsersWithChange(int changeID) {
        Set<Integer> usersWithChange = new HashSet<>();
        try {
            getUsersWithChange.setInt(1, changeID);
            try (ResultSet resultSet = getUsersWithChange.executeQuery()) {
                while (resultSet.next()) usersWithChange.add(resultSet.getInt(StringDB.COLUMN_USER_ID));
            }
            getUsersWithChange.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return usersWithChange;
    }

    public synchronized void deleteAllChangesForUser(int userID) {
        try {
            Set<Integer> userChanges = getUserChanges(userID);
            deleteAllChangesForUser.setInt(1, userID);
            deleteAllChangesForUser.execute();
            deleteAllChangesForUser.clearParameters();
            userChanges.forEach(changeID -> {
                if (getUsersWithChange(changeID).isEmpty()) removeChange(changeID);
            });
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized void setAllSeenForUser(int userID) {
        try {
            setAllSeenForUser.setInt(1, userID);
            setAllSeenForUser.execute();
            setAllSeenForUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }
}
