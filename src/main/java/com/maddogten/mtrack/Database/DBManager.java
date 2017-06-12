package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.Main;
import com.maddogten.mtrack.util.GenericMethods;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DBManager {
    private final Logger log = Logger.getLogger(DBManager.class.getName());

    private final Connection connection;

    public DBManager(String databaseLocation, boolean shouldCreateDDB) {
        connection = this.getConnection(databaseLocation, shouldCreateDDB);
        if (connection == null) Main.stop(null, true);
    }

    synchronized Connection getConnection() {
        return connection;
    }

    synchronized Statement getStatement() throws SQLException {
        return connection.createStatement();
    }

    // This will get the connection to the database, created one if necessary.
    private Connection getConnection(String databaseLocation, boolean createDB) {
        System.setProperty("derby.system.home", databaseLocation);

        Connection connection = null;
        String URL = "jdbc:derby:" + DBStrings.DBFolderName;
        Properties properties = new Properties();
        if (createDB) {
            properties.put("create", "true");
            log.info("Creating Database at \"" + databaseLocation + "\".");
        }
        properties.put("user", "MTrack");
        properties.put("password", "MTrackSimplePassword");
        try {
            Class.forName(DBStrings.DRIVER);
            connection = DriverManager.getConnection(URL, properties);
        } catch (SQLException e) {
            if (e.getNextException().getSQLState().matches("XSDB6")) {
                log.info("Database already has a connection, Check if another instance of the program is running..."); // TODO Add user popup
                return null;
            }
            GenericMethods.printStackTrace(log, e, this.getClass());
            System.exit(0);
        } catch (ClassNotFoundException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return connection;
    }

    public synchronized void closeConnection() throws SQLException {
        if (connection != null) connection.close();
    }

    public boolean hasConnection() {
        boolean hasConnection = false;
        try {
            hasConnection = (connection != null && !connection.isClosed());
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return hasConnection;
    }

    boolean createTable(String tableInfo) {
        boolean createdTable = false;
        try {
            getStatement().execute("CREATE TABLE " + tableInfo);
            createdTable = true;
        } catch (SQLException e) {
            if (!GenericMethods.doesTableExistsFromError(e)) GenericMethods.printStackTrace(log, e, this.getClass());
        }
        return createdTable;
    }

    void dropTable(String table) {
        try {
            getStatement().execute("DROP TABLE " + table);
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
    }

    PreparedStatement prepareStatement(String sqlStatement) {
        try {
            return connection.prepareStatement(sqlStatement);
        } catch (SQLException e) {
            GenericMethods.printStackTrace(log, e, this.getClass());
        }
        log.warning("Failed to create prepared statement! \"" + sqlStatement + "\".");
        return null;
    }
}
