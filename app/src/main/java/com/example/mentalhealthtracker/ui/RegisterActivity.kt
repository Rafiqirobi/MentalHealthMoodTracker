package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mentalhealthtracker.databinding.ActivityRegisterBinding
import com.example.mentalhealthtracker.utils.AuthManager
import com.example.mentalhealthtracker.utils.PreferencesManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var authManager: AuthManager
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager()
        preferencesManager = PreferencesManager(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.registerButton.setOnClickListener {
            val name = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (validateInput(name, email, password, confirmPassword)) {
                register(name, email, password)
            }
        }

        binding.signInTextView.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (name.isEmpty()) {
            binding.nameLayout.error = "Name is required"
            return false
        }

        if (name.length < 2) {
            binding.nameLayout.error = "Name must be at least 2 characters"
            return false
        }

        if (email.isEmpty()) {
            binding.emailLayout.error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Invalid email format"
            return false
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            binding.passwordLayout.error = "Password must be at least 6 characters"
            return false
        }

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordLayout.error = "Please confirm your password"
            return false
        }

        if (password != confirmPassword) {
            binding.confirmPasswordLayout.error = "Passwords do not match"
            return false
        }

        // Clear all errors
        binding.nameLayout.error = null
        binding.emailLayout.error = null
        binding.passwordLayout.error = null
        binding.confirmPasswordLayout.error = null

        return true
    }

    private fun register(name: String, email: String, password: String) {
        showLoading(true)

        lifecycleScope.launch {
            val result = authManager.register(email, password, name)

            showLoading(false)

            result.onSuccess {
                // Set flag that biometric should be disabled for new users
                preferencesManager.isBiometricEnabled = false

                Toast.makeText(
                    this@RegisterActivity,
                    "Account created successfully! Welcome, $name!",
                    Toast.LENGTH_SHORT
                ).show()

                // Go directly to dashboard after registration
                navigateToDashboard()
            }.onFailure { exception ->
                val errorMessage = when {
                    exception.message?.contains("already in use") == true ->
                        "This email is already registered"
                    exception.message?.contains("network") == true ->
                        "Network error. Please check your connection"
                    exception.message?.contains("weak-password") == true ->
                        "Password is too weak. Use a stronger password"
                    else -> "Registration failed: ${exception.message}"
                }
                Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("JUST_REGISTERED", true)
        startActivity(intent)
        finish()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.registerButton.isEnabled = !show
        binding.registerButton.text = if (show) "" else "Sign Up"
    }
}