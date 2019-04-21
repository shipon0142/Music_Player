package com.example.musicplayer;

public class Song {
    private String title;
    private String path;
    private String artist;

    public Song() {
    }

    public Song(String title, String path, String artist) {
        this.title = title;
        this.path = path;
        this.artist = artist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
