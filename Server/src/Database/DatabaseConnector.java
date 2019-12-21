package Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private Connection connection;
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root";
    public static final String CONNECTION = "jdbc:mysql://localhost:3306/PD_Streamer";

    public DatabaseConnector() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
    }
}
