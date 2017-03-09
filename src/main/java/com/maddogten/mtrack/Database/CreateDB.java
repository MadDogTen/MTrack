package com.maddogten.mtrack.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class CreateDB {
    static final String JDBC_URL = "jdbc:derby:zadb;create=true";
    private static final Logger log = Logger.getLogger(CreateDB.class.getName());
    private static final String DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

    public static void createDB() throws ClassNotFoundException, SQLException {
        Class.forName(DRIVER);
        Connection connection = DriverManager.getConnection(JDBC_URL);
        connection.createStatement().execute("create table channels(channel varchar(20), videoclip varchar(20))");
        connection.createStatement().execute("insert into channels values " +
                "('oodp', 'singleton'), " +
                "('oodp', 'factory method'), " +
                "('oodp', 'abstract factory')");
        log.info("Database was created.");
    }
}
