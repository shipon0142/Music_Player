package com.example.musicplayer;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {
    public SharedPreferences.Editor PrefsEditor;
    public SharedPreferences Preferences;
    ImageView playButtonIV;
    RecyclerView songRecyclerView;
    ListAdapter listAdapter;
    TextView songTitleTV, songArtistTV;
    ArrayList<PojoClass> pojoClassArrayList;
    boolean isselected = false;
    ContentResolver contentResolver;
    Cursor cursor;
    int songNo, songDuration;
    boolean isRunning;
    Uri uri;
    MediaPlayer mp;
    SeekBar seekBar;
    TextView elapsedTimeLabel, remainingTimeLabel;
    int totalTime;
    PojoClass song;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            seekBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            songDuration = currentPosition;
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime - currentPosition);
            // remainingTimeLabel.setText("- " + remainingTime);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen1);
        playButtonIV = (ImageView) findViewById(R.id.playbutton1);
        seekBar = findViewById(R.id.SeekBar);
        elapsedTimeLabel = findViewById(R.id.ElapsedTimeLabel);
        remainingTimeLabel = findViewById(R.id.RemainingTimeLabel);
        songTitleTV = findViewById(R.id.SongTitleTV);
        songArtistTV = findViewById(R.id.SongArtistTV);
        pojoClassArrayList = new ArrayList<>();

        Preferences = this.getSharedPreferences("Prefs", MODE_PRIVATE);
        PrefsEditor = Preferences.edit();




   /*     for (int i = 0; i < song_name.length; i++) {

            PojoClass pojoClass = new PojoClass();
            pojoClass.setSong_name(song_name[i]);
            pojoClass.setSinger_name(singer_name[i]);
            pojoClass.setTime(time[i]);

            pojoClassArrayList.add(pojoClass);
        }*/

        songRecyclerView = (RecyclerView) findViewById(R.id.rv);

        setPlayButtonClickListener();

    }

    private void setPlayButtonClickListener() {
        playButtonIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!mp.isPlaying()) {
                    // Stopping

                    mp.start();

                    PrefsEditor.putBoolean("songIsRunning", true);
                    PrefsEditor.commit();
                    playButtonIV.setImageResource(R.drawable.ic_pause);

                } else {
                    // Playing
                    PrefsEditor.putBoolean("songIsRunning", false);
                    PrefsEditor.commit();
                    mp.pause();
                    playButtonIV.setImageResource(R.drawable.ic_play);
                }
               /* if (isselected == false) {
                    playButtonIV.setImageResource(R.drawable.ic_pause);
                    isselected = true;
                } else {
                    playButtonIV.setImageResource(R.drawable.ic_play);
                    isselected = false;
                }*/
              //  setPlayButtonClickListener();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pojoClassArrayList.clear();
        GetAllMediaMp3Files();
        setAdopter();
        songNo = Preferences.getInt("songNo", 0);
        songDuration = Preferences.getInt("songDuration", 0);
        isRunning=Preferences.getBoolean("songIsRunning",false);


        /*song=pojoClassArrayList.get(songNo);
        songTitleTV.setText(song.getSong_name());
        songArtistTV.setText(song.getSinger_name());*/
        if(!isRunning){
            //if(!mp.isPlaying()) {
                playSong2();
            //}
           // else {
            //    String str="dff";
           // }
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void setAdopter() {
        listAdapter = new ListAdapter(MusicActivity.this, pojoClassArrayList, new PojoClassCallback() {
            @Override
            public void getSong(int song) {

                songNo = song;
                playSong();

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MusicActivity.this);
        songRecyclerView.setLayoutManager(mLayoutManager);
        songRecyclerView.setItemAnimator(new DefaultItemAnimator());
        songRecyclerView.setAdapter(listAdapter);
    }

    private void playSong() {
        song = pojoClassArrayList.get(songNo);
        if (mp != null) {
            mp.reset();
        }
        songTitleTV.setText(song.getSong_name());
        songArtistTV.setText(song.getSinger_name());

        mp = MediaPlayer.create(MusicActivity.this, Uri.parse(song.getPath()));
        mp.setLooping(true);
        mp.seekTo(0);
        mp.setVolume(0.5f, 0.5f);
        totalTime = mp.getDuration();
        seekBar.setMax(totalTime);
        remainingTimeLabel.setText("" + createTimeLabel(totalTime));
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mp.seekTo(progress);
                            seekBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );
        mp.start();
        playButtonIV.setImageResource(R.drawable.ic_pause);
        PrefsEditor.putBoolean("songIsRunning", true);
        PrefsEditor.commit();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }
    private void playSong2() {
        song = pojoClassArrayList.get(songNo);
        if (mp != null) {
            mp.reset();
        }
        songTitleTV.setText(song.getSong_name());
        songArtistTV.setText(song.getSinger_name());

        mp = MediaPlayer.create(MusicActivity.this, Uri.parse(song.getPath()));
        mp.setLooping(true);
        mp.seekTo(songDuration);
        mp.setVolume(0.5f, 0.5f);
        totalTime = mp.getDuration();
        seekBar.setMax(totalTime);
        remainingTimeLabel.setText("" + createTimeLabel(totalTime));
        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mp.seekTo(progress);
                            seekBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefsEditor.putInt("songNo", songNo);
        PrefsEditor.putInt("songDuration", songDuration);
        PrefsEditor.putBoolean("songIsRunning", false);
        PrefsEditor.commit();
    }

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }


    public void GetAllMediaMp3Files() {
        int i = 1;
        contentResolver = this.getContentResolver();

        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {

            Toast.makeText(MusicActivity.this, "Something Went Wrong.", Toast.LENGTH_LONG);

        } else if (!cursor.moveToFirst()) {

            Toast.makeText(MusicActivity.this, "No Music Found on SD Card.", Toast.LENGTH_LONG);

        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int singer = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int time = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);


            //Getting Song ID From Cursor.
            //int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            do {

                // You can also get the Song ID using cursor.getLong(id).
                //long SongID = cursor.getLong(id);
                //String PATH=cursor.
                String path = cursor.getString(column_index);
                String SongTitle = cursor.getString(Title);
                String artist = cursor.getString(singer);
                String Time = cursor.getString(time);
                if (artist.contains("unknown")) {
                    artist = "UnKnown artist";
                }
                // Adding Media File Names to ListElementsArrayList.
                //ListElementsArrayList.add(SongTitle);
                // songs.add(new Song(SongTitle,path,artist));
                pojoClassArrayList.add(new PojoClass(this, SongTitle, artist, Time, path));

            } while (cursor.moveToNext());
        }
    }

}
