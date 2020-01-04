package model.server;

import model.constants.MessageTypes;
import model.server.Constants.Constants;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server implements Constants {
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

    public void upload(String filePath) throws IOException {
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

    public File receiveFile(String fileName) throws IOException {
        String filePath;
        File localPath;
        InputStream inputStream;
        FileOutputStream fileOutputStream;
        byte[] fileChunk = new byte[MAX_SIZE];
        int nbytes;

        localPath = new File("./Downloads");
        localPath.mkdir();

        filePath = localPath.getCanonicalPath() + File.separator + fileName;

        inputStream = socket.getInputStream();
        fileOutputStream = new FileOutputStream(filePath);

        do {
            if (((nbytes = inputStream.read(fileChunk)) > 0))
                fileOutputStream.write(fileChunk, 0, nbytes);
        } while (nbytes == MAX_SIZE);

        fileOutputStream.close();

        return new File("." + File.separator +  "Downloads" + File.separator + fileName);
    }

    public void registerUser(String registerInfo) throws IOException {
        sendMessage(registerInfo);
    }

    public void login(String loginInfo) throws IOException {
        sendMessage(loginInfo);
    }

    public void logout() throws IOException {
        sendMessage(MessageTypes.LOGOUT);
    }

    @Override
    public String toString() {
        return "Server address:" + address + '\n' +
                "Server port: " + port;
    }

    public void close() throws IOException {
        sendMessage(MessageTypes.CLOSE);
        socketIn.close();
        socketOut.close();
        socket.close();
    }

    public String getPlaylists() throws IOException, ClassNotFoundException {
        sendMessage(MessageTypes.PLAYLISTS);
        return receiveMessage();
    }

    public String getSongs() throws IOException, ClassNotFoundException {
        sendMessage(MessageTypes.SONGS);
        return receiveMessage();
    }

    public String getSongInfo(int songId) throws IOException, ClassNotFoundException {
        sendMessage(MessageTypes.PLAY_SONG + "\n" + songId);
        return receiveMessage();
    }


}
