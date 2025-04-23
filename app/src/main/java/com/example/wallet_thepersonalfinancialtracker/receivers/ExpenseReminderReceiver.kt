package com.example.wallet_thepersonalfinancialtracker.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.wallet_thepersonalfinancialtracker.utils.NotificationHelper

class ExpenseReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showExpenseReminderNotification()
    }
}
