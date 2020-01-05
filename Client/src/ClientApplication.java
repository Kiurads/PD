import UI.controllers.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.utils.FileUtils;

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
            FileUtils.deleteDownloads();
            Platform.exit();
        });
        Scene scene = new Scene(root, 800, 400);
        primaryStage.setTitle("ISECify");
        primaryStage.setMinWidth(700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
