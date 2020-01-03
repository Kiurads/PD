package model;

import UI.controllers.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/FXML/ISECify.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        primaryStage.setOnHidden(event -> {
            try {
                controller.shutdown();
            } catch (IOException ignored) {
            }
            Platform.exit();
        });
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
