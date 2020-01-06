package model.database;

import UI.controllers.Controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static model.database.Queries.*;

public class DatabaseConnector {
    private Connection connection;
    public static final String USERNAME = "root";
    public static final String PASSWORD = "root";
    public static final String MAIN_CONNECTION = "jdbc:mysql://localhost:3306/";
    public static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/PD_Streamer?allowMultipleQueries=true";

    public DatabaseConnector(Controller controller) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        createDatabase();

        connection = DriverManager.getConnection(DB_CONNECTION, USERNAME, PASSWORD);
        controller.addText("[Database] Connection established");
    }

    public void createDatabase() throws SQLException {
        connection = DriverManager.getConnection(MAIN_CONNECTION, USERNAME, PASSWORD);
        Statement statement = connection.createStatement();
        statement.executeUpdate(DATABASE_CREATION);

        connection = DriverManager.getConnection(DB_CONNECTION, USERNAME, PASSWORD);
        statement = connection.createStatement();

        statement.executeUpdate(CREATE_USER_TABLE);
        statement.executeUpdate(CREATE_SONG_TABLE);
        statement.executeUpdate(CREATE_PLAYLIST_TABLE);
        statement.executeUpdate(CREATE_PLAYLIST_SONG_TABLE);

        statement.close();
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() throws SQLException {
        connection.close();
    }
}
