package com.example.wallet_thepersonalfinancialtracker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.wallet_thepersonalfinancialtracker.ui.PinEntryActivity
import com.example.wallet_thepersonalfinancialtracker.ui.SignUpActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Find the Get Started button
        val btnGetStarted = findViewById<AppCompatButton>(R.id.btnGetStarted)

        // Set click listener for the button
        btnGetStarted.setOnClickListener {
            // Check if user is already registered
            val isUserRegistered = sharedPreferences.contains("userPin")

            if (isUserRegistered) {
                // User already registered, navigate to PIN entry
                Toast.makeText(this, "Welcome back! Please enter your PIN", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, PinEntryActivity::class.java))
            } else {
                // New user, navigate to Sign Up
                Toast.makeText(this, "Navigating to Sign Up page", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, SignUpActivity::class.java))
            }

            // Optional: finish the landing page activity if you don't want users to come back to it
            // finish()
        }
    }
}
