package com.example.aqihealthmonitor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import androidx.appcompat.app.AppCompatDelegate;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import com.example.aqihealthmonitor.api.AQIApiService;
import com.example.aqihealthmonitor.api.ApiClient;
import com.example.aqihealthmonitor.models.AQIData;
import com.example.aqihealthmonitor.models.UserProfile;
import com.example.aqihealthmonitor.receivers.AlarmReceiver;
import com.example.aqihealthmonitor.utils.AQIHelper;
import com.example.aqihealthmonitor.utils.NotificationHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String API_KEY = "7a9ddfe4b867da8d229dc42ecef5b21b";
    private static final int LOCATION_PERMISSION_CODE = 100;
    private static final String USER_ID = "user_123";
    private EditText searchEditText;
    private LinearLayout searchResultsLayout;
    private CardView aqiCard;
    private TextView aqiValueText, aqiCategoryText, locationText, adviceText, timestampText;
    private TextView pm25Text, pm10Text, o3Text, no2Text, so2Text, coText;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNav;
    private ApiClient apiClient;
    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference databaseRef;
    private UserProfile userProfile;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        SharedPreferences prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        
        setContentView(R.layout.activity_home);

        initializeViews();
        setupAPI();
        setupFirebase();
        setupLocationClient();
        setupSearchBar();
        setupBottomNavigation();
        loadUserProfile();
        requestLocationAndFetchAQI();
    }

    private void initializeViews() {
        searchEditText = findViewById(R.id.searchEditText);
        searchResultsLayout = findViewById(R.id.searchResultsLayout);
        aqiCard = findViewById(R.id.aqiCard);
        aqiValueText = findViewById(R.id.aqiValueText);
        aqiCategoryText = findViewById(R.id.aqiCategoryText);
        locationText = findViewById(R.id.locationText);
        adviceText = findViewById(R.id.adviceText);
        timestampText = findViewById(R.id.timestampText);
        pm25Text = findViewById(R.id.pm25Text);
        pm10Text = findViewById(R.id.pm10Text);
        o3Text = findViewById(R.id.o3Text);
        no2Text = findViewById(R.id.no2Text);
        so2Text = findViewById(R.id.so2Text);
        coText = findViewById(R.id.coText);
        progressBar = findViewById(R.id.progressBar);
        bottomNav = findViewById(R.id.bottomNavigation);
    }

    private void setupAPI() {
        apiClient = ApiClient.getInstance();
        notificationHelper = new NotificationHelper(this);
    }

    private void setupFirebase() {
        databaseRef = FirebaseDatabase.getInstance().getReference();
    }

    private void setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupSearchBar() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    searchCity(s.toString());
                } else {
                    searchResultsLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }
            return false;
        });
        bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void loadUserProfile() {
        databaseRef.child("users").child(USER_ID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            userProfile = snapshot.getValue(UserProfile.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(HomeActivity.this, "Failed to load profile", 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void requestLocationAndFetchAQI() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 
                    LOCATION_PERMISSION_CODE);
            return;
        }
        getCurrentLocationAndFetchAQI();
    }

    private void getCurrentLocationAndFetchAQI() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        fetchAQIData(location.getLatitude(), location.getLongitude(), 
                                "Current Location");
                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Location error: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void searchCity(String query) {
        apiClient.getApiService().searchCity(query, 5, API_KEY)
                .enqueue(new Callback<List<AQIApiService.GeocodingResponse>>() {
                    @Override
                    public void onResponse(Call<List<AQIApiService.GeocodingResponse>> call,
                                           Response<List<AQIApiService.GeocodingResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            displaySearchResults(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AQIApiService.GeocodingResponse>> call, 
                                          Throwable t) {
                        Toast.makeText(HomeActivity.this, "Search failed", 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displaySearchResults(List<AQIApiService.GeocodingResponse> results) {
        searchResultsLayout.removeAllViews();
        searchResultsLayout.setVisibility(View.VISIBLE);

        for (AQIApiService.GeocodingResponse result : results) {
            TextView resultView = new TextView(this);
            String displayName = result.name + ", " + result.country;
            resultView.setText(displayName);
            resultView.setTextSize(16);
            resultView.setPadding(32, 24, 32, 24);
            resultView.setTextColor(getColor(R.color.text_primary));
            resultView.setBackground(getDrawable(R.drawable.bg_search_result_item));
            resultView.setOnClickListener(v -> {
                fetchAQIData(result.lat, result.lon, result.name);
                searchEditText.setText("");
                searchResultsLayout.setVisibility(View.GONE);
            });
            searchResultsLayout.addView(resultView);
        }
    }

    private void fetchAQIData(double lat, double lon, String location) {
        progressBar.setVisibility(View.VISIBLE);

        apiClient.getApiService().getAirPollution(lat, lon, API_KEY)
                .enqueue(new Callback<AQIApiService.AirPollutionResponse>() {
                    @Override
                    public void onResponse(Call<AQIApiService.AirPollutionResponse> call,
                                           Response<AQIApiService.AirPollutionResponse> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful() && response.body() != null && 
                                !response.body().list.isEmpty()) {
                            processAQIData(response.body().list.get(0), location, lat, lon);
                        } else {
                            Toast.makeText(HomeActivity.this, "Failed to fetch AQI data", 
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AQIApiService.AirPollutionResponse> call, 
                                          Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(HomeActivity.this, "Error: " + t.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void processAQIData(AQIApiService.AirPollutionResponse.AirQuality airQuality,
                                String location, double lat, double lon) {
        int aqi = AQIHelper.calculateAQIFromPM25(airQuality.components.pm2_5);

        AQIData aqiData = new AQIData(location, aqi,
                airQuality.components.pm2_5,
                airQuality.components.pm10,
                airQuality.components.o3,
                airQuality.components.no2,
                airQuality.components.so2,
                airQuality.components.co,
                lat, lon);

        displayAQIData(aqiData);

        saveAQIDataToFirebase(aqiData);

        checkAndSendAlerts(aqiData);

        AlarmReceiver.saveLastAQIData(this, location, aqi, aqiData.getHealthAdvice());
    }

    private void displayAQIData(AQIData aqiData) {
        aqiValueText.setText(String.valueOf(aqiData.getAqi()));
        aqiCategoryText.setText(aqiData.getCategory());
        locationText.setText(aqiData.getLocation());
        timestampText.setText("Updated: " + AQIHelper.formatTimestamp(aqiData.getTimestamp()));

        aqiCard.setCardBackgroundColor(aqiData.getCategoryColor());

        String advice = userProfile != null ?
                userProfile.getPersonalizedWarning(aqiData.getAqi()) : null;
        adviceText.setText(advice != null ? advice : aqiData.getHealthAdvice());

        pm25Text.setText(String.format("PM2.5: %.1f µg/m³", aqiData.getPm25()));
        pm10Text.setText(String.format("PM10: %.1f µg/m³", aqiData.getPm10()));
        o3Text.setText(String.format("O₃: %.1f µg/m³", aqiData.getO3()));
        no2Text.setText(String.format("NO₂: %.1f µg/m³", aqiData.getNo2()));
        so2Text.setText(String.format("SO₂: %.1f µg/m³", aqiData.getSo2()));
        coText.setText(String.format("CO: %.1f µg/m³", aqiData.getCo()));
    }

    private void saveAQIDataToFirebase(AQIData aqiData) {
        databaseRef.child("aqi_history").child(USER_ID)
                .child(String.valueOf(System.currentTimeMillis()))
                .setValue(aqiData);
    }

    private void checkAndSendAlerts(AQIData aqiData) {
        if (userProfile != null && userProfile.isNotificationsEnabled()) {
            if (aqiData.getAqi() > userProfile.getAlertThreshold()) {
                notificationHelper.showAQIAlert(aqiData.getLocation(), 
                        aqiData.getAqi(), aqiData.getCategory());

                String warning = userProfile.getPersonalizedWarning(aqiData.getAqi());
                if (warning != null) {
                    notificationHelper.showHealthWarning(warning);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndFetchAQI();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomNav.setSelectedItemId(R.id.nav_home);
        loadUserProfile();
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        return false;
    }
}
