package com.example.wallet_thepersonalfinancialtracker.ui

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.utils.EmailService
import com.example.wallet_thepersonalfinancialtracker.utils.OtpManager

class OtpActivity : AppCompatActivity() {

    private lateinit var otpBoxes: List<TextView>
    private lateinit var tvError: TextView
    private lateinit var tvResend: TextView
    private lateinit var tvTimer: TextView
    private lateinit var countDownTimer: CountDownTimer

    private lateinit var otpManager: OtpManager
    private var enteredOtp = StringBuilder(4)
    private var timerMillis: Long = 3 * 60 * 1000 // 3 minutes

    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        // Add debugging logs
        Log.d("OtpActivity", "Intent extras: ${intent.extras?.keySet()?.joinToString()}")

        // Get user email from intent extras
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        Log.d("OtpActivity", "Retrieved email: '$userEmail'")

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "User email not provided", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Rest of your code...

        otpManager = OtpManager(this)

        otpBoxes = listOf(
            findViewById(R.id.otpBox1),
            findViewById(R.id.otpBox2),
            findViewById(R.id.otpBox3),
            findViewById(R.id.otpBox4)
        )
        tvError = findViewById(R.id.tvError)
        tvResend = findViewById(R.id.tvResend)
        tvTimer = findViewById(R.id.tvTimer)

        // Setup keypad buttons
        for (i in 0..9) {
            val btn = findViewById<Button>(resources.getIdentifier("btn$i", "id", packageName))
            btn.setOnClickListener { onDigitPressed(i) }
        }
        findViewById<Button>(R.id.btnOk).setOnClickListener { onOkPressed() }
        findViewById<ImageButton>(R.id.btnBackspace).setOnClickListener { onBackspacePressed() }
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        tvResend.setOnClickListener {
            if (tvResend.isEnabled) {
                resendOtp()
            }
        }

        sendOtp() // Send OTP email on activity start
        updateOtpBoxes()
    }

    private fun sendOtp() {
        val otp = otpManager.generateOtp()
        EmailService.sendOtpEmail(this, userEmail, otp)
        Toast.makeText(this, "OTP sent to $userEmail", Toast.LENGTH_SHORT).show()
        tvResend.isEnabled = false
        startTimer()
    }

    private fun resendOtp() {
        enteredOtp.clear()
        updateOtpBoxes()
        clearError()
        sendOtp()
    }

    private fun startTimer() {
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        val remainingTime = otpManager.getOtpRemainingTime()
        countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val min = (millisUntilFinished / 1000) / 60
                val sec = (millisUntilFinished / 1000) % 60
                tvTimer.text = String.format("OTP expires in - %d:%02d", min, sec)
            }

            override fun onFinish() {
                tvTimer.text = "OTP expired."
                tvResend.isEnabled = true
                otpManager.clearOtp()
            }
        }.start()
    }

    private fun onDigitPressed(num: Int) {
        if (enteredOtp.length < 4) {
            enteredOtp.append(num)
            updateOtpBoxes()
            clearError()
        }
    }

    private fun onBackspacePressed() {
        if (enteredOtp.isNotEmpty()) {
            enteredOtp.deleteCharAt(enteredOtp.length - 1)
            updateOtpBoxes()
            clearError()
        }
    }

    private fun onOkPressed() {
        if (enteredOtp.length == 4) {
            if (otpManager.verifyOtp(enteredOtp.toString())) {
                Toast.makeText(this, "OTP Verified!", Toast.LENGTH_SHORT).show()
                // Navigate to Update PIN page
                val intent = Intent(this, UpdatePinActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                showError("Incorrect OTP. Please try again.")
                enteredOtp.clear()
                updateOtpBoxes()
            }
        } else {
            Toast.makeText(this, "Please enter all 4 digits", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateOtpBoxes() {
        for (i in otpBoxes.indices) {
            otpBoxes[i].text = if (i < enteredOtp.length) enteredOtp[i].toString() else ""
        }
    }

    private fun showError(msg: String) {
        tvError.text = msg
        tvError.visibility = TextView.VISIBLE
        otpBoxes.forEach { it.background = ContextCompat.getDrawable(this, R.drawable.otp_box_error) }
    }

    private fun clearError() {
        tvError.visibility = TextView.GONE
        otpBoxes.forEach { it.background = ContextCompat.getDrawable(this, R.drawable.otp_box_bg) }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }
}
