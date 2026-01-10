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

        // Check authentication state immediately
        checkAuthState()
    }

    private fun checkAuthState() {
        if (authManager.isUserLoggedIn()) {
            // User is logged in, ALWAYS show biometric auth screen
            // (which has password fallback)
            navigateToBiometricAuth()
        } else {
            // User not logged in
            if (preferencesManager.isFirstLaunch) {
                // First launch, show welcome screen
                setupWelcomeScreen()
            } else {
                // Not first launch, go to login
                navigateToLogin()
            }
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
}