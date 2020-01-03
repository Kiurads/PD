package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private Statement statement;
    private String query;
    private ResultSet result;
    private DatabaseConnector db;

    public Database() throws SQLException, ClassNotFoundException {
        db = new DatabaseConnector();
    }

    public void addUser(String name, String username, String password) throws SQLException {
        query = "insert into user (fullname, username, password) " +
                "values ('" + name + "','" + username + "','" + password + "')";

        statement = db.getConnection().createStatement();

        statement.executeUpdate(query);
        statement.close();
    }

    public int findUser(String username, String password) throws SQLException {
        query = "select id, username, password from user";

        statement = db.getConnection().createStatement();

        ResultSet result = statement.executeQuery(query);

        while (result.next()) {
            int id = Integer.parseInt(result.getString("id"));
            String queryUsername = result.getString("username");
            String queryPassword = result.getString("password");

            if (username.equals(queryUsername) && password.equals(queryPassword)) {
                System.out.println("[Database] User " + username + " found");

                statement.close();
                return id;
            }
        }

        statement.close();
        return -1;
    }

    public String getSongs(int user_id) throws SQLException {
        StringBuilder songList;

        query = "select name, artist, album, year, length, genre, filelocation " +
                "from Song ";

        if (user_id > 0)
            query += "where user_id= " + user_id;

        statement = db.getConnection().createStatement();

        result = statement.executeQuery(query);

        songList = new StringBuilder(result.getFetchSize() + "\n");

        while (result.next()) {
            songList.append(result.getString("name")).append("\n")
                    .append(result.getString("artist")).append("\n")
                    .append(result.getString("album")).append("\n")
                    .append(result.getString("year")).append("\n")
                    .append(result.getString("length")).append("\n")
                    .append(result.getString("genre")).append("\n")
                    .append(result.getString("filelocation")).append("\n");
        }

        statement.close();
        return songList.toString();
    }

    public String getPlaylists(int userId) throws SQLException {
        StringBuilder playlists = new StringBuilder();
        int count = 0;

        query = "select id, name " +
                "from playlist ";

        if (userId > 0)
            query += "where user_id=" + userId;

        statement = db.getConnection().createStatement();

        result = statement.executeQuery(query);

        while (result.next()) {
            count++;
            playlists.append(result.getInt("id")).append(" - ").append(result.getString("name")).append("\n");
        }

        playlists.insert(0, count + "\n");

        statement.close();
        return playlists.toString();
    }

    public boolean addPlaylist(String name, int userId) throws SQLException {
        query = "insert into playlist (name, user_id)" +
                " values ('" + name + "'," + userId + ")";

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        statement.close();
        return result > 0;
    }

    public void removePlaylist(int id) throws SQLException {
        query = "delete from playlist_has_song where playlist_id=" + id;

        statement = db.getConnection().createStatement();

        try {
            statement.executeUpdate(query);
        } catch (SQLException ignored) {

        }

        query = "delete from playlist where id = " + id;

        statement.executeUpdate(query);
        statement.close();
    }

    public void addSong(int user_id, String name, String artist, String album, int year, String genre, int length, String filelocation) throws SQLException {
        query = "insert into song(name, artist, album, year, length, genre, filelocation, user_id) " +
                "values('" + name + "', '" + artist + "', " + "'" + album + "', " + year + ", " + length + ", '" + genre + "', '" + filelocation + "', " + user_id + ")";

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        statement.close();
    }

    public boolean removeSong(int song_id) throws SQLException {
        query = "delete from song where id=" + song_id + " " +
                "delete from playlist_has_song where song_id=" + song_id;

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        statement.close();
        return result > 0;
    }


    public boolean updateSong(int id, String name, String artist, String album, int year, int length, String genre, String filelocation) throws SQLException {
        query = "update song " +
                "set name ='" + name + "', artist ='" + artist + "', album ='" + album + "', " +
                "year = " + year + ", length = " + length + ", genre = '" + genre + "', filelocation = '" + genre +
                "' where id = " + id;

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        statement.close();
        return result > 0;
    }

    public boolean addSongToPlaylist(int songId, int playlistId) throws SQLException {
        query = "insert into playlist_has_song(playlist_id, song_id)" +
                " values(" + playlistId + "," + songId + ")";

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        statement.close();
        return result > 0;
    }

    public boolean removeSongFromPlaylist(int songID, int playlistId) throws SQLException {
        query = "delete from playlist_has_song " +
                "where song_id=" + songID + " and playlist_id=" + playlistId;

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        statement.close();
        return result > 0;
    }

    public void close() throws SQLException {
        db.close();
    }
}
