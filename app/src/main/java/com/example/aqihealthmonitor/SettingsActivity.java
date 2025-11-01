package com.example.aqihealthmonitor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aqihealthmonitor.models.UserProfile;
import com.example.aqihealthmonitor.receivers.AlarmReceiver;
import com.example.aqihealthmonitor.utils.AQIHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private static final String USER_ID = "user_123";
    private static final int DAILY_ALARM_REQUEST_CODE = 1000;
    private Switch notificationsSwitch, dailyUpdatesSwitch, darkModeSwitch;
    private SeekBar thresholdSeekBar;
    private TextView thresholdValueText, thresholdCategoryText;
    private Button saveButton;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNav;
    private SharedPreferences sharedPreferences;

    private DatabaseReference databaseRef;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeViews();
        setupFirebase();
        setupBottomNavigation();
        setupSeekBar();
        setupDarkMode();
        loadUserProfile();
        setupSaveButton();
    }

    private void initializeViews() {
        notificationsSwitch = findViewById(R.id.notificationsSwitch);
        dailyUpdatesSwitch = findViewById(R.id.dailyUpdatesSwitch);
        darkModeSwitch = findViewById(R.id.darkModeSwitch);
        thresholdSeekBar = findViewById(R.id.thresholdSeekBar);
        thresholdValueText = findViewById(R.id.thresholdValueText);
        thresholdCategoryText = findViewById(R.id.thresholdCategoryText);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);
        bottomNav = findViewById(R.id.bottomNavigation);
        sharedPreferences = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
    }

    private void setupFirebase() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_settings);
    }

    private void setupSeekBar() {
        thresholdSeekBar.setMax(300);
        thresholdSeekBar.setProgress(100);

        thresholdSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 10) {
                    seekBar.setProgress(10);
                    progress = 10;
                }
                updateThresholdDisplay(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        updateThresholdDisplay(100);
    }

    private void setupDarkMode() {
        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(isDarkMode);

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });
    }

    private void updateThresholdDisplay(int value) {
        thresholdValueText.setText(String.valueOf(value));
        String category = AQIHelper.getAQICategory(value);
        thresholdCategoryText.setText("Alert when AQI exceeds " + value + " (" + category + ")");
    }

    private void loadUserProfile() {
        progressBar.setVisibility(View.VISIBLE);

        databaseRef.child("users").child(USER_ID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressBar.setVisibility(View.GONE);

                        if (snapshot.exists()) {
                            userProfile = snapshot.getValue(UserProfile.class);
                            displaySettings();
                        } else {
                            // Create new profile
                            userProfile = new UserProfile(USER_ID);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(SettingsActivity.this, 
                                "Failed to load settings", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displaySettings() {
        if (userProfile == null) return;

        notificationsSwitch.setChecked(userProfile.isNotificationsEnabled());
        dailyUpdatesSwitch.setChecked(userProfile.isDailyUpdates());
        thresholdSeekBar.setProgress(userProfile.getAlertThreshold());
        updateThresholdDisplay(userProfile.getAlertThreshold());
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        if (userProfile == null) {
            userProfile = new UserProfile(USER_ID);
        }

        userProfile.setNotificationsEnabled(notificationsSwitch.isChecked());
        userProfile.setDailyUpdates(dailyUpdatesSwitch.isChecked());
        userProfile.setAlertThreshold(thresholdSeekBar.getProgress());
        userProfile.setLastUpdated(System.currentTimeMillis());

        if (dailyUpdatesSwitch.isChecked()) {
            scheduleDailyAlarm();
        } else {
            cancelDailyAlarm();
        }

        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);

        databaseRef.child("users").child(USER_ID).setValue(userProfile)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    Toast.makeText(SettingsActivity.this, 
                            "Settings saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    Toast.makeText(SettingsActivity.this, 
                            "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void scheduleDailyAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 
                DAILY_ALARM_REQUEST_CODE, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, 
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, 
                pendingIntent);

        Toast.makeText(this, "Daily updates scheduled for 8:00 AM", Toast.LENGTH_SHORT).show();
    }

    private void cancelDailyAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 
                DAILY_ALARM_REQUEST_CODE, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        alarmManager.cancel(pendingIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_settings);
    }
}
