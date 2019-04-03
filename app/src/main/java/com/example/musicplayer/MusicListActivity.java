package com.example.musicplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MusicListActivity extends AppCompatActivity {
    public static final int RUNTIME_PERMISSION_CODE = 7;
    public static String music[] = new String[60];
    public static Song playSong = new Song();
    Context context;

    String[] ListElements = new String[]{};
    ListView listView;
  public static   ArrayList<Song> songs = new ArrayList<>();
    List<String> ListElementsArrayList;
    ArrayAdapter<String> adapter;
    ContentResolver contentResolver;
    Cursor cursor;
    Uri uri;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music_list);

        listView = (ListView) findViewById(R.id.listView1);

        button = (Button) findViewById(R.id.button);

        context = getApplicationContext();

        ListElementsArrayList = new ArrayList<>(Arrays.asList(ListElements));

        adapter = new ArrayAdapter<String>
                (MusicListActivity.this, android.R.layout.simple_list_item_1, ListElementsArrayList);

        // Requesting run time permission for Read External Storage.
        AndroidRuntimePermission();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GetAllMediaMp3Files();

                listView.setAdapter(adapter);

            }
        });

        // ListView on item selected listener.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playSong = songs.get(position);
                // TODO Auto-generated method stub
                // Showing ListView Item Click Value using Toast.
                startActivity(new Intent(MusicListActivity.this, MainActivity.class));

                Toast.makeText(MusicListActivity.this, parent.getAdapter().getItem(position).toString(), Toast.LENGTH_LONG).show();

            }
        });

    }


    public void GetAllMediaMp3Files() {
        int i = 1;
        contentResolver = context.getContentResolver();

        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        cursor = contentResolver.query(
                uri, // Uri
                null,
                null,
                null,
                null
        );

        if (cursor == null) {

            Toast.makeText(MusicListActivity.this, "Something Went Wrong.", Toast.LENGTH_LONG);

        } else if (!cursor.moveToFirst()) {

            Toast.makeText(MusicListActivity.this, "No Music Found on SD Card.", Toast.LENGTH_LONG);

        } else {

            int Title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);


            //Getting Song ID From Cursor.
            //int id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);

            do {

                // You can also get the Song ID using cursor.getLong(id).
                //long SongID = cursor.getLong(id);
//String PATH=cursor.
                String path = cursor.getString(column_index);
                String SongTitle = cursor.getString(Title);

                // Adding Media File Names to ListElementsArrayList.
                ListElementsArrayList.add(SongTitle);
                songs.add(new Song(SongTitle,path));

            } while (cursor.moveToNext());
        }
    }

    // Creating Runtime permission function.
    public void AndroidRuntimePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(MusicListActivity.this);
                    alert_builder.setMessage("External Storage Permission is Required.");
                    alert_builder.setTitle("Please Grant Permission.");
                    alert_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            ActivityCompat.requestPermissions(
                                    MusicListActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    RUNTIME_PERMISSION_CODE

                            );
                        }
                    });

                    alert_builder.setNeutralButton("Cancel", null);

                    AlertDialog dialog = alert_builder.create();

                    dialog.show();

                } else {

                    ActivityCompat.requestPermissions(
                            MusicListActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            RUNTIME_PERMISSION_CODE
                    );
                }
            } else {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case RUNTIME_PERMISSION_CODE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
            }
        }
    }
}
