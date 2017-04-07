package com.maddogten.mtrack.Database;

import com.maddogten.mtrack.util.DatabaseStrings;
import com.maddogten.mtrack.util.GenericMethods;

import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

public class DatabaseManager {
    private final Logger log = Logger.getLogger(DatabaseManager.class.getName());

    private final Connection connection;

    public DatabaseManager(String databaseLocation, boolean shouldCreateDDB) throws SQLException {
        connection = this.getConnection(databaseLocation, shouldCreateDDB);

        if (shouldCreateDDB) initDatabase();
    }

    public Connection getDatabaseConnection() {
        return connection;
    }

    // This will get the connection to the database, created one if necessary.
    private Connection getConnection(String databaseLocation, boolean createDB) {
        System.setProperty("derby.system.home", databaseLocation);

        Connection connection = null;
        String URL = "jdbc:derby:" + DatabaseStrings.DBFolderName;
        Properties properties = new Properties();
        if (createDB) properties.put("create", "true");
        properties.put("user", "dbuser");
        properties.put("password", "dbuserpwd");
        try {
            Class.forName(DatabaseStrings.DRIVER);
            connection = DriverManager.getConnection(URL, properties);
        } catch (SQLException | ClassNotFoundException e) {
            GenericMethods.printStackTrace(log, e, DatabaseManager.class);
        }
        return connection;
    }

    private void initDatabase() throws SQLException {
        Statement statement = connection.createStatement();
        createShowTables(statement);
        statement.close();
    }

    private void createShowTables(Statement statement) throws SQLException {
        statement.execute("CREATE TABLE " + DatabaseStrings.ShowsTable + "(" + DatabaseStrings.NameField + " VARCHAR(20))");
    }

    ResultSet executeQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        statement.close();
        return resultSet;
    }

    void execute(String execute) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(execute);
        statement.close();
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

}
