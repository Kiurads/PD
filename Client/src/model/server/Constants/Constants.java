package model.server.Constants;

public interface Constants {
    String REQUEST_SERVER = "REQUESTING SERVER";
    String NO_SERVERS = "NO_SERVERS";

    String LOGIN_FAILED = "LOGIN FAILED";
    String LOGIN_SUCCESSFUL = "LOGIN SUCCESSFUL";

    String REGISTER_FAILED = "REGISTER FAILED";
    String REGISTER_SUCCESSFUL = "REGISTER SUCCESSFUL";

    //Inteiros
    int TIMEOUT = 10; //segundos
    int MAX_SIZE = 256;
    int DEFAULT_DS_PORT = 5005;
}
