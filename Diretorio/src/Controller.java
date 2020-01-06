import javafx.scene.control.TextArea;

import java.io.IOException;

public class Controller {
    DiretorioThread thread;
    public TextArea textArea;

    public void initialize() {
        try {
            thread = new DiretorioThread(new Diretorio(this));

            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addText(String text) {
        textArea.setText(textArea.getText() + "\n" + text);
    }

    public void close() throws InterruptedException {
        thread.terminate();

        thread.join();
    }
}
