package ServerCommunication;

import ServerCommunication.Constants.Constants;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Proxy implements Constants {
    private DatagramSocket socket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private ByteArrayOutputStream bOut;
    private ObjectOutputStream out;

    public Proxy(InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT * 1000);

        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        sendPacket = new DatagramPacket(bOut.toByteArray(), 0, bOut.size());
        sendPacket.setAddress(address);
        sendPacket.setPort(DEFAULT_DS_PORT);

        receivePacket = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
    }

    public Server getNewServer() throws IOException, ClassNotFoundException {
        String result;

        send(REQUEST_SERVER);

        result = receive();

        if (result.equalsIgnoreCase(NO_SERVERS)) throw new IOException(result);

        Scanner scanner = new Scanner(result);
        String serverAddress = scanner.nextLine();
        Integer serverPort = Integer.parseInt(scanner.nextLine());

        return new Server(serverAddress, serverPort);
    }

    private void send(String message) throws IOException {
        out.writeObject(message);
        out.flush();

        sendPacket.setData(bOut.toByteArray(), 0, bOut.size());
        socket.send(sendPacket);
    }

    private String receive() throws IOException, ClassNotFoundException {
        ObjectInputStream in;

        socket.receive(receivePacket);
        in = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData(), 0, receivePacket.getLength()));

        return (String) in.readObject();
    }
}
