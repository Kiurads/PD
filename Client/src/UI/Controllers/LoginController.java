package UI.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class LoginController {
    public TextField usernameTextField;
    public PasswordField passwordField;

    public Button loginButton;

    public void onRegister(ActionEvent event) {

    }

    public void updateChanges(KeyEvent keyEvent) {
        if (!usernameTextField.getText().isEmpty() && !passwordField.getText().isEmpty())
            loginButton.setDisable(false);
        else
            loginButton.setDisable(true);
    }

    public void close(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.close();
    }
}
