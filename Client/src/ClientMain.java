import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) {
        try {
            Client client = new Client("localhost");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
