import client.ClientThread;
import database.Database;
import proxy.constants.Constants;
import proxy.ProxyThread;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Server implements Constants {
    private List<ClientThread> clientThreads;
    private ProxyThread proxyThread;
    private Database database;

    public Server(InetAddress address) throws IOException, SQLException, ClassNotFoundException {
        database = new Database();
        clientThreads = new ArrayList<>();
        proxyThread = new ProxyThread(address, clientThreads, database);

        proxyThread.start();
    }

    public void start() throws InterruptedException, SQLException {
        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equalsIgnoreCase("EXIT")) {
            input = scanner.nextLine();
        }

        database.close();
        proxyThread.terminate();

        for (ClientThread thread : clientThreads) {
            try {
                thread.terminate();
            } catch (IOException e) {
                e.printStackTrace();
            }

            thread.join();
        }
    }
}
