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
        query = "select id " +
                "from User " +
                "where username = '" + username + "' and password = '" + password + "'";

        statement = db.getConnection().createStatement();

        result = statement.executeQuery(query);

        result.next();

        int id = Integer.parseInt(result.getString("id"));

        statement.close();
        return id;
    }

    public String getSong(int songId) throws SQLException {
        StringBuilder songInfo = new StringBuilder();

        query = "select name, artist, album, year, genre, filelocation " +
                "from Song " +
                "where id = " + songId;

        statement = db.getConnection().createStatement();

        result = statement.executeQuery(query);

        result.next();

        songInfo.append(result.getString("name")).append("\n")
                .append(result.getString("artist")).append("\n")
                .append(result.getString("album")).append("\n")
                .append(result.getString("year")).append("\n")
                .append(result.getString("genre")).append("\n")
                .append(result.getString("filelocation"));

        statement.close();
        return songInfo.toString();
    }

    public String getSongs(int user_id) throws SQLException {
        StringBuilder songList = new StringBuilder();
        int count = 0;

        query = "select id, name " +
                "from song " +
                "order by id";

        if (user_id > 0)
            query += "where user_id= " + user_id;

        statement = db.getConnection().createStatement();

        result = statement.executeQuery(query);

        while (result.next()) {
            count++;
            songList.append(result.getInt("id")).append(" - ").append(result.getString("name")).append("\n");
        }

        songList.insert(0, count + "\n");

        statement.close();
        return songList.toString();
    }

    public String getPlaylists(int userId) throws SQLException {
        StringBuilder playlists = new StringBuilder();
        int count = 0;

        query = "select id, name " +
                "from playlist " +
                "where user_id=" + userId +
                " order by id ";

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

    public void addPlaylist(String name, int userId) throws SQLException {
        query = "insert into playlist (name, user_id)" +
                " values ('" + name + "'," + userId + ")";

        statement = db.getConnection().createStatement();
        statement.executeUpdate(query);
        statement.close();
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
                "year = " + year + ", length = " + length + ", genre = '" + genre +
                "' where id = " + id;

        statement = db.getConnection().createStatement();

        int result = statement.executeUpdate(query);

        statement.close();
        return result > 0;
    }

    public void addSongToPlaylist(int playlistId, int songId) throws SQLException {
        query = "insert into playlist_has_song(playlist_id, song_id)" +
                " values(" + playlistId + "," + songId + ")";

        statement = db.getConnection().createStatement();

        statement.executeUpdate(query);
        statement.close();
    }

    public String getSongsFromPlaylist(int playlistId) throws SQLException {
        StringBuilder songList = new StringBuilder();
        int count = 0;
        query = "select s.id, s.name\n" +
                "from song as s \n" +
                "inner join playlist_has_song as ph \n" +
                "on ph.Song_id = s.id\n" +
                "inner join playlist as p \n" +
                "on p.id = ph.Playlist_id\n" +
                "where p.id = " + playlistId +
                " order by s.id";

        statement = db.getConnection().createStatement();

        result = statement.executeQuery(query);

        while (result.next()) {
            count++;
            songList.append(result.getInt("id")).append(" - ").append(result.getString("name")).append("\n");
        }

        songList.insert(0, count + "\n");

        statement.close();
        return songList.toString();
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

    public void updatePlaylist(int playlistId, String playlistName) throws SQLException {
        query = "update playlist " +
                "set name ='" + playlistName +
                "' where id = " + playlistId;

        statement = db.getConnection().createStatement();
        statement.executeUpdate(query);
        statement.close();
    }
}
