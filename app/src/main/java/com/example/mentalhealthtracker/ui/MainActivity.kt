package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mentalhealthtracker.databinding.ActivityMainBinding
import com.example.mentalhealthtracker.utils.AuthManager
import com.example.mentalhealthtracker.utils.PreferencesManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var authManager: AuthManager
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager()
        preferencesManager = PreferencesManager(this)

        // Check authentication state
        checkAuthState()

        setupWelcomeScreen()
    }

    private fun checkAuthState() {
        if (authManager.isUserLoggedIn()) {
            // User is logged in, check biometric preference
            if (preferencesManager.isBiometricEnabled) {
                navigateToBiometricAuth()
            } else {
                navigateToDashboard()
            }
        } else {
            // User not logged in
            if (!preferencesManager.isFirstLaunch) {
                // Not first launch, go to login
                navigateToLogin()
            }
            // Otherwise show welcome screen
        }
    }

    private fun setupWelcomeScreen() {
        binding.getStartedButton.setOnClickListener {
            preferencesManager.isFirstLaunch = false
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
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