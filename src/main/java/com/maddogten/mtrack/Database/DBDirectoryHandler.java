package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.ClassHandler;
import com.maddogten.mtrack.util.GenericMethods;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

public class DBDirectoryHandler {
    private final Logger log = Logger.getLogger(DBDirectoryHandler.class.getName());

    private final DBManager dbManager;

    private PreparedStatement getDirectoryID;
    private PreparedStatement getDirectory;
    private PreparedStatement addDirectory;
    private PreparedStatement changeDirectory;
    //private PreparedStatement removeDirectory; // TODO Add
    private PreparedStatement checkDirectory;
    private PreparedStatement getDirectoryPriority;
    private PreparedStatement updateDirectoryPriority;
    private PreparedStatement updateDirectoryPriorityWithoutID;
    private PreparedStatement doesContainPriority;
    private PreparedStatement getDirectoryActiveStatus;
    private PreparedStatement setDirectoryActiveStatus;
    private PreparedStatement getAllDirectories;
    private PreparedStatement getDirectoriesFromActiveStatus;
    private PreparedStatement getDirectoryFromPriority;

    public DBDirectoryHandler(DBManager dbManager) {
        this.dbManager = dbManager;
        this.dbManager.createTable(DBStrings.CREATE_DIRECTORIESTABLE);
    }

    public synchronized Set<Integer> getAllDirectories() {
        if (isNull(getAllDirectories))
            getAllDirectories = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_getAllDirectoriesSQL);
        Set<Integer> directories = new HashSet<>();
        try (ResultSet resultSet = getAllDirectories.executeQuery()) {
            while (resultSet.next()) directories.add(resultSet.getInt(DBStrings.COLUMN_DIRECTORY_ID));
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return directories;
    }

    public synchronized Set<Integer> getActiveDirectories() {
        if (isNull(getDirectoriesFromActiveStatus))
            getDirectoriesFromActiveStatus = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_getDirectoriesFromActiveStatusSQL);
        Set<Integer> directories = new HashSet<>();
        try {
            getDirectoriesFromActiveStatus.setBoolean(1, true);
            try (ResultSet resultSet = getDirectoriesFromActiveStatus.executeQuery()) {
                while (resultSet.next()) directories.add(resultSet.getInt(DBStrings.COLUMN_DIRECTORY_ID));
            }
            getDirectoriesFromActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return directories;
    }

    public synchronized Set<Integer> getInactiveDirectories() {
        if (isNull(getDirectoriesFromActiveStatus))
            getDirectoriesFromActiveStatus = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_getDirectoriesFromActiveStatusSQL);
        Set<Integer> directories = new HashSet<>();
        try {
            getDirectoriesFromActiveStatus.setBoolean(1, false);
            try (ResultSet resultSet = getDirectoriesFromActiveStatus.executeQuery()) {
                while (resultSet.next()) directories.add(resultSet.getInt(DBStrings.COLUMN_DIRECTORY_ID));
            }
            getDirectoriesFromActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return directories;
    }

    public synchronized int getDirectoryPriority(int directoryID) {
        if (isNull(getDirectoryPriority))
            getDirectoryPriority = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_getDirectoryPrioritySQL);
        int priority = -2;
        try {
            getDirectoryPriority.setInt(1, directoryID);
            try (ResultSet resultSet = getDirectoryPriority.executeQuery()) {
                if (resultSet.next()) priority = resultSet.getInt(DBStrings.COLUMN_DIRECTORYPRIORITY);
            }
            getDirectoryPriority.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return priority;
    }

