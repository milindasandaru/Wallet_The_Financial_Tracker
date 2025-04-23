package com.example.wallet_thepersonalfinancialtracker.utils

import android.content.Context
import android.util.Log
import com.example.wallet_thepersonalfinancialtracker.model.Transaction
import com.example.wallet_thepersonalfinancialtracker.model.TransactionGroup
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong

class TransactionManager(private val context: Context) {

    private val sharedPreferencesManager = SharedPreferencesManager(context)
    private var transactions: MutableList<Transaction> = sharedPreferencesManager.getTransactions().toMutableList()
    private var idGenerator: AtomicLong = AtomicLong(sharedPreferencesManager.getIdCounter())

    init {
        // Add sample data if the list is empty
        if (transactions.isEmpty()) {
            addSampleTransactions()
        }
    }

    private fun saveTransactions() {
        sharedPreferencesManager.saveTransactions(transactions)
        sharedPreferencesManager.saveIdCounter(idGenerator.get())
    }

    private fun addSampleTransactions() {
        try {
            // Sample data for demonstration (reduced)
            val calendar = Calendar.getInstance()

            // Current month transactions
            calendar.set(Calendar.DAY_OF_MONTH, 5)
            addTransaction(26.38, "Starbucks Coffee", "Eating Out", calendar.time, true)

            calendar.set(Calendar.DAY_OF_MONTH, 10)
            addTransaction(102.21, "Walmart", "Food", calendar.time, true)

            calendar.set(Calendar.DAY_OF_MONTH, 15)
            addTransaction(1500.00, "Monthly Salary", "Salary", calendar.time, false)

            calendar.set(Calendar.DAY_OF_MONTH, 20)
            addTransaction(56.32, "Amazon", "Shopping", calendar.time, true)
        } catch (e: Exception) {
            Log.e("TransactionManager", "Error adding sample transactions: ${e.message}", e)
        }
    }

    fun addTransaction(amount: Double, note: String, category: String, date: Date, isExpense: Boolean): Long {
        try {
            val id = idGenerator.getAndIncrement()
            val transaction = Transaction(id, amount, note, category, date, isExpense)
            transactions.add(transaction)
            saveTransactions()
            Log.d("TransactionManager", "Added transaction: $transaction")
            return id
        } catch (e: Exception) {
            Log.e("TransactionManager", "Error adding transaction: ${e.message}", e)
            return -1
        }
    }

    fun updateTransaction(id: Long, amount: Double, note: String, category: String, date: Date, isExpense: Boolean): Boolean {
        try {
            val index = transactions.indexOfFirst { it.id == id }
            if (index != -1) {
                transactions[index] = Transaction(id, amount, note, category, date, isExpense)
                saveTransactions()
                Log.d("TransactionManager", "Updated transaction with ID: $id")
                return true
            }
            return false
        } catch (e: Exception) {
            Log.e("TransactionManager", "Error updating transaction: ${e.message}", e)
            return false
        }
    }

    fun deleteTransaction(id: Long): Boolean {
        try {
            val result = transactions.removeIf { it.id == id }
            if (result) {
                saveTransactions()
                Log.d("TransactionManager", "Deleted transaction with ID: $id")
            }
            return result
        } catch (e: Exception) {
            Log.e("TransactionManager", "Error deleting transaction: ${e.message}", e)
            return false
        }
    }

    fun getTransactionById(id: Long): Transaction? {
        return transactions.find { it.id == id }
    }

    fun getAllTransactions(): List<Transaction> {
        return transactions.sortedByDescending { it.date }
    }

    fun getAllExpenses(): List<Transaction> {
        return transactions.filter { it.isExpense }.sortedByDescending { it.date }
    }

    fun getAllIncome(): List<Transaction> {
        return transactions.filter { !it.isExpense }.sortedByDescending { it.date }
    }

