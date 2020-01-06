package UI.controllers;

import UI.AlertUtils;
import javafx.scene.control.TextArea;
import model.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;

public class Controller {
    private Server server;
    public TextArea textArea;

    public void initialize() throws SQLException, IOException, ClassNotFoundException {
        server = new Server(InetAddress.getByName(AlertUtils.getText(
                "Proxy Address",
                "Proxy Address",
                "localhost")),
                this);
    }

    public void addText(String text) {
        textArea.setText(textArea.getText() + "\n" + text);
    }

    public void close() throws InterruptedException, SQLException {
        server.stop();
    }
}
