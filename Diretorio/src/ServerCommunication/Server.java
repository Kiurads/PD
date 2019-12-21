package ServerCommunication;

import java.net.InetAddress;

public class Server {
    private InetAddress address;
    private Integer port;

    public Server(InetAddress address, Integer port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public Integer getPort() {
        return port;
    }

    @Override
    public String toString() {
        return getAddress().getHostAddress() + "\n" + getPort();
    }
}
