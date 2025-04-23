package com.example.wallet_thepersonalfinancialtracker.model

data class Category(
    val name: String,
    val type: Type,
    val iconRes: Int,
    val budget: Double = 0.0,  // Default value
    val startDate: String = ""  // Default value
) {
    enum class Type { INCOME, EXPENSE }
}
