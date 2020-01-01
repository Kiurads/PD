package UI.Controllers;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RegisterController {
    public TextField nameTextField;
    public TextField usernameTextField;
    public PasswordField passwordField;

    public Button registerButton;

    private String registerDetails;

    public void initialize() {
        registerDetails = null;
    }

    public void onRegister(ActionEvent event) {
        registerDetails = nameTextField.getText() + "\n" +
                usernameTextField.getText() + "\n" +
                passwordField.getText();

        close(event);
    }

    public void updateChanges(KeyEvent keyEvent) {
        if (!nameTextField.getText().isEmpty() && !usernameTextField.getText().isEmpty() && !passwordField.getText().isEmpty())
            registerButton.setDisable(false);
        else
            registerButton.setDisable(true);
    }

    public void close(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();

        stage.close();
    }

    public String getRegisterDetails() {
        return registerDetails;
    }
}
