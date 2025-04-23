package com.example.wallet_thepersonalfinancialtracker.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.wallet_thepersonalfinancialtracker.R

class SignUpActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPin = findViewById<EditText>(R.id.etPin)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        btnBack.setOnClickListener {
            finish()
        }

        btnContinue.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()
            val pin = etPin.text.toString()

            // Validation
            if (fullName.isEmpty()) {
                etFullName.error = "Full name required"
                etFullName.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                etEmail.error = "Valid email required"
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (username.isEmpty()) {
                etUsername.error = "Username required"
                etUsername.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty() || password.length < 6) {
                etPassword.error = "Password must be at least 6 characters"
                etPassword.requestFocus()
                return@setOnClickListener
            }
            if (pin.length != 4 || !TextUtils.isDigitsOnly(pin)) {
                etPin.error = "Enter a valid 4-digit PIN"
                etPin.requestFocus()
                return@setOnClickListener
            }

            // Save user data to SharedPreferences
            saveUserData(fullName, email, username, password, pin)

            // Show success message
            Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show()

            // Navigate directly to PIN entry for verification
            val intent = Intent(this, PinEntryActivity::class.java)
            intent.putExtra("FROM_SIGNUP", true) // Flag to indicate coming from signup
            startActivity(intent)
            finish() // Close this activity
        }
    }

    private fun saveUserData(fullName: String, email: String, username: String, password: String, pin: String) {
        val editor = sharedPreferences.edit()
        editor.putString("fullName", fullName)
        editor.putString("email", email)
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putString("userPin", pin)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }
}