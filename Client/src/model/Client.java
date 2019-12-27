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

    public void register(String username, String password) {
        boolean success;

        success = server.registerUser(username, password);
    }

    public void login(String username, String password) {
        boolean success;

        success = server.registerUser(username, password);
    }

    public Server getServer() {
        return server;
    }

    public void upload(String fileName, String filePath) throws IOException {
        server.sendMessage(UPLOAD + "\n" + fileName);
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
}
