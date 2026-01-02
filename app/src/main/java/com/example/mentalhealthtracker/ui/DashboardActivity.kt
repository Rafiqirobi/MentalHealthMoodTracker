package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.os.Bundle
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
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MoodViewModel::class.java]

        setupClickListeners()
        observeViewModel()
        loadDashboardData()
    }

    private fun setupClickListeners() {
        binding.logMoodCard.setOnClickListener {
            startActivity(Intent(this, AddMoodActivity::class.java))
        }

        binding.historyCard.setOnClickListener {
            startActivity(Intent(this, MoodHistoryActivity::class.java))
        }

        binding.placesCard.setOnClickListener {
            startActivity(Intent(this, StressReliefPlacesActivity::class.java))
        }

        binding.breathingCard.setOnClickListener {
            startActivity(Intent(this, BreathingExerciseActivity::class.java))
        }

        binding.resourcesCard.setOnClickListener {
            startActivity(Intent(this, ResourcesActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.averageMood7Days.observe(this) { average ->
            updateAverageMoodText(average)
        }

        viewModel.moodCount.observe(this) { count ->
            updateWelcomeText(count)
        }
    }

    private fun loadDashboardData() {
        viewModel.loadDashboardData()
    }

    private fun updateAverageMoodText(average: Float?) {
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
    }

    private fun updateWelcomeText(count: Int) {
        if (count > 0) {
            binding.welcomeTextView.text = "Welcome back!"
        } else {
            binding.welcomeTextView.text = "Welcome to Mental Health Tracker!"
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to dashboard
        loadDashboardData()
    }
}