package com.example.aqihealthmonitor.utils;

public class AQIHelper {

    public static int calculateAQIFromPM25(double pm25) {
        if (pm25 < 0) return 0;
        if (pm25 <= 12.0) return linearInterpolation(pm25, 0, 12.0, 0, 50);
        if (pm25 <= 35.4) return linearInterpolation(pm25, 12.1, 35.4, 51, 100);
        if (pm25 <= 55.4) return linearInterpolation(pm25, 35.5, 55.4, 101, 150);
        if (pm25 <= 150.4) return linearInterpolation(pm25, 55.5, 150.4, 151, 200);
        if (pm25 <= 250.4) return linearInterpolation(pm25, 150.5, 250.4, 201, 300);
        if (pm25 <= 350.4) return linearInterpolation(pm25, 250.5, 350.4, 301, 400);
        if (pm25 <= 500.4) return linearInterpolation(pm25, 350.5, 500.4, 401, 500);
        return 500;
    }

    private static int linearInterpolation(double concentration, double cLow, double cHigh, 
                                          int aqiLow, int aqiHigh) {
        return (int) Math.round(((aqiHigh - aqiLow) / (cHigh - cLow)) * 
                                (concentration - cLow) + aqiLow);
    }

    public static String getAQICategory(int aqi) {
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Moderate";
        if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        if (aqi <= 200) return "Unhealthy";
        if (aqi <= 300) return "Very Unhealthy";
        return "Hazardous";
    }

    public static String formatTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy hh:mm a", 
                                                                        java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }

}
