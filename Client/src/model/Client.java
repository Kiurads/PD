package model;

import model.constants.MessageTypes;
import model.server.Proxy;
import model.server.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
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

    public String receiveMessage() throws IOException, ClassNotFoundException {
        return server.receiveMessage();
    }

    public void upload(String songInfo, String filePath) throws IOException {
        server.sendMessage(songInfo);
        server.upload(filePath);
    }

    public void createPlaylist(String playlistInfo) throws IOException {
        server.sendMessage(playlistInfo);
    }

    public void deletePlaylist(int playlistId) throws IOException {
        server.sendMessage(MessageTypes.PLAYLIST_DELETE + "\n" + playlistId);
    }

    public void resetDetails() {
        username = password = null;
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

    public void setDetails(String loginDetails) {
        String[] info = loginDetails.split("\n");

        username = info[0];
        password = info[1];
    }

    public String getPlaylistNames() throws IOException, ClassNotFoundException {
        server.getPlaylists();
        return receiveMessage();
    }
}
