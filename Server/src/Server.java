import ProxyCommunication.Constants.Constants;
import ProxyCommunication.Proxy;

import java.io.*;
import java.net.*;
import java.util.List;


public class Server implements Constants {
    private List<ServerSocket> mySockets;
    private List<Socket> clients;
    private Proxy proxy;

    public Server() throws IOException {
        proxy = new Proxy();
    }

    public void connectDirectory(InetAddress dsAddr) throws IOException, ClassNotFoundException {
        ObjectInputStream in;

        datagramSocket = new DatagramSocket(DEFAULT_SERVER_PORT);
        datagramSocket.setSoTimeout(TIMEOUT * 1000);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bOut);

        out.writeObject(REGISTER_SERVER);
        out.flush();

        packet = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), dsAddr, DEFAULT_DS_PORT);
        datagramSocket.send(packet);

        packet = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        datagramSocket.receive(packet);

        in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
        String dsAnswer = (String) in.readObject();

        if (dsAnswer.contains(REGISTER_SERVER_SUCCESS)) {
            System.out.println("You connected to the DS!");
            startAnswering();
        }
    }

    public void answerLogin(String loginRequest) {
        String s[] = loginRequest.split(System.getProperty("Line.seperator"));
    }

    public void startAnswering() throws IOException, ClassNotFoundException {
        ServerSocket socket = new ServerSocket(DEFAULT_SERVER_PORT);
        byte[] barray = new byte[MAX_SIZE];
        for (; ; ) {
            Socket cliSocket = socket.accept();
            cliSocket.setSoTimeout(TIMEOUT * 1000);
            OutputStream out = cliSocket.getOutputStream();
            ObjectOutputStream oOut = new ObjectOutputStream(out);
            InputStream in = cliSocket.getInputStream();
            ObjectInputStream oIn = new ObjectInputStream(in);

            String s = (String) oIn.readObject();

            if (s.equalsIgnoreCase(CONNECT_REQUEST)) {
                System.out.println(s + "request received");

                oOut.writeObject(CONNECT_CONFIRM);

            } else
                socket.close();
        }
    }

    public void answerRegister(String registerrequest) {

    }

    public void answerRequests(String request) {

    }



}
