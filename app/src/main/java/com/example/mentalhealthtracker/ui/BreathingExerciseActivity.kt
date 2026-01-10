package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mentalhealthtracker.databinding.ActivityBreathingExerciseBinding

class BreathingExerciseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreathingExerciseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreathingExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        setupExerciseCards()
    }

    private fun setupExerciseCards() {
        // 4-7-8 Breathing
        binding.exercise478Card.setOnClickListener {
            startExercise(
                name = "4-7-8 Breathing",
                type = ExerciseType.FOUR_SEVEN_EIGHT
            )
        }

        // Box Breathing
        binding.exerciseBoxCard.setOnClickListener {
            startExercise(
                name = "Box Breathing",
                type = ExerciseType.BOX
            )
        }

        // Deep Breathing
        binding.exerciseDeepCard.setOnClickListener {
            startExercise(
                name = "Deep Breathing",
                type = ExerciseType.DEEP
            )
        }

        // Calm Breathing
        binding.exerciseCalmCard.setOnClickListener {
            startExercise(
                name = "Calm Breathing",
                type = ExerciseType.CALM
            )
        }
    }

    private fun startExercise(name: String, type: ExerciseType) {
        val intent = Intent(this, BreathingExercisePerformActivity::class.java)
        intent.putExtra("EXERCISE_NAME", name)
        intent.putExtra("EXERCISE_TYPE", type.name)
        startActivity(intent)
    }

    enum class ExerciseType {
        FOUR_SEVEN_EIGHT,  // 4s in, 7s hold, 8s out
        BOX,                // 4s in, 4s hold, 4s out, 4s hold
        DEEP,               // 5s in, 2s hold, 5s out
        CALM                // 4s in, 6s out
    }
}