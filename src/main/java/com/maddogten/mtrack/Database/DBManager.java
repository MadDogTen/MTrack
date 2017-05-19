package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.Main;
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

    public DBManager(String databaseLocation, boolean shouldCreateDDB) {
        connection = this.getConnection(databaseLocation, shouldCreateDDB);
        if (connection == null) Main.stop(null, true);
    }

    public synchronized Connection getConnection() { // TODO Make package private
        return connection;
    }

    synchronized Statement getStatement() throws SQLException {
        return connection.createStatement();
    }

    // This will get the connection to the database, created one if necessary.
    private Connection getConnection(String databaseLocation, boolean createDB) {
        System.setProperty("derby.system.home", databaseLocation);

        Connection connection = null;
        String URL = "jdbc:derby:" + StringDB.DBFolderName;
        Properties properties = new Properties();
        if (createDB) {
            properties.put("create", "true");
            log.info("Creating Database at \"" + databaseLocation + "\".");
        }
        properties.put("user", "MTrack");
        properties.put("password", "MTrackSimplePassword");
        try {
            Class.forName(StringDB.DRIVER);
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

    public synchronized void closeConnection() throws SQLException {
        if (connection != null) connection.close();
    }
}
