package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class DBChangeTracker {
    private final Logger log = Logger.getLogger(DBChangeTracker.class.getName());

    private final DBManager dbManager;

    private PreparedStatement addChange;
    private PreparedStatement getChangeInfo;
    private PreparedStatement removeChange;
    private PreparedStatement addUserChange;
    private PreparedStatement getUserChanges;
    private PreparedStatement removeUserChange;
    private PreparedStatement removeChangeForUsers;
    private PreparedStatement setUserChangeSeen;
    private PreparedStatement findUsersSeenChange;
    private PreparedStatement changeFoundStatus;
    private PreparedStatement getUsersWithChange;
    private PreparedStatement getChangesWithUsers;
    //private PreparedStatement getAllChanges;
    private PreparedStatement deleteAllChangesForUser;
    private PreparedStatement setAllSeenForUser;
    private PreparedStatement isShowChanged;
    private PreparedStatement doesUserContainChange;

    public DBChangeTracker(DBManager dbManager) {
        this.dbManager = dbManager;
        this.dbManager.createTable(DBStrings.CREATE_USERCHANGETRACKINGTABLE);
        this.dbManager.createTable(DBStrings.CREATE_SHOWCHANGESTABLE);
    }

    public synchronized int addChange(int showID, int season, int episode, boolean found) {
        if (isNull(addChange)) addChange = dbManager.prepareStatement(DBStrings.DBChangeTracker_addChangeSQL);
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
        if (isNull(changeFoundStatus))
            changeFoundStatus = dbManager.prepareStatement(DBStrings.DBChangeTracker_changeFoundStatusSQL);
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
        if (isNull(removeChange)) removeChange = dbManager.prepareStatement(DBStrings.DBChangeTracker_removeChangeSQL);
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
        if (isNull(removeChangeForUsers))
            removeChangeForUsers = dbManager.prepareStatement(DBStrings.DBChangeTracker_removeChangeForUsersSQL);
        try {
            removeChangeForUsers.setInt(1, changeID);
            removeChangeForUsers.execute();
            removeChangeForUsers.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized int[] getChangeInfo(int changeID) {
        if (isNull(getChangeInfo))
            getChangeInfo = dbManager.prepareStatement(DBStrings.DBChangeTracker_getChangeInfoSQL);
        int[] result = null;
        try {
            getChangeInfo.setInt(1, changeID);
            try (ResultSet resultSet = getChangeInfo.executeQuery()) {
                if (resultSet.next()) {
                    result = new int[6];
                    result[0] = resultSet.getInt(DBStrings.COLUMN_CHANGE_ID);
                    result[1] = resultSet.getInt(DBStrings.COLUMN_SHOW_ID);
                    result[2] = resultSet.getInt(DBStrings.COLUMN_SEASON);
                    result[3] = resultSet.getInt(DBStrings.COLUMN_EPISODE);
                    result[4] = (resultSet.getBoolean(DBStrings.COLUMN_SHOWFOUND) ? 1 : 0);
                    result[5] = resultSet.getInt(DBStrings.COLUMN_TIMEADDED);
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
        if (isNull(addUserChange))
            addUserChange = dbManager.prepareStatement(DBStrings.DBChangeTracker_addUserChangeSQL);
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
        if (isNull(setUserChangeSeen))
            setUserChangeSeen = dbManager.prepareStatement(DBStrings.DBChangeTracker_setUserChangeSeenSQL);
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
        if (isNull(findUsersSeenChange))
            findUsersSeenChange = dbManager.prepareStatement(DBStrings.DBChangeTracker_findUsersSeenChangeSQL);
        Set<Integer> seenBy = new HashSet<>();
        try {
            findUsersSeenChange.setInt(1, changeID);
            try (ResultSet resultSet = findUsersSeenChange.executeQuery()) {
                while (resultSet.next()) seenBy.add(resultSet.getInt(DBStrings.COLUMN_USER_ID));
            }
            findUsersSeenChange.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return seenBy;
    }

    public synchronized Set<Integer> getUserChanges(int userID) {
        if (isNull(getUserChanges))
            getUserChanges = dbManager.prepareStatement(DBStrings.DBChangeTracker_getUserChangesSQL);
        Set<Integer> userChanges = new HashSet<>();
        try {
            getUserChanges.setInt(1, userID);
            try (ResultSet resultSet = getUserChanges.executeQuery()) {
                while (resultSet.next()) userChanges.add(resultSet.getInt(DBStrings.COLUMN_CHANGE_ID));
            }
            getUserChanges.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return userChanges;
    }

    public synchronized void removeUserChange(int userID, int changeID) {
        if (isNull(removeUserChange))
            removeUserChange = dbManager.prepareStatement(DBStrings.DBChangeTracker_removeUserChangeSQL);
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
        if (isNull(getUsersWithChange))
            getUsersWithChange = dbManager.prepareStatement(DBStrings.DBChangeTracker_getUsersWithChangeSQL);
        Set<Integer> usersWithChange = new HashSet<>();
        try {
            getUsersWithChange.setInt(1, changeID);
            try (ResultSet resultSet = getUsersWithChange.executeQuery()) {
                while (resultSet.next()) usersWithChange.add(resultSet.getInt(DBStrings.COLUMN_USER_ID));
            }
            getUsersWithChange.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return usersWithChange;
    }

    public synchronized void deleteAllChangesForUser(int userID) {
        if (isNull(deleteAllChangesForUser))
            deleteAllChangesForUser = dbManager.prepareStatement(DBStrings.DBChangeTracker_deleteAllChangesForUserSQL);
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
        if (isNull(setAllSeenForUser))
            setAllSeenForUser = dbManager.prepareStatement(DBStrings.DBChangeTracker_setAllSeenForUserSQL);
        try {
            setAllSeenForUser.setInt(1, userID);
            setAllSeenForUser.execute();
            setAllSeenForUser.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized boolean isShowChangedForUser(int userID, int showID) {
        if (isNull(isShowChanged))
            isShowChanged = dbManager.prepareStatement(DBStrings.DBChangeTracker_isShowChangedSQL);
        if (isNull(doesUserContainChange))
            doesUserContainChange = dbManager.prepareStatement(DBStrings.DBChangeTracker_doesUserContainChangeSQL);
        boolean result = false;
        try {
            isShowChanged.setInt(1, showID);
            try (ResultSet resultSet = isShowChanged.executeQuery()) {
                while (!result && resultSet.next()) {
                    int changeID = resultSet.getInt(DBStrings.COLUMN_CHANGE_ID);
                    doesUserContainChange.setInt(1, userID);
                    doesUserContainChange.setInt(2, changeID);
                    try (ResultSet resultSet1 = doesUserContainChange.executeQuery()) {
                        if (resultSet1.next()) result = true;
                    }
                    doesUserContainChange.clearParameters();
                }
            }
            isShowChanged.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    private boolean isNull(Object object) {
        return object == null;
    }
}
