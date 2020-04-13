package com.minicap.collarapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "Whistle_Alert";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        //starting from Oreo, notifications need to belong to a channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel alertChannel = new NotificationChannel(CHANNEL_ID, "Whistle Alert Channel", NotificationManager.IMPORTANCE_DEFAULT);
            alertChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), new AudioAttributes.Builder().setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED).setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT).build());
            alertChannel.setVibrationPattern( new long[]{100,100,200,200} );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(alertChannel);
        }
    }
}
