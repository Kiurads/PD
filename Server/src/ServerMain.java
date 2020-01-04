import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.Scanner;

public class ServerMain {
    public static void main(String[] args) {
        Server server;
        Scanner scanner = new Scanner(System.in);

        System.out.print("Insert proxy address>");

        try {
            server = new Server(InetAddress.getByName(scanner.nextLine()));

            server.start();
        } catch (IOException | InterruptedException e) {
            System.out.println("No proxy available");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
