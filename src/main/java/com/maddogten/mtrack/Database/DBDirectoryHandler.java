package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.StringDB;

import java.sql.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

public class DBDirectoryHandler {
    private final Logger log = Logger.getLogger(DBDirectoryHandler.class.getName());

    private final PreparedStatement getDirectoryID;
    private final PreparedStatement getDirectory;
    private final PreparedStatement addDirectory;
    private final PreparedStatement changeDirectory;
    private final PreparedStatement removeDirectory; // TODO Add
    private final PreparedStatement checkDirectory;
    private final PreparedStatement getDirectoryPriority;
    private final PreparedStatement updateDirectoryPriority;
    private final PreparedStatement getAllDirectoriesPriories;
    private final PreparedStatement updateDirectoryPriorityWithoutID;
    private final PreparedStatement doesContainPriority;


    public DBDirectoryHandler(Connection connection) throws SQLException {
        initTables(connection);

        getDirectoryID = connection.prepareStatement("SELECT " + StringDB.directoryID + " FROM " + StringDB.directories + " WHERE " + StringDB.directory + "=?");
        getDirectory = connection.prepareStatement("SELECT " + StringDB.directory + " FROM " + StringDB.directories + " WHERE " + StringDB.directoryID + "=?");
        addDirectory = connection.prepareStatement("INSERT INTO " + StringDB.directories + " VALUES (?, ?, ?)");
        changeDirectory = connection.prepareStatement("UPDATE " + StringDB.directories + " SET " + StringDB.directory + "=? WHERE " + StringDB.directoryID + "=?");
        removeDirectory = connection.prepareStatement("DELETE FROM " + StringDB.directories + " WHERE " + StringDB.directoryID + "=?");
        checkDirectory = connection.prepareStatement("SELECT " + StringDB.directory + " FROM " + StringDB.directories + " WHERE " + StringDB.directory + "=?");
        getDirectoryPriority = connection.prepareStatement("SELECT " + StringDB.directoryPriority + " FROM " + StringDB.directories + " WHERE " + StringDB.directoryID + "=?");
        updateDirectoryPriority = connection.prepareStatement("UPDATE " + StringDB.directories + " SET " + StringDB.directoryPriority + "=?" + " WHERE " + StringDB.directoryID + "=?");
        getAllDirectoriesPriories = connection.prepareStatement("SELECT " + StringDB.directoryPriority + " FROM " + StringDB.directories);
        updateDirectoryPriorityWithoutID = connection.prepareStatement("UPDATE " + StringDB.directories + " SET " + StringDB.directoryPriority + "=? WHERE " + StringDB.directoryPriority + "=?");
        doesContainPriority = connection.prepareStatement("SELECT " + StringDB.directoryPriority + " FROM " + StringDB.directories + " WHERE " + StringDB.directoryPriority + "=?");
    }

