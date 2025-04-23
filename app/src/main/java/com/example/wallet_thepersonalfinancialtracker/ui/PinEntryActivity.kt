package com.example.wallet_thepersonalfinancialtracker.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_thepersonalfinancialtracker.R

class PinEntryActivity : AppCompatActivity() {

    private lateinit var pinDots: List<ImageView>
    private lateinit var sharedPreferences: SharedPreferences
    private val enteredPin = StringBuilder(4)
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_entry)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize PIN dots
        pinDots = listOf(
            findViewById(R.id.pinDot1),
            findViewById(R.id.pinDot2),
            findViewById(R.id.pinDot3),
            findViewById(R.id.pinDot4)
        )

        // Set up number buttons
        for (i in 0..9) {
            val buttonId = resources.getIdentifier("btn$i", "id", packageName)
            findViewById<Button>(buttonId).setOnClickListener { onNumberButtonClicked(i) }
        }

        // Set up OK button
        findViewById<Button>(R.id.btnOk).setOnClickListener { onOkButtonClicked() }

        // Set up Backspace button
        findViewById<ImageButton>(R.id.btnBackspace).setOnClickListener { onBackspaceButtonClicked() }

        // Set up Forget PIN text
        findViewById<TextView>(R.id.tvForgetPin).setOnClickListener { onForgetPinClicked() }
    }

    private fun onNumberButtonClicked(number: Int) {
        if (enteredPin.length < 4) {
            enteredPin.append(number)
            updatePinDots()

            // Highlight the button with green color briefly
            val buttonId = resources.getIdentifier("btn$number", "id", packageName)
            val button = findViewById<Button>(buttonId)
            button.isPressed = true
            handler.postDelayed({ button.isPressed = false }, 150)
        }
    }

    private fun onBackspaceButtonClicked() {
        if (enteredPin.isNotEmpty()) {
            enteredPin.deleteCharAt(enteredPin.length - 1)
            updatePinDots()
        }
    }

    private fun onOkButtonClicked() {
        if (enteredPin.length == 4) {
            // Get stored PIN from SharedPreferences
            val storedPin = sharedPreferences.getString("userPin", "")

            if (enteredPin.toString() == storedPin) {
                // PIN is correct, navigate to home screen
                Toast.makeText(this, "PIN verified successfully", Toast.LENGTH_SHORT).show()
                // Navigate to home screen
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                // PIN is incorrect, show error
                Toast.makeText(this, "Incorrect PIN. Please try again.", Toast.LENGTH_SHORT).show()
                // Clear the entered PIN
                enteredPin.clear()
                updatePinDots()
            }
        } else {
            Toast.makeText(this, "Please enter all 4 digits", Toast.LENGTH_SHORT).show()
        }
    }

    // In PinEntryActivity.kt
    private fun onForgetPinClicked() {
        // Get user email from SharedPreferences
        val userEmail = sharedPreferences.getString("email", "")

        if (userEmail.isNullOrEmpty()) {
            Toast.makeText(this, "User email not found in preferences", Toast.LENGTH_SHORT).show()
            return
        }

        // For debugging - verify email is actually found
        Log.d("PinEntryActivity", "Found user email: $userEmail")

        // Show dialog for PIN recovery
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Reset PIN")
            .setMessage("We will send an OTP to your registered email address ($userEmail) to reset your PIN.")
            .setPositiveButton("Send OTP") { dialog, _ ->
                dialog.dismiss()

                // Create a new intent with the email extra
                val intent = Intent(this, OtpActivity::class.java)
                intent.putExtra("USER_EMAIL", userEmail)

                // For debugging - verify intent has the extra
                Log.d(
                    "PinEntryActivity",
                    "Starting OtpActivity with email: ${intent.getStringExtra("USER_EMAIL")}"
                )

                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    private fun updatePinDots() {
        // Update the PIN dots based on enteredPin
        for (i in pinDots.indices) {
            if (i < enteredPin.length) {
                pinDots[i].setImageResource(R.drawable.pin_dot_filled)
            } else {
                pinDots[i].setImageResource(R.drawable.pin_dot_empty)
            }
        }
    }
}
