package UI.controllers;

import UI.utils.AlertUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class PlaySongController {
    public ListView<String> songListView;
    public Button playButton;

    private int songId;

    public void initialize() {
        songId = -1;

        songListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateChanges());
    }

    public void onPlay(ActionEvent event) {
        String[] details = songListView.getSelectionModel().getSelectedItem().split(" - ");

        songId = Integer.parseInt(details[0]);

        close(event);
    }

    public void setSongListView(List<String> playlistList) {
        ObservableList<String> list = FXCollections.observableList(playlistList);
        songListView.setItems(list);
    }

    public void updateChanges() {
        if (songListView.getSelectionModel().getSelectedItem() != null)
            playButton.setDisable(false);
        else
            playButton.setDisable(true);
    }

    public void close(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.close();
    }

    public int getSongId() {
        return songId;
    }
}
