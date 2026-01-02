package com.example.mentalhealthtracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mentalhealthtracker.data.StressReliefPlace
import com.example.mentalhealthtracker.databinding.ItemStressReliefPlaceBinding
import com.example.mentalhealthtracker.utils.LocationHelper

class StressReliefPlaceAdapter(
    private val onDirectionsClick: (StressReliefPlace) -> Unit
) : ListAdapter<StressReliefPlace, StressReliefPlaceAdapter.PlaceViewHolder>(PlaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemStressReliefPlaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaceViewHolder(
        private val binding: ItemStressReliefPlaceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(place: StressReliefPlace) {
            binding.placeNameTextView.text = place.name
            binding.placeTypeTextView.text = place.type.displayName

            // Format distance
            val locationHelper = LocationHelper(binding.root.context)
            binding.distanceTextView.text = locationHelper.formatDistance(place.distance)

            // Set address
            if (place.address != null) {
                binding.addressTextView.visibility = View.VISIBLE
                binding.addressTextView.text = place.address
            } else {
                binding.addressTextView.visibility = View.GONE
            }

            // Set rating
            if (place.rating != null) {
                binding.ratingTextView.visibility = View.VISIBLE
                binding.ratingTextView.text = "⭐ ${String.format("%.1f", place.rating)}"
            } else {
                binding.ratingTextView.visibility = View.GONE
            }

            // Set open status
            if (place.isOpen != null) {
                binding.openStatusTextView.visibility = View.VISIBLE
                if (place.isOpen == true) {
                    binding.openStatusTextView.text = "Open now"
                    binding.openStatusTextView.setTextColor(
                        binding.root.context.getColor(android.R.color.holo_green_dark)
                    )
                } else {
                    binding.openStatusTextView.text = "Closed"
                    binding.openStatusTextView.setTextColor(
                        binding.root.context.getColor(android.R.color.holo_red_dark)
                    )
                }
            } else {
                binding.openStatusTextView.visibility = View.GONE
            }

            // Directions button click
            binding.directionsButton.setOnClickListener {
                onDirectionsClick(place)
            }
        }
    }

    private class PlaceDiffCallback : DiffUtil.ItemCallback<StressReliefPlace>() {
        override fun areItemsTheSame(oldItem: StressReliefPlace, newItem: StressReliefPlace): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.latitude == newItem.latitude &&
                    oldItem.longitude == newItem.longitude
        }

        override fun areContentsTheSame(oldItem: StressReliefPlace, newItem: StressReliefPlace): Boolean {
            return oldItem == newItem
        }
    }
}