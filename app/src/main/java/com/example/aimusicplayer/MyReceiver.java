package com.example.aimusicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MainActivity mainActivity = MainActivity.getInstance();
        String statue = intent.getStringExtra("songStatue");

        if ("playPauseSong".equals(statue)){
            mainActivity.playPauseSong();
        }
       else if ("nextSong".equals(statue)) {
            mainActivity.playNextSong();

        } else if ("previousSong".equals(statue)) {
            mainActivity.playPreviousSong();
        }


    }
}
