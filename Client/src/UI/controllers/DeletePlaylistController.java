package UI.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.List;

public class DeletePlaylistController {
    public ListView<String> playlistListView;
    public Button deleteButton;

    private String playlistName;

    public void initialize() {
        playlistListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> updateChanges());
    }

    public void onDelete(ActionEvent event) {
        playlistName = playlistListView.getSelectionModel().getSelectedItem();

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

    public String getPlaylistName() {
        return playlistName;
    }
}
