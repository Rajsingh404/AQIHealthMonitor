package com.example.aqihealthmonitor.models;

public class AQIData {
    private String location;
    private int aqi;
    private double pm25;
    private double pm10;
    private double o3;
    private double no2;
    private double so2;
    private double co;
    private long timestamp;
    private double latitude;
    private double longitude;

    public AQIData() {
    }

    public AQIData(String location, int aqi, double pm25, double pm10, double o3, 
                   double no2, double so2, double co, double latitude, double longitude) {
        this.location = location;
        this.aqi = aqi;
        this.pm25 = pm25;
        this.pm10 = pm10;
        this.o3 = o3;
        this.no2 = no2;
        this.so2 = so2;
        this.co = co;
        this.timestamp = System.currentTimeMillis();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getAqi() { return aqi; }
    public void setAqi(int aqi) { this.aqi = aqi; }

    public double getPm25() { return pm25; }
    public void setPm25(double pm25) { this.pm25 = pm25; }

    public double getPm10() { return pm10; }
    public void setPm10(double pm10) { this.pm10 = pm10; }

    public double getO3() { return o3; }
    public void setO3(double o3) { this.o3 = o3; }

    public double getNo2() { return no2; }
    public void setNo2(double no2) { this.no2 = no2; }

    public double getSo2() { return so2; }
    public void setSo2(double so2) { this.so2 = so2; }

    public double getCo() { return co; }
    public void setCo(double co) { this.co = co; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getCategory() {
        if (aqi <= 50) return "Good";
        if (aqi <= 100) return "Moderate";
        if (aqi <= 150) return "Unhealthy for Sensitive Groups";
        if (aqi <= 200) return "Unhealthy";
        if (aqi <= 300) return "Very Unhealthy";
        return "Hazardous";
    }

    public int getCategoryColor() {
        if (aqi <= 50) return 0xFF00E676; // Green
        if (aqi <= 100) return 0xFFFDD835; // Yellow
        if (aqi <= 150) return 0xFFFF9800; // Orange
        if (aqi <= 200) return 0xFFF44336; // Red
        if (aqi <= 300) return 0xFF9C27B0; // Purple
        return 0xFF880E4F; // Deep Red
    }


    public String getHealthAdvice() {
        if (aqi <= 50) {
            return "Air quality is good. Perfect for outdoor activities!";
        } else if (aqi <= 100) {
            return "Air quality is acceptable. Sensitive individuals should limit prolonged outdoor activities.";
        } else if (aqi <= 150) {
            return "Sensitive groups should reduce prolonged outdoor activities.";
        } else if (aqi <= 200) {
            return "Everyone should limit prolonged outdoor activities.";
        } else if (aqi <= 300) {
            return "Health alert! Everyone should avoid prolonged outdoor activities.";
        } else {
            return "Health warning! Avoid all outdoor activities.";
        }
    }
}
