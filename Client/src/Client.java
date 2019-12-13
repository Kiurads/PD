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

    public void getMyServer(InetAddress dsAddr) throws UnknownHostException, SocketException, IOException {
        DatagramSocket socket = null;
        DatagramPacket pkt = null;
        byte[] barray = new byte[MAX_SIZE];
        String dsAnswer;
        ByteArrayOutputStream bout;
        ObjectOutputStream out;

        socket = new DatagramSocket();

        socket.setSoTimeout(TIMEOUT * 1000);

        pkt = new DatagramPacket(REQUEST_SERVER.getBytes(), REQUEST_SERVER.length(),
                dsAddr, DEFAULT_DS_PORT);
        socket.send(pkt);

        pkt = new DatagramPacket(barray, barray.length);
        socket.receive(pkt);

        dsAnswer = new String(barray);

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

        serverIP = InetAddress.getByName(sc.nextLine());
        serverPort = Integer.parseInt(sc.nextLine());

    }

    public void connectToMyServer() {

        try {
            socketToServer = new Socket(serverIP, serverPort);
            socketToServer.setSoTimeout(TIMEOUT * 1000);
            out = new ObjectOutputStream(socketToServer.getOutputStream());
            in = new ObjectInputStream(socketToServer.getInputStream());

            out.writeObject(CONNECT_REQUEST);

            String s = (String) in.readObject();

            if (s.compareToIgnoreCase(CONNECT_CONFIRM) != 0)
                throw new IOException();

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

            while(true){

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
