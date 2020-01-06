package model.proxy;

import UI.controllers.Controller;
import model.client.ClientThread;
import model.database.Database;
import model.proxy.constants.Constants;
import model.proxy.constants.MessageTypes;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class ProxyThread extends Thread implements Constants {
    private Controller controller;
    private List<ClientThread> clientThreads;
    private Database database;
    private Proxy proxy;
    private boolean running;

    public ProxyThread(InetAddress proxyAddress, List<ClientThread> clientThreads, Database database, Controller controller) throws IOException {
        this.controller = controller;
        proxy = new Proxy(proxyAddress, controller);
        this.clientThreads = clientThreads;
        this.database = database;

        running = true;
    }

    public void terminate() throws IOException {
        proxy.send(MessageTypes.REMOVE_SERVER);
        running = false;
    }

    @Override
    public void run() {
        try {
            proxy.register();
        } catch (IOException | ClassNotFoundException e) {
            return;
        }

        while (running) {
            try {
                String message = proxy.receive();

                switch (message) {
                    case MessageTypes.REQUEST_SERVER:
                        controller.addText("[Proxy] model.Server requested");

                        ClientThread thread = new ClientThread(database, controller);
                        proxy.send(String.valueOf(thread.getPort()));

                        thread.start();

                        clientThreads.add(thread);
                        break;

                    case MessageTypes.REMOVE_SERVER:
                        running = false;
                        break;

                    case MessageTypes.PING:
                        proxy.send(MessageTypes.PING);
                        break;
                }
            } catch (IOException ignored) {
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        proxy.remove();
    }
}
