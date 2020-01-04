package model.data;

import java.io.File;

public class Song {
    private int id;
    private String name;
    private String artist;
    private String album;
    private String genre;
    private int year;

    private File file;

    public Song(int id, String name, String artist, String album, String genre, int year) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
    }

    public Song(String[] songInfo) {
        //TODO
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenre() {
        return genre;
    }

    public int getYear() {
        return year;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
