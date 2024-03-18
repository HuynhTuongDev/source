package control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connector {
    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;"
            + "databaseName=student_management_program;"
            + "encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "finn";
    private static final String PASSWORD = "130104";
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }
}
