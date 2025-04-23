package com.example.wallet_thepersonalfinancialtracker.model

data class TransactionGroup(
    val title: String,
    val transactions: List<Transaction>,
    val totalAmount: Double,
    val isExpanded: Boolean = true
) {
    fun getFormattedTotal(): String {
        return "$ ${String.format("%.2f", totalAmount)}"
    }
}
