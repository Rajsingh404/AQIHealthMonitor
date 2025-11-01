package com.example.aqihealthmonitor.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.example.aqihealthmonitor.utils.NotificationHelper;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String PREFS_NAME = "AQIPrefs";
    private static final String KEY_LAST_LOCATION = "last_location";
    private static final String KEY_LAST_AQI = "last_aqi";
    private static final String KEY_LAST_ADVICE = "last_advice";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String location = prefs.getString(KEY_LAST_LOCATION, "Unknown");
        int aqi = prefs.getInt(KEY_LAST_AQI, 0);
        String advice = prefs.getString(KEY_LAST_ADVICE, "No data available");

        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.showDailyUpdate(location, aqi, advice);
    }

    public static void saveLastAQIData(Context context, String location, int aqi, String advice) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(KEY_LAST_LOCATION, location)
                .putInt(KEY_LAST_AQI, aqi)
                .putString(KEY_LAST_ADVICE, advice)
                .apply();
    }
}
