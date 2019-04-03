package com.example.musicplayer;

public class Song {
    private String title;
    private String path;

    public Song() {
    }

    public String getTitle() {
        return title;
    }

    public Song(String title, String path) {
        this.title = title;
        this.path = path;
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
