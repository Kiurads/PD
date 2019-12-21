package ProxyCommunication;

import ProxyCommunication.Constants.Constants;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Proxy implements Constants {
    private DatagramSocket socket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private ByteArrayOutputStream bOut;
    private ObjectOutputStream out;

    public Proxy() throws IOException {
        socket = new DatagramSocket();
        socket.setSoTimeout(TIMEOUT * 1000);

        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        sendPacket = new DatagramPacket(bOut.toByteArray(), 0, bOut.size());
        sendPacket.setAddress(InetAddress.getLocalHost());
        sendPacket.setPort(DEFAULT_DS_PORT);

        receivePacket = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
    }

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
