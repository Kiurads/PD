import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        Server server;

        try {
            server = new Server();

            server.start();
        } catch (IOException | InterruptedException e) {
            System.out.println("No proxy available");
        }
    }
}
