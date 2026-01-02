package com.example.mentalhealthtracker.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Helper class for date and time operations
 */
object DateTimeHelper {

    private const val DATE_FORMAT_FULL = "MMM dd, yyyy 'at' hh:mm a"
    private const val DATE_FORMAT_SHORT = "MMM dd, yyyy"
    private const val TIME_FORMAT = "hh:mm a"
    private const val DATE_FORMAT_DAY_MONTH = "MMM dd"

    /**
     * Format timestamp to full date and time string
     */
    fun formatDateTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATE_FORMAT_FULL, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Format timestamp to date only string
     */
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATE_FORMAT_SHORT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Format timestamp to time only string
     */
    fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat(TIME_FORMAT, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Format timestamp to day and month only
     */
    fun formatDayMonth(timestamp: Long): String {
        val sdf = SimpleDateFormat(DATE_FORMAT_DAY_MONTH, Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Get relative time string (e.g., "Today", "Yesterday", "2 days ago")
     */
    fun getRelativeTimeString(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

        return when {
            diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                "$minutes minute${if (minutes > 1) "s" else ""} ago"
            }
            diff < TimeUnit.DAYS.toMillis(1) && isSameDay(now, timestamp) -> {
                "Today at ${formatTime(timestamp)}"
            }
            diff < TimeUnit.DAYS.toMillis(2) && isYesterday(timestamp) -> {
                "Yesterday at ${formatTime(timestamp)}"
            }
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diff)
                "$days day${if (days > 1) "s" else ""} ago"
            }
            else -> formatDate(timestamp)
        }
    }

    /**
     * Check if two timestamps are on the same day
     */
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * Check if timestamp was yesterday
     */
    fun isYesterday(timestamp: Long): Boolean {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }

        return isSameDay(yesterday.timeInMillis, timestamp)
    }

    /**
     * Get start of day timestamp
     */
    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    /**
     * Get end of day timestamp
     */
    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return calendar.timeInMillis
    }

    /**
     * Get timestamp N days ago
     */
    fun getDaysAgo(days: Int): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
        }
        return calendar.timeInMillis
    }

    /**
     * Get timestamp N days from now
     */
    fun getDaysFromNow(days: Int): Long {
        val calendar = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, days)
        }
        return calendar.timeInMillis
    }

    /**
     * Get day of week name
     */
    fun getDayOfWeek(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    /**
     * Get month name
     */
    fun getMonthName(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMMM", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}