package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mentalhealthtracker.R
import com.example.mentalhealthtracker.databinding.ActivityDashboardBinding
import com.example.mentalhealthtracker.viewmodel.MoodViewModel

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var viewModel: MoodViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityDashboardBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Setup toolbar
            setSupportActionBar(binding.toolbar)

            // Add menu
            binding.toolbar.inflateMenu(R.menu.dashboard_menu)
            binding.toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_profile -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        true
                    }
                    else -> false
                }
            }

            // Initialize ViewModel
            viewModel = ViewModelProvider(this)[MoodViewModel::class.java]

            setupClickListeners()
            observeViewModel()
            loadDashboardData()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing dashboard: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupClickListeners() {
        binding.logMoodCard.setOnClickListener {
            try {
                startActivity(Intent(this, AddMoodActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.historyCard.setOnClickListener {
            try {
                startActivity(Intent(this, MoodHistoryActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.placesCard.setOnClickListener {
            try {
                startActivity(Intent(this, StressReliefPlacesActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.breathingCard.setOnClickListener {
            try {
                startActivity(Intent(this, BreathingExerciseActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.resourcesCard.setOnClickListener {
            try {
                startActivity(Intent(this, ResourcesActivity::class.java))
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        try {
            viewModel.averageMood7Days.observe(this) { average ->
                updateAverageMoodText(average)
            }

            viewModel.moodCount.observe(this) { count ->
                updateWelcomeText(count)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadDashboardData() {
        try {
            viewModel.loadDashboardData()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAverageMoodText(average: Float?) {
        try {
            if (average != null && average > 0) {
                val moodDescription = when {
                    average >= 4.5 -> "Very Good"
                    average >= 3.5 -> "Good"
                    average >= 2.5 -> "Okay"
                    average >= 1.5 -> "Not Great"
                    else -> "Struggling"
                }

                binding.averageMoodTextView.text = getString(
                    R.string.average_mood_7days,
                    String.format("%.1f/5 - %s", average, moodDescription)
                )
            } else {
                binding.averageMoodTextView.text = getString(R.string.no_mood_data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.averageMoodTextView.text = "Error loading mood data"
        }
    }

    private fun updateWelcomeText(count: Int) {
        try {
            if (count > 0) {
                binding.welcomeTextView.text = "Welcome back!"
            } else {
                binding.welcomeTextView.text = "Welcome to Mental Health Tracker!"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.welcomeTextView.text = "Welcome!"
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            // Refresh data when returning to dashboard
            loadDashboardData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}