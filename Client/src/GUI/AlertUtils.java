package GUI;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class AlertUtils {
    public static String inputSongName(String fileName) {
        TextInputDialog songName = new TextInputDialog(fileName.substring(0, fileName.length() - 4));
        songName.setTitle("Song name");
        songName.setContentText("Insert song name");
        Optional<String> result = songName.showAndWait();

        String name = result.get();

        if (name.isEmpty())
            name = fileName;
        else
            name += ".mp3";

        return name;
    }

    public static void showAlert(String receiveMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(receiveMessage);

        alert.showAndWait();
    }
}
