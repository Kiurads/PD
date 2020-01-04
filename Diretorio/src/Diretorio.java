import server.constants.Constants;
import server.Server;
import server.constants.MessageTypes;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Diretorio implements Constants {
    private int currentServer = 0;
    private DatagramSocket socket;
    private DatagramPacket packet;
    private List<Server> servers;

    public Diretorio() throws SocketException {
        socket = new DatagramSocket(DEFAULT_DS_PORT);
        socket.setSoTimeout(TIMEOUT * 1000);

        servers = new ArrayList<>();
    }

    public String waitDatagram() throws IOException {
        String message;
        ObjectInputStream in;

        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        try {
            socket.receive(packet);
        } catch (SocketTimeoutException e) {
            throw new SocketTimeoutException();
        }

        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));

        try {
            message = (String) in.readObject();
        } catch (ClassNotFoundException e) {
            System.out.println("[Proxy] Invalid request received: " + e.getCause());
            return "";
        }

        return message;
    }

    public void answerRequests() throws IOException {
        InetAddress packetAddress;
        int packetPort;
        ByteArrayOutputStream bOut;
        ObjectOutputStream out;
        String message;

        while (true) {
            pingServers();

            try {
                message = waitDatagram();
            } catch (SocketTimeoutException e) {
                message = "";
            }

            packetAddress = packet.getAddress();
            packetPort = packet.getPort();

            bOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bOut);

            switch (message) {
                case MessageTypes.REGISTER_SERVER:
                    try {
                        out.writeObject(MessageTypes.REGISTER_SERVER_SUCCESS);
                        out.flush();

                        packet = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), packetAddress, packetPort);
                        socket.send(packet);

                        servers.add(new Server(packetAddress, packetPort));
                    } catch (IOException e) {
                        break;
                    }

                    System.out.println("[Server] Server " + packetAddress.getHostAddress() + " registered");
                    break;

                case MessageTypes.REQUEST_SERVER:
                    String clientReply;
                    if (!servers.isEmpty()) {
                        servers.get(currentServer).request();
                        message = waitDatagram();

                        clientReply = servers.get(currentServer).getAddress().getHostAddress() + '\n' + message;

                        if (currentServer == servers.size() - 1) currentServer = 0;
                        else currentServer++;
                    } else
                        clientReply = MessageTypes.NO_SERVERS;

                    out.writeObject(clientReply);
                    out.flush();

                    packet = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), packetAddress, packetPort);
                    socket.send(packet);

                    System.out.println("[Client] Request has been handled");

                    break;
                case MessageTypes.REMOVE_SERVER:
                    for (int i = 0; i < servers.size(); i++) {
                        if (servers.get(i).getAddress().equals(packetAddress)) {
                            System.out.println("[Proxy] Removing server at " + packetAddress.getHostAddress());
                            if (currentServer == servers.size() - 1) currentServer = 0;
                            out.writeObject(MessageTypes.REMOVE_SERVER);
                            out.flush();

                            packet = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), packetAddress, packetPort);
                            socket.send(packet);

                            servers.remove(i);
                            break;
                        }
                    }
            }
        }

    }

    private void pingServers() throws IOException {
        for (int i = 0; i < servers.size(); i++) {
            servers.get(i).sendMessage(MessageTypes.PING);

            try {
                String reply = waitDatagram();
            } catch (SocketTimeoutException e) {
                System.out.println("[Proxy] Removing server at " + servers.get(i).getAddress().getHostAddress() + " for inactivity");
                if (currentServer == servers.size() - 1) currentServer = 0;
                servers.remove(i--);
            }
        }
    }

}
