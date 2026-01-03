package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mentalhealthtracker.databinding.ActivityProfileBinding
import com.example.mentalhealthtracker.utils.AuthManager
import com.example.mentalhealthtracker.utils.DateTimeHelper
import com.example.mentalhealthtracker.utils.PreferencesManager
import com.example.mentalhealthtracker.viewmodel.MoodViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var authManager: AuthManager
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewModel: MoodViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        authManager = AuthManager()
        preferencesManager = PreferencesManager(this)
        viewModel = ViewModelProvider(this)[MoodViewModel::class.java]

        loadUserInfo()
        setupClickListeners()
        observeViewModel()
        loadStatistics()
    }

    private fun loadUserInfo() {
        // Display user info
        binding.userNameTextView.text = authManager.getUserDisplayName()
        binding.userEmailTextView.text = authManager.getUserEmail()

        // Display member since date
        val creationTime = authManager.getUserCreationTime()
        if (creationTime > 0) {
            binding.memberSinceTextView.text = DateTimeHelper.formatDate(creationTime)
        }

        // Set biometric switch state
        binding.biometricSwitch.isChecked = preferencesManager.isBiometricEnabled
    }

    private fun setupClickListeners() {
        // Biometric switch
        binding.biometricSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.isBiometricEnabled = isChecked
            Toast.makeText(
                this,
                if (isChecked) "Biometric lock enabled" else "Biometric lock disabled",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Change password
        binding.changePasswordLayout.setOnClickListener {
            showChangePasswordDialog()
        }

        // Sign out
        binding.signOutButton.setOnClickListener {
            showSignOutConfirmation()
        }
    }

    private fun observeViewModel() {
        viewModel.moodCount.observe(this) { count ->
            binding.totalEntriesTextView.text = count.toString()
        }

        viewModel.averageMood7Days.observe(this) { average ->
            if (average != null && average > 0) {
                binding.averageMoodTextView.text = String.format("%.1f / 5", average)
            } else {
                binding.averageMoodTextView.text = "N/A"
            }
        }
    }

    private fun loadStatistics() {
        viewModel.loadDashboardData()
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(
            android.R.layout.simple_list_item_1,
            null
        )

        // Create custom dialog with password fields
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Change Password")
        builder.setMessage("Enter your new password")

        val newPasswordEditText = com.google.android.material.textfield.TextInputEditText(this)
        newPasswordEditText.hint = "New Password"
        newPasswordEditText.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        val confirmPasswordEditText = com.google.android.material.textfield.TextInputEditText(this)
        confirmPasswordEditText.hint = "Confirm New Password"
        confirmPasswordEditText.inputType =
            android.text.InputType.TYPE_CLASS_TEXT or
                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD

        val container = android.widget.LinearLayout(this)
        container.orientation = android.widget.LinearLayout.VERTICAL
        container.setPadding(50, 20, 50, 20)

        container.addView(newPasswordEditText)
        container.addView(confirmPasswordEditText)

        builder.setView(container)
        builder.setPositiveButton("Change") { _, _ ->
            val newPassword = newPasswordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (newPassword.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            changePassword(newPassword)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun changePassword(newPassword: String) {
        // In a real app, you'd use authManager.updatePassword()
        // For now, show a message
        Toast.makeText(
            this,
            "To change password, please use the 'Forgot Password' option on the login screen",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showSignOutConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Sign Out") { _, _ ->
                signOut()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun signOut() {
        // Clear preferences
        preferencesManager.clearAll()

        // Sign out from Firebase
        authManager.signOut()

        // Navigate to login
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()

        Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show()
    }
}