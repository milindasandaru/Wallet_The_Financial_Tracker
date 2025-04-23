package com.example.wallet_thepersonalfinancialtracker.model

data class Expense(
    val id: Long = 0,           // Unique identifier for each expense
    val amount: Double,         // Amount spent
    val category: String,       // Category of expense (Food, Transport, etc.)
    val date: String,           // Date in "yyyy-MM-dd" format
    val note: String = ""       // Optional note about the expense
)