    fun getTransactionsByMonth(year: Int, month: Int): List<Transaction> {
        val calendar = Calendar.getInstance()
        return transactions.filter {
            calendar.time = it.date
            calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month
        }.sortedByDescending { it.date }
    }

    fun getCurrentMonthTransactions(): List<Transaction> {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        return getTransactionsByMonth(currentYear, currentMonth)
    }

    fun getCurrentMonthExpenses(): List<Transaction> {
        return getCurrentMonthTransactions().filter { it.isExpense }
    }

    fun getCurrentMonthIncome(): List<Transaction> {
        return getCurrentMonthTransactions().filter { !it.isExpense }
    }

    fun getMonthlyTotal(year: Int, month: Int): Double {
        val income = getMonthlyIncome(year, month)
        val expenses = getMonthlyExpenses(year, month)
        return income - expenses
    }

    fun getMonthlyIncome(year: Int, month: Int): Double {
        return getTransactionsByMonth(year, month)
            .filter { !it.isExpense }
            .sumOf { it.amount }
    }

    fun getMonthlyExpenses(year: Int, month: Int): Double {
        return getTransactionsByMonth(year, month)
            .filter { it.isExpense }
            .sumOf { it.amount }
    }

    fun getCurrentMonthTotal(): Double {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        return getMonthlyTotal(currentYear, currentMonth)
    }

    fun getCurrentMonthIncomeTotal(): Double {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        return getMonthlyIncome(currentYear, currentMonth)
    }

    fun getCurrentMonthExpensesTotal(): Double {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        return getMonthlyExpenses(currentYear, currentMonth)
    }

    fun groupTransactionsByDate(): List<TransactionGroup> {
        try {
            val groupedTransactions = getAllTransactions().groupBy {
                val calendar = Calendar.getInstance()
                calendar.time = it.date
                val dateFormat = SimpleDateFormat("MMMM dd, EEEE", Locale.getDefault())
                dateFormat.format(it.date)
            }

            return groupedTransactions.map { (date, transactions) ->
                val total = transactions.sumOf { if (it.isExpense) -it.amount else it.amount }
                TransactionGroup(date, transactions, total)
            }
        } catch (e: Exception) {
            Log.e("TransactionManager", "Error grouping transactions by date: ${e.message}", e)
            return emptyList()
        }
    }

    fun groupTransactionsByCategory(): List<TransactionGroup> {
        try {
            // First separate expenses and income
            val expenseGroups = getAllTransactions()
                .filter { it.isExpense }
                .groupBy { it.category }
                .map { (category, transactions) ->
                    val total = transactions.sumOf { it.amount }
                    TransactionGroup("$category (${transactions.size})", transactions, total)
                }

            val incomeGroups = getAllTransactions()
                .filter { !it.isExpense }
                .groupBy { it.category }
                .map { (category, transactions) ->
                    val total = transactions.sumOf { it.amount }
                    TransactionGroup("$category (${transactions.size})", transactions, total)
                }

            // Combine them with income first, then expenses
            return incomeGroups + expenseGroups
        } catch (e: Exception) {
            Log.e("TransactionManager", "Error grouping transactions by category: ${e.message}", e)
            return emptyList()
        }
    }

    // Get total expenses for a date range
    fun getTotalExpenses(start: Date, end: Date): Double {
        return getAllTransactions()
            .filter { it.isExpense && !it.date.before(start) && !it.date.after(end) }
            .sumOf { it.amount }
    }

    // Get total income for a date range
    fun getTotalIncome(start: Date, end: Date): Double {
        return getAllTransactions()
            .filter { !it.isExpense && !it.date.before(start) && !it.date.after(end) }
            .sumOf { it.amount }
    }

    // Get expenses by category for a date range
    fun getExpensesByCategory(start: Date, end: Date): Map<String, Double> {
        return getAllTransactions()
            .filter { it.isExpense && !it.date.before(start) && !it.date.after(end) }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
    }
}
