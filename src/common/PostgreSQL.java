package common;

import java.sql.*;

public class PostgreSQL {
    private static final String user = System.getenv("PG_USER");
    private static final String password = System.getenv("PG_PASSWORD");
    private Statement stmt;

    public void createDb() {
        final String url = "jdbc:postgresql://localhost/";
        try {
            connectAndCreateStmt(url);
            executeUpdate("DROP DATABASE openadvisor");
            executeUpdate("CREATE DATABASE openadvisor");
            close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void connectAndCreateStmt(String url) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        stmt = connection.createStatement();
    }
    
    public void connect() {
        final String url = "jdbc:postgresql://localhost/openadvisor";
        try {
            connectAndCreateStmt(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        try {
            resultSet = stmt.executeQuery(query);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return resultSet;
    }

    public void printResultSet(ResultSet resultSet){
        try {
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsNumber; i++) {
                    if (i > 1) System.out.print(",  ");
                    String columnValue = resultSet.getString(i);
                    System.out.print(columnValue + " " + rsmd.getColumnName(i));
                }
                System.out.println();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void executeUpdate(String query) {
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static void deleteTable(String tableName) {
        PostgreSQL postgres = new PostgreSQL();
        postgres.connect();
        postgres.executeUpdate("DROP TABLE IF EXISTS " + tableName + ";");
        postgres.close();
    }
    
}
