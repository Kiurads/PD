import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        Server server;

        try {
            server = new Server();
        } catch (IOException e) {
            System.out.println("No proxy available");
        }


    }
}
