import ServerCommunication.Constants.Constants;
import ServerCommunication.Proxy;
import ServerCommunication.Server;

import java.io.*;
import java.net.*;

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
}
