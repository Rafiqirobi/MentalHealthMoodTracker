package com.example.mentalhealthtracker.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mentalhealthtracker.R
import com.example.mentalhealthtracker.databinding.ActivityBreathingExercisePerformBinding

class BreathingExercisePerformActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreathingExercisePerformBinding
    private var currentTimer: CountDownTimer? = null
    private var isExercising = false
    private var isPaused = false
    private var currentCycle = 1
    private val totalCycles = 5

    // Exercise parameters
    private var breathInDuration = 4000L
    private var holdAfterInhaleDuration = 7000L
    private var breathOutDuration = 8000L
    private var holdAfterExhaleDuration = 0L

    private var currentPhase = BreathingPhase.BREATHE_IN
    private var exerciseName = "Breathing Exercise"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreathingExercisePerformBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupExerciseFromIntent()
        setupClickListeners()
        startExercise()
    }

    private fun setupExerciseFromIntent() {
        exerciseName = intent.getStringExtra("EXERCISE_NAME") ?: "Breathing Exercise"
        val exerciseType = intent.getStringExtra("EXERCISE_TYPE") ?: "FOUR_SEVEN_EIGHT"

        binding.exerciseNameTextView.text = exerciseName

        when (exerciseType) {
            "FOUR_SEVEN_EIGHT" -> {
                breathInDuration = 4000L
                holdAfterInhaleDuration = 7000L
                breathOutDuration = 8000L
                holdAfterExhaleDuration = 0L
            }
            "BOX" -> {
                breathInDuration = 4000L
                holdAfterInhaleDuration = 4000L
                breathOutDuration = 4000L
                holdAfterExhaleDuration = 4000L
            }
            "DEEP" -> {
                breathInDuration = 5000L
                holdAfterInhaleDuration = 2000L
                breathOutDuration = 5000L
                holdAfterExhaleDuration = 0L
            }
            "CALM" -> {
                breathInDuration = 4000L
                holdAfterInhaleDuration = 0L
                breathOutDuration = 6000L
                holdAfterExhaleDuration = 0L
            }
        }
    }

    private fun setupClickListeners() {
        binding.closeButton.setOnClickListener {
            showExitConfirmation()
        }

        binding.pausePlayButton.setOnClickListener {
            if (isPaused) {
                resumeExercise()
            } else {
                pauseExercise()
            }
        }

        binding.stopButton.setOnClickListener {
            showExitConfirmation()
        }

        binding.doneButton.setOnClickListener {
            finish()
        }
    }

    private fun startExercise() {
        isExercising = true
        isPaused = false
        currentCycle = 1
        updateCycleText()
        updateProgressIndicator()
        executePhase()
    }

    private fun executePhase() {
        if (!isExercising || isPaused) return

        when (currentPhase) {
            BreathingPhase.BREATHE_IN -> {
                binding.instructionTextView.text = "Breathe In"
                animateCircle(expand = true, duration = breathInDuration)
                startCountdown(breathInDuration) {
                    currentPhase = if (holdAfterInhaleDuration > 0) {
                        BreathingPhase.HOLD_AFTER_INHALE
                    } else {
                        BreathingPhase.BREATHE_OUT
                    }
                    executePhase()
                }
            }

            BreathingPhase.HOLD_AFTER_INHALE -> {
                binding.instructionTextView.text = "Hold"
                startCountdown(holdAfterInhaleDuration) {
                    currentPhase = BreathingPhase.BREATHE_OUT
                    executePhase()
                }
            }

            BreathingPhase.BREATHE_OUT -> {
                binding.instructionTextView.text = "Breathe Out"
                animateCircle(expand = false, duration = breathOutDuration)
                startCountdown(breathOutDuration) {
                    currentPhase = if (holdAfterExhaleDuration > 0) {
                        BreathingPhase.HOLD_AFTER_EXHALE
                    } else {
                        BreathingPhase.BREATHE_IN
                    }

                    if (currentPhase == BreathingPhase.BREATHE_IN) {
                        // Cycle completed
                        currentCycle++
                        updateCycleText()
                        updateProgressIndicator()

                        if (currentCycle > totalCycles) {
                            completeExercise()
                            return@startCountdown
                        }
                    }

                    executePhase()
                }
            }

            BreathingPhase.HOLD_AFTER_EXHALE -> {
                binding.instructionTextView.text = "Hold"
                startCountdown(holdAfterExhaleDuration) {
                    currentPhase = BreathingPhase.BREATHE_IN
                    currentCycle++
                    updateCycleText()
                    updateProgressIndicator()

                    if (currentCycle > totalCycles) {
                        completeExercise()
                        return@startCountdown
                    }

                    executePhase()
                }
            }
        }
    }

    private fun startCountdown(duration: Long, onComplete: () -> Unit) {
        val seconds = (duration / 1000).toInt()

        currentTimer?.cancel()
        currentTimer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingSeconds = (millisUntilFinished / 1000).toInt() + 1
                binding.countdownTextView.text = remainingSeconds.toString()
            }

            override fun onFinish() {
                if (!isPaused) {
                    onComplete()
                }
            }
        }.start()
    }

    private fun animateCircle(expand: Boolean, duration: Long) {
        val startScale = if (expand) 1f else 1.8f
        val endScale = if (expand) 1.8f else 1f

        val scaleX = ObjectAnimator.ofFloat(binding.breathingCircle, "scaleX", startScale, endScale)
        val scaleY = ObjectAnimator.ofFloat(binding.breathingCircle, "scaleY", startScale, endScale)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.duration = duration
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    private fun pauseExercise() {
        isPaused = true
        currentTimer?.cancel()
        binding.pausePlayButton.setImageResource(android.R.drawable.ic_media_play)
    }

    private fun resumeExercise() {
        isPaused = false
        binding.pausePlayButton.setImageResource(android.R.drawable.ic_media_pause)
        executePhase()
    }

    private fun updateCycleText() {
        binding.cycleTextView.text = "Cycle $currentCycle of $totalCycles"
    }

    private fun updateProgressIndicator() {
        val progressViews = listOf(
            binding.progress1,
            binding.progress2,
            binding.progress3,
            binding.progress4,
            binding.progress5
        )

        progressViews.forEachIndexed { index, view ->
            if (index < currentCycle) {
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.primary))
            } else {
                view.setBackgroundColor(ContextCompat.getColor(this, R.color.divider))
            }
        }
    }

    private fun completeExercise() {
        isExercising = false
        currentTimer?.cancel()

        // Hide exercise UI
        binding.circleContainer.visibility = View.GONE
        binding.progressLayout.visibility = View.GONE
        binding.controlButtons.visibility = View.GONE
        binding.cycleTextView.visibility = View.GONE

        // Show completion message
        binding.completionLayout.visibility = View.VISIBLE
    }

    private fun showExitConfirmation() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Exit Exercise?")
            .setMessage("Are you sure you want to stop the breathing exercise?")
            .setPositiveButton("Exit") { _, _ ->
                finish()
            }
            .setNegativeButton("Continue", null)
            .show()
    }

    override fun onPause() {
        super.onPause()
        if (isExercising && !isPaused) {
            pauseExercise()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentTimer?.cancel()
    }

    override fun onBackPressed() {
        showExitConfirmation()
    }

    enum class BreathingPhase {
        BREATHE_IN,
        HOLD_AFTER_INHALE,
        BREATHE_OUT,
        HOLD_AFTER_EXHALE
    }
}