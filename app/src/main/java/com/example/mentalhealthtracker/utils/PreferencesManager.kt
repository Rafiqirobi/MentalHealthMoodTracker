package com.example.mentalhealthtracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferencesManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val preferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "mental_health_preferences",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // Keys
    companion object {
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_LOCATION_TRACKING = "location_tracking_enabled"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_HAS_SEEN_ONBOARDING = "has_seen_onboarding"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_NOTIFICATION_TIME = "notification_time"
        private const val KEY_DARK_MODE = "dark_mode_enabled"
        private const val KEY_TOTAL_MOOD_ENTRIES = "total_mood_entries"
        private const val KEY_LAST_SYNC = "last_sync_timestamp"
    }

    // First Launch
    var isFirstLaunch: Boolean
        get() = preferences.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = preferences.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()

    // Biometric Authentication
    var isBiometricEnabled: Boolean
        get() = preferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = preferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    // Location Tracking
    var isLocationTrackingEnabled: Boolean
        get() = preferences.getBoolean(KEY_LOCATION_TRACKING, true)
        set(value) = preferences.edit().putBoolean(KEY_LOCATION_TRACKING, value).apply()

    // User Name
    var userName: String
        get() = preferences.getString(KEY_USER_NAME, "") ?: ""
        set(value) = preferences.edit().putString(KEY_USER_NAME, value).apply()

    // Onboarding
    var hasSeenOnboarding: Boolean
        get() = preferences.getBoolean(KEY_HAS_SEEN_ONBOARDING, false)
        set(value) = preferences.edit().putBoolean(KEY_HAS_SEEN_ONBOARDING, value).apply()

    // Notifications
    var notificationsEnabled: Boolean
        get() = preferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = preferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    var notificationTime: String
        get() = preferences.getString(KEY_NOTIFICATION_TIME, "20:00") ?: "20:00"
        set(value) = preferences.edit().putString(KEY_NOTIFICATION_TIME, value).apply()

    // Dark Mode
    var isDarkModeEnabled: Boolean
        get() = preferences.getBoolean(KEY_DARK_MODE, false)
        set(value) = preferences.edit().putBoolean(KEY_DARK_MODE, value).apply()

    // Total Mood Entries
    var totalMoodEntries: Int
        get() = preferences.getInt(KEY_TOTAL_MOOD_ENTRIES, 0)
        set(value) = preferences.edit().putInt(KEY_TOTAL_MOOD_ENTRIES, value).apply()

    // Last Sync Timestamp
    var lastSyncTimestamp: Long
        get() = preferences.getLong(KEY_LAST_SYNC, 0L)
        set(value) = preferences.edit().putLong(KEY_LAST_SYNC, value).apply()

    /**
     * Clear all preferences (for sign out)
     */
    fun clearAll() {
        preferences.edit().clear().apply()
    }

    /**
     * Generic save methods
     */
    fun saveString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return preferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    fun saveInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return preferences.getInt(key, defaultValue)
    }

    fun saveLong(key: String, value: Long) {
        preferences.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return preferences.getLong(key, defaultValue)
    }
}