import java.io.IOException;

public class DiretorioThread extends Thread {
    private Diretorio diretorio;
    private boolean running;

    public DiretorioThread(Diretorio diretorio) {
        this.diretorio = diretorio;
        running = true;
    }

    @Override
    public void run() {
        try {
            while(running)
                diretorio.answerRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void terminate() {
        running = false;
    }
}
