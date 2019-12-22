package proxy;

import client.ClientThread;
import proxy.constants.Constants;

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

                if (message.contains(REQUEST_SERVER)) {
                    System.out.println("[Proxy] Server requested");

                    clientThreads.add(new ClientThread());
                    clientThreads.get(clientThreads.size() - 1).start();
                }
            } catch (IOException ignored) {
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            proxy.remove();
        } catch (IOException ignored) {
        }
    }
}
