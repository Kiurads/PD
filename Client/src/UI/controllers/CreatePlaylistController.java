package UI.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreatePlaylistController {
    public TextField nameTextField;
    public Button createButton;

    private String playlistName;

    public void onCreate(ActionEvent event) {
        playlistName = nameTextField.getText();

        close(event);
    }

    public void updateChanges() {
        if (!nameTextField.getText().isEmpty())
            createButton.setDisable(false);
        else
            createButton.setDisable(true);
    }

    public void close(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.close();
    }

    public String getPlaylistName() {
        return playlistName;
    }
}
