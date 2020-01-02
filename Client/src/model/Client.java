package model;

import model.constants.MessageTypes;
import model.server.Proxy;
import model.server.Server;

import java.io.IOException;
import java.net.InetAddress;

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
}
