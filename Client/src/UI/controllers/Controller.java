package UI.controllers;

import UI.utils.AlertUtils;
import UI.utils.DialogUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Client;
import model.constants.MessageTypes;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class Controller {
    public MenuItem loginItem;
    public MenuItem logoutItem;
    public MenuItem registerItem;

    public MenuItem uploadItem;
    public MenuItem createPlaylistItem;
    public MenuItem deletePlaylistItem;
    public ListView<String> playlistList;

    private MediaPlayer currentMediaPlayer;
    public Slider timeSlider;

    private Client client;

    @FXML
    public void initialize() {
        TextInputDialog alert = new TextInputDialog("localhost");
        alert.setTitle("Proxy address");
        alert.setContentText("Insert proxy address");

        Optional<String> result = alert.showAndWait();
        try {
            if (result.isPresent())
                client = new Client(result.get());
        } catch (IOException | ClassNotFoundException e) {
            AlertUtils.showException(e);
            Platform.exit();
        }

        timeSlider.valueProperty().addListener(ov -> {
            if (timeSlider.isPressed() && currentMediaPlayer != null)
                currentMediaPlayer.seek(currentMediaPlayer.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
        });

        uploadItem.setDisable(true);
    }

    public void onRegister() throws IOException, ClassNotFoundException {
        String registerDetails = DialogUtils.getRegisterDetails();

        if (registerDetails == null) return;

        registerDetails = MessageTypes.REGISTER + "\n" + registerDetails;

        try {
            client.register(registerDetails);
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.register(registerDetails);
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
                return;
            }
        }

        AlertUtils.showAlert(client.receiveMessage());
    }

    public void onLogin() throws IOException, ClassNotFoundException {
        String loginDetails = DialogUtils.getLoginDetails();

        if (loginDetails == null) return;

        String loginInfo = MessageTypes.LOGIN + "\n" + loginDetails;

        try {
            client.login(loginInfo);
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.login(loginInfo);
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
                return;
            }
        }

        String reply = client.receiveMessage();

        if (reply.equals(MessageTypes.SUCCESS)) {
            client.setDetails(loginDetails);

            loginItem.setDisable(true);
            registerItem.setDisable(true);
            logoutItem.setDisable(false);
            uploadItem.setDisable(false);
            createPlaylistItem.setDisable(false);
            deletePlaylistItem.setDisable(false);

            updatePlaylists();
        }

        AlertUtils.showAlert(reply);
    }

    public void onLogout() throws IOException, ClassNotFoundException {
        Optional<ButtonType> choice = AlertUtils.getConfirmation();

        if (!choice.isPresent() || choice.get() != ButtonType.OK) return;

        client.logout();

        String reply = client.receiveMessage();

        if (reply.equals(MessageTypes.SUCCESS)) {
            client.resetDetails();

            loginItem.setDisable(false);
            registerItem.setDisable(false);
            logoutItem.setDisable(true);
            uploadItem.setDisable(true);
            createPlaylistItem.setDisable(true);
            deletePlaylistItem.setDisable(true);

            playlistList.setItems(null);
        }

        AlertUtils.showAlert(reply);
    }

    public void onUpload() throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 files", "*.mp3"));
        File file = fileChooser.showOpenDialog(null);

        if (file == null) return;

        String songDetails = DialogUtils.getSongDetails();

        if (songDetails == null) return;

        songDetails = MessageTypes.UPLOAD + "\n" + songDetails + file.getName();

        try {
            client.upload(songDetails, file.getCanonicalPath());
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.upload(songDetails, file.getCanonicalPath());
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }

        AlertUtils.showAlert(client.receiveMessage());
    }

    public void onCreatePlaylist() throws IOException, ClassNotFoundException {
        String playlistName = DialogUtils.getPlaylistName();

        if (playlistName == null) return;

        playlistName = MessageTypes.PLAYLIST_CREATE + "\n" + playlistName;

        try {
            client.createPlaylist(playlistName);
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.createPlaylist(playlistName);
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }

        AlertUtils.showAlert(client.receiveMessage());

        updatePlaylists();
    }

    private void updatePlaylists() throws IOException, ClassNotFoundException {
        String playlists = client.getPlaylistNames();
        String[] message = playlists.split("\n");

        if (message[0].equals(MessageTypes.FAILURE)) {
            AlertUtils.showAlert(playlists);
            return;
        }

        int playlistNumber = Integer.parseInt(message[1]);
        playlistList.setItems(FXCollections.observableList(new ArrayList<>(Arrays.asList(message).subList(2, 2 + playlistNumber))));
    }

    public void onDeletePlaylist(ActionEvent event) throws IOException, ClassNotFoundException {
        String playlists = client.getPlaylistNames();
        String[] message = playlists.split("\n");

        if (message[0].equals(MessageTypes.FAILURE)) {
            AlertUtils.showAlert(playlists);
            return;
        }

        int playlistNumber = Integer.parseInt(message[1]);
        String playlistName = DialogUtils.deletePlaylist(new ArrayList<>(Arrays.asList(message).subList(2, 2 + playlistNumber)));

        if (playlistName == null) return;

        String[] playlistDetails = playlistName.split(" - ");
        int playlistId = Integer.parseInt(playlistDetails[0]);

        try {
            client.deletePlaylist(playlistId);
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.deletePlaylist(playlistId);
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }

        AlertUtils.showAlert(client.receiveMessage());

        updatePlaylists();
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

    private void updateChanges() {
        Platform.runLater(() -> timeSlider.setValue(currentMediaPlayer.getCurrentTime().toMillis() / currentMediaPlayer.getTotalDuration().toMillis() * 100));
    }

    public void shutdown() throws IOException {
        if (client != null) client.shutdown();
    }
}
