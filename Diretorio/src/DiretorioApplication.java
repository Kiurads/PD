import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DiretorioApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UI/GUI.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        stage.setOnHidden(event -> {
            try {
                controller.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.exit();
        });
        Scene scene = new Scene(root, 800, 400);
        stage.setTitle("Proxy");
        stage.setScene(scene);
        stage.show();
    }
}
