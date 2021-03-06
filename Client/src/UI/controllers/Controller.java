package UI.controllers;

import UI.utils.AlertUtils;
import UI.utils.DialogUtils;
import UI.utils.SongOperations;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import model.Client;
import model.constants.MessageTypes;
import model.data.Song;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.*;

public class Controller {
    public MenuItem loginItem;
    public MenuItem logoutItem;
    public MenuItem registerItem;

    public MenuItem uploadItem;
    public MenuItem playItem;
    public MenuItem createPlaylistItem;
    public MenuItem editPlaylistItem;
    public MenuItem deletePlaylistItem;

    public ListView<String> playlistList;

    public ListView<String> songList;
    public HBox playlistControls;
    public Button removeButton;

    public Label songDetails;
    private MediaPlayer currentMediaPlayer;
    public Slider timeSlider;
    public Slider volumeSlider;

    private Client client;
    private Song currentSong;

    private boolean playlist;
    private List<Song> currentPlaylist;
    private int currentPlaylistSong;

    public void initialize() {
        playlist = false;
        currentPlaylist = new ArrayList<>();
        currentPlaylistSong = 0;

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

        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> onVolumeChange());

        playlistList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            try {
                playlistSelect();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        });

        songList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                songSelect();
            }
        });

        playlistControls.setDisable(true);
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
            editPlaylistItem.setDisable(false);
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
            editPlaylistItem.setDisable(true);
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

        songDetails = MessageTypes.SONG_UPLOAD + "\n" + songDetails + "\n" + file.getName();

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
        int songId = DialogUtils.chooseSong(new ArrayList<>(Arrays.asList(songs).subList(2, 2 + size)), SongOperations.PLAY);

        if (songId == -1) return;

        try {
            message = client.getSongFile(songId);
            String[] songInfo = message.split("\n");

            if (songInfo[0].equals(MessageTypes.FAILURE)) {
                AlertUtils.showAlert(message);
                return;
            }
            currentSong = new Song(songInfo);
            currentSong.setFile(client.receiveSong(songInfo[6]));

            playlist = false;
            playCurrentSong();
        } catch (SocketException e) {
            try {
                client.reconnect();

                message = client.getSongFile(songId);
                String[] songInfo = message.split("\n");

                if (songInfo[0].equals(MessageTypes.FAILURE)) {
                    AlertUtils.showAlert(message);
                    return;
                }
                currentSong = new Song(songInfo);
                currentSong.setFile(client.receiveSong(songInfo[6]));

                playlist = false;
                playCurrentSong();
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }
    }

    public void onEditSong() throws IOException, ClassNotFoundException {
        String message = client.getUserSongs();
        String[] songs = message.split("\n");

        if (songs[0].equals(MessageTypes.FAILURE)) {
            AlertUtils.showAlert(message);
            return;
        }

        int size = Integer.parseInt(songs[1]);
        int songId = DialogUtils.chooseSong(new ArrayList<>(Arrays.asList(songs).subList(2, 2 + size)), SongOperations.EDIT);

        if (songId == -1) return;

        message = client.getSongInfo(songId);
        String[] songDetails = message.split("\n");

        if (songDetails[0].equals(MessageTypes.FAILURE)) {
            AlertUtils.showAlert(message);
            return;
        }

        try {
            client.editSong(songId, DialogUtils.getSongDetails(songDetails));
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.editSong(songId, DialogUtils.getSongDetails(songDetails));
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }

        AlertUtils.showAlert(client.receiveMessage());
    }

    public void onDeleteSong() {
    }

    public void onCreatePlaylist() throws IOException, ClassNotFoundException {
        String playlistName = DialogUtils.createPlaylist();

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
        String message = client.getPlaylistNames();
        String[] playlists = message.split("\n");

        if (playlists[0].equals(MessageTypes.FAILURE)) {
            AlertUtils.showAlert(message);
            return;
        }

        int playlistNumber = Integer.parseInt(playlists[1]);
        playlistList.setItems(FXCollections.observableList(new ArrayList<>(Arrays.asList(playlists).subList(2, 2 + playlistNumber))));
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

    public void addToPlaylist() throws IOException, ClassNotFoundException {
        String message = client.getSongs();
        String[] songs = message.split("\n");
        int playlistId = Integer.parseInt(playlistList.getSelectionModel().getSelectedItem().split(" - ")[0]);

        if (songs[0].equals(MessageTypes.FAILURE)) {
            AlertUtils.showAlert(message);
            return;
        }

        int size = Integer.parseInt(songs[1]);
        int songId = DialogUtils.chooseSong(new ArrayList<>(Arrays.asList(songs).subList(2, 2 + size)), SongOperations.ADD);

        if (songId == -1) return;

        try {
            client.addToPlaylist(playlistId, songId);
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.addToPlaylist(playlistId, songId);
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }

        AlertUtils.showAlert(client.receiveMessage());
        updateSongList();
    }

    public void removeFromPlaylist() throws IOException, ClassNotFoundException {
        int playlistId = Integer.parseInt(playlistList.getSelectionModel().getSelectedItem().split(" - ")[0]);
        int songId = Integer.parseInt(songList.getSelectionModel().getSelectedItem().split(" - ")[0]);

        if (AlertUtils.getConfirmation().get() != ButtonType.OK) return;

        try {
            client.removeFromPlaylist(playlistId, songId);
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.removeFromPlaylist(playlistId, songId);
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }

        AlertUtils.showAlert(client.receiveMessage());
        updateSongList();
    }

    public void loadPlaylist() throws IOException, ClassNotFoundException {
        int playlistId = Integer.parseInt(playlistList.getSelectionModel().getSelectedItem().split(" - ")[0]);
        String message = client.requestPlaylist(playlistId);
        String[] content = message.split("\n");

        if (content[0].equals(MessageTypes.FAILURE)) {
            AlertUtils.showAlert(message);
            return;
        }

        try {
            currentPlaylist = client.loadPlaylist(Integer.parseInt(content[1]));

            playlist = true;
            currentPlaylistSong = 0;
            playCurrentSong();
        } catch (SocketException e) {
            try {
                client.reconnect();

                currentPlaylist = client.loadPlaylist(Integer.parseInt(content[1]));

                playlist = true;
                currentPlaylistSong = 0;
                playCurrentSong();
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }
    }

    private void playCurrentSong() {
        Media media;
        if (!playlist) media = new Media(currentSong.getFile().toURI().toString());
        else media = new Media(currentPlaylist.get(currentPlaylistSong).getFile().toURI().toString());
        if (currentMediaPlayer != null) currentMediaPlayer.stop();
        currentMediaPlayer = new MediaPlayer(media);
        if (!playlist) songDetails.setText(currentSong.toString());
        else songDetails.setText(currentPlaylist.get(currentPlaylistSong).toString());
        onVolumeChange();

        onPlay();
    }

    public void onPlay() {
        if (currentMediaPlayer == null) return;

        currentMediaPlayer.play();

        currentMediaPlayer.currentTimeProperty().addListener(observable -> updateChanges());
    }

    public void onPause() {
        if (currentMediaPlayer == null) return;

        currentMediaPlayer.pause();
    }

    public void onStop() {
        if (currentMediaPlayer == null) return;

        currentMediaPlayer.stop();
    }

    public void onNext() {
        if (!playlist) return;

        currentPlaylistSong++;
        if (currentPlaylistSong == currentPlaylist.size()) currentPlaylistSong = 0;

        playCurrentSong();
    }

    public void onPrevious() {
        if (!playlist) return;

        currentPlaylistSong--;
        if (currentPlaylistSong == -1) currentPlaylistSong += currentPlaylist.size();

        playCurrentSong();
    }

    private void playlistSelect() throws IOException, ClassNotFoundException {
        if (playlistList.getSelectionModel().getSelectedItem() != null) {
            playlistControls.setDisable(false);
            removeButton.setDisable(true);
            songList.getSelectionModel().clearSelection();
            updateSongList();
        } else {
            playlistControls.setDisable(true);
            songList.setItems(null);
        }
    }

    private void songSelect() {
        removeButton.setDisable(songList.getSelectionModel().getSelectedItem() == null);
    }

    private void updateSongList() throws IOException, ClassNotFoundException {
        int playlistId = Integer.parseInt(playlistList.getSelectionModel().getSelectedItem().split(" - ")[0]);
        String message = client.getSongsInPlaylist(playlistId);
        String[] songs = message.split("\n");

        if (songs[0].equals(MessageTypes.FAILURE)) return;

        int songNumber = Integer.parseInt(songs[1]);
        songList.setItems(FXCollections.observableList(new ArrayList<>(Arrays.asList(songs).subList(2, 2 + songNumber))));
    }

    public void onDragDone() {
        if (currentMediaPlayer != null)
            currentMediaPlayer.seek(currentMediaPlayer.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
    }

    public void onVolumeChange() {
        if (currentMediaPlayer != null)
            currentMediaPlayer.setVolume(volumeSlider.getValue());
    }

    private void updateChanges() {
        Platform.runLater(() -> timeSlider.setValue(currentMediaPlayer.getCurrentTime().toMillis() / currentMediaPlayer.getTotalDuration().toMillis() * 100));
    }

    public void shutdown() throws IOException {
        if (client != null) client.shutdown();
    }
}
