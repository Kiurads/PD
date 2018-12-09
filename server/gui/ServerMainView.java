package PD.server.gui;

import PD.server.Server;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class ServerMainView extends JFrame implements Observer {
    private Server server;

    public ServerMainView(Server server) {
        super("File Server");

        this.server = server;
        server.addObserver(this);

        setVisible(true);
        setSize(882, 650);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        validate();
    }

    @Override
    public void update(Observable o, Object arg) {
        //TODO Update View
    }
}
