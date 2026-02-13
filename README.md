# Mental Health Mood Tracker

An Android application designed to help students track their emotional wellbeing privately and identify mood patterns over time.

## Features

### 🔐 Security & Privacy
- **Biometric Authentication**: Secure access to sensitive mood data using fingerprint/face recognition
- **Encrypted Storage**: All mood entries are stored in an encrypted database
- **Private & Stigma-Free**: Your data stays on your device

### 📍 Location-Aware Support
- **GPS Integration**: Discover nearby stress relief places
- **Smart Recommendations**: Find parks, nature spots, quiet cafes, and peaceful locations
- **Distance Tracking**: See how far stress relief places are from your current location

### 📊 Mood Tracking
- **Daily Mood Logging**: Track your emotions using an intuitive emoji scale (1-5)
- **Journal Entries**: Write detailed notes about your feelings and experiences
- **Trigger Identification**: Record what might have influenced your mood
- **Pattern Analysis**: View graphs and insights about your mood over time

### 🧘 Wellness Tools
- **Breathing Exercises**: Guided breathing techniques for stress relief
- **Helpful Resources**: Quick access to mental health hotlines and support services
- **Activity Insights**: Correlate your mood with your daily activities

## Technology Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Database**: Room (with SQLCipher encryption)
- **Authentication**: AndroidX Biometric API
- **Location**: Google Location Services
- **Maps**: Google Maps SDK
- **Charts**: MPAndroidChart
- **Minimum SDK**: Android 8.0 (API 26)
- **Target SDK**: Android 14 (API 34)

## Prerequisites

- Android Studio Hedgehog | 2023.1.1 or newer
- Android SDK 26 or higher
- Google Maps API Key (for location features)

## Setup Instructions

1. **Clone the repository**
```bash
   git clone https://github.com/YOUR-USERNAME/mental-health-mood-tracker.git
   cd mental-health-mood-tracker
```

2. **Open in Android Studio**
    - Open Android Studio
    - Select "Open an Existing Project"
    - Navigate to the cloned repository

3. **Get Google Maps API Key**
    - Go to [Google Cloud Console](https://console.cloud.google.com/)
    - Create a new project or select existing
    - Enable "Maps SDK for Android" and "Places API"
    - Create credentials → API Key
    - Copy your API key

4. **Add API Key**
    - Open `AndroidManifest.xml`
    - Replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` with your actual API key

5. **Build and Run**
    - Connect an Android device or start an emulator
    - Click "Run" in Android Studio

## Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/yourname/mentalhealthtracker/
│   │   │   ├── data/
│   │   │   │   ├── MoodEntry.kt
│   │   │   │   ├── StressReliefPlace.kt
│   │   │   │   ├── AppDatabase.kt
│   │   │   │   ├── MoodDao.kt
│   │   │   │   └── MoodRepository.kt
│   │   │   ├── ui/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── BiometricAuthActivity.kt
│   │   │   │   ├── DashboardActivity.kt
│   │   │   │   ├── AddMoodActivity.kt
│   │   │   │   └── ...
│   │   │   └── utils/
│   │   │       ├── BiometricAuthManager.kt
│   │   │       └── LocationManager.kt
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── values/
│   │   │   └── drawable/
│   │   └── AndroidManifest.xml
│   └── test/
└── build.gradle.kts
```

## Security & Privacy

- All mood data is encrypted using SQLCipher
- Biometric authentication required for app access
- No data is transmitted to external servers
- Location data is only used locally for recommendations

## Development Status

🚧 **Under Active Development**

- [x] Project setup
- [ ] Biometric authentication implementation
- [ ] Database with encryption
- [ ] Mood logging interface
- [ ] GPS location features
- [ ] Mood analytics and charts
- [ ] Breathing exercises
- [ ] Resources and hotlines

## Contributing

This is a personal/academic project. If you'd like to suggest features or report bugs, please open an issue.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Disclaimer

This app is designed as a personal mood tracking tool and is not a substitute for professional mental health care. If you're experiencing a mental health crisis, please contact:

- **National Suicide Prevention Lifeline**: 988 (US)
- **Crisis Text Line**: Text HOME to 741741
- **International Association for Suicide Prevention**: https://www.iasp.info/resources/Crisis_Centres/

## Acknowledgments

- Material Design Guidelines
- Android Developer Documentation
- Mental Health Organizations for guidance on sensitive data handling

## Contact

Your Name - [2023410832@student.uitm.edu.my)

Project Link: [https://github.com/YOUR-USERNAME/mental-health-mood-tracker](https://github.com/YOUR-USERNAME/mental-health-mood-tracker)
