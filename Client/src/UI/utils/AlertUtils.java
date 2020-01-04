package UI.utils;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

public class AlertUtils {
    public static void showAlert(String receiveMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(receiveMessage);

        alert.showAndWait();
    }

    public static void showException(Exception ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception Dialog");
        alert.setHeaderText("Exception Dialog");
        alert.setContentText(ex.getMessage());

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    public static String getText(String title, String content, String text) {
        TextInputDialog alert = new TextInputDialog(text);
        alert.setContentText(content);
        alert.setTitle(title);

        return alert.showAndWait().orElse(null);
    }

    public static Optional<ButtonType> getConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        return alert.showAndWait();
    }
}
