package client;

import client.constants.MessageTypes;

import java.io.File;
import java.io.IOException;

public class ClientThread extends Thread implements MessageTypes {
    Client client;
    boolean running;

    public ClientThread() throws IOException {
        client = new Client();

        running = true;
    }

    public void terminate() {
        running = false;
    }

    @Override
    public void run() {
        String[] message;

        System.out.println("[ClientThread:" + getId() + "] Started");
        while (running) {
            try {
                message = client.receive().split(System.getProperty("line.separator"));
            } catch (IOException | ClassNotFoundException e) {
                break;
            }

            switch (message[0]) {
                case UPLOAD:
                    String filePath;
                    File localPath;
                    int nbytes;
                    try {
                        localPath = new File("/Uploads");
                        filePath = localPath.getCanonicalPath() + File.separator + message[1];
                    } catch (IOException e) {
                        break;
                    }

                    client.getFile(filePath);
            }
        }

        try {
            client.close();
        } catch (IOException e) {
            System.out.println("[Error] [Client:" + getId() + "] " + e.getCause());
        }
    }
}
