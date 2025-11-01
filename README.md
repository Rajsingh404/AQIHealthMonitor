# 🌍 AQI Health Monitor - Android App

A simple and functional Air Quality Index (AQI) monitoring Android application with personalized health tracking built with Java in Android Studio and Firebase.

![Android](https://img.shields.io/badge/Platform-Android-green)
![Java](https://img.shields.io/badge/Language-Java-orange)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-blue)
![Target SDK](https://img.shields.io/badge/Target%20SDK-34-blue)

---

## 📋 Project Overview

A straightforward Android app that monitors air quality in real-time and provides personalized health warnings based on user's medical conditions. Built with simplicity and functionality in mind.

---

## ✨ Core Features

### 🔍 Live AQI Monitoring
- Real-time AQI data for searched locations
- Simple search bar for city names
- Color-coded AQI display (Good to Hazardous)
- Last updated timestamp
- 6 pollutants tracked (PM2.5, PM10, O₃, NO₂, SO₂, CO)

### 🏥 Health Personalization
Select from 6 health conditions:
- 🫁 Asthma
- 🤧 Allergies
- ❤️ Heart Condition
- 🫁 Lung Disease
- 👴 Elderly (65+)
- 👶 Children

### 🚨 Custom Alert System
- Set AQI threshold (10-300+) using SeekBar
- Get personalized health warnings
- Enable/disable notifications
- Daily updates at 8:00 AM

---

## 🛠️ Technology Stack

### Core Technologies
- **Language**: Java
- **IDE**: Android Studio
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Database**: Firebase Realtime Database
- **API**: OpenWeatherMap Air Pollution API

### Required Libraries
```gradle
dependencies {
    // Firebase
    implementation platform('com.google.firebase:firebase-bom:32.7.0')
    implementation 'com.google.firebase:firebase-database'
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    
    // Material Design
    implementation 'com.google.android.material:material:1.11.0'
    
    // Location
    implementation 'com.google.android.gms:play-services-location:21.1.0'
}
```

---

## 📱 App Structure

### 3 Main Screens
1. **HomeActivity.java** - Display AQI and search
2. **ProfileActivity.java** - Select health conditions
3. **SettingsActivity.java** - Configure alerts and notifications

### Package Structure
```
com.example.aqihealthmonitor/
├── HomeActivity.java
├── ProfileActivity.java
├── SettingsActivity.java
├── models/
│   ├── AQIData.java
│   └── UserProfile.java
├── api/
│   ├── AQIApiService.java
│   └── ApiClient.java
├── utils/
│   ├── AQIHelper.java
│   └── NotificationHelper.java
└── receivers/
    └── AlarmReceiver.java
```

---

## 🚀 Setup Instructions

### Step 1: Clone/Download Project
```bash
cd /Users/rajsingh/CascadeProjects/AQIHealthMonitor/
```

### Step 2: Open in Android Studio
1. Open Android Studio
2. File → Open
3. Select the `AQIHealthMonitor` folder
4. Wait for Gradle sync

### Step 3: Configure Firebase
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create new project "AQI-Health-Monitor"
3. Add Android app with package: `com.example.aqihealthmonitor`
4. Download `google-services.json`
5. Replace the template file in `app/` folder
6. Enable Firebase Realtime Database
7. Set database rules (for testing):
```json
{
  "rules": {
    ".read": true,
    ".write": true
  }
}
```

### Step 4: Get OpenWeatherMap API Key
1. Visit [OpenWeatherMap API](https://openweathermap.org/api)
2. Sign up for free account
3. Get API key
4. ✅ **Already configured** in `HomeActivity.java`

### Step 5: Build and Run
1. Connect Android device or start emulator
2. Click Run button (▶️)
3. Grant location permissions when prompted
4. Enjoy! 🎉

---

## 💾 Firebase Database Structure

```json
{
  "users": {
    "user_123": {
      "conditions": ["asthma", "allergies"],
      "alertThreshold": 100,
      "notificationsEnabled": true,
      "dailyUpdates": true,
      "lastLocation": "Mumbai"
    }
  },
  "aqi_history": {
    "user_123": {
      "1730000000000": {
        "location": "Mumbai",
        "aqi": 150,
        "pm25": 55.5,
        "timestamp": 1730000000000
      }
    }
  }
}
```

---

## 🎨 UI/UX Features

### Modern Design Elements
- ✨ **Gradient AQI Card** - Purple to pink gradient background
- 🔍 **Rounded Search Bar** - 24dp radius with elevation
- 📊 **Clean Cards** - 20dp radius, 6-8dp elevation
- 💡 **Health Advice** - Personalized warnings
- 🎯 **Bottom Navigation** - Material Design 3-tab nav

### Color Scheme
- **Primary**: Purple (#667eea) → Deep Purple (#764ba2) → Pink (#f093fb)
- **AQI Colors**: Green → Yellow → Orange → Red → Purple → Maroon

---

## 📸 Screenshots

### Home Screen
- Large AQI value display (72sp bold)
- Gradient background card
- City search with autocomplete
- Pollutants list
- Health advice card

### Profile Screen
- 6 health condition checkboxes
- Save button
- Info card

### Settings Screen
- Notification toggles
- AQI threshold SeekBar (10-300+)
- Daily updates option
- Save button

---

## 🔑 API Configuration

### OpenWeatherMap API
- **Base URL**: `https://api.openweathermap.org/`
- **Endpoints Used**:
  - `/data/2.5/air_pollution` - Get AQI data
  - `/geo/1.0/direct` - Search cities

### Your API Key
✅ Already configured in `HomeActivity.java`:
```java
private static final String API_KEY = "sk-ws-01-OyD4ymO...";
```

---

## 📱 Permissions Required

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
```

---

## 🐛 Troubleshooting

### Gradle Sync Failed
```bash
File → Invalidate Caches / Restart
```

### Firebase Error
- Check `google-services.json` is in `app/` folder
- Verify package name matches: `com.example.aqihealthmonitor`
- Enable Firebase Realtime Database in console

### Location Not Working
- Grant location permission in device settings
- Enable GPS on device
- Test on physical device for best results

### API Not Responding
- Verify API key in `HomeActivity.java`
- Check internet connection
- Confirm OpenWeatherMap API key is active

---

## 📊 Project Statistics

| Metric | Count |
|--------|-------|
| **Java Files** | 10 |
| **Activities** | 3 |
| **Models** | 2 |
| **Utils** | 2 |
| **API Classes** | 2 |
| **Layout Files** | 3 |
| **Drawable Resources** | 6 |
| **Total Files** | 30+ |

---

## ✅ Features Checklist

- [x] Real-time AQI monitoring
- [x] GPS location detection
- [x] City search functionality
- [x] 6 health conditions support
- [x] Custom alert thresholds
- [x] Personalized health warnings
- [x] Notifications system
- [x] Daily updates at 8:00 AM
- [x] Firebase integration
- [x] Modern gradient UI
- [x] Bottom navigation
- [x] Material Design 3

---

## 🎯 Future Enhancements

- [ ] Historical AQI data charts
- [ ] Multiple location favorites
- [ ] Weather forecast integration
- [ ] Social sharing
- [ ] Widget support
- [ ] Dark theme support

---

## 📄 License

This project is open source and available for educational purposes.

---

## 👨‍💻 Developer

Created with ❤️ for clean air and better health!

---

## 📞 Support

For issues or questions:
1. Check Firebase console for database errors
2. Verify API key is active
3. Check Logcat for error messages
4. Ensure all permissions are granted

---

**🎉 Happy Coding! Breathe Better, Live Better! 🌍**

---

*Last Updated: October 31, 2025*  
*Version: 1.0*  
*Package: com.example.aqihealthmonitor*
