package ServerCommunication;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
    private InetAddress address;
    private Integer port;
    private Socket socket;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;

    public Server(String address, Integer port) {
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

            socketIn = new ObjectInputStream(socket.getInputStream());
            socketOut = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public boolean sendMessage(String message) {
        try {
            socketOut.writeObject(message);
            socketOut.flush();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public String receiveMessage() {
        String reply;

        try {
            reply = (String) socketIn.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return "";
        }

        return reply;
    }

    public boolean registerUser(String username, String password) { //TODO
        return false;
    }
}
