import ServerCommunication.Server;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class Diretorio implements Constants {
    private static int currentServer = 0;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private List<Server> servers;

    public Diretorio() throws SocketException {
        socket = new DatagramSocket(DEFAULT_DS_PORT);
        servers = new ArrayList<>();
    }

    public String waitDatagram() throws IOException {
        String request;
        ObjectInputStream in;

        if (socket == null)
            return null;

        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        socket.receive(packet);

        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));

        try {
            request = (String) in.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("[Proxy] Invalid request received: " + e.getCause());
            return null;
        }

        if (request == null)
            return null;

        System.out.println("[Proxy] Request received: " + request);
        return request;
    }

    public void answerRequests() throws IOException {
        InetAddress packetAddress;
        int packetPort;
        ByteArrayOutputStream bOut;
        ObjectOutputStream out;
        ObjectInputStream in = null;
        String message;

        while (true) {
            message = waitDatagram();

            if (message == null)
                break;

            packetAddress = packet.getAddress();
            packetPort = packet.getPort();

            bOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bOut);

            switch (message) {
                case CONNECT_REQUEST:
                    try {
                        out.writeObject(CONNECT_CONFIRM);
                        out.flush();

                        packet = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), packetAddress, packetPort);
                        socket.send(packet);

                        servers.add(new Server(packetAddress, packetPort));
                    } catch (IOException e) {
                        break;
                    }

                    System.out.println("[Server] Server " + packetAddress.getHostAddress() + ":" + packetPort + " registered");
                    break;

                case REQUEST_SERVER:
                    String clientReply;
                    if (!servers.isEmpty()) clientReply = getServerDetails();
                    else clientReply = NO_SERVERS;

                    out.writeObject(clientReply);
                    out.flush();

                    packet = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), packetAddress, packetPort);
                    socket.send(packet);

                    System.out.println("[Client] Request has been replied with: " + clientReply);

                    break;
            }
        }

    }

    public String getServerDetails() {
        String serverDetails = servers.get(currentServer).toString();

        if (currentServer == servers.size() - 1) currentServer = 0;
        else currentServer++;

        return serverDetails;
    }
}
