package com.music.musicplayer;

import android.content.Context;

/**
 * Created by kuldeep on 13/02/18.
 */

public class PojoClass {


    private String song_name;
    private Context context;

    public PojoClass(Context context,String song_name, String singer_name, String time, String path) {
        this.song_name = song_name;
        this.singer_name = singer_name;
        this.time = time;
        this.path = path;
        this.context=context;
    }

    private String singer_name;
    private String time;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getSinger_name() {
        return singer_name;
    }

    public void setSinger_name(String singer_name) {
        this.singer_name = singer_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
