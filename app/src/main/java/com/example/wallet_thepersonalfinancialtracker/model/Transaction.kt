package com.example.wallet_thepersonalfinancialtracker.model

import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Transaction(
    val id: Long,
    val amount: Double,
    val note: String,
    val category: String,
    val date: Date,
    val isExpense: Boolean
) : Serializable {

    fun getFormattedDate(): String {
        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getFormattedTime(): String {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return timeFormat.format(date)
    }

    fun getShortDate(): String {
        val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getMonthYear(): String {
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getDayOfWeek(): String {
        val dateFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dateFormat.format(date)
    }

    fun getFormattedAmount(): String {
        return "$ ${String.format("%.2f", amount)}"
    }
}
