package PD.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Observable;

public class Client extends Observable {
    InetAddress serverAddr = null;
    int serverPort = -1;
    Socket socket = null;
    ObjectInputStream in = null;
    ObjectOutputStream out = null;
}
