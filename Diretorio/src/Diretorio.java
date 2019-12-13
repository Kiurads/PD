import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class Diretorio implements MyMessages {
    public static final int MAX_SIZE = 256;
    private byte[] defaultBArray = new byte[MAX_SIZE];
    private DatagramSocket socket;
    private DatagramPacket pkt;
    private List<ServerData> serverAddrs;
    private List<ClientData> clntData;
    static int currentServer = 0;

    public Diretorio() throws SocketException {
        socket = new DatagramSocket(DEFAULT_DS_PORT);
        serverAddrs = new ArrayList<>();
        clntData = new ArrayList<>();
    }

    public String waitDatagram() throws IOException, ClassNotFoundException {
        String request;
        ObjectInputStream in;

        if (socket == null)
            return null;

        pkt = new DatagramPacket(defaultBArray, MAX_SIZE);
        socket.receive(pkt);

        in = new ObjectInputStream(new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength()));

        request = (String) in.readObject();

        if (request != null)
            System.out.println("Novo Pedido Recebido: " + request);

        return request;
    }

    public void answerRequest() throws IOException, ClassNotFoundException {
        String receivedMsg, reply;
        InetAddress newServerAddr, newClientAddr;
        int newServerPort, newClientPort;
        ByteArrayOutputStream bOut = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;

        while (true) {
            receivedMsg = waitDatagram();

            if (receivedMsg == null)
                break;

            switch (receivedMsg) {
                case CONNECT_REQUEST:
                    newServerAddr = pkt.getAddress();
                    newServerPort = pkt.getPort();

                    bOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(bOut);

                    out.writeObject(CONNECT_CONFIRM);
                    out.flush();

                    pkt = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), newServerAddr, newServerPort);
                    socket.send(pkt);

                    serverAddrs.add(new ServerData(newServerAddr, DEFAULT_SERVER_PORT));
                    System.out.println("New Server added successfully!");
                    break;

                case REQUEST_SERVER:

                    newClientAddr = pkt.getAddress();
                    newClientPort = pkt.getPort();

                    String clientReply = getServerDetails(serverAddrs.get(currentServer).getAddr(),
                            serverAddrs.get(currentServer).getPort());

                    if (currentServer > serverAddrs.size() - 1)
                        currentServer = 0;

                    bOut = new ByteArrayOutputStream();
                    out = new ObjectOutputStream(bOut);

                    out.writeObject(clientReply);
                    out.flush();

                    pkt = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), newClientAddr, newClientPort);
                    socket.send(pkt);

                    clntData.add(new ClientData(newClientAddr, newClientPort));
                    System.out.println("New Client added successfully!");
                    break;

                default:
                    continue;
            }
        }

    }

    public void connectClient() {

    }

    public static String getServerDetails(InetAddress addr, int port) {
        currentServer++;
        return CONNECT_CONFIRM + "\n"
                + addr.getHostAddress() + "\n"
                + port;
    }

    public static void main(String[] args) {

        try {
            Diretorio dir = new Diretorio();

            dir.answerRequest();


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
