package com.maddogten.mtrack.Database;

import java.sql.*;
import java.util.logging.Logger;

public class QueryDB {
    private static final String SQL_STATEMENT = "select * from channels";
    private final Logger log = Logger.getLogger(QueryDB.class.getName());

    public static void QueryDB() throws SQLException {
        Connection connection = DriverManager.getConnection(CreateDB.JDBC_URL);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(SQL_STATEMENT);
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        for (int x = 1; x <= columnCount; x++) {
            System.out.format("%20s", resultSetMetaData.getColumnName(x) + " | ");
        }
        while (resultSet.next()) {
            System.out.println("");
            for (int x = 1; x <= columnCount; x++) System.out.format("%20s", resultSet.getString(x) + " | ");
        }
        if (statement != null) statement.close();
        if (connection != null) connection.close();
    }
}
