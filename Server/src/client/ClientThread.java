package client;

import client.constants.MessageTypes;
import database.Database;

import java.io.IOException;
import java.sql.SQLException;

public class ClientThread extends Thread {
    private Client client;
    private Database database;
    private boolean running;

    public ClientThread(Database database) throws IOException {
        client = new Client();
        this.database = database;

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

            switch (message[0]) {
                case MessageTypes.REGISTER:
                    try {
                        database.addUser(message[1], message[2], message[3]);
                        System.out.println("[Client:" + client.getServerPort() + "] Register successful");
                        client.send(MessageTypes.SUCCESS);
                    } catch (SQLException e) {
                        try {
                            client.send(MessageTypes.FAILURE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case MessageTypes.LOGIN:
                    try {
                        client.setUserID(database.findUser(message[1], message[2]));

                        if (client.getUserID() != -1) {
                            client.send(MessageTypes.SUCCESS);

                            System.out.println("[Client:" + client.getServerPort() + "] Login successful");
                        } else client.send(MessageTypes.FAILURE);
                    } catch (SQLException e) {
                        try {
                            client.send(MessageTypes.FAILURE);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case MessageTypes.UPLOAD:
                    try {
                        String filePath;
                        if ((filePath = client.getFile(message[7])) != null) {
                            database.addSong(client.getUserID(),
                                    message[1],
                                    message[2],
                                    message[3],
                                    Integer.parseInt(message[4]),
                                    message[5],
                                    Integer.parseInt(message[6]),
                                    filePath);

                            System.out.println("[Client:" + client.getServerPort() + "] Uploaded file");
                            client.send(MessageTypes.SUCCESS);
                            break;
                        }

                        client.send(MessageTypes.FAILURE);
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                    break;

                case MessageTypes.LOGOUT:
                    try {
                        client.setUserID(-1);

                        System.out.println("[Client:" + client.getServerPort() + "] Logout successful");
                        client.send(MessageTypes.SUCCESS);
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
            System.out.println("[Error] [Client:" + client.getServerPort() + "] " + e.getCause());
        }

        System.out.println("[Client:" + client.getServerPort() + "] Closing connection");
    }

    public int getPort() {
        return client.getServerPort();
    }
}
