package UI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class AddSongController {
    public TextField songNameTextField;
    public TextField artistTextField;
    public TextField albumTextField;
    public TextField yearTextField;
    public TextField genreTextField;

    public Button uploadButton;

    private int songDuration;

    private String songDetails;

    public void initialize() {
        songDetails = null;
    }

    public void onUpload(ActionEvent actionEvent) {
        songDetails = songNameTextField.getText() + "\n" +
                artistTextField.getText() + "\n" +
                albumTextField.getText() + "\n" +
                yearTextField.getText() + "\n" +
                genreTextField.getText() + "\n" +
                songDuration + "\n";
        close(actionEvent);
    }

    public void updateChanges(KeyEvent keyEvent) {
        if (!songNameTextField.getText().isEmpty() && !artistTextField.getText().isEmpty() && !albumTextField.getText().isEmpty() && !yearTextField.getText().isEmpty() && !genreTextField.getText().isEmpty()) {
            if (!checkYear(yearTextField.getText())) {
                uploadButton.setDisable(true);
                return;
            };

            uploadButton.setDisable(false);
            return;
        }

        uploadButton.setDisable(true);
    }

    private static boolean checkYear(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }

    public String getSongDetails() {
        return songDetails;
    }

    public void setSongDuration(int songDuration) {
        this.songDuration = songDuration;
    }

    @FXML
    private void close(ActionEvent actionEvent) {
        Node node = (Node) actionEvent.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.close();
    }
}
