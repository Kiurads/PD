package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DataBase {
    Statement statement;
    String query;
    ResultSet result;
    DatabaseConnector db;

    public DataBase(DatabaseConnector db) throws SQLException {
        this.db = db;
    }

    public void addUser(String user, String pass, String fullname) throws SQLException {
        query = "insert into user (username, password, fullname) " +
                "values ('" + user + "','" + pass + "','" + fullname + "')";

        statement = db.getConnection().createStatement();

        statement.executeUpdate(query);
        statement.close();
    }

    public int findUser(String username, String password) throws SQLException {
        Statement statement = null;
        String sql = "SELECT id, username, password FROM user";

        statement = db.getConnection().createStatement();

        ResultSet result = statement.executeQuery(sql);

        while (result.next()) {
            int id = Integer.parseInt(result.getString("id"));
            String qUsername = result.getString("username");
            String qPassword = result.getString("password");

            if (username.equals(qUsername) && password.equals(qPassword)) {
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

    public boolean addSong(int user_id, String name, String artist, String album, int year, int length, String genre, String filelocation) throws SQLException {
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

    public static void main(String[] args) {
        try {
            DatabaseConnector connector = new DatabaseConnector();
            DataBase db = new DataBase(connector);

            db.addUser("Kiurads", "qwerty123", "Guilherme Silva");

            //db.findUser("Kiurads", "qwerty123");

            //String songs = db.getSongs(1);

            //String playlists = db.getPlaylists(1);

            //System.out.print(songs);
            //System.out.print(playlists);

            System.out.println(db.addPlaylist("Portuguesices", 1));
            System.out.println(db.addPlaylist("Meu nome é Gabs", 1));
            //System.out.println(db.deletePlaylist(1));

            System.out.println(db.addSong(1, "name", "artist", "album", 2020, 600, "genre", "filelocation"));
            System.out.println(db.updateSong(1, "This Love", "Maroon 5","Songs About Jane", 2002, 306, "Pop", "./Uploads"));


            System.out.println(db.addSongToPlaylist(1, 1 ));
            System.out.println(db.addSongToPlaylist(1, 2));
            //System.out.println(db.removeSong(1));

            //System.out.println(db.removePlaylist(3));

            //System.out.println(db.removeSongFromPlaylist(1, 1));

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
