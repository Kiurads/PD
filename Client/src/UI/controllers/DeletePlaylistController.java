package UI.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.List;

public class DeletePlaylistController {
    public ListView<String> playlistListView;
    public Button deleteButton;

    private int playlistId;

    public void initialize() {
        playlistId = -1;

        playlistListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateChanges());
    }

    public void onDelete(ActionEvent event) {
        String[] details = playlistListView.getSelectionModel().getSelectedItem().split(" - ");

        playlistId = Integer.parseInt(details[0]);

        close(event);
    }

    public void setPlaylistListView(List<String> playlistList) {
        ObservableList<String> list = FXCollections.observableList(playlistList);
        playlistListView.setItems(list);
    }



    public void updateChanges() {
        if (playlistListView.getSelectionModel().getSelectedItem() != null)
            deleteButton.setDisable(false);
        else
            deleteButton.setDisable(true);
    }

    public void close(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.close();
    }

    public int getPlaylistId() {
        return playlistId;
    }
}
