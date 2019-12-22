import java.io.IOException;
import java.net.SocketException;

public class DiretorioMain {
    public static void main(String[] args) {
        try {
            Diretorio diretorio = new Diretorio();

            diretorio.answerRequests();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
