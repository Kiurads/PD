import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;
import java.util.List;


public class ServerMain implements MyMessages {
    private int myPort, dirPort;
    private InetAddress DSaddr;
    private DatagramPacket pkt;
    private DatagramSocket dSocket;
    private List<ServerSocket> mySockets;
    private List<Socket> clients;

    public ServerMain() throws SocketException {
        dSocket = new DatagramSocket();
        myPort = DEFAULT_DS_PORT;
    }

    public List<Socket> getClients() {
        return clients;
    }

    public void setClients(List<Socket> clients) {
        this.clients = clients;
    }

    public int getMyPort() {
        return myPort;
    }

    public void setMyPort(int myPort) {
        this.myPort = myPort;
    }

    public int getDirPort() {
        return dirPort;
    }

    public void setDirPort(int dirPort) {
        this.dirPort = dirPort;
    }

    public InetAddress getDSaddr() {
        return DSaddr;
    }

    public void setDSAddr(InetAddress myAddr) {
        this.DSaddr = myAddr;
    }

    public void connectDirectory(InetAddress dsAddr) throws IOException, ClassNotFoundException {
        ObjectInputStream in;

        dSocket = new DatagramSocket(DEFAULT_SERVER_PORT);
        dSocket.setSoTimeout(TIMEOUT * 1000);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bOut);

        out.writeObject(CONNECT_REQUEST);
        out.flush();

        pkt = new DatagramPacket(bOut.toByteArray(), 0, bOut.size(), dsAddr, DEFAULT_DS_PORT);
        dSocket.send(pkt);

        pkt = new DatagramPacket(new byte[MAX_SIZE], MAX_SIZE);
        dSocket.receive(pkt);

        in = new ObjectInputStream(new ByteArrayInputStream(pkt.getData(), 0, pkt.getLength()));
        String dsAnswer = (String) in.readObject();

        if (dsAnswer.contains(CONNECT_CONFIRM)) {
            System.out.println("You connected to the DS!");
            startAnswering();
        } else if (dsAnswer.contains(CONNECT_FAILED))
            System.out.println("Well, at least you tried!");
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

    public static void main(String[] args) {
        Database db = null;
        ServerMain server = null;

        /*if(args.length != 2) {
            System.out.println("Syntax Error: <DS_IP> <DB_IP>");
            return;
        }*/

        try {
            server = new ServerMain();
            server.setDSAddr(InetAddress.getByName("localhost"));

            db = new Database();

            System.out.println("Connection to Database Successful");

            server.connectDirectory(server.getDSaddr());


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            System.out.println("Ocorreu um erro a aceder ao socket");
            // e.printStackTrace();
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException)
                System.out.println("Erro de timeout no acesso ao Socket ");
            else
                System.out.println("Erro de E/S");
        } finally {
            if (server.getClients() != null)
                for (int i = 0; i < server.getClients().size(); i++) {
                    try {
                        server.getClients().get(i).close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

}
