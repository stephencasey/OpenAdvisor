package common;

import javax.xml.transform.Result;
import java.sql.*;

public class PostgreSQL {
    private final String user = System.getenv("PG_USER");
    private final String password = System.getenv("PG_PASSWORD");
    private Statement stmt;

    public Connection connect() {
        final String url = "jdbc:postgresql://localhost/openadvisor";

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            stmt = connection.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    public void createDb() {
        final String url = "jdbc:postgresql://localhost/";
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
            stmt = connection.createStatement();
            executeUpdate("DROP DATABASE openadvisor");
            executeUpdate("CREATE DATABASE openadvisor");
            close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void close() {
        try {
            stmt.close();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        try {
            resultSet = stmt.executeQuery(query);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
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
                System.out.println("");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void executeUpdate(String query) {
        try {
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void getDatabaseMetadata(){
        try {
            DatabaseMetaData metadata = stmt.getConnection().getMetaData();
            ResultSet columns = metadata.getColumns(null,null, "schools", null);
            while(columns.next())
            {
                String columnName = columns.getString("COLUMN_NAME");
                String datatype = columns.getString("DATA_TYPE");
                String columnsize = columns.getString("COLUMN_SIZE");
                String decimaldigits = columns.getString("DECIMAL_DIGITS");
                String isNullable = columns.getString("IS_NULLABLE");
                String is_autoIncrment = columns.getString("IS_AUTOINCREMENT");
                System.out.println(columnName + "---" + datatype + "---" + columnsize + "---" + decimaldigits + "---" + isNullable + "---" + is_autoIncrment);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
