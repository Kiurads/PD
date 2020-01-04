package UI.utils;

import UI.AddSongController;
import UI.controllers.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DialogUtils {
    public static String getSongDetails() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogUtils.class.getResource("/UI/FXML/AddSong.fxml"));
        Parent parent = fxmlLoader.load();
        AddSongController dialogController = fxmlLoader.getController();

        Scene scene = new Scene(parent, 400, 300);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Upload Song");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        return dialogController.getSongDetails();
    }

    public static String getLoginDetails() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogUtils.class.getResource("/UI/FXML/Login.fxml"));
        Parent parent = fxmlLoader.load();
        LoginController dialogController = fxmlLoader.getController();

        Scene scene = new Scene(parent, 400, 200);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Login");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        return dialogController.getLoginDetails();
    }

    public static String getRegisterDetails() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogUtils.class.getResource("/UI/FXML/Register.fxml"));
        Parent parent = fxmlLoader.load();
        RegisterController dialogController = fxmlLoader.getController();

        Scene scene = new Scene(parent, 400, 200);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Register");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        return dialogController.getRegisterDetails();
    }

    public static String getPlaylistName() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogUtils.class.getResource("/UI/FXML/CreatePlaylist.fxml"));
        Parent parent = fxmlLoader.load();
        CreatePlaylistController dialogController = fxmlLoader.getController();

        Scene scene = new Scene(parent, 400, 200);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Create playlist");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        return dialogController.getPlaylistName();
    }

    public static int deletePlaylist(List<String> playlistNames) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogUtils.class.getResource("/UI/FXML/DeletePlaylist.fxml"));
        Parent parent = fxmlLoader.load();
        DeletePlaylistController dialogController = fxmlLoader.getController();
        dialogController.setPlaylistListView(playlistNames);

        Scene scene = new Scene(parent, 400, 200);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Delete playlist");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        return dialogController.getPlaylistId();
    }

    public static String editPlaylist(List<String> playlistNames) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogUtils.class.getResource("/UI/FXML/EditPlaylist.fxml"));
        Parent parent = fxmlLoader.load();
        EditPlaylistController dialogController = fxmlLoader.getController();
        dialogController.setPlaylistListView(playlistNames);

        Scene scene = new Scene(parent, 400, 200);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Edit playlist");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        if (dialogController.getPlaylistId() != -1)
            return dialogController.getPlaylistId() + "\n" + dialogController.getPlaylistName();
        return null;
    }

    public static int playSong(ArrayList<String> songNames) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DialogUtils.class.getResource("/UI/FXML/PlaySong.fxml"));
        Parent parent = fxmlLoader.load();
        PlaySongController dialogController = fxmlLoader.getController();
        dialogController.setSongListView(songNames);

        Scene scene = new Scene(parent, 400, 200);
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Edit playlist");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();

        if (dialogController.getSongId() != -1)
            return dialogController.getSongId();
        return -1;
    }
}
