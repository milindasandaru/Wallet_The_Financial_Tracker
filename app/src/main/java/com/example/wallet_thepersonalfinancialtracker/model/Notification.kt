package com.example.wallet_thepersonalfinancialtracker.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean,
    val type: Type
) : Serializable {

    enum class Type {
        WARNING,
        ALERT,
        REMINDER
    }

    fun getFormattedDate(): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
        return format.format(date)
    }
}
