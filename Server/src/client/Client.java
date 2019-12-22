package client;

import client.constants.Constants;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Client implements Constants {
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int userID;

    public Client() throws IOException {
        serverSocket = new ServerSocket(DEFAULT_SERVER_PORT);

        System.out.println("[Client] Waiting for client");

        socket = serverSocket.accept();

        System.out.println("[Client:" + socket.getInetAddress().getHostAddress() + "] Accepted");

        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    public String receive() throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    public void send(Object object) throws IOException {
        out.writeObject(object);
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void close() throws IOException {
        socket.close();
        serverSocket.close();
    }

    public void getFile(String filePath) {
        //TODO
    }
}
