package com.example.wallet_thepersonalfinancialtracker.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.utils.NotificationHelper
import com.example.wallet_thepersonalfinancialtracker.utils.SharedPreferencesManager
import com.example.wallet_thepersonalfinancialtracker.utils.TransactionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BudgetSettingsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var etBudget: EditText
    private lateinit var btnSave: Button
    private lateinit var btnTestNotification: Button
    private lateinit var tvCurrentSpending: TextView
    private lateinit var tvRemainingBudget: TextView
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var transactionManager: TransactionManager
    private lateinit var notificationHelper: NotificationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_settings)

        try {
            // Initialize managers
            sharedPreferencesManager = SharedPreferencesManager(applicationContext)
            transactionManager = TransactionManager(applicationContext)
            notificationHelper = NotificationHelper(applicationContext)

            // Initialize views
            initViews()

            // Load current budget and spending data
            loadBudgetData()

            // Set click listeners
            setClickListeners()
        } catch (e: Exception) {
            Log.e("BudgetSettingsActivity", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing budget settings", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvTitle = findViewById(R.id.tvTitle)
        etBudget = findViewById(R.id.etBudget)
        btnSave = findViewById(R.id.btnSave)
        btnTestNotification = findViewById(R.id.btnTestNotification)
        tvCurrentSpending = findViewById(R.id.tvCurrentSpending)
        tvRemainingBudget = findViewById(R.id.tvRemainingBudget)

        // Set title with current month
        val calendar = Calendar.getInstance()
        val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val currentMonth = monthFormat.format(calendar.time)
        tvTitle.text = "Budget for $currentMonth"
    }

    private fun loadBudgetData() {
        try {
            // Get current budget
            val currentBudget = sharedPreferencesManager.getMonthlyBudget()
            if (currentBudget > 0) {
                etBudget.setText(currentBudget.toString())
            }

            // Get current month's expenses
            val currentSpending = transactionManager.getCurrentMonthExpensesTotal()
            val currencySymbol = sharedPreferencesManager.getCurrencyType()

            // Update UI
            tvCurrentSpending.text = "Current Spending: $currencySymbol ${String.format("%.2f", currentSpending)}"

            // Calculate remaining budget
            if (currentBudget > 0) {
                val remaining = currentBudget - currentSpending
                tvRemainingBudget.text = "Remaining Budget: $currencySymbol ${String.format("%.2f", remaining)}"
            } else {
                tvRemainingBudget.text = "Set a budget to track your spending"
            }
        } catch (e: Exception) {
            Log.e("BudgetSettingsActivity", "Error loading budget data: ${e.message}", e)
        }
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveBudget()
        }

        btnTestNotification.setOnClickListener {
            showTestNotification()
        }
    }

    private fun saveBudget() {
        try {
            val budgetText = etBudget.text.toString().trim()
            if (budgetText.isEmpty()) {
                Toast.makeText(this, "Please enter a budget amount", Toast.LENGTH_SHORT).show()
                return
            }

            val budget = budgetText.toDoubleOrNull()
            if (budget == null || budget <= 0) {
                Toast.makeText(this, "Please enter a valid budget amount", Toast.LENGTH_SHORT).show()
                return
            }

            // Save the budget
            sharedPreferencesManager.setMonthlyBudget(budget)
            Toast.makeText(this, "Budget saved successfully", Toast.LENGTH_SHORT).show()

            // Reload data to update UI
            loadBudgetData()
        } catch (e: Exception) {
            Log.e("BudgetSettingsActivity", "Error saving budget: ${e.message}", e)
            Toast.makeText(this, "Error saving budget", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTestNotification() {
        try {
            Toast.makeText(this, "Sending test notification...", Toast.LENGTH_SHORT).show()

            // Method 1: Use NotificationHelper
            notificationHelper.showBudgetWarningNotification()

            // Method 2: Direct notification (as a fallback)
            showDirectTestNotification()
        } catch (e: Exception) {
            Log.e("BudgetSettingsActivity", "Error showing test notification: ${e.message}", e)
            Toast.makeText(this, "Error showing notification: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDirectTestNotification() {
        val channelId = "test_channel"
        val notificationId = 9999

        // Create notification channel if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Test Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for test notifications"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open this activity when tapped
        val intent = Intent(this, BudgetSettingsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.bell) // Use your bell icon or any drawable
            .setContentTitle("Budget Test Notification")
            .setContentText("This is a direct test notification!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Check permission before showing notification (Android 13+)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(this)) {
                notify(notificationId, builder.build())
            }
        } else {
            Log.e("BudgetSettingsActivity", "Notification permission not granted")
            Toast.makeText(this, "Notification permission not granted", Toast.LENGTH_SHORT).show()
        }
    }
}
