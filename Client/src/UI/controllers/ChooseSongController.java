package UI.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class ChooseSongController {
    public ListView<String> songListView;
    public Button chooseButton;

    private int songId;

    public void initialize() {
        songId = -1;

        songListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateChanges());
    }

    public void onAction(ActionEvent event) {
        String[] details = songListView.getSelectionModel().getSelectedItem().split(" - ");

        songId = Integer.parseInt(details[0]);

        close(event);
    }

    public void setSongListView(List<String> playlistList) {
        ObservableList<String> list = FXCollections.observableList(playlistList);
        songListView.setItems(list);
    }

    public void setButtonText(String operation) {
        chooseButton.setText(operation);
    }

    public void updateChanges() {
        if (songListView.getSelectionModel().getSelectedItem() != null)
            chooseButton.setDisable(false);
        else
            chooseButton.setDisable(true);
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
