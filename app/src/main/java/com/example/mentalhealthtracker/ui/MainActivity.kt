package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.mentalhealthtracker.databinding.ActivityMainBinding
import com.example.mentalhealthtracker.utils.PreferencesManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)

        // Check if first launch
        if (!preferencesManager.isFirstLaunch) {
            // Not first launch, check biometric
            if (preferencesManager.isBiometricEnabled) {
                navigateToBiometricAuth()
            } else {
                navigateToDashboard()
            }
            return
        }

        // First launch - show welcome screen
        setupWelcomeScreen()
    }

    private fun setupWelcomeScreen() {
        binding.getStartedButton.setOnClickListener {
            preferencesManager.isFirstLaunch = false
            navigateToBiometricAuth()
        }
    }

    private fun navigateToBiometricAuth() {
        val intent = Intent(this, BiometricAuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}