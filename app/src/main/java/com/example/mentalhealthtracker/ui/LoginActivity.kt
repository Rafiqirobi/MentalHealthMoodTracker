package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mentalhealthtracker.databinding.ActivityLoginBinding
import com.example.mentalhealthtracker.utils.AuthManager
import com.example.mentalhealthtracker.utils.PreferencesManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var authManager: AuthManager
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager()
        preferencesManager = PreferencesManager(this)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(email, password)) {
                signIn(email, password)
            }
        }

        binding.signUpTextView.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.forgotPasswordTextView.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
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

        binding.emailLayout.error = null
        binding.passwordLayout.error = null
        return true
    }

    private fun signIn(email: String, password: String) {
        showLoading(true)

        lifecycleScope.launch {
            val result = authManager.signIn(email, password)

            showLoading(false)

            result.onSuccess {
                Toast.makeText(this@LoginActivity, "Welcome back!", Toast.LENGTH_SHORT).show()
                navigateToNext()
            }.onFailure { exception ->
                val errorMessage = when {
                    exception.message?.contains("password") == true -> "Invalid email or password"
                    exception.message?.contains("network") == true -> "Network error. Please check your connection"
                    exception.message?.contains("user") == true -> "No account found with this email"
                    else -> "Login failed: ${exception.message}"
                }
                Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showForgotPasswordDialog() {
        val emailEditText = android.widget.EditText(this)
        emailEditText.hint = "Enter your email"
        emailEditText.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        val container = android.widget.FrameLayout(this)
        val params = android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(android.R.dimen.app_icon_size) / 2
        params.rightMargin = resources.getDimensionPixelSize(android.R.dimen.app_icon_size) / 2
        emailEditText.layoutParams = params
        container.addView(emailEditText)

        AlertDialog.Builder(this)
            .setTitle("Reset Password")
            .setMessage("Enter your email address to receive password reset instructions")
            .setView(container)
            .setPositiveButton("Send") { _, _ ->
                val email = emailEditText.text.toString().trim()
                if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    sendPasswordResetEmail(email)
                } else {
                    Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun sendPasswordResetEmail(email: String) {
        showLoading(true)

        lifecycleScope.launch {
            val result = authManager.sendPasswordResetEmail(email)

            showLoading(false)

            result.onSuccess {
                AlertDialog.Builder(this@LoginActivity)
                    .setTitle("Email Sent")
                    .setMessage("Password reset instructions have been sent to $email")
                    .setPositiveButton("OK", null)
                    .show()
            }.onFailure { exception ->
                Toast.makeText(
                    this@LoginActivity,
                    "Failed to send reset email: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun navigateToNext() {
        // Check if biometric is enabled
        if (preferencesManager.isBiometricEnabled) {
            startActivity(Intent(this, BiometricAuthActivity::class.java))
        } else {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        finish()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !show
        binding.loginButton.text = if (show) "" else "Sign In"
    }
}