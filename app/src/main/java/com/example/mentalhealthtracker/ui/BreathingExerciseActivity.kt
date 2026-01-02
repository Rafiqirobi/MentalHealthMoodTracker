package com.example.mentalhealthtracker.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.mentalhealthtracker.R
import com.example.mentalhealthtracker.databinding.ActivityBreathingExerciseBinding

class BreathingExerciseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreathingExerciseBinding
    private var isExercising = false
    private var currentTimer: CountDownTimer? = null
    private var currentPhase = BreathingPhase.BREATHE_IN

    // Exercise parameters
    private var breathInDuration = 4000L
    private var holdDuration = 7000L
    private var breathOutDuration = 8000L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreathingExerciseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        setupExerciseTypeSelection()
        setupStartStopButton()
    }

    private fun setupExerciseTypeSelection() {
        binding.exerciseTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio478 -> {
                    // 4-7-8 Breathing
                    breathInDuration = 4000L
                    holdDuration = 7000L
                    breathOutDuration = 8000L
                }
                R.id.radioBox -> {
                    // Box Breathing (4-4-4-4)
                    breathInDuration = 4000L
                    holdDuration = 4000L
                    breathOutDuration = 4000L
                }
                R.id.radioDeep -> {
                    // Deep Breathing (5-2-5)
                    breathInDuration = 5000L
                    holdDuration = 2000L
                    breathOutDuration = 5000L
                }
            }

            if (isExercising) {
                stopExercise()
            }
        }
    }

    private fun setupStartStopButton() {
        binding.startStopButton.setOnClickListener {
            if (isExercising) {
                stopExercise()
            } else {
                startExercise()
            }
        }
    }

    private fun startExercise() {
        isExercising = true
        binding.startStopButton.text = getString(R.string.stop_exercise)
        binding.exerciseTypeRadioGroup.isEnabled = false

        // Start with breathe in phase
        currentPhase = BreathingPhase.BREATHE_IN
        executePhase()
    }

    private fun stopExercise() {
        isExercising = false
        binding.startStopButton.text = getString(R.string.start_exercise)
        binding.exerciseTypeRadioGroup.isEnabled = true

        currentTimer?.cancel()
        resetUI()
    }

    private fun executePhase() {
        if (!isExercising) return

        when (currentPhase) {
            BreathingPhase.BREATHE_IN -> {
                binding.instructionTextView.text = getString(R.string.breathe_in)
                animateCircle(expand = true, duration = breathInDuration)
                startCountdown(breathInDuration) {
                    currentPhase = BreathingPhase.HOLD
                    executePhase()
                }
            }
            BreathingPhase.HOLD -> {
                binding.instructionTextView.text = getString(R.string.hold)
                startCountdown(holdDuration) {
                    currentPhase = BreathingPhase.BREATHE_OUT
                    executePhase()
                }
            }
            BreathingPhase.BREATHE_OUT -> {
                binding.instructionTextView.text = getString(R.string.breathe_out)
                animateCircle(expand = false, duration = breathOutDuration)
                startCountdown(breathOutDuration) {
                    currentPhase = BreathingPhase.BREATHE_IN
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
                binding.timerTextView.text = remainingSeconds.toString()
            }

            override fun onFinish() {
                onComplete()
            }
        }.start()
    }

    private fun animateCircle(expand: Boolean, duration: Long) {
        val startScale = if (expand) 1f else 1.5f
        val endScale = if (expand) 1.5f else 1f

        val scaleX = ObjectAnimator.ofFloat(binding.breathingCircle, "scaleX", startScale, endScale)
        val scaleY = ObjectAnimator.ofFloat(binding.breathingCircle, "scaleY", startScale, endScale)

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY)
        animatorSet.duration = duration
        animatorSet.interpolator = AccelerateDecelerateInterpolator()
        animatorSet.start()
    }

    private fun resetUI() {
        binding.instructionTextView.text = getString(R.string.breathe_in)
        binding.timerTextView.text = "4"
        binding.breathingCircle.scaleX = 1f
        binding.breathingCircle.scaleY = 1f
    }

    override fun onPause() {
        super.onPause()
        if (isExercising) {
            stopExercise()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentTimer?.cancel()
    }

    enum class BreathingPhase {
        BREATHE_IN,
        HOLD,
        BREATHE_OUT
    }
}