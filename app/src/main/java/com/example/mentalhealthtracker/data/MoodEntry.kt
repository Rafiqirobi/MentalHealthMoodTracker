package com.example.mentalhealthtracker.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val moodLevel: Int, // 1-5 (1=Very Bad, 5=Very Good)
    val moodEmoji: String,
    val journalEntry: String? = null,
    val triggers: String? = null, // Comma-separated
    val timestamp: Long = Date().time,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val locationName: String? = null
) : Parcelable

enum class MoodLevel(val value: Int, val emoji: String, val description: String) {
    VERY_BAD(1, "😢", "Very Bad"),
    BAD(2, "😔", "Bad"),
    OKAY(3, "😐", "Okay"),
    GOOD(4, "🙂", "Good"),
    VERY_GOOD(5, "😊", "Very Good");

    companion object {
        fun fromValue(value: Int): MoodLevel {
            return values().find { it.value == value } ?: OKAY
        }
    }
}