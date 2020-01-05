package model.data;

import java.io.File;

public class Song {
    private String name;
    private String artist;
    private String album;
    private String genre;
    private int year;

    private File file;

    public Song(String[] songInfo) {
        name = songInfo[1];
        artist = songInfo[2];
        album = songInfo[3];
        year = Integer.parseInt(songInfo[4]);
        genre = songInfo[5];
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return name + "\n" + artist + "\n" + album + " - " + year + "\n" + genre;
    }

    public String getName() {
        return name;
    }
}
