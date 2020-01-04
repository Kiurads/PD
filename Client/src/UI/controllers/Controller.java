package UI.controllers;

import UI.utils.AlertUtils;
import UI.utils.DialogUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import model.Client;
import model.constants.Constants;
import model.constants.MessageTypes;
import model.data.Song;

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
    public MenuItem playItem;

    public MenuItem createPlaylistItem;
    public MenuItem deletePlaylistItem;

    public ListView<String> playlistList;

    private MediaPlayer currentMediaPlayer;
    public Slider timeSlider;
    public Slider volumeSlider;

    private Client client;

    private Song currentSong;

    @FXML
    public void initialize() {
        try {
            String proxyAddress = AlertUtils.getText("Proxy Address", "Insert proxy address", "localhost");
            if (proxyAddress != null)
                client = new Client(proxyAddress);
        } catch (IOException | ClassNotFoundException e) {
            AlertUtils.showException(e);
            Platform.exit();
        }

        timeSlider.valueProperty().addListener(ov -> {
            if (timeSlider.isPressed() && currentMediaPlayer != null)
                currentMediaPlayer.seek(currentMediaPlayer.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
        });

        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.3);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (volumeSlider.isPressed()) {
                    currentMediaPlayer.setVolume(volumeSlider.getValue());
                }
            }
        });
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
            playItem.setDisable(false);
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
            playItem.setDisable(true);
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

    public void onPlaySong() throws IOException, ClassNotFoundException {
        String message = client.getSongs();
        String[] songs = message.split("\n");

        if (songs[0].equals(MessageTypes.FAILURE)) {
            AlertUtils.showAlert(message);
            return;
        }

        int size = Integer.parseInt(songs[1]);
        int songId = DialogUtils.playSong(new ArrayList<>(Arrays.asList(songs).subList(2, 2 + size)));

        if (songId == -1) return;

        try {
            message = client.getSongInfo(songId);
            String[] songInfo = message.split("\n");

            if (songInfo[0].equals(MessageTypes.FAILURE)) {
                AlertUtils.showAlert(message);
                return;
            }

            currentSong = new Song(songInfo);

            currentSong.setFile(client.receiveSong(songInfo[6]));

            playCurrentSong();
        } catch (SocketException e) {
            try {
                client.reconnect();

                message = client.getSongInfo(songId);
                String[] songInfo = message.split("\n");

                if (songInfo[0].equals(MessageTypes.FAILURE)) {
                    AlertUtils.showAlert(message);
                    return;
                }

                currentSong = new Song(songInfo);

                currentSong.setFile(client.receiveSong(songInfo[6]));

                playCurrentSong();
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }
    }

    private void playCurrentSong() {
        Media media = new Media(currentSong.getFile().toURI().toString());
        currentMediaPlayer.stop();
        currentMediaPlayer = new MediaPlayer(media);
        onVolumeChange(null);

        onPlay(null);
    }

    public void onCreatePlaylist() throws IOException, ClassNotFoundException {
        String playlistName = DialogUtils.getPlaylistName();

        if (playlistName == null) return;

        try {
            client.createOrEditPlaylist(MessageTypes.PLAYLIST_CREATE + "\n" + playlistName);
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.createOrEditPlaylist(MessageTypes.PLAYLIST_CREATE + "\n" + playlistName);
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

    public void onEditPlaylist() throws IOException, ClassNotFoundException {
        String message = client.getPlaylistNames();
        String[] playlists = message.split("\n");

        if (playlists[0].equals(MessageTypes.FAILURE)) {
            AlertUtils.showAlert(message);
            return;
        }

        int size = Integer.parseInt(playlists[1]);
        String playlistDetails = DialogUtils.editPlaylist(new ArrayList<>(Arrays.asList(playlists).subList(2, 2 + size)));

        if (playlistDetails == null) return;

        try {
            client.createOrEditPlaylist(MessageTypes.PLAYLIST_EDIT + "\n" + playlistDetails);
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.createOrEditPlaylist(MessageTypes.PLAYLIST_EDIT + "\n" + playlistDetails);
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }

        AlertUtils.showAlert(client.receiveMessage());

        updatePlaylists();
    }

    public void onDeletePlaylist() throws IOException, ClassNotFoundException {
        String playlists = client.getPlaylistNames();
        String[] message = playlists.split("\n");

        if (message[0].equals(MessageTypes.FAILURE)) {
            AlertUtils.showAlert(playlists);
            return;
        }

        int size = Integer.parseInt(message[1]);
        int playlistId = DialogUtils.deletePlaylist(new ArrayList<>(Arrays.asList(message).subList(2, 2 + size)));

        if (playlistId == -1) return;

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

    public void onPause(ActionEvent event) {
        if (currentMediaPlayer == null) return;

        currentMediaPlayer.pause();
    }

    public void onStop(ActionEvent event) {
        if (currentMediaPlayer == null) return;

        currentMediaPlayer.stop();
    }

    public void onDragDone(DragEvent dragEvent) {
        if (currentMediaPlayer == null) return;

        currentMediaPlayer.seek(currentMediaPlayer.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
    }

    private void updateChanges() {
        Platform.runLater(() -> timeSlider.setValue(currentMediaPlayer.getCurrentTime().toMillis() / currentMediaPlayer.getTotalDuration().toMillis() * 100));
    }

    public void shutdown() throws IOException {
        if (client != null) client.shutdown();
    }

    public void onVolumeChange(DragEvent dragEvent) {
        currentMediaPlayer.setVolume(volumeSlider.getValue());
    }
}
