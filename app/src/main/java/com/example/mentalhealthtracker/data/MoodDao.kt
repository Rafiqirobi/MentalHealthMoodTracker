package com.example.mentalhealthtracker.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MoodDao {

    @Insert
    suspend fun insert(moodEntry: MoodEntry): Long

    @Update
    suspend fun update(moodEntry: MoodEntry)

    @Delete
    suspend fun delete(moodEntry: MoodEntry)

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun getAllMoods(): LiveData<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE timestamp >= :startDate AND timestamp <= :endDate ORDER BY timestamp DESC")
    fun getMoodsByDateRange(startDate: Long, endDate: Long): LiveData<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMoods(limit: Int): LiveData<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE timestamp >= :startDate ORDER BY timestamp ASC")
    suspend fun getMoodsSince(startDate: Long): List<MoodEntry>

    @Query("SELECT AVG(moodLevel) FROM mood_entries WHERE timestamp >= :startDate")
    suspend fun getAverageMood(startDate: Long): Float?

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    suspend fun getMoodById(id: Long): MoodEntry?

    @Query("SELECT COUNT(*) FROM mood_entries")
    suspend fun getMoodCount(): Int

    @Query("SELECT COUNT(*) FROM mood_entries WHERE timestamp >= :startDate")
    suspend fun getMoodCountSince(startDate: Long): Int

    @Query("SELECT * FROM mood_entries WHERE moodLevel = :moodLevel ORDER BY timestamp DESC")
    fun getMoodsByLevel(moodLevel: Int): LiveData<List<MoodEntry>>

    @Query("DELETE FROM mood_entries")
    suspend fun deleteAll()

    @Query("DELETE FROM mood_entries WHERE timestamp < :beforeDate")
    suspend fun deleteOlderThan(beforeDate: Long)
}