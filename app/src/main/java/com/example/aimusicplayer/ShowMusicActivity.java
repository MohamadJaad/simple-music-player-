package com.example.aimusicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class ShowMusicActivity extends AppCompatActivity {
    private String[] itemAll;
    private ListView mSongsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_music);
        mSongsList = findViewById(R.id.songsList);
        appExternalStoragePermission();

    }

    public void appExternalStoragePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        displayAudioSongsName();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> readOnlySongs(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        for (File individualFile : files) {
            if (individualFile.isDirectory() && !individualFile.isHidden()) {
                arrayList.addAll(readOnlySongs(individualFile));

            } else if (individualFile.getName().endsWith(".mp3") || individualFile.getName().endsWith(".aac") || individualFile.getName().endsWith(".wav") || individualFile.getName().endsWith(".wma")) {
                arrayList.add(individualFile);
            }
        }
        return arrayList;
    }

    private void displayAudioSongsName() {
        final ArrayList<File> arrayList = readOnlySongs(Environment.getExternalStorageDirectory());
        itemAll = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            itemAll[i] = arrayList.get(i).getName();



        }
        Container.titles = itemAll;
        Adapter adapter = new Adapter(getApplicationContext(),itemAll);
        mSongsList.setAdapter(adapter);
        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String name = Container.titles[i];
                Intent intent = new Intent(ShowMusicActivity.this,MainActivity.class);
                intent.putExtra("songs",arrayList);
                intent.putExtra("name",name);
                intent.putExtra("position",i);
                startActivity(intent);
            }
        });

    }
}
