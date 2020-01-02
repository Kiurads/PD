package UI.controllers;

import UI.AddSongController;
import UI.AlertUtils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.DragEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Client;
import model.constants.MessageTypes;

import java.io.File;
import java.io.IOException;
import java.net.SocketException;
import java.util.Map;
import java.util.Optional;

public class Controller {
    public MenuItem uploadItem;
    public MenuItem loginItem;
    public MenuItem registerItem;

    public Slider timeSlider;
    public MenuItem logoutItem;

    private Client client;

    private MediaPlayer currentMediaPlayer;

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

    public void onRegister(ActionEvent event) throws IOException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UI/FXML/Register.fxml"));
        Parent parent = fxmlLoader.load();
        RegisterController dialogController = fxmlLoader.getController();

        Scene scene = new Scene(parent, 400, 200);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Register");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        if (dialogController.getRegisterDetails() == null) return;

        String registerInfo = MessageTypes.REGISTER + "\n" + dialogController.getRegisterDetails();

        try {
            client.register(registerInfo);
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.register(registerInfo);
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
                return;
            }
        }

        AlertUtils.showAlert(client.receiveMessage());
    }

    public void onLogin(ActionEvent event) throws IOException, ClassNotFoundException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UI/FXML/Login.fxml"));
        Parent parent = fxmlLoader.load();
        LoginController dialogController = fxmlLoader.getController();

        Scene scene = new Scene(parent, 400, 200);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Register");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        if (dialogController.getLoginDetails() == null) return;

        String loginInfo = MessageTypes.LOGIN + "\n" + dialogController.getLoginDetails();

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
            client.setDetails(dialogController.getLoginDetails());

            loginItem.setDisable(true);
            registerItem.setDisable(true);
            logoutItem.setDisable(false);
            uploadItem.setDisable(false);
        }

        AlertUtils.showAlert(reply);
    }

    public void onLogout(ActionEvent event) throws IOException, ClassNotFoundException {
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
        }

        AlertUtils.showAlert(reply);
    }

    public void onUpload() throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3 files", "*.mp3"));
        File file = fileChooser.showOpenDialog(null);

        if (file == null) return;

        Media mediaFile = new Media(file.toURI().toString());
        MediaPlayer player = new MediaPlayer(mediaFile);

        player.setOnReady(new Runnable() {
            @Override
            public void run() {}
        });

        int duration = (int) mediaFile.getDuration().toSeconds();

        System.out.println(duration);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/UI/FXML/AddSong.fxml"));
        Parent parent = fxmlLoader.load();
        AddSongController dialogController = fxmlLoader.getController();
        dialogController.setSongDuration(duration);

        Scene scene = new Scene(parent, 400, 300);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Upload Song");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        if (dialogController.getSongDetails() == null) return;

        String songInfo = MessageTypes.UPLOAD + "\n" + dialogController.getSongDetails() + file.getName();

        try {
            client.upload(songInfo, file.getCanonicalPath());
        } catch (SocketException e) {
            try {
                client.reconnect();

                client.upload(songInfo, file.getCanonicalPath());
            } catch (IOException | ClassNotFoundException ex) {
                AlertUtils.showException(ex);
            }
        }

        AlertUtils.showAlert(client.receiveMessage());
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
