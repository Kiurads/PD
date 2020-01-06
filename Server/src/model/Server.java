package model;

import UI.controllers.Controller;
import model.client.ClientThread;
import model.database.Database;
import model.proxy.ProxyThread;
import model.proxy.constants.Constants;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Server implements Constants {
    private List<ClientThread> clientThreads;
    private ProxyThread proxyThread;
    private Database database;

    public Server(InetAddress address, Controller controller) throws SQLException, ClassNotFoundException, IOException {
        database = new Database(controller);
        clientThreads = new ArrayList<>();
        proxyThread = new ProxyThread(address, clientThreads, database, controller);

        proxyThread.start();
    }

    public void stop() throws SQLException, InterruptedException {
        database.close();

        try {
            proxyThread.terminate();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (ClientThread thread : clientThreads) {
            thread.terminate();

            thread.join();
        }
    }
}
