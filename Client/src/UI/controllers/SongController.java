package UI.controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class SongController {
    public TextField songNameTextField;
    public TextField artistTextField;
    public TextField albumTextField;
    public TextField yearTextField;
    public TextField genreTextField;

    public Button okButton;

    private int songDuration;

    private String songDetails;

    public void initialize() {
        songDuration = 0;
        songDetails = null;
    }

    public void onUpload(ActionEvent actionEvent) {
        songDetails = songNameTextField.getText() + "\n" +
                artistTextField.getText() + "\n" +
                albumTextField.getText() + "\n" +
                yearTextField.getText() + "\n" +
                genreTextField.getText() + "\n" +
                songDuration;
        close(actionEvent);
    }

    private static boolean checkYear(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }

    public void updateChanges() {
        if (!songNameTextField.getText().isEmpty() && !artistTextField.getText().isEmpty() && !albumTextField.getText().isEmpty() && !yearTextField.getText().isEmpty() && !genreTextField.getText().isEmpty()) {
            okButton.setDisable(!checkYear(yearTextField.getText()));
            return;
        }

        okButton.setDisable(true);
    }

    private void close(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.close();
    }

    public String getSongDetails() {
        return songDetails;
    }

    public void setDetails(String[] songDetails) {
        songNameTextField.setText(songDetails[1]);
        artistTextField.setText(songDetails[2]);
        albumTextField.setText(songDetails[3]);
        yearTextField.setText(songDetails[4]);
        genreTextField.setText(songDetails[5]);
    }
}
