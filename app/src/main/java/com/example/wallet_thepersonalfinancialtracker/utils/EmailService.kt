package com.example.wallet_thepersonalfinancialtracker.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailService {
    companion object {
        private const val TAG = "EmailService"

        // Schedule email to be sent in background
        fun sendOtpEmail(context: Context, recipientEmail: String, otp: String) {
            val inputData = Data.Builder()
                .putString("recipientEmail", recipientEmail)
                .putString("otp", otp)
                .build()

            val emailWorkRequest = OneTimeWorkRequestBuilder<EmailWorker>()
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(emailWorkRequest)
        }
    }

    // Worker class to handle email sending in background
    class EmailWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
            try {
                val recipientEmail = inputData.getString("recipientEmail") ?: return@withContext Result.failure()
                val otp = inputData.getString("otp") ?: return@withContext Result.failure()

                // Your Gmail account credentials
                val senderEmail = "sampleprojecte@gmail.com"  // Replace with your app email
                val senderPassword = "tlgn hfeq lcwe nbnb"      // Replace with app password

                // Configure JavaMail
                val props = Properties()
                props["mail.smtp.auth"] = "true"
                props["mail.smtp.starttls.enable"] = "true"
                props["mail.smtp.host"] = "smtp.gmail.com"
                props["mail.smtp.port"] = "587"

                // Create session
                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(senderEmail, senderPassword)
                    }
                })

                // Create message
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(senderEmail))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                message.subject = "Your PIN Reset OTP"
                message.setText("Your OTP code for PIN reset is: $otp\n\nThis code will expire in 3 minutes.")

                // Send message
                Transport.send(message)
                Log.d(TAG, "Email sent successfully to $recipientEmail")

                Result.success()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send email", e)
                Result.failure()
            }
        }
    }
}
