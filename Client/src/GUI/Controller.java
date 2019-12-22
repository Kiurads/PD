package GUI;

import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import model.Client;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class Controller {
    private Client client;
    public TextArea info;

    @FXML
    public void initialize() {
        TextInputDialog alert = new TextInputDialog("localhost");
        alert.setTitle("Proxy address");
        alert.setContentText("Insert proxy address");

        Optional<String> result = alert.showAndWait();
        try {
            client = new Client(result.get());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void onUpload(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);

        client.upload(file);
    }
}
