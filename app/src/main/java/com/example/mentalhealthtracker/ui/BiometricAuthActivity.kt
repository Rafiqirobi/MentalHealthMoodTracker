package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mentalhealthtracker.databinding.ActivityBiometricAuthBinding
import com.example.mentalhealthtracker.utils.BiometricAuthManager
import com.example.mentalhealthtracker.utils.PreferencesManager

class BiometricAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBiometricAuthBinding
    private lateinit var biometricAuthManager: BiometricAuthManager
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        biometricAuthManager = BiometricAuthManager(this)
        preferencesManager = PreferencesManager(this)

        checkBiometricAvailability()
        setupClickListeners()
    }

    private fun checkBiometricAvailability() {
        val status = biometricAuthManager.checkBiometricAvailability()

        when (status) {
            BiometricAuthManager.BiometricStatus.AVAILABLE -> {
                // Biometric is available
                binding.authenticateButton.isEnabled = true
                binding.statusTextView.visibility = View.GONE
            }

            BiometricAuthManager.BiometricStatus.NONE_ENROLLED -> {
                // No biometric enrolled
                binding.statusTextView.text = biometricAuthManager.getStatusMessage(status)
                binding.statusTextView.visibility = View.VISIBLE
                binding.authenticateButton.isEnabled = false
            }

            else -> {
                // Other errors (no hardware, etc.)
                binding.statusTextView.text = biometricAuthManager.getStatusMessage(status)
                binding.statusTextView.visibility = View.VISIBLE
                binding.authenticateButton.isEnabled = false
            }
        }
    }

    private fun setupClickListeners() {
        binding.authenticateButton.setOnClickListener {
            authenticate()
        }

        binding.skipButton.setOnClickListener {
            preferencesManager.isBiometricEnabled = false
            navigateToDashboard()
        }
    }

    private fun authenticate() {
        biometricAuthManager.authenticate(
            title = "Biometric Authentication",
            subtitle = "Verify your identity",
            description = "Use your fingerprint or face to access your mood data",
            negativeButtonText = "Cancel",
            onSuccess = {
                // Authentication successful
                preferencesManager.isBiometricEnabled = true
                Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()
                navigateToDashboard()
            },
            onError = { errorCode, errorMessage ->
                // Authentication error
                binding.statusTextView.text = "Authentication error: $errorMessage"
                binding.statusTextView.visibility = View.VISIBLE
            },
            onFailed = {
                // Authentication failed
                Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}