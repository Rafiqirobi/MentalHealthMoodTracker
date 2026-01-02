package com.example.mentalhealthtracker.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mentalhealthtracker.R
import com.example.mentalhealthtracker.data.MoodEntry
import com.example.mentalhealthtracker.data.MoodLevel
import com.example.mentalhealthtracker.databinding.ActivityMoodHistoryBinding
import com.example.mentalhealthtracker.ui.adapters.MoodHistoryAdapter
import com.example.mentalhealthtracker.utils.DateTimeHelper
import com.example.mentalhealthtracker.viewmodel.MoodViewModel

class MoodHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoodHistoryBinding
    private lateinit var viewModel: MoodViewModel
    private lateinit var adapter: MoodHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoodHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[MoodViewModel::class.java]

        setupRecyclerView()
        setupFilterChips()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MoodHistoryAdapter { moodEntry ->
            showMoodDetailsDialog(moodEntry)
        }

        binding.moodRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.moodRecyclerView.adapter = adapter
    }

    private fun setupFilterChips() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            when (checkedIds[0]) {
                R.id.chipAllTime -> loadAllMoods()
                R.id.chip7Days -> loadMoodsByDays(7)
                R.id.chip30Days -> loadMoodsByDays(30)
                R.id.chip90Days -> loadMoodsByDays(90)
            }
        }

        // Load all moods initially
        loadAllMoods()
    }

    private fun loadAllMoods() {
        viewModel.allMoods.observe(this) { moods ->
            updateUI(moods)
        }
    }

    private fun loadMoodsByDays(days: Int) {
        val startDate = DateTimeHelper.getDaysAgo(days)
        val endDate = System.currentTimeMillis()

        viewModel.getMoodsByDateRange(startDate, endDate).observe(this) { moods ->
            updateUI(moods)
        }
    }

    private fun updateUI(moods: List<MoodEntry>) {
        if (moods.isEmpty()) {
            binding.moodRecyclerView.visibility = View.GONE
            binding.emptyStateLayout.visibility = View.VISIBLE
        } else {
            binding.moodRecyclerView.visibility = View.VISIBLE
            binding.emptyStateLayout.visibility = View.GONE
            adapter.submitList(moods)
        }
    }

    private fun showMoodDetailsDialog(moodEntry: MoodEntry) {
        val message = buildString {
            append("Mood: ${moodEntry.moodEmoji} ${MoodLevel.fromValue(moodEntry.moodLevel).description}\n\n")
            append("Date: ${DateTimeHelper.formatDateTime(moodEntry.timestamp)}\n\n")

            if (!moodEntry.journalEntry.isNullOrBlank()) {
                append("Journal:\n${moodEntry.journalEntry}\n\n")
            }

            if (!moodEntry.triggers.isNullOrBlank()) {
                append("Triggers: ${moodEntry.triggers}\n\n")
            }

            if (!moodEntry.locationName.isNullOrBlank()) {
                append("Location: ${moodEntry.locationName}")
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Mood Entry Details")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setNegativeButton("Delete") { _, _ ->
                confirmDelete(moodEntry)
            }
            .show()
    }

    private fun confirmDelete(moodEntry: MoodEntry) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_entry))
            .setMessage(getString(R.string.confirm_delete))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                viewModel.deleteMood(moodEntry)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}