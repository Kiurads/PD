package GUI;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.DragEvent;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import model.Client;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.Optional;

public class Controller {
    MediaPlayer currentMediaPlayer;
    public Slider timeSlider;
    private Client client;

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

        timeSlider.valueProperty().addListener(ov -> {
            if (timeSlider.isPressed() && currentMediaPlayer != null)
                currentMediaPlayer.seek(currentMediaPlayer.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
        });
    }

    public void onUpload() throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 files", "*.mp3"));
        File file = fileChooser.showOpenDialog(null);
        String uploadName;

        if (file == null) return;

        uploadName = AlertUtils.inputSongName(file.getName());

        try {
            client.upload(uploadName, file.getCanonicalPath());
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.upload(uploadName, file.getCanonicalPath());
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        }

        AlertUtils.showAlert(client.getServer().receiveMessage());
    }

    public void onPlay(ActionEvent event) {
        if (currentMediaPlayer == null) return;

        currentMediaPlayer.play();

        currentMediaPlayer.currentTimeProperty().addListener(observable -> updateChanges());
    }

    public void onDragDone(DragEvent dragEvent) {
        if (currentMediaPlayer == null) return;

        currentMediaPlayer.seek(currentMediaPlayer.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
    }

    public void onPause(ActionEvent event) {
        if (currentMediaPlayer == null) return;

        currentMediaPlayer.pause();
    }

    public void onStop(ActionEvent event) {
        if (currentMediaPlayer == null) return;

        currentMediaPlayer.stop();
    }

    protected void updateChanges() {
        Platform.runLater(() -> timeSlider.setValue(currentMediaPlayer.getCurrentTime().toMillis() / currentMediaPlayer.getTotalDuration().toMillis() * 100));
    }

    public void shutdown() throws IOException {
        client.shutdown();
    }
}
