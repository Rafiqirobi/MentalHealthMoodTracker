package com.example.mentalhealthtracker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Manager class for handling encrypted shared preferences
 * Stores app settings and user preferences securely
 */
class PreferencesManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = try {
        EncryptedSharedPreferences.create(
            context,
            PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        // Fallback to regular SharedPreferences if encryption fails
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Check if this is the first launch of the app
     */
    var isFirstLaunch: Boolean
        get() = sharedPreferences.getBoolean(KEY_FIRST_LAUNCH, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_FIRST_LAUNCH, value).apply()

    /**
     * Check if biometric authentication is enabled
     */
    var isBiometricEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_BIOMETRIC_ENABLED, value).apply()

    /**
     * Check if location tracking is enabled
     */
    var isLocationTrackingEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_LOCATION_TRACKING, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_LOCATION_TRACKING, value).apply()

    /**
     * Get user's preferred name
     */
    var userName: String?
        get() = sharedPreferences.getString(KEY_USER_NAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_USER_NAME, value).apply()

    /**
     * Check if user has seen the onboarding
     */
    var hasSeenOnboarding: Boolean
        get() = sharedPreferences.getBoolean(KEY_SEEN_ONBOARDING, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_SEEN_ONBOARDING, value).apply()

    /**
     * Get last sync timestamp
     */
    var lastSyncTimestamp: Long
        get() = sharedPreferences.getLong(KEY_LAST_SYNC, 0L)
        set(value) = sharedPreferences.edit().putLong(KEY_LAST_SYNC, value).apply()

    /**
     * Check if notifications are enabled
     */
    var notificationsEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    /**
     * Get notification time (in minutes from midnight)
     */
    var notificationTime: Int
        get() = sharedPreferences.getInt(KEY_NOTIFICATION_TIME, 1200) // Default: 8 PM (20:00)
        set(value) = sharedPreferences.edit().putInt(KEY_NOTIFICATION_TIME, value).apply()

    /**
     * Check if dark mode is enabled
     */
    var isDarkModeEnabled: Boolean
        get() = sharedPreferences.getBoolean(KEY_DARK_MODE, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_DARK_MODE, value).apply()

    /**
     * Get total mood entries count (cached)
     */
    var totalMoodEntries: Int
        get() = sharedPreferences.getInt(KEY_TOTAL_MOODS, 0)
        set(value) = sharedPreferences.edit().putInt(KEY_TOTAL_MOODS, value).apply()

    /**
     * Save a custom string preference
     */
    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    /**
     * Get a custom string preference
     */
    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    /**
     * Save a custom boolean preference
     */
    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    /**
     * Get a custom boolean preference
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    /**
     * Save a custom integer preference
     */
    fun saveInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    /**
     * Get a custom integer preference
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    /**
     * Save a custom long preference
     */
    fun saveLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    /**
     * Get a custom long preference
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    /**
     * Clear all preferences
     */
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    /**
     * Remove a specific preference
     */
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    /**
     * Check if a preference exists
     */
    fun contains(key: String): Boolean {
        return sharedPreferences.contains(key)
    }

    companion object {
        private const val PREFS_NAME = "mental_health_prefs"

        // Preference keys
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_LOCATION_TRACKING = "location_tracking"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_SEEN_ONBOARDING = "seen_onboarding"
        private const val KEY_LAST_SYNC = "last_sync"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_NOTIFICATION_TIME = "notification_time"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_TOTAL_MOODS = "total_moods"
    }
}