package com.example.aqihealthmonitor.models;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private String userId;
    private List<String> conditions;
    private int alertThreshold;
    private boolean notificationsEnabled;
    private boolean dailyUpdates;
    private String lastLocation;
    private long lastUpdated;

    public static final String CONDITION_ASTHMA = "asthma";
    public static final String CONDITION_ALLERGIES = "allergies";
    public static final String CONDITION_HEART = "heart";
    public static final String CONDITION_LUNG = "lung";
    public static final String CONDITION_ELDERLY = "elderly";
    public static final String CONDITION_CHILDREN = "children";

    public UserProfile() {
        this.conditions = new ArrayList<>();
        this.alertThreshold = 100;
        this.notificationsEnabled = true;
        this.dailyUpdates = false;
    }

    public UserProfile(String userId) {
        this.userId = userId;
        this.conditions = new ArrayList<>();
        this.alertThreshold = 100;
        this.notificationsEnabled = true;
        this.dailyUpdates = false;
        this.lastUpdated = System.currentTimeMillis();
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public List<String> getConditions() { return conditions; }
    public void setConditions(List<String> conditions) { this.conditions = conditions; }

    public int getAlertThreshold() { return alertThreshold; }
    public void setAlertThreshold(int alertThreshold) { this.alertThreshold = alertThreshold; }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(boolean notificationsEnabled) { 
        this.notificationsEnabled = notificationsEnabled; 
    }

    public boolean isDailyUpdates() { return dailyUpdates; }
    public void setDailyUpdates(boolean dailyUpdates) { this.dailyUpdates = dailyUpdates; }

    public String getLastLocation() { return lastLocation; }
    public void setLastLocation(String lastLocation) { this.lastLocation = lastLocation; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }

    public void addCondition(String condition) {
        if (!conditions.contains(condition)) {
            conditions.add(condition);
        }
    }

    public void removeCondition(String condition) {
        conditions.remove(condition);
    }

    public boolean hasCondition(String condition) {
        return conditions != null && conditions.contains(condition);
    }

    public boolean hasAnyCondition() {
        return conditions != null && !conditions.isEmpty();
    }


    public String getPersonalizedWarning(int currentAQI) {
        if (!hasAnyCondition()) {
            return null;
        }

        StringBuilder warning = new StringBuilder();

        if (currentAQI > alertThreshold) {
            warning.append("ALERT: AQI exceeds your threshold (").append(alertThreshold).append(")! ");

            if (hasCondition(CONDITION_ASTHMA)) {
                warning.append("Keep rescue inhaler nearby. ");
            }
            if (hasCondition(CONDITION_ALLERGIES)) {
                warning.append("Allergy symptoms may worsen. ");
            }
            if (hasCondition(CONDITION_HEART)) {
                warning.append("Avoid strenuous activities. ");
            }
            if (hasCondition(CONDITION_LUNG)) {
                warning.append("Stay indoors if possible. ");
            }
            if (hasCondition(CONDITION_ELDERLY)) {
                warning.append("Seniors should minimize exposure. ");
            }
            if (hasCondition(CONDITION_CHILDREN)) {
                warning.append("Keep children indoors. ");
            }
        }

        return warning.length() > 0 ? warning.toString().trim() : null;
    }
}
