package PD.client.gui;

import PD.Messages.Login;
import PD.client.Client;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class ClientMainView extends JFrame implements Observer {
    private Client client;
    private JButton register;
    private JButton login;

    //TODO Add remaining elements

    public ClientMainView(Client client) {
        this.client = client;
        register = new JButton("Register");
        login = new JButton("Login");

        login.addActionListener(e -> login());

        setupLayout();
        setVisible(true);
        setSize(new Dimension(500, 500));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        validate();
    }

    private void setupLayout() {
        setLayout(new FlowLayout());

        add(register);
        add(login);
    }

    private void login() {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setVisible(true);

        Login login = new Login(loginDialog.getUsername(), loginDialog.getPassword());


    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
