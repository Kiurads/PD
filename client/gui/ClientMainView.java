package PD.client.gui;

import PD.client.Client;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;

public class ClientMainView extends JFrame implements Observer {
    private Client client;
    private JButton Register;
    private JButton Login;

    //TODO Add remaining elements

    public ClientMainView(Client client) {
        this.client = client;


    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