    private synchronized void adjustDirectoryPriories(int priority) {
        if (isNull(doesContainPriority))
            doesContainPriority = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_doesContainPrioritySQL);
        if (isNull(updateDirectoryPriorityWithoutID))
            updateDirectoryPriorityWithoutID = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_updateDirectoryPriorityWithoutIDSQL);
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

    public synchronized void updateDirectoryPriority(int directoryID, int priority) {
        if (isNull(updateDirectoryPriority))
            updateDirectoryPriority = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_updateDirectoryPrioritySQL);
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

    public synchronized Set<Integer> getAllDirectoryPriories() {
        if (isNull(getDirectoryPriority))
            getDirectoryPriority = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_getDirectoryPrioritySQL);
        Set<Integer> priories = new HashSet<>();
        try (ResultSet resultSet = getDirectoryPriority.executeQuery()) {
            while (resultSet.next()) {
                int next = resultSet.getInt(DBStrings.COLUMN_DIRECTORYPRIORITY);
                if (next != -2) priories.add(resultSet.getInt(DBStrings.COLUMN_DIRECTORYPRIORITY));
            }
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return priories;
    }

    public synchronized boolean addDirectory(File directory, boolean active) {
        if (isNull(checkDirectory))
            checkDirectory = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_checkDirectorySQL);
        if (isNull(addDirectory))
            addDirectory = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_addDirectorySQL);
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

    public synchronized String getDirectory(int directoryID) {
        if (isNull(getDirectory))
            getDirectory = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_getDirectorySQL);
        String result = "";
        try {
            getDirectory.setInt(1, directoryID);
            try (ResultSet resultSet = getDirectory.executeQuery()) {
                if (resultSet.next()) {
                    result = resultSet.getString(DBStrings.COLUMN_DIRECTORY);
                } else log.warning("Couldn't find Directory for \"" + directoryID + "\".");
            }
            getDirectory.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    public synchronized int getDirectoryID(String directory) {
        if (isNull(getDirectoryID))
            getDirectoryID = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_getDirectoryIDSQL);
        int result = -2;
        try {
            getDirectoryID.setString(1, directory);
            try (ResultSet resultSet = getDirectoryID.executeQuery()) {
                if (resultSet.next()) {
                    result = resultSet.getInt(DBStrings.COLUMN_DIRECTORY_ID);
                } else log.warning("Couldn't find DirectoryID for \"" + directory + "\".");
            }
            getDirectoryID.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return result;
    }

    private synchronized int generateDirectoryID() throws SQLException {
        if (isNull(getDirectory))
            getDirectory = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_getDirectorySQL);
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

    private synchronized boolean changeDirectory(int directoryID, String newDirectory) {
        if (isNull(changeDirectory))
            changeDirectory = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_changeDirectorySQL);
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

    public synchronized boolean isDirectoryActive(int directoryID) {
        if (isNull(getDirectoryActiveStatus))
            getDirectoryActiveStatus = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_getDirectoryActiveStatusSQL);
        boolean active = true;
        try {
            getDirectoryActiveStatus.setInt(1, directoryID);
            try (ResultSet resultSet = getDirectoryActiveStatus.executeQuery()) {
                if (resultSet.next()) active = resultSet.getBoolean(DBStrings.COLUMN_DIRECTORYACTIVE);
            }
            getDirectoryActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return active;
    }

    public synchronized void setDirectoryActiveStatus(int directoryID, boolean active) {
        if (isNull(setDirectoryActiveStatus))
            setDirectoryActiveStatus = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_setDirectoryActiveStatusSQL);
        try {
            setDirectoryActiveStatus.setBoolean(1, active);
            setDirectoryActiveStatus.setInt(2, directoryID);
            setDirectoryActiveStatus.execute();
            setDirectoryActiveStatus.clearParameters();
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    public synchronized int getDirectoryWithLowestPriorityFromList(Set<Integer> directories) {
        if (isNull(getDirectoryFromPriority))
            getDirectoryFromPriority = dbManager.prepareStatement(DBStrings.DBDirectoryHandler_getDirectoryFromPrioritySQL);
        int result = -2;
        try (Statement statement = ClassHandler.getDBManager().getStatement()) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int directory : directories) {
                if (!stringBuilder.toString().isEmpty()) stringBuilder.append(" OR ");
                stringBuilder.append(DBStrings.COLUMN_DIRECTORY_ID).append("=").append(directory);
            }
            try (ResultSet resultSet = statement.executeQuery("SELECT MIN(" + DBStrings.COLUMN_DIRECTORYPRIORITY + ") FROM " + DBStrings.TABLE_DIRECTORIES + " WHERE " + stringBuilder)) {
                if (resultSet.next()) {
                    getDirectoryFromPriority.setInt(1, resultSet.getInt(1));
                    try (ResultSet resultSet1 = getDirectoryFromPriority.executeQuery()) {
                        if (resultSet1.next()) result = resultSet1.getInt(DBStrings.COLUMN_DIRECTORY_ID);
                    }
                    getDirectoryFromPriority.clearParameters();
                }
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
