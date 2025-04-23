package com.example.wallet_thepersonalfinancialtracker.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.Random

class OtpManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("OtpPrefs", Context.MODE_PRIVATE)

    companion object {
        private const val OTP_KEY = "generated_otp"
        private const val OTP_EXPIRY_KEY = "otp_expiry_time"
        private const val OTP_LENGTH = 4
    }

    // Generate a new OTP and save it
    fun generateOtp(): String {
        val chars = "0123456789"
        val random = Random()
        val otp = StringBuilder(OTP_LENGTH)

        // Generate random 4-digit code
        for (i in 0 until OTP_LENGTH) {
            otp.append(chars[random.nextInt(chars.length)])
        }

        val otpString = otp.toString()

        // Set expiry time (3 minutes from now)
        val expiryTime = System.currentTimeMillis() + (3 * 60 * 1000)

        // Save OTP and expiry
        sharedPreferences.edit()
            .putString(OTP_KEY, otpString)
            .putLong(OTP_EXPIRY_KEY, expiryTime)
            .apply()

        return otpString
    }

    // Verify the provided OTP
    fun verifyOtp(inputOtp: String): Boolean {
        val storedOtp = sharedPreferences.getString(OTP_KEY, "") ?: ""
        val expiryTime = sharedPreferences.getLong(OTP_EXPIRY_KEY, 0)

        // Check if OTP is correct and not expired
        return inputOtp == storedOtp && System.currentTimeMillis() < expiryTime
    }

    // Get remaining time in milliseconds
    fun getOtpRemainingTime(): Long {
        val expiryTime = sharedPreferences.getLong(OTP_EXPIRY_KEY, 0)
        val remainingTime = expiryTime - System.currentTimeMillis()
        return if (remainingTime > 0) remainingTime else 0
    }

    // Clear OTP data
    fun clearOtp() {
        sharedPreferences.edit()
            .remove(OTP_KEY)
            .remove(OTP_EXPIRY_KEY)
            .apply()
    }
}
