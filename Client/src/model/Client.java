package model;

import model.server.Constants.Constants;
import model.server.Proxy;
import model.server.Server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class Client implements Constants {
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

    public void upload(File file) {
        server.upload(file);
    }
}