    private void initTables(Connection connection) throws SQLException {
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.directories, null)) {
            if (!resultSet.next()) {
                log.fine("Directory table doesn't exist, creating...");
                try (Statement statement = connection.createStatement()) {
                    createDirectoryTable(statement);
                }
            }
        }
        try (ResultSet resultSet = connection.getMetaData().getTables(null, null, StringDB.showsInDirectory, null)) {
            if (!resultSet.next()) {
                log.fine("Shows in directory table doesn't exist, creating...");
                try (Statement statement = connection.createStatement()) {
                    createShowsInDirectoryTable(statement);
                }
            }
        }
    }

    private void createShowsInDirectoryTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.showsInDirectory + "(" + StringDB.directoryID + " INTEGER NOT NULL, " + StringDB.showID + " INTEGER NOT NULL)");
    }

    private void createDirectoryTable(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + StringDB.directories + "(" + StringDB.directoryID + " INTEGER NOT NULL UNIQUE, " + StringDB.directory + " VARCHAR(" + StringDB.directoryLength + ") NOT NULL UNIQUE, " + StringDB.directoryPriority + " INTEGER NOT NULL)");
    }

    public boolean doesShowExistsInOtherDirectories(int showID, int... directoryIDs) {
        boolean result = false;
        if (directoryIDs.length > 0) {
            try (Statement statement = ClassHandler.getDBManager().getStatement()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT * FROM ").append(StringDB.showsInDirectory).append(" WHERE ").append(StringDB.showID).append("=").append(showID);
                for (int directoryID : directoryIDs) {
                    stringBuilder.append(" AND ").append(StringDB.directoryID).append("!=").append(directoryID);
                }
                try (ResultSet resultSet = statement.executeQuery(stringBuilder.toString())) {
                    if (resultSet.next()) result = true;
                }
            } catch (SQLException e) {
                GenericMethods.printStackTrace(log, e, this.getClass());
            }
        }
        return result;
    }

    public int getDirectoryPriority(int directoryID) {
        int priority = -2;
        try {
            getDirectoryPriority.setInt(1, directoryID);
            try (ResultSet resultSet = getDirectoryPriority.executeQuery()) {
                if (resultSet.next()) priority = resultSet.getInt(StringDB.directoryPriority);
            }
            getDirectoryPriority.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return priority;
    }

    private void adjustDirectoryPriories(int priority) {
        try {
            int priorityNext = priority + 1;
            doesContainPriority.setInt(1, priorityNext);
            try (ResultSet resultSet = doesContainPriority.executeQuery()) {
                if (resultSet.next()) {
                    doesContainPriority.clearParameters();
                    adjustDirectoryPriories(priorityNext);
                } else {
                    doesContainPriority.clearParameters();
                    updateDirectoryPriorityWithoutID.setInt(1, priorityNext);
                    updateDirectoryPriorityWithoutID.setInt(2, priority);
                    updateDirectoryPriorityWithoutID.execute();
                    updateDirectoryPriorityWithoutID.clearParameters();
                }
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public void updateDirectoryPriority(int directoryID, int priority) {
        try {
            Set<Integer> priories = getAllDirectoryPriories();
            if (priories.contains(priority)) {
                adjustDirectoryPriories(priority);
            } else {
                updateDirectoryPriority.setInt(1, priority);
                updateDirectoryPriority.setInt(2, directoryID);
                updateDirectoryPriority.execute();
                updateDirectoryPriority.clearParameters();
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public Set<Integer> getAllDirectoryPriories() {
        Set<Integer> priories = new HashSet<>();
        try (ResultSet resultSet = getDirectoryPriority.executeQuery()) {
            while (resultSet.next()) {
                int next = resultSet.getInt(StringDB.directoryPriority);
                if (next != -2) priories.add(resultSet.getInt(StringDB.directoryPriority));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return priories;
    }

    public boolean addDirectory(String directory) {
        boolean added = false;
        try {
            checkDirectory.setString(1, directory);
            try (ResultSet resultSet = checkDirectory.executeQuery()) {
                if (resultSet.next()) log.info("Directory \"" + directory + "\" was already added.");
                else {
                    addDirectory.setInt(1, generateDirectoryID());
                    addDirectory.setString(2, directory);
                    addDirectory.setInt(3, -2);
                    addDirectory.execute();
                    addDirectory.clearParameters();
                    log.info("\"" + directory + "\" was added");
                    added = true;
                }
            }
            checkDirectory.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return added;
    }

    public String getDirectory(int directoryID) throws SQLException {
        String result = "";
        getDirectory.setInt(1, directoryID);
        try (ResultSet resultSet = getDirectory.executeQuery()) {
            if (resultSet.next()) {
                result = resultSet.getString(StringDB.directory);
            } else log.warning("Couldn't find Directory for \"" + directoryID + "\".");
        }
        getDirectory.clearParameters();
        return result;
    }

    public int getDirectoryID(String directory) throws SQLException {
        int result = -2;
        getDirectoryID.setString(1, directory);
        try (ResultSet resultSet = getDirectoryID.executeQuery()) {
            if (resultSet.next()) {
                result = resultSet.getInt(StringDB.userID);
            } else log.warning("Couldn't find DirectoryID for \"" + directory + "\".");
        }
        getDirectoryID.clearParameters();
        return result;
    }

    private int generateDirectoryID() throws SQLException {
        Random random = new Random();
        int directoryID;
        ResultSet resultSet;
        do {
            directoryID = random.nextInt(Integer.MAX_VALUE - 100000000) + 100000000;
            getDirectory.setInt(1, directoryID);
            resultSet = getDirectory.executeQuery();
        } while (resultSet.next());
        resultSet.close();
        getDirectory.clearParameters();
        return directoryID;
    }

    private boolean changeDirectory(int directoryID, String newDirectory) {
        boolean changed = false;
        try {
            checkDirectory.setString(1, newDirectory);
            try (ResultSet resultSet = checkDirectory.executeQuery()) {
                if (resultSet.next()) log.info("Directory \"" + newDirectory + "\" is already added.");
                else {
                    changeDirectory.setString(1, newDirectory);
                    changeDirectory.setInt(2, directoryID);
                    changeDirectory.execute();
                    changeDirectory.clearParameters();
                    log.info("\"" + directoryID + "\" was changed to \"" + newDirectory + "\".");
                    changed = true;
                }
            }
            checkDirectory.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return changed;
    }
}
