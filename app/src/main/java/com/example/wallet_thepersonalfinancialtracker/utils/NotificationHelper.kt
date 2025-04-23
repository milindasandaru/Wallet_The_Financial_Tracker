package com.example.wallet_thepersonalfinancialtracker.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.model.Notification
import com.example.wallet_thepersonalfinancialtracker.ui.BudgetSettingsActivity
import com.example.wallet_thepersonalfinancialtracker.ui.ExpenseActivity
import com.example.wallet_thepersonalfinancialtracker.ui.HomeActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

class NotificationHelper(private val context: Context) {

    companion object {
        private const val CHANNEL_ID_BUDGET = "budget_channel"
        private const val CHANNEL_ID_REMINDER = "reminder_channel"
        private const val GROUP_KEY_WALLET = "com.example.wallet_thepersonalfinancialtracker.NOTIFICATIONS"
        private val notificationId = AtomicInteger(0)
    }

    private val sharedPreferencesManager = SharedPreferencesManager(context)

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create Budget Alert channel
            val budgetChannel = NotificationChannel(
                CHANNEL_ID_BUDGET,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications about your budget status"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }

            // Create Reminder channel
            val reminderChannel = NotificationChannel(
                CHANNEL_ID_REMINDER,
                "Expense Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to record your expenses"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
            }

            // Register the channels with the system
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(budgetChannel)
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }

    fun showBudgetWarningNotification() {
        try {
            val id = notificationId.incrementAndGet()

            // Create an intent to open the BudgetSettingsActivity
            val intent = Intent(context, BudgetSettingsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Build the notification
            val builder = NotificationCompat.Builder(context, CHANNEL_ID_BUDGET)
                .setSmallIcon(R.drawable.warningicon)
                .setContentTitle("Budget Warning")
                .setContentText("You've used 80% of your monthly budget!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY_WALLET)
                .setColor(Color.parseColor("#FFA500")) // Orange color

            // Show the notification
            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context, // <--- use context, not this
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission not granted, do not post notification
                    return@with
                }
                notify(id, builder.build())
            }

            // Save notification to SharedPreferences
            saveNotification(
                Notification(
                    id = id,
                    title = "Budget Warning",
                    message = "You've used 80% of your monthly budget!",
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    type = Notification.Type.WARNING
                )
            )
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error showing budget warning notification: ${e.message}", e)
        }
    }

    fun showBudgetExceededNotification() {
        try {
            val id = notificationId.incrementAndGet()

            // Create an intent to open the BudgetSettingsActivity
            val intent = Intent(context, BudgetSettingsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Build the notification
            val builder = NotificationCompat.Builder(context, CHANNEL_ID_BUDGET)
                .setSmallIcon(R.drawable.remindericon)
                .setContentTitle("Budget Exceeded")
                .setContentText("You've exceeded your monthly budget!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY_WALLET)
                .setColor(Color.RED)

            // Show the notification
            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context, // <--- use context, not this
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission not granted, do not post notification
                    return@with
                }
                notify(id, builder.build())
            }


            // Save notification to SharedPreferences
            saveNotification(
                Notification(
                    id = id,
                    title = "Budget Exceeded",
                    message = "You've exceeded your monthly budget!",
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    type = Notification.Type.ALERT
                )
            )
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error showing budget exceeded notification: ${e.message}", e)
        }
    }

    fun showExpenseReminderNotification() {
        try {
            val id = notificationId.incrementAndGet()

            // Create an intent to open the ExpenseActivity
            val intent = Intent(context, ExpenseActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Build the notification
            val builder = NotificationCompat.Builder(context, CHANNEL_ID_REMINDER)
                .setSmallIcon(R.drawable.remindericon)
                .setContentTitle("Expense Reminder")
                .setContentText("Don't forget to record today's expenses!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setGroup(GROUP_KEY_WALLET)
                .setColor(Color.GREEN)

            // Show the notification
            with(NotificationManagerCompat.from(context)) {
                if (ActivityCompat.checkSelfPermission(
                        context, // <--- use context, not this
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission not granted, do not post notification
                    return@with
                }
                notify(id, builder.build())
            }


            // Save notification to SharedPreferences
            saveNotification(
                Notification(
                    id = id,
                    title = "Expense Reminder",
                    message = "Don't forget to record today's expenses!",
                    timestamp = System.currentTimeMillis(),
                    isRead = false,
                    type = Notification.Type.REMINDER
                )
            )
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error showing expense reminder notification: ${e.message}", e)
        }
    }

    private fun saveNotification(notification: Notification) {
        val notifications = sharedPreferencesManager.getNotifications().toMutableList()
        notifications.add(notification)
        sharedPreferencesManager.saveNotifications(notifications)
    }

    fun getUnreadNotificationCount(): Int {
        return sharedPreferencesManager.getNotifications().count { !it.isRead }
    }

    fun getAllNotifications(): List<Notification> {
        return sharedPreferencesManager.getNotifications().sortedByDescending { it.timestamp }
    }

    fun markNotificationAsRead(id: Int) {
        val notifications = sharedPreferencesManager.getNotifications().toMutableList()
        val index = notifications.indexOfFirst { it.id == id }
        if (index != -1) {
            notifications[index] = notifications[index].copy(isRead = true)
            sharedPreferencesManager.saveNotifications(notifications)
        }
    }

    fun markAllNotificationsAsRead() {
        val notifications = sharedPreferencesManager.getNotifications().map {
            it.copy(isRead = true)
        }
        sharedPreferencesManager.saveNotifications(notifications)
    }

    fun clearAllNotifications() {
        sharedPreferencesManager.saveNotifications(emptyList())
    }

    // Add this to NotificationHelper.kt
    fun testNotifications() {
        // Test all notification types
        showBudgetWarningNotification()
        showBudgetExceededNotification()
        showExpenseReminderNotification()
    }

    fun showTestNotification(context: Context) {
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
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open HomeActivity when tapped
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.bell) // Use your bell icon or any drawable
            .setContentTitle("Test Notification")
            .setContentText("This is a test notification!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Check permission before showing notification (Android 13+)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
            }
        }
    }

}
