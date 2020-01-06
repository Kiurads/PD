package client;

import client.constants.Constants;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Client implements Constants {
    private static int clientPorts = 6000;
    private int port;
    private ServerSocket serverSocket;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private int userID;

    public Client() throws IOException {
        port = clientPorts++;
        serverSocket = new ServerSocket(port);
    }

    public void accept() throws IOException {
        socket = serverSocket.accept();

        System.out.println("[Client:" + socket.getInetAddress().getHostAddress() + "] Accepted");

        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());

        userID = -1;
    }

    public String receive() throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    public void send(Object object) throws IOException {
        out.writeObject(object);
    }

    public void sendFile(String filePath) throws IOException {
        FileInputStream fileInputStream;
        OutputStream outputStream;
        byte[] fileChunk = new byte[MAX_SIZE];
        int nbytes;

        fileInputStream = new FileInputStream(filePath);
        outputStream = socket.getOutputStream();

        while ((nbytes = fileInputStream.read(fileChunk)) > 0) {
            outputStream.write(fileChunk, 0, nbytes);
        }
        outputStream.flush();
        fileInputStream.close();
    }

    public String getFile(String fileName) throws IOException {
        if (userID == -1) return null;

        String filePath;
        File localPath;
        InputStream inputStream;
        FileOutputStream fileOutputStream;
        byte[] fileChunk = new byte[MAX_SIZE];
        int nbytes;

        localPath = new File("./Uploads");
        localPath.mkdir();

        filePath = localPath.getCanonicalPath() + File.separator + fileName;

        inputStream = socket.getInputStream();
        fileOutputStream = new FileOutputStream(filePath);

        do {
            if (((nbytes = inputStream.read(fileChunk)) > 0))
                fileOutputStream.write(fileChunk, 0, nbytes);
        } while (nbytes == MAX_SIZE);

        fileOutputStream.close();

        return "." + File.separator + File.separator + "Uploads" + File.separator + File.separator + fileName;
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

    public int getServerPort() {
        return port;
    }
}
