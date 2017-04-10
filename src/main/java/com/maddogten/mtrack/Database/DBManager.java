package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.StringDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

public class DBManager {
    private final Logger log = Logger.getLogger(DBManager.class.getName());

    private final Connection connection;
    private DBShowManager dbShowManager;
    private DBUserManager dbUserManager;
    private DBUserSettingsManager dbUserSettingsManager;
    private DBDirectoryHandler dbDirectoryHandler;

    public DBManager(String databaseLocation, boolean shouldCreateDDB) throws SQLException {
        connection = this.getConnection(databaseLocation, shouldCreateDDB);
        initTables();
    }

    public Connection getConnection() { // TODO Make package private
        return connection;
    }

    public Statement getStatement() throws SQLException {
        return connection.createStatement();
    }

    // This will get the connection to the database, created one if necessary.
    private Connection getConnection(String databaseLocation, boolean createDB) {
        System.setProperty("derby.system.home", databaseLocation);

        Connection connection = null;
        String URL = "jdbc:derby:" + StringDB.DBFolderName;
        Properties properties = new Properties();
        if (createDB) properties.put("create", "true");
        properties.put("user", "MTrack");
        properties.put("password", "MTrackSimplePassword");
        try {
            Class.forName(StringDB.DRIVER);
            connection = DriverManager.getConnection(URL, properties);
        } catch (SQLException | ClassNotFoundException e) {
            GenericMethods.printStackTrace(log, e, DBManager.class);
        }
        return connection;
    }

    Statement executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeQuery(query);
        return statement;
    }

    boolean execute(String execute) throws SQLException {
        boolean result;
        try (Statement statement = connection.createStatement()) {
            result = statement.execute(execute);
        }
        return result;
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    private void initTables() throws SQLException {
        dbDirectoryHandler = new DBDirectoryHandler(connection);
        dbUserManager = new DBUserManager(connection);
        dbUserSettingsManager = new DBUserSettingsManager(connection);
        dbShowManager = new DBShowManager(connection);
    }

    public DBShowManager getDbShowManager() {
        return dbShowManager;
    }

    public DBUserManager getDbUserManager() {
        return dbUserManager;
    }

    public DBUserSettingsManager getDbUserSettingsManager() {
        return dbUserSettingsManager;
    }

    public DBDirectoryHandler getDbDirectoryHandler() {
        return dbDirectoryHandler;
    }
}
