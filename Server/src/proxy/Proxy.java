package proxy;

import proxy.constants.Constants;

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

    public Proxy(InetAddress address) throws IOException {
        socket = new DatagramSocket();
        socket.setSoTimeout(10 * 1000);

        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        sendPacket = new DatagramPacket(bOut.toByteArray(), 0, bOut.size());
        sendPacket.setAddress(address);
        sendPacket.setPort(DEFAULT_DS_PORT);

        receivePacket = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
    }

    public void register() throws IOException, ClassNotFoundException {
        String reply;

        send(REGISTER_SERVER);
        reply = receive();

        if (reply.contains(REGISTER_SERVER_SUCCESS)) System.out.println("[Proxy] Server registered");
    }

    public void remove() throws IOException {
        send(REMOVE_SERVER);
        socket.close();
    }

    public void send(String message) throws IOException {
        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        out.writeObject(message);
        out.flush();

        sendPacket.setData(bOut.toByteArray(), 0, bOut.size());
        socket.send(sendPacket);
    }

    public String receive() throws ClassNotFoundException, IOException {
        ObjectInputStream in;

        socket.receive(receivePacket);
        in = new ObjectInputStream(new ByteArrayInputStream(receivePacket.getData(), 0, receivePacket.getLength()));

        System.out.println("[Proxy] Message received");

        return (String) in.readObject();
    }
}
