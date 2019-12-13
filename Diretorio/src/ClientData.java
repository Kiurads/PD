import java.net.InetAddress;

public class ClientData {
    private InetAddress addr;
    private int port;

    public ClientData(InetAddress a, int p){
        addr = a;
        port = p;
    }

    public InetAddress getAddr() {
        return addr;
    }

    public void setAddr(InetAddress addr) {
        this.addr = addr;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
