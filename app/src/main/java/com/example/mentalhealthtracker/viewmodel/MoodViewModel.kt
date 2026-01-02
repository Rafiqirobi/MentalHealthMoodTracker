package com.example.mentalhealthtracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.mentalhealthtracker.data.MoodEntry
import com.example.mentalhealthtracker.data.MoodRepository
import com.example.mentalhealthtracker.data.MoodStatistics
import kotlinx.coroutines.launch

class MoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MoodRepository = MoodRepository(application)

    // LiveData for all moods
    val allMoods: LiveData<List<MoodEntry>> = repository.allMoods

    // LiveData for UI state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    private val _averageMood7Days = MutableLiveData<Float?>()
    val averageMood7Days: LiveData<Float?> = _averageMood7Days

    private val _averageMood30Days = MutableLiveData<Float?>()
    val averageMood30Days: LiveData<Float?> = _averageMood30Days

    private val _moodCount = MutableLiveData<Int>()
    val moodCount: LiveData<Int> = _moodCount

    private val _recentMoods = MutableLiveData<List<MoodEntry>>()
    val recentMoods: LiveData<List<MoodEntry>> = _recentMoods

    private val _moodStatistics = MutableLiveData<MoodStatistics>()
    val moodStatistics: LiveData<MoodStatistics> = _moodStatistics

    /**
     * Insert a new mood entry
     */
    fun insertMood(moodEntry: MoodEntry) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.insert(moodEntry)
            _successMessage.value = "Mood saved successfully!"
            loadDashboardData()
        } catch (e: Exception) {
            _errorMessage.value = "Error saving mood: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Update an existing mood entry
     */
    fun updateMood(moodEntry: MoodEntry) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.update(moodEntry)
            _successMessage.value = "Mood updated successfully!"
        } catch (e: Exception) {
            _errorMessage.value = "Error updating mood: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Delete a mood entry
     */
    fun deleteMood(moodEntry: MoodEntry) = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.delete(moodEntry)
            _successMessage.value = "Mood deleted successfully!"
            loadDashboardData()
        } catch (e: Exception) {
            _errorMessage.value = "Error deleting mood: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Get mood by ID
     */
    fun getMoodById(id: Long, callback: (MoodEntry?) -> Unit) = viewModelScope.launch {
        try {
            val mood = repository.getMoodById(id)
            callback(mood)
        } catch (e: Exception) {
            _errorMessage.value = "Error fetching mood: ${e.message}"
            callback(null)
        }
    }

    /**
     * Get moods by date range
     */
    fun getMoodsByDateRange(startDate: Long, endDate: Long): LiveData<List<MoodEntry>> {
        return repository.getMoodsByDateRange(startDate, endDate)
    }

    /**
     * Load dashboard data (averages, counts)
     */
    fun loadDashboardData() = viewModelScope.launch {
        try {
            _isLoading.value = true

            // Load average moods
            val avg7 = repository.getAverageMoodLast7Days()
            val avg30 = repository.getAverageMoodLast30Days()

            _averageMood7Days.value = avg7
            _averageMood30Days.value = avg30

            // Load mood count
            val count = repository.getMoodCount()
            _moodCount.value = count

            // Load recent moods
            val recent = repository.getMoodsLastNDays(7)
            _recentMoods.value = recent

        } catch (e: Exception) {
            _errorMessage.value = "Error loading dashboard data: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Load mood statistics for a date range
     */
    fun loadMoodStatistics(startDate: Long, endDate: Long) = viewModelScope.launch {
        try {
            _isLoading.value = true
            val stats = repository.getMoodStatistics(startDate, endDate)
            _moodStatistics.value = stats
        } catch (e: Exception) {
            _errorMessage.value = "Error loading statistics: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Get moods for the last N days
     */
    fun getMoodsLastNDays(days: Int, callback: (List<MoodEntry>) -> Unit) = viewModelScope.launch {
        try {
            val moods = repository.getMoodsLastNDays(days)
            callback(moods)
        } catch (e: Exception) {
            _errorMessage.value = "Error fetching moods: ${e.message}"
            callback(emptyList())
        }
    }

    /**
     * Delete all moods
     */
    fun deleteAllMoods() = viewModelScope.launch {
        try {
            _isLoading.value = true
            repository.deleteAllMoods()
            _successMessage.value = "All moods deleted successfully!"
            loadDashboardData()
        } catch (e: Exception) {
            _errorMessage.value = "Error deleting all moods: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Clear error message
     */
    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    /**
     * Clear success message
     */
    fun clearSuccessMessage() {
        _successMessage.value = null
    }
}