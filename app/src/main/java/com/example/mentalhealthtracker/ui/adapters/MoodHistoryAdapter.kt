package com.example.mentalhealthtracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mentalhealthtracker.data.MoodEntry
import com.example.mentalhealthtracker.data.MoodLevel
import com.example.mentalhealthtracker.databinding.ItemMoodEntryBinding
import com.example.mentalhealthtracker.utils.DateTimeHelper

class MoodHistoryAdapter(
    private val onItemClick: (MoodEntry) -> Unit
) : ListAdapter<MoodEntry, MoodHistoryAdapter.MoodViewHolder>(MoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MoodViewHolder(
        private val binding: ItemMoodEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(moodEntry: MoodEntry) {
            // Set mood emoji
            binding.moodEmojiTextView.text = moodEntry.moodEmoji

            // Set mood level text
            binding.moodLevelTextView.text = MoodLevel.fromValue(moodEntry.moodLevel).description

            // Set timestamp
            binding.timestampTextView.text = DateTimeHelper.getRelativeTimeString(moodEntry.timestamp)

            // Set journal entry
            if (moodEntry.journalEntry.isNullOrBlank()) {
                binding.journalTextView.visibility = View.GONE
            } else {
                binding.journalTextView.visibility = View.VISIBLE
                binding.journalTextView.text = moodEntry.journalEntry
            }

            // Set triggers
            if (moodEntry.triggers.isNullOrBlank()) {
                binding.triggersTextView.visibility = View.GONE
            } else {
                binding.triggersTextView.visibility = View.VISIBLE
                binding.triggersTextView.text = "Triggers: ${moodEntry.triggers}"
            }

            // Set location
            if (moodEntry.locationName.isNullOrBlank()) {
                binding.locationTextView.visibility = View.GONE
            } else {
                binding.locationTextView.visibility = View.VISIBLE
                binding.locationTextView.text = "📍 ${moodEntry.locationName}"
            }

            // Set click listener
            binding.root.setOnClickListener {
                onItemClick(moodEntry)
            }
        }
    }

    private class MoodDiffCallback : DiffUtil.ItemCallback<MoodEntry>() {
        override fun areItemsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean {
            return oldItem == newItem
        }
    }
}