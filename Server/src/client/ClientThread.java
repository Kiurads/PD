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

    public void terminate() {
        running = false;
    }

    @Override
    public void run() {
        String[] message = new String[0];

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
                e.printStackTrace();
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
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
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
                            client.send(MessageTypes.FAILURE + "\n" + e.getMessage());
                        } catch (IOException ex) {
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
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
                    } catch (SQLException e) {
                        try {
                            client.send(MessageTypes.FAILURE + "\n" + e.getMessage());
                        } catch (IOException ex) {
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
                    }
                    break;

                case MessageTypes.SONGS:
                    try {
                        client.send(MessageTypes.SUCCESS + "\n" + database.getSongs(-1));
                    } catch (SQLException e) {
                        try {
                            client.send(MessageTypes.FAILURE + "\n" + e.getMessage());
                        } catch (IOException ex) {
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
                    }
                    break;

                case MessageTypes.PLAY_SONG:
                    try {
                        String songInfo = database.getSong(Integer.parseInt(message[1]));
                        client.send(MessageTypes.SUCCESS + "\n" + songInfo);

                        client.sendFile(songInfo.split("\n")[5]);
                    } catch (SQLException e) {
                        try {
                            e.printStackTrace();
                            client.send(MessageTypes.FAILURE + "\n" + e.getMessage());
                        } catch (IOException ex) {
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
                    }
                    break;

                case MessageTypes.PLAYLISTS:
                    try {
                        client.send(MessageTypes.SUCCESS + "\n" + database.getPlaylists(client.getUserID()));
                    } catch (SQLException e) {
                        try {
                            client.send(MessageTypes.FAILURE + "\n" + e.getMessage());
                        } catch (IOException ex) {
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
                    }
                    break;

                case MessageTypes.PLAYLIST_CREATE:
                    try {
                        database.addPlaylist(message[1], client.getUserID());

                        System.out.println("[Client:" + client.getServerPort() + "] Created playlist");
                        client.send(MessageTypes.SUCCESS);
                        break;
                    } catch (SQLException e) {
                        try {
                            client.send(MessageTypes.FAILURE + "\n" + e.getMessage());
                        } catch (IOException ex) {
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
                    }
                    break;

                case MessageTypes.PLAYLIST_EDIT:
                    try {
                        database.updatePlaylist(Integer.parseInt(message[1]), message[2]);

                        System.out.println("[Client:" + client.getServerPort() + "] Edited playlist");
                        client.send(MessageTypes.SUCCESS);
                        break;
                    } catch (SQLException e) {
                        try {
                            client.send(MessageTypes.FAILURE + "\n" + e.getMessage());
                        } catch (IOException ex) {
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
                    }
                    break;

                case MessageTypes.PLAYLIST_DELETE:
                    try {
                        database.removePlaylist(Integer.parseInt(message[1]));

                        System.out.println("[Client:" + client.getServerPort() + "] Deleted playlist");
                        client.send(MessageTypes.SUCCESS);
                        break;
                    } catch (SQLException e) {
                        try {
                            client.send(MessageTypes.FAILURE + "\n" + e.getMessage());
                        } catch (IOException ex) {
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
                    }
                    break;

                case MessageTypes.PLAYLIST_ADD:
                    try {
                        database.addSongToPlaylist(Integer.parseInt(message[1]), Integer.parseInt(message[2]));

                        System.out.println("[Client:" + client.getServerPort() + "] Added song to playlist");
                        client.send(MessageTypes.SUCCESS);
                        break;
                    } catch (SQLException e) {
                        try {
                            client.send(MessageTypes.FAILURE + "\n" + e.getMessage());
                        } catch (IOException ex) {
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
                    }
                    break;

                case MessageTypes.PLAYLIST_SONGS:
                    try {
                        client.send(MessageTypes.SUCCESS + "\n" + database.getSongsFromPlaylist(Integer.parseInt(message[1])));
                    } catch (SQLException e) {
                        try {
                            client.send(MessageTypes.FAILURE + "\n" + e.getMessage());
                        } catch (IOException ex) {
                            terminate();
                        }
                    } catch (IOException e) {
                        terminate();
                    }
                    break;

                case MessageTypes.LOGOUT:
                    try {
                        client.setUserID(-1);

                        System.out.println("[Client:" + client.getServerPort() + "] Logout successful");
                        client.send(MessageTypes.SUCCESS);
                    } catch (IOException e) {
                        terminate();
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
