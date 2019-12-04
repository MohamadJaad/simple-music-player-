package com.example.aimusicplayer;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class App extends Application {
    public static final String CHANNEL_ID = "channel1";
    @Override
    public void onCreate() {
        super.onCreate();
        createMediaPlayerNotificationChannel();
    }

    private void createMediaPlayerNotificationChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"music channel", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("music notification listening");
        }
    }
}
