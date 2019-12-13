public interface MyMessages {
    public static final String REQUEST_SERVER = "REQUESTING SERVER";
    public static final String LOGIN_REQUEST = "LOGIN REQUEST";
    public static final String LOGIN_FAILED = "LOGIN FAILED";
    public static final String LOGIN_SUCCESSFUL = "LOGIN SUCCESSFUL";

    public static final String REGISTER = "register";
    public static final String REGISTER_FAILED = "REGISTER FAILED";
    public static final String REGISTER_SUCCESSFUL = "REGISTER SUCCESSFUL";

    public static final String CONNECT_REQUEST = "CONNECT REQUEST";
    public static final String CONNECT_CONFIRM = "CONNECT SUCCESSFUL";
    public static final String CONNECT_FAILED = "CONNECT UNSUCCESSFUL";
    public static final String DISCONNECT_SERVER = "DISCONNECT SERVER";

    public static final String LIST_SONGS = "listSongs";
    public static final String LIST_PLAYLISTS = "listPlaylists";

    public static final String CREATE_SONG = "createSong";
    public static final String EDIT_SONG = "editSong";
    public static final String DELETE_SONG = "deleteSong";

    public static final String SHOW_PLAYLIST = "showPlaylist";
    public static final String CREATE_PLAYLIST = "createPlaylist";
    public static final String EDIT_PLAYLIST = "editPlaylist";
    public static final String DELETE_PLAYLIST = "deletePlaylist";

    public static final String PLAY_SONG = "playSong";
    public static final String PLAY_PLAYLIST = "playPlaylist";

    public static final String EXIT_MESSAGE = "EXIT";

    //Inteiros
    public static final int TIMEOUT = 10; //segundos
    public static final int MAX_SIZE = 256;
    public static final int DEFAULT_DS_PORT = 5005;
    public static final int DEFAULT_SERVER_PORT = 5006;
    //Doubles

    //Longs
}
