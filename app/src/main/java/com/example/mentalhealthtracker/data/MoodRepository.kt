package com.example.mentalhealthtracker.data

import android.content.Context
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar

class MoodRepository(context: Context) {

    private val moodDao = AppDatabase.getDatabase(context).moodDao()

    // LiveData for observing all moods
    val allMoods: LiveData<List<MoodEntry>> = moodDao.getAllMoods()

    /**
     * Insert a new mood entry
     * @return The ID of the inserted entry
     */
    suspend fun insert(moodEntry: MoodEntry): Long {
        return withContext(Dispatchers.IO) {
            moodDao.insert(moodEntry)
        }
    }

    /**
     * Update an existing mood entry
     */
    suspend fun update(moodEntry: MoodEntry) {
        withContext(Dispatchers.IO) {
            moodDao.update(moodEntry)
        }
    }

    /**
     * Delete a mood entry
     */
    suspend fun delete(moodEntry: MoodEntry) {
        withContext(Dispatchers.IO) {
            moodDao.delete(moodEntry)
        }
    }

    /**
     * Get recent moods (limited number)
     */
    fun getRecentMoods(limit: Int = 10): LiveData<List<MoodEntry>> {
        return moodDao.getRecentMoods(limit)
    }

    /**
     * Get moods within a date range
     */
    fun getMoodsByDateRange(startDate: Long, endDate: Long): LiveData<List<MoodEntry>> {
        return moodDao.getMoodsByDateRange(startDate, endDate)
    }

    /**
     * Get moods by specific mood level
     */
    fun getMoodsByLevel(moodLevel: Int): LiveData<List<MoodEntry>> {
        return moodDao.getMoodsByLevel(moodLevel)
    }

    /**
     * Get mood by ID
     */
    suspend fun getMoodById(id: Long): MoodEntry? {
        return withContext(Dispatchers.IO) {
            moodDao.getMoodById(id)
        }
    }

    /**
     * Get average mood for the last N days
     */
    suspend fun getAverageMoodLastNDays(days: Int): Float? {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return withContext(Dispatchers.IO) {
            moodDao.getAverageMood(calendar.timeInMillis)
        }
    }

    /**
     * Get average mood for the last 7 days
     */
    suspend fun getAverageMoodLast7Days(): Float? {
        return getAverageMoodLastNDays(7)
    }

    /**
     * Get average mood for the last 30 days
     */
    suspend fun getAverageMoodLast30Days(): Float? {
        return getAverageMoodLastNDays(30)
    }

    /**
     * Get average mood for the last 90 days
     */
    suspend fun getAverageMoodLast90Days(): Float? {
        return getAverageMoodLastNDays(90)
    }

    /**
     * Get total count of mood entries
     */
    suspend fun getMoodCount(): Int {
        return withContext(Dispatchers.IO) {
            moodDao.getMoodCount()
        }
    }

    /**
     * Get count of mood entries since a date
     */
    suspend fun getMoodCountLastNDays(days: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return withContext(Dispatchers.IO) {
            moodDao.getMoodCountSince(calendar.timeInMillis)
        }
    }

    /**
     * Get all moods since a specific date
     */
    suspend fun getMoodsSinceDate(startDate: Long): List<MoodEntry> {
        return withContext(Dispatchers.IO) {
            moodDao.getMoodsSince(startDate)
        }
    }

    /**
     * Get moods for the last N days (for charting)
     */
    suspend fun getMoodsLastNDays(days: Int): List<MoodEntry> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        return withContext(Dispatchers.IO) {
            moodDao.getMoodsSince(calendar.timeInMillis)
        }
    }

    /**
     * Delete all mood entries
     */
    suspend fun deleteAllMoods() {
        withContext(Dispatchers.IO) {
            moodDao.deleteAll()
        }
    }

    /**
     * Delete moods older than N days
     */
    suspend fun deleteOlderThanDays(days: Int) {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -days)
        withContext(Dispatchers.IO) {
            moodDao.deleteOlderThan(calendar.timeInMillis)
        }
    }

    /**
     * Get mood statistics for a date range
     */
    suspend fun getMoodStatistics(startDate: Long, endDate: Long): MoodStatistics {
        return withContext(Dispatchers.IO) {
            val moods = moodDao.getMoodsByDateRange(startDate, endDate).value ?: emptyList()

            if (moods.isEmpty()) {
                return@withContext MoodStatistics(
                    totalEntries = 0,
                    averageMood = 0f,
                    mostCommonMood = null,
                    moodCounts = emptyMap()
                )
            }

            val totalEntries = moods.size
            val averageMood = moods.map { it.moodLevel }.average().toFloat()
            val moodCounts = moods.groupingBy { it.moodLevel }.eachCount()
            val mostCommonMood = moodCounts.maxByOrNull { it.value }?.key

            MoodStatistics(
                totalEntries = totalEntries,
                averageMood = averageMood,
                mostCommonMood = mostCommonMood,
                moodCounts = moodCounts
            )
        }
    }
}

/**
 * Data class for mood statistics
 */
data class MoodStatistics(
    val totalEntries: Int,
    val averageMood: Float,
    val mostCommonMood: Int?,
    val moodCounts: Map<Int, Int>
)