package com.example.aqihealthmonitor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.aqihealthmonitor.models.UserProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ProfileActivity extends AppCompatActivity {

    private static final String USER_ID = "user_123";

    private CheckBox asthmaCheckBox, allergiesCheckBox, heartCheckBox;
    private CheckBox lungCheckBox, elderlyCheckBox, childrenCheckBox;
    private Button saveButton;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNav;


    private DatabaseReference databaseRef;
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        setupFirebase();
        setupBottomNavigation();
        loadUserProfile();
        setupSaveButton();
    }

    private void initializeViews() {
        asthmaCheckBox = findViewById(R.id.asthmaCheckBox);
        allergiesCheckBox = findViewById(R.id.allergiesCheckBox);
        heartCheckBox = findViewById(R.id.heartCheckBox);
        lungCheckBox = findViewById(R.id.lungCheckBox);
        elderlyCheckBox = findViewById(R.id.elderlyCheckBox);
        childrenCheckBox = findViewById(R.id.childrenCheckBox);
        saveButton = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);
        bottomNav = findViewById(R.id.bottomNavigation);
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
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_profile);
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
                            displayProfile();
                        } else {
                            userProfile = new UserProfile(USER_ID);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(ProfileActivity.this, 
                                "Failed to load profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayProfile() {
        if (userProfile == null) return;

        asthmaCheckBox.setChecked(userProfile.hasCondition(UserProfile.CONDITION_ASTHMA));
        allergiesCheckBox.setChecked(userProfile.hasCondition(UserProfile.CONDITION_ALLERGIES));
        heartCheckBox.setChecked(userProfile.hasCondition(UserProfile.CONDITION_HEART));
        lungCheckBox.setChecked(userProfile.hasCondition(UserProfile.CONDITION_LUNG));
        elderlyCheckBox.setChecked(userProfile.hasCondition(UserProfile.CONDITION_ELDERLY));
        childrenCheckBox.setChecked(userProfile.hasCondition(UserProfile.CONDITION_CHILDREN));
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        if (userProfile == null) {
            userProfile = new UserProfile(USER_ID);
        }

        userProfile.getConditions().clear();

        if (asthmaCheckBox.isChecked()) {
            userProfile.addCondition(UserProfile.CONDITION_ASTHMA);
        }
        if (allergiesCheckBox.isChecked()) {
            userProfile.addCondition(UserProfile.CONDITION_ALLERGIES);
        }
        if (heartCheckBox.isChecked()) {
            userProfile.addCondition(UserProfile.CONDITION_HEART);
        }
        if (lungCheckBox.isChecked()) {
            userProfile.addCondition(UserProfile.CONDITION_LUNG);
        }
        if (elderlyCheckBox.isChecked()) {
            userProfile.addCondition(UserProfile.CONDITION_ELDERLY);
        }
        if (childrenCheckBox.isChecked()) {
            userProfile.addCondition(UserProfile.CONDITION_CHILDREN);
        }

        userProfile.setLastUpdated(System.currentTimeMillis());

        progressBar.setVisibility(View.VISIBLE);
        saveButton.setEnabled(false);

        databaseRef.child("users").child(USER_ID).setValue(userProfile)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, 
                            "Profile saved successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    saveButton.setEnabled(true);
                    Toast.makeText(ProfileActivity.this, 
                            "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_profile);
    }
}
