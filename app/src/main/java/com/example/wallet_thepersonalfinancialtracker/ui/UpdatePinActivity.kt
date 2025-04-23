package com.example.wallet_thepersonalfinancialtracker.ui

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_thepersonalfinancialtracker.R

class UpdatePinActivity : AppCompatActivity() {

    private lateinit var pinDots: List<ImageView>
    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView
    private val enteredPin = StringBuilder(4)
    private var confirmPin = ""
    private var isConfirmPinStep = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_entry) // Reuse the PIN entry layout

        // Initialize title and subtitle
        tvTitle = findViewById(R.id.tvWelcome)
        tvSubtitle = findViewById(R.id.tvEnterPin)

        tvTitle.text = "Create New PIN"
        tvSubtitle.text = "Enter a new 4-digit PIN"

        // Hide the Forget PIN option
        findViewById<TextView>(R.id.tvForgetPin).visibility = TextView.GONE

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

        // Set up Back button
        //findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }

    private fun onNumberButtonClicked(number: Int) {
        if (enteredPin.length < 4) {
            enteredPin.append(number)
            updatePinDots()
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
            if (!isConfirmPinStep) {
                // First PIN entry - store and move to confirmation
                confirmPin = enteredPin.toString()
                enteredPin.clear()
                updatePinDots()
                tvTitle.text = "Confirm PIN"
                tvSubtitle.text = "Re-enter your new PIN"
                isConfirmPinStep = true
            } else {
                // Confirm PIN entry
                if (enteredPin.toString() == confirmPin) {
                    // PINs match - save the new PIN
                    val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                    sharedPreferences.edit()
                        .putString("userPin", confirmPin)
                        .apply()

                    Toast.makeText(this, "PIN updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    // PINs don't match
                    Toast.makeText(this, "PINs don't match. Try again.", Toast.LENGTH_SHORT).show()
                    enteredPin.clear()
                    updatePinDots()
                }
            }
        } else {
            Toast.makeText(this, "Please enter all 4 digits", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePinDots() {
        for (i in pinDots.indices) {
            if (i < enteredPin.length) {
                pinDots[i].setImageResource(R.drawable.pin_dot_filled)
            } else {
                pinDots[i].setImageResource(R.drawable.pin_dot_empty)
            }
        }
    }
}
