package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    Statement statement;
    String query;
    ResultSet result;
    DatabaseConnector db;

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
        String sql = "SELECT id, username, password FROM user";

        statement = db.getConnection().createStatement();

        ResultSet result = statement.executeQuery(sql);

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
        String songList;

        query = "select name, artist, album, year, length, genre, filelocation " +
                "from Song ";

        if (user_id > 0)
            query += "where user_id= " + user_id;

        statement = db.getConnection().createStatement();

        result = statement.executeQuery(query);

        songList = result.getFetchSize() + "\n";

        while (result.next()) {
            songList += result.getString("name") + "\n"
                    + result.getString("artist") + "\n"
                    + result.getString("album") + "\n"
                    + result.getString("year") + "\n"
                    + result.getString("length") + "\n"
                    + result.getString("genre") + "\n"
                    + result.getString("filelocation") + "\n";
        }

        statement.close();

        return songList;
    }

    public String getPlaylists(int user_id) throws SQLException {
        String playlists = "";
        int count = 0;

        query = "select name " +
                "from playlist ";

        if (user_id > 0)
            query += "where user_id=" + user_id;

        statement = db.getConnection().createStatement();

        result = statement.executeQuery(query);

        while (result.next()) {
            count++;
            playlists += result.getString("name");
        }

        playlists = count + "\n" + playlists;

        statement.close();

        return playlists;
    }

    public boolean addPlaylist(String name, int userId) throws SQLException {
        query = "insert into playlist (name, user_id)" +
                " values ('" + name + "'," + userId + ")";

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        if (result > 0) {
            statement.close();
            return true;
        }
        statement.close();
        return false;
    }

    public boolean removePlaylist(int id) throws SQLException {
        query = "delete from playlist_has_song where playlist_id=" + id + "; " +
                "delete from playlist where id = " + id;

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        if (result > 0) {
            statement.close();
            return true;
        }

        statement.close();
        return false;
    }

    public boolean addSong(int user_id, String name, String artist, String album, int year, String genre, int length, String filelocation) throws SQLException {
        query = "insert into song(name, artist, album, year, length, genre, filelocation, user_id) " +
                "values('" + name + "', '" + artist + "', " + "'" + album + "', " + year + ", " + length + ", '" + genre + "', '" + filelocation + "', " + user_id + ")";

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        if (result > 0) {
            statement.close();
            return true;
        }

        statement.close();
        return false;
    }

    public boolean removeSong(int song_id) throws SQLException {
        query = "delete from song where id=" + song_id + " " +
                "delete from playlist_has_song where song_id=" + song_id;

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        if (result > 0) {
            statement.close();
            return true;
        }

        statement.close();
        return false;
    }


    public boolean updateSong(int id, String name, String artist, String album, int year, int length, String genre, String filelocation) throws SQLException {
        query = "update song " +
                "set name ='" + name + "', artist ='" + artist + "', album ='" + album + "', " +
                "year = " + year + ", length = " + length + ", genre = '" + genre + "', filelocation = '" + genre +
                "' where id = " + id;

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        if (result > 0) {
            statement.close();
            return true;
        }
        statement.close();
        return false;
    }

    public boolean addSongToPlaylist(int songId, int playlistId) throws SQLException {
        query = "insert into playlist_has_song(playlist_id, song_id)" +
                " values(" + playlistId + "," + songId + ")";

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        if (result > 0) {
            statement.close();
            return true;
        }

        statement.close();
        return false;
    }

    public boolean removeSongFromPlaylist(int songID, int playlistId) throws SQLException {
        query = "delete from playlist_has_song " +
                "where song_id=" + songID + " and playlist_id=" + playlistId;

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        if (result > 0) {
            statement.close();
            return true;
        }

        statement.close();
        return false;
    }

    public void close() throws SQLException {
        db.close();
    }
}
