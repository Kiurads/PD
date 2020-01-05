package model;

import model.constants.MessageTypes;
import model.data.Song;
import model.server.Proxy;
import model.server.Server;
import model.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Client implements MessageTypes {
    private String username;
    private String password;
    private Proxy proxy;
    private Server server;

    public Client(String proxyAddress) throws IOException, ClassNotFoundException {
        proxy = new Proxy(InetAddress.getByName(proxyAddress));
        server = proxy.getNewServer();

        server.connect();

        username = password = null;
    }

    public void register(String registerInfo) throws IOException {
        server.registerUser(registerInfo);
    }

    public void login(String loginInfo) throws IOException {
        server.login(loginInfo);
    }

    public void logout() throws IOException {
        server.logout();
    }

    public void upload(String songInfo, String filePath) throws IOException {
        server.sendMessage(songInfo);
        server.upload(filePath);
    }

    public String getSongs() throws IOException, ClassNotFoundException {
        return server.getSongs();
    }

    public String getPlaylistNames() throws IOException, ClassNotFoundException {
        return server.getPlaylists();
    }

    public void createOrEditPlaylist(String playlistInfo) throws IOException {
        server.sendMessage(playlistInfo);
    }

    public void deletePlaylist(int playlistId) throws IOException {
        server.sendMessage(MessageTypes.PLAYLIST_DELETE + "\n" + playlistId);
    }

    public void addToPlaylist(int playlistId, int songId) throws IOException {
        server.sendMessage(MessageTypes.PLAYLIST_ADD + "\n" + playlistId + "\n" + songId);
    }

    public void removeFromPlaylist(int playlistId, int songId) throws IOException {
        server.sendMessage(MessageTypes.PLAYLIST_REMOVE + "\n" + playlistId + "\n" + songId);
    }

    public String getSongsInPlaylist(int playlistId) throws IOException, ClassNotFoundException {
        return server.getSongsInPlaylist(playlistId);
    }

    public String requestPlaylist(int playlistId) throws IOException, ClassNotFoundException {
        return server.requestPlaylist(playlistId);
    }

    public List<Song> loadPlaylist(int songs) throws IOException, ClassNotFoundException {
        List<Song> songList = new ArrayList<>();

        for (int i = 0; i < songs; i++) {
            String[] message = server.receiveMessage().split("\n");
            songList.add(new Song(message));

            songList.get(i).setFile(receiveSong(message[6]));
        }

        return songList;
    }

    public String receiveMessage() throws IOException, ClassNotFoundException {
        return server.receiveMessage();
    }

    public void reconnect() throws IOException, ClassNotFoundException {
        server = proxy.getNewServer();

        server.connect();

        if ((username != null && password != null))
            server.login(MessageTypes.LOGIN + "\n" + username + "\n" + password);
    }

    public void shutdown() throws IOException {
        proxy.close();
        server.close();
    }

    public void resetDetails() {
        username = password = null;
    }

    public void setDetails(String loginDetails) {
        String[] info = loginDetails.split("\n");

        username = info[0];
        password = info[1];
    }

    public String getSongFile(int songId) throws IOException, ClassNotFoundException {
        return server.getSongFile(songId);
    }

    public File receiveSong(String filePath) throws IOException {
        return server.receiveFile(FileUtils.getFileFromPath(filePath));
    }

    public void editSong(int songId, String songDetails) throws IOException {
        server.editSong(songId, songDetails);
    }

    public String getUserSongs() throws IOException, ClassNotFoundException {
        return server.getUserSongs();
    }

    public String getSongInfo(int songId) throws IOException, ClassNotFoundException {
        return server.getSongInfo(songId);
    }
}
