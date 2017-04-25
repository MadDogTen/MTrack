package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.StringDB;

import java.io.File;
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
    private final PreparedStatement getDirectoryActiveStatus;
    private final PreparedStatement setDirectoryActiveStatus;
    private final PreparedStatement getAllDirectories;
    private final PreparedStatement getDirectoriesFromActiveStatus;
    private final PreparedStatement getDirectoryFromPriority;

    public DBDirectoryHandler(Connection connection) throws SQLException {
        initTables(connection);

        getDirectoryID = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORY_ID + " FROM " + StringDB.TABLE_DIRECTORIES + " WHERE " + StringDB.COLUMN_DIRECTORY + "=?");
        getDirectory = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORY + " FROM " + StringDB.TABLE_DIRECTORIES + " WHERE " + StringDB.COLUMN_DIRECTORY_ID + "=?");
        addDirectory = connection.prepareStatement("INSERT INTO " + StringDB.TABLE_DIRECTORIES + " VALUES (?, ?, ?, ?)");
        changeDirectory = connection.prepareStatement("UPDATE " + StringDB.TABLE_DIRECTORIES + " SET " + StringDB.COLUMN_DIRECTORY + "=? WHERE " + StringDB.COLUMN_DIRECTORY_ID + "=?");
        removeDirectory = connection.prepareStatement("DELETE FROM " + StringDB.TABLE_DIRECTORIES + " WHERE " + StringDB.COLUMN_DIRECTORY_ID + "=?");
        checkDirectory = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORY + " FROM " + StringDB.TABLE_DIRECTORIES + " WHERE " + StringDB.COLUMN_DIRECTORY + "=?");
        getDirectoryPriority = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORYPRIORITY + " FROM " + StringDB.TABLE_DIRECTORIES + " WHERE " + StringDB.COLUMN_DIRECTORY_ID + "=?");
        updateDirectoryPriority = connection.prepareStatement("UPDATE " + StringDB.TABLE_DIRECTORIES + " SET " + StringDB.COLUMN_DIRECTORYPRIORITY + "=?" + " WHERE " + StringDB.COLUMN_DIRECTORY_ID + "=?");
        getAllDirectoriesPriories = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORYPRIORITY + " FROM " + StringDB.TABLE_DIRECTORIES);
        updateDirectoryPriorityWithoutID = connection.prepareStatement("UPDATE " + StringDB.TABLE_DIRECTORIES + " SET " + StringDB.COLUMN_DIRECTORYPRIORITY + "=? WHERE " + StringDB.COLUMN_DIRECTORYPRIORITY + "=?");
        doesContainPriority = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORYPRIORITY + " FROM " + StringDB.TABLE_DIRECTORIES + " WHERE " + StringDB.COLUMN_DIRECTORYPRIORITY + "=?");
        getDirectoryActiveStatus = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORYACTIVE + " FROM " + StringDB.TABLE_DIRECTORIES + " WHERE " + StringDB.COLUMN_DIRECTORY_ID + "=?");
        setDirectoryActiveStatus = connection.prepareStatement("UPDATE " + StringDB.TABLE_DIRECTORIES + " SET " + StringDB.COLUMN_DIRECTORYACTIVE + "=? WHERE " + StringDB.COLUMN_DIRECTORY_ID + "=?");
        getAllDirectories = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORY_ID + " FROM " + StringDB.TABLE_DIRECTORIES);
        getDirectoriesFromActiveStatus = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORY_ID + " FROM " + StringDB.TABLE_DIRECTORIES + " WHERE " + StringDB.COLUMN_DIRECTORYACTIVE + "=?");
        getDirectoryFromPriority = connection.prepareStatement("SELECT " + StringDB.COLUMN_DIRECTORY_ID + " FROM " + StringDB.TABLE_DIRECTORIES + " WHERE " + StringDB.COLUMN_DIRECTORYPRIORITY + "=?");
    }

    private void initTables(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            createDirectoryTable(statement);
        }
    }

    private void createDirectoryTable(Statement statement) {
        try {
            statement.execute("CREATE TABLE " + StringDB.TABLE_DIRECTORIES + "(" + StringDB.COLUMN_DIRECTORY_ID + " INTEGER NOT NULL UNIQUE, " + StringDB.COLUMN_DIRECTORY + " VARCHAR(" + StringDB.directoryLength + ") NOT NULL UNIQUE, " + StringDB.COLUMN_DIRECTORYPRIORITY + " INTEGER NOT NULL, " + StringDB.COLUMN_DIRECTORYACTIVE + " BOOLEAN NOT NULL)");
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public Set<Integer> getAllDirectories() {
        Set<Integer> directories = new HashSet<>();
        try (ResultSet resultSet = getAllDirectories.executeQuery()) {
            while (resultSet.next()) directories.add(resultSet.getInt(StringDB.COLUMN_DIRECTORY_ID));
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return directories;
    }

    public Set<Integer> getActiveDirectories() {
        Set<Integer> directories = new HashSet<>();
        try {
            getDirectoriesFromActiveStatus.setBoolean(1, true);
            try (ResultSet resultSet = getDirectoriesFromActiveStatus.executeQuery()) {
                while (resultSet.next()) directories.add(resultSet.getInt(StringDB.COLUMN_DIRECTORY_ID));
            }
            getDirectoriesFromActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return directories;
    }

    public Set<Integer> getInactiveDirectories() {
        Set<Integer> directories = new HashSet<>();
        try {
            getDirectoriesFromActiveStatus.setBoolean(1, false);
            try (ResultSet resultSet = getDirectoriesFromActiveStatus.executeQuery()) {
                while (resultSet.next()) directories.add(resultSet.getInt(StringDB.COLUMN_DIRECTORY_ID));
            }
            getDirectoriesFromActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return directories;
    }

    public boolean doesShowExistsInOtherDirectories(int showID, int... directoryIDs) {
        boolean result = false;
        if (directoryIDs.length > 0) {
            try (Statement statement = ClassHandler.getDBManager().getStatement()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT * FROM ").append(StringDB.TABLE_SHOWSINDIRECTORY).append(" WHERE ").append(StringDB.COLUMN_SHOW_ID).append("=").append(showID);
                for (int directoryID : directoryIDs) {
                    stringBuilder.append(" AND ").append(StringDB.COLUMN_DIRECTORY_ID).append("!=").append(directoryID);
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
                if (resultSet.next()) priority = resultSet.getInt(StringDB.COLUMN_DIRECTORYPRIORITY);
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
                int next = resultSet.getInt(StringDB.COLUMN_DIRECTORYPRIORITY);
                if (next != -2) priories.add(resultSet.getInt(StringDB.COLUMN_DIRECTORYPRIORITY));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return priories;
    }

    public boolean addDirectory(File directory, boolean active) {
        boolean added = false;
        try {
            checkDirectory.setString(1, directory.toString());
            try (ResultSet resultSet = checkDirectory.executeQuery()) {
                if (resultSet.next()) log.info("Directory \"" + directory + "\" was already added.");
                else {
                    addDirectory.setInt(1, generateDirectoryID());
                    addDirectory.setString(2, directory.toString());
                    addDirectory.setInt(3, -2);
                    addDirectory.setBoolean(4, active);
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

    public String getDirectory(int directoryID) {
        String result = "";
        try {
            getDirectory.setInt(1, directoryID);
            try (ResultSet resultSet = getDirectory.executeQuery()) {
                if (resultSet.next()) {
                    result = resultSet.getString(StringDB.COLUMN_DIRECTORY);
                } else log.warning("Couldn't find Directory for \"" + directoryID + "\".");
            }
            getDirectory.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public int getDirectoryID(String directory) {
        int result = -2;
        try {
            getDirectoryID.setString(1, directory);
            try (ResultSet resultSet = getDirectoryID.executeQuery()) {
                if (resultSet.next()) {
                    result = resultSet.getInt(StringDB.COLUMN_DIRECTORY_ID);
                } else log.warning("Couldn't find DirectoryID for \"" + directory + "\".");
            }
            getDirectoryID.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
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

    public boolean isDirectoryActive(int directoryID) {
        boolean active = true;
        try {
            getDirectoryActiveStatus.setInt(1, directoryID);
            try (ResultSet resultSet = getDirectoryActiveStatus.executeQuery()) {
                if (resultSet.next()) active = resultSet.getBoolean(StringDB.COLUMN_DIRECTORYACTIVE);
            }
            getDirectoryActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return active;
    }

    public void setDirectoryActiveStatus(int directoryID, boolean active) {
        try {
            setDirectoryActiveStatus.setBoolean(1, active);
            setDirectoryActiveStatus.setInt(2, directoryID);
            setDirectoryActiveStatus.execute();
            setDirectoryActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public int getDirectoryWithLowestPriorityFromList(Set<Integer> directories) {
        int result = -2;

        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int directory : directories) {
                if (!stringBuilder.toString().isEmpty()) stringBuilder.append(" OR ");
                stringBuilder.append(StringDB.COLUMN_DIRECTORY_ID).append("=").append(directory);
            }
            try (ResultSet resultSet = statement.executeQuery("SELECT MIN(" + StringDB.COLUMN_DIRECTORYPRIORITY + ") FROM " + StringDB.TABLE_DIRECTORIES + " WHERE " + stringBuilder)) {
                if (resultSet.next()) {
                    getDirectoryFromPriority.setInt(1, resultSet.getInt(StringDB.COLUMN_DIRECTORYPRIORITY));
                    try (ResultSet resultSet1 = getAllDirectoriesPriories.executeQuery()) {
                        if (resultSet1.next()) result = resultSet1.getInt(StringDB.COLUMN_DIRECTORY_ID);
                    }
                    getDirectoryFromPriority.clearParameters();
                }
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }

        return result;
    }
}
