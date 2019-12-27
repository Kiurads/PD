import client.ClientThread;
import database.DatabaseConnector;
import proxy.constants.Constants;
import proxy.ProxyThread;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Server implements Constants {
    private List<ClientThread> clientThreads;
    private ProxyThread proxyThread;

    public Server(InetAddress localHost) throws IOException {
        clientThreads = new ArrayList<>();
        proxyThread = new ProxyThread(InetAddress.getLocalHost(), clientThreads);

        proxyThread.start();
    }

    public void start() throws InterruptedException {
        Scanner scanner = new Scanner(System.in);
        String input = "";

        while (!input.equalsIgnoreCase("EXIT")) {
            input = scanner.nextLine();
        }

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
