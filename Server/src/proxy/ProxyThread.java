package proxy;

import client.ClientThread;
import proxy.constants.Constants;
import proxy.constants.MessageTypes;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

public class ProxyThread extends Thread implements Constants {
    List<ClientThread> clientThreads;
    Proxy proxy;
    boolean running;

    public ProxyThread(InetAddress proxyAddress, List<ClientThread> clientThreads) throws IOException {
        proxy = new Proxy(proxyAddress);
        this.clientThreads = clientThreads;

        running = true;
    }

    public void terminate() {
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
                        System.out.println("[Proxy] Server requested");

                        ClientThread thread = new ClientThread();
                        proxy.send(String.valueOf(thread.getPort()));

                        thread.start();

                        clientThreads.add(thread);
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

        try {
            System.out.println("[Proxy] Closing connection");
            proxy.remove();
        } catch (IOException ignored) {
        }
    }
}
