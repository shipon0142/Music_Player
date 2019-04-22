package com.music.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import java.util.Collections;
import java.util.Comparator;

public class MusicActivity extends AppCompatActivity {
    public SharedPreferences.Editor PrefsEditor;
    public SharedPreferences Preferences;
    ImageView playButtonIV;
    RecyclerView songRecyclerView;
    ListAdapter listAdapter;
    TextView songTitleTV, songArtistTV;
    ArrayList<PojoClass> pojoClassArrayList;
    boolean isselected = false;
    public static int songNo, songDuration, lastsong;
    ContentResolver contentResolver;
    Cursor cursor;
    boolean backPressed = false;
    boolean isRunning;
    Uri uri;
    MediaPlayer mp;
    SeekBar seekBar;
    TextView elapsedTimeLabel, remainingTimeLabel;
    int totalTime;
    PojoClass song;
    ImageView nextIV, prevIV;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            seekBar.setProgress(currentPosition);

            String elapsedTime = createTimeLabel(currentPosition);
            songDuration = currentPosition;
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime - currentPosition);
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
        nextIV = findViewById(R.id.NextIV);
        prevIV = findViewById(R.id.PrevIV);
        pojoClassArrayList = new ArrayList<>();

        Preferences = this.getSharedPreferences("Prefs", MODE_PRIVATE);
        PrefsEditor = Preferences.edit();
        backPressed = false;

        songRecyclerView = (RecyclerView) findViewById(R.id.rv);
        pojoClassArrayList.clear();
        isGivenPermission();


    }

    private void retriveAndSetAllmusic() {
        GetAllMediaMp3Files();
        setAdopter();

        songNo = Preferences.getInt("songNo", 0);
        songDuration = Preferences.getInt("songDuration", 0);
        isRunning = Preferences.getBoolean("songIsRunning", false);
        lastsong = songNo;
        playSong2();
        setPlayButtonClickListener();
    }

    private void isGivenPermission() {
        boolean flag = false;
        if (ContextCompat.checkSelfPermission(MusicActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MusicActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MusicActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        11);
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MusicActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        11);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            retriveAndSetAllmusic();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 11: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    retriveAndSetAllmusic();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    onDestroy();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    private void setPlayButtonClickListener() {
        playButtonIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mp.isPlaying()) {
                    mp.start();
                    //  listAdapter.notifyItemChanged(songNo);
                    playButtonIV.setImageResource(R.drawable.ic_pause);

                } else {

                    mp.pause();
                    playButtonIV.setImageResource(R.drawable.ic_play);
                }

            }
        });
        nextIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songNo = songNo + 1;
                try {
                    playSong();

                } catch (Exception e) {
                    songNo = 0;
                    playSong();
                }

            }
        });
        prevIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                songNo = songNo - 1;
                try {
                    playSong();
                } catch (Exception e) {
                    songNo = 0;
                    playSong();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //  Toast.makeText(this,"onStop",Toast.LENGTH_SHORT).show();

    }

    private void setAdopter() {
        listAdapter = new ListAdapter(MusicActivity.this, pojoClassArrayList, new PojoClassCallback() {
            @Override
            public void getSong(int song) {

                songNo = song;
                if (songNo == lastsong) {

                } else {
                    playSong();
                }
                lastsong = songNo;


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
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                songNo = songNo + 1;
                try {
                    playSong();
                } catch (Exception e) {
                    songNo = 0;
                    playSong();
                }
            }
        });
    }

    private void playSong2() {
        song = pojoClassArrayList.get(songNo);
        if (mp != null) {
            mp.reset();
        }
        songTitleTV.setText(song.getSong_name());
        songArtistTV.setText(song.getSinger_name());

        mp = MediaPlayer.create(MusicActivity.this, Uri.parse(song.getPath()));
        // mp.setLooping(true);
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
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                songNo = songNo + 1;
                try {
                    playSong();
                } catch (Exception e) {
                    songNo = 0;
                    playSong();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PrefsEditor.putInt("songNo", songNo);
        PrefsEditor.putInt("songDuration", songDuration);
        PrefsEditor.putBoolean("songIsRunning", false);
        PrefsEditor.commit();
    }

    @Override
    public void onBackPressed() {
        // onStop();
        if (mp.isPlaying()) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

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



            do {


                String path = cursor.getString(column_index);
                String SongTitle = cursor.getString(Title);
                String artist = cursor.getString(singer);
                String Time = cursor.getString(time);
                if (artist.contains("unknown")) {
                    artist = "UnKnown artist";
                }

                pojoClassArrayList.add(new PojoClass(this, SongTitle, artist, Time, path));

            } while (cursor.moveToNext());
            Collections.sort(pojoClassArrayList, new Comparator<PojoClass>() {
                @Override
                public int compare(PojoClass s1, PojoClass s2) {
                    return s1.getSong_name().compareToIgnoreCase(s2.getSong_name());
                }
            });
        }
    }

}
