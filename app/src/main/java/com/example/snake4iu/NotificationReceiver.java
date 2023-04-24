package com.example.snake4iu;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "1",
                    "App-Benachrichtigungskanal",
                    NotificationManager.IMPORTANCE_DEFAULT
                    );
            NotificationManager notificationManager = null;

            notificationManager = context.getSystemService(NotificationManager.class);

            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
