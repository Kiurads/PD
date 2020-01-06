package UI;

import javafx.scene.control.TextInputDialog;

public class AlertUtils {
    public static String getText(String title, String content, String text) {
        TextInputDialog alert = new TextInputDialog(text);
        alert.setContentText(content);
        alert.setTitle(title);

        return alert.showAndWait().orElse(null);
    }
}
