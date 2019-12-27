package client;

import client.constants.MessageTypes;

import java.io.IOException;

public class ClientThread extends Thread {
    Client client;
    boolean running;

    public ClientThread() throws IOException {
        client = new Client();

        running = true;
    }

    public void terminate() throws IOException {
        running = false;
        client.close();
    }

    @Override
    public void run() {
        String[] message;

        try {
            client.accept();
        } catch (IOException e) {
            System.out.println("ERROR " + e.getCause());
            running = false;
        }

        while (running) {
            try {
                message = client.receive().split("\n");
            } catch (IOException | ClassNotFoundException e) {
                break;
            }

            System.out.println("[ClientThread:" + getId() + "] " + message[0]);

            switch (message[0]) {
                case MessageTypes.UPLOAD:
                    try {
                        if(client.getFile(message[1])) {
                            System.out.println("[ClientThread:" + getId() + "] Success");
                            client.send(MessageTypes.SUCCESS);
                            break;
                        }

                        client.send(MessageTypes.FAILURE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case MessageTypes.CLOSE:
                    running = false;
                    break;
            }
        }

        try {
            client.close();
        } catch (IOException e) {
            System.out.println("[Error] [Client:" + getId() + "] " + e.getCause());
        }

        System.out.println("[ClientThread:" + getId() + "] Closing connection");
    }

    public int getPort() {
        return client.getServerPort();
    }
}
