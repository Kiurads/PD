import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Client implements MyMessages {

    private InetAddress serverIP;
    private int serverPort;
    private Socket socketToServer;
    ObjectInputStream in = null;
    ObjectOutputStream out = null;
    boolean loggedUser = false;

    public void getMyServer(InetAddress dsAddr) throws UnknownHostException, SocketException, IOException, ClassNotFoundException {
        DatagramSocket socket = null;
        DatagramPacket pkt = null;
        byte[] barray = new byte[MAX_SIZE];
        String dsAnswer;
        ByteArrayOutputStream bOut;
        ObjectOutputStream out;

        socket = new DatagramSocket();

        socket.setSoTimeout(TIMEOUT * 1000);

        bOut = new ByteArrayOutputStream();
        out = new ObjectOutputStream(bOut);

        out.writeObject(REQUEST_SERVER);
        out.flush();

        pkt = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), dsAddr, DEFAULT_DS_PORT);
        socket.send(pkt);

        pkt = new DatagramPacket(barray, barray.length);
        socket.receive(pkt);

        in = new ObjectInputStream(new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength()));
        dsAnswer = (String) in.readObject();

        Scanner sc = new Scanner(dsAnswer);
        String msgType = sc.nextLine();

        if (msgType.equalsIgnoreCase(CONNECT_CONFIRM)) {
            System.out.println("You connected to the DS!");
        } else if (msgType.equalsIgnoreCase(CONNECT_FAILED)) {
            System.out.println("Well, at least you tried!");
            return;
        }

        String sip = sc.nextLine();
        String spo = sc.nextLine();

        serverIP = InetAddress.getByName(sip);
        serverPort = Integer.parseInt(spo);

    }

    public void connectToMyServer() {

        try {
            socketToServer = new Socket(serverIP, serverPort);
            socketToServer.setSoTimeout(TIMEOUT * 1000);
            out = new ObjectOutputStream(socketToServer.getOutputStream());
            in = new ObjectInputStream(socketToServer.getInputStream());

            out.writeObject(CONNECT_REQUEST);
            out.flush();

            String s = (String) in.readObject();
            System.out.println(s);

            if (s.compareToIgnoreCase(CONNECT_CONFIRM) != 0)
                throw new IOException();

            while(true){}

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socketToServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void tryRegister(String credenciais) {
        String serverReply;
        String registerRequest = "Register " + credenciais;

        try {
            out.writeObject(registerRequest);

            serverReply = (String) in.readObject();

            if (serverReply.compareToIgnoreCase(REGISTER_SUCCESSFUL) == 0) {
                loggedUser = false;
            } else if (serverReply.compareToIgnoreCase(REGISTER_FAILED) == 0) {

            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void tryLogin(String loginRequest) {

        String serverReply;

        try {
            out.writeObject(loginRequest);

            serverReply = (String) in.readObject();

            if (serverReply.compareToIgnoreCase(LOGIN_SUCCESSFUL) == 0) {

            } else if (serverReply.compareToIgnoreCase(LOGIN_FAILED) == 0) {

            } else
                throw new IOException();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client me = new Client();

        try {
            me.getMyServer(InetAddress.getByName("localhost"));

            me.connectToMyServer();

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
