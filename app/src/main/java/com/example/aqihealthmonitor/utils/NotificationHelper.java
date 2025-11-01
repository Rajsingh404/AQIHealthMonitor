package com.example.aqihealthmonitor.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.aqihealthmonitor.R;


public class NotificationHelper {
    
    private static final String CHANNEL_ID = "aqi_alerts";
    private static final String CHANNEL_NAME = "AQI Alerts";
    private static final String CHANNEL_DESC = "Air Quality Index alert notifications";
    private static final int NOTIFICATION_ID = 1001;

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) 
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void showAQIAlert(String location, int aqi, String category) {
        String title = "AQI Alert: " + location;
        String message = "Current AQI: " + aqi + " (" + category + ")";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 250, 500});

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void showDailyUpdate(String location, int aqi, String advice) {
        String title = "Daily AQI Update: " + location;
        String message = "AQI: " + aqi + " - " + advice;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        notificationManager.notify(NOTIFICATION_ID + 1, builder.build());
    }

    public void showHealthWarning(String warning) {
        String title = "Health Warning";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(warning)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(warning))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 500, 250, 500});

        notificationManager.notify(NOTIFICATION_ID + 2, builder.build());
    }

}
