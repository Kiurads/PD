package server;

import server.constants.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Server implements Constants {
    private InetAddress address;
    private int port;

    public Server(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public void request() throws IOException {
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet;
        ObjectOutputStream out;
        ByteArrayOutputStream bOut;

        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        out.writeObject(REQUEST_SERVER);
        out.flush();

        packet = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), address, port);
        socket.send(packet);

        System.out.println("[Server:" + address.getHostAddress() + "] Requesting server");
    }

    public InetAddress getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return getAddress().getHostAddress() + "\n" + DEFAULT_SERVER_PORT;
    }
}
