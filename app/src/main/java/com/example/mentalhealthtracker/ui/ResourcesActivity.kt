package com.example.mentalhealthtracker.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mentalhealthtracker.databinding.ActivityResourcesBinding

class ResourcesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResourcesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResourcesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        setupHotlineCards()
    }

    private fun setupHotlineCards() {
        // National Suicide Prevention Lifeline
        binding.suicidePreventionCard.setOnClickListener {
            dialPhoneNumber("988")
        }

        // Crisis Text Line
        binding.crisisTextCard.setOnClickListener {
            sendSMS("741741", "HOME")
        }

        // SAMHSA Helpline
        binding.samhsaCard.setOnClickListener {
            dialPhoneNumber("1-800-662-4357")
        }

        // Domestic Violence Hotline
        binding.domesticViolenceCard.setOnClickListener {
            dialPhoneNumber("1-800-799-7233")
        }
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }

    private fun sendSMS(phoneNumber: String, message: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:$phoneNumber")
            putExtra("sms_body", message)
        }
        startActivity(intent)
    }
}