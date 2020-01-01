package model;

import model.server.Proxy;
import model.server.Server;

import java.io.IOException;
import java.net.InetAddress;

public class Client implements MessageTypes {
    private Proxy proxy;
    private Server server;

    public Client(String proxyAddress) throws IOException, ClassNotFoundException {
        proxy = new Proxy(InetAddress.getByName(proxyAddress));
        server = proxy.getNewServer();

        server.connect();
    }

    public void register(String registerInfo) throws IOException {
        server.registerUser(registerInfo);
    }

    public void login(String username, String password) {
        server.login(username);
    }

    public Server getServer() {
        return server;
    }

    public void upload(String songInfo, String filePath) throws IOException {
        server.sendMessage(songInfo);
        server.upload(filePath);
    }

    public void reconnect() throws IOException, ClassNotFoundException {
        server = proxy.getNewServer();

        server.connect();

        //TODO If user is registered login on new server
    }

    public void shutdown() throws IOException {
        proxy.close();

        server.close();
    }

    public String receiveMessage() throws IOException, ClassNotFoundException {
        return server.receiveMessage();
    }
}
