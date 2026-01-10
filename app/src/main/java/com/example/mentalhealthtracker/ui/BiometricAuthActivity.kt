package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mentalhealthtracker.databinding.ActivityBiometricAuthBinding
import com.example.mentalhealthtracker.utils.AuthManager
import com.example.mentalhealthtracker.utils.BiometricAuthManager
import com.example.mentalhealthtracker.utils.PreferencesManager
import com.google.firebase.auth.EmailAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BiometricAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBiometricAuthBinding
    private lateinit var biometricAuthManager: BiometricAuthManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var authManager: AuthManager
    private var showingPasswordSection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityBiometricAuthBinding.inflate(layoutInflater)
            setContentView(binding.root)

            biometricAuthManager = BiometricAuthManager(this)
            preferencesManager = PreferencesManager(this)
            authManager = AuthManager()

            // Check if this is right after registration
            val skipBiometric = intent.getBooleanExtra("SKIP_BIOMETRIC", false)
            if (skipBiometric) {
                // User just registered, go directly to dashboard
                navigateToDashboard()
                return
            }

            checkBiometricAvailability()
            setupClickListeners()

            // Auto-trigger biometric if available and enabled
            val status = biometricAuthManager.checkBiometricAvailability()
            if (status == BiometricAuthManager.BiometricStatus.AVAILABLE &&
                preferencesManager.isBiometricEnabled) {
                authenticate()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing: ${e.message}", Toast.LENGTH_LONG).show()
            showPasswordOption()
        }
    }

    private fun checkBiometricAvailability() {
        try {
            val status = biometricAuthManager.checkBiometricAvailability()

            when (status) {
                BiometricAuthManager.BiometricStatus.AVAILABLE -> {
                    binding.authenticateButton.isEnabled = true
                    binding.statusTextView.visibility = View.GONE
                }

                BiometricAuthManager.BiometricStatus.NONE_ENROLLED -> {
                    binding.statusTextView.text = "No biometric enrolled. Please use password."
                    binding.statusTextView.visibility = View.VISIBLE
                    binding.authenticateButton.isEnabled = false
                    // Auto-show password if no biometric
                    showPasswordOption()
                }

                else -> {
                    binding.statusTextView.text = biometricAuthManager.getStatusMessage(status)
                    binding.statusTextView.visibility = View.VISIBLE
                    binding.authenticateButton.isEnabled = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.statusTextView.text = "Biometric unavailable. Use password instead."
            binding.statusTextView.visibility = View.VISIBLE
            binding.authenticateButton.isEnabled = false
        }
    }

    private fun setupClickListeners() {
        // Biometric authentication
        binding.authenticateButton.setOnClickListener {
            authenticate()
        }

        // Show password section
        binding.usePasswordButton.setOnClickListener {
            showPasswordOption()
        }

        // Back to biometric
        binding.backToBiometricTextView.setOnClickListener {
            hidePasswordOption()
        }

        // Login with password
        binding.loginWithPasswordButton.setOnClickListener {
            val password = binding.passwordEditText.text.toString()
            if (password.isNotEmpty()) {
                loginWithPassword(password)
            } else {
                binding.passwordLayout.error = "Password is required"
            }
        }

        // Handle "Done" action on keyboard
        binding.passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.loginWithPasswordButton.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun authenticate() {
        try {
            biometricAuthManager.authenticate(
                title = "Unlock App",
                subtitle = "Verify your identity",
                description = "Use your fingerprint or face to access the app",
                negativeButtonText = "Use Password",
                onSuccess = {
                    preferencesManager.isBiometricEnabled = true
                    Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show()
                    navigateToDashboard()
                },
                onError = { errorCode, errorMessage ->
                    binding.statusTextView.text = "Error: $errorMessage"
                    binding.statusTextView.visibility = View.VISIBLE

                    // If user cancels or clicks "Use Password"
                    if (errorCode == 13 || errorCode == 10) {
                        showPasswordOption()
                    }
                },
                onFailed = {
                    Toast.makeText(this, "Authentication failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            showPasswordOption()
        }
    }

    private fun showPasswordOption() {
        showingPasswordSection = true

        // Hide biometric section
        binding.fingerprintIcon.visibility = View.GONE
        binding.authTitleTextView.text = "Enter Password"
        binding.authDescriptionTextView.text = "Login with your account password"
        binding.authenticateButton.visibility = View.GONE
        binding.dividerLayout.visibility = View.GONE
        binding.usePasswordButton.visibility = View.GONE

        // Show password section
        binding.passwordSection.visibility = View.VISIBLE
        binding.passwordEditText.requestFocus()

        // Show keyboard
        binding.passwordEditText.postDelayed({
            val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(binding.passwordEditText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }

    private fun hidePasswordOption() {
        showingPasswordSection = false

        // Show biometric section
        binding.fingerprintIcon.visibility = View.VISIBLE
        binding.authTitleTextView.text = getString(com.example.mentalhealthtracker.R.string.secure_access)
        binding.authDescriptionTextView.text = getString(com.example.mentalhealthtracker.R.string.biometric_description)
        binding.authenticateButton.visibility = View.VISIBLE
        binding.dividerLayout.visibility = View.VISIBLE
        binding.usePasswordButton.visibility = View.VISIBLE

        // Hide password section
        binding.passwordSection.visibility = View.GONE
        binding.passwordEditText.text?.clear()
        binding.passwordLayout.error = null

        // Hide keyboard
        val imm = getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.hideSoftInputFromWindow(binding.passwordEditText.windowToken, 0)
    }

    private fun loginWithPassword(password: String) {
        val currentUser = authManager.getCurrentUser()
        if (currentUser == null || currentUser.email == null) {
            Toast.makeText(this, "Error: No user logged in", Toast.LENGTH_LONG).show()
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                // Re-authenticate user with email and password
                val credential = EmailAuthProvider.getCredential(currentUser.email!!, password)
                currentUser.reauthenticate(credential).await()

                showLoading(false)
                Toast.makeText(this@BiometricAuthActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                navigateToDashboard()

            } catch (e: Exception) {
                showLoading(false)

                val errorMessage = when {
                    e.message?.contains("password") == true -> "Incorrect password"
                    e.message?.contains("network") == true -> "Network error"
                    else -> "Authentication failed: ${e.message}"
                }

                binding.passwordLayout.error = errorMessage
                Toast.makeText(this@BiometricAuthActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.loginWithPasswordButton.isEnabled = !show
        binding.loginWithPasswordButton.text = if (show) "" else "Login"
    }

    private fun navigateToDashboard() {
        try {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error navigating: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        if (showingPasswordSection) {
            hidePasswordOption()
        } else {
            // Don't allow back press - user must authenticate
            Toast.makeText(this, "Please authenticate to continue", Toast.LENGTH_SHORT).show()
        }
    }
}