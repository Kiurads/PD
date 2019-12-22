package model.server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
    private InetAddress address;
    private int port;
    private Socket socket;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;

    public Server(String address, int port) {
        try {
            this.address = InetAddress.getByName(address);
            this.port = port;

            socketIn = null;
            socketOut = null;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public boolean connect() {
        try {
            socket = new Socket(address, port);
            socket.setSoTimeout(10 * 1000);

            socketOut = new ObjectOutputStream(socket.getOutputStream());
            socketIn = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public void sendMessage(String message) throws IOException {
        socketOut.writeObject(message);
        socketOut.flush();
    }

    public String receiveMessage() throws IOException, ClassNotFoundException {
        return (String) socketIn.readObject();
    }

    public void upload(File file) {

    }

    public boolean registerUser(String username, String password) { //TODO
        return false;
    }

    @Override
    public String toString() {
        return "Server address:" + address + '\n' +
                "Server port: " + port;
    }
}
