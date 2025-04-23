package com.example.wallet_thepersonalfinancialtracker.utils

import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.atomic.AtomicLong

data class Expense(
    val id: Long,
    val amount: Double,
    val category: String,
    val date: String,
    val note: String
)

class ExpenseManager(private val context: Context) {

    private val expenses = mutableListOf<Expense>()
    private val idGenerator = AtomicLong(1)

    fun addExpense(amount: Double, category: String, date: String, note: String): Long {
        try {
            val id = idGenerator.getAndIncrement()
            val expense = Expense(id, amount, category, date, note)
            expenses.add(expense)
            Log.d("ExpenseManager", "Added expense: $expense")
            return id
        } catch (e: Exception) {
            Log.e("ExpenseManager", "Error adding expense: ${e.message}", e)
            return -1
        }
    }

    fun updateExpense(id: Long, amount: Double, category: String, date: String, note: String): Boolean {
        try {
            val index = expenses.indexOfFirst { it.id == id }
            if (index != -1) {
                expenses[index] = Expense(id, amount, category, date, note)
                Log.d("ExpenseManager", "Updated expense with ID: $id")
                return true
            }
            return false
        } catch (e: Exception) {
            Log.e("ExpenseManager", "Error updating expense: ${e.message}", e)
            return false
        }
    }

    fun getExpenseById(id: Long): Expense? {
        return expenses.find { it.id == id }
    }

    fun getAllExpenses(): List<Expense> {
        return expenses.toList()
    }

    fun getMonthlyExpenses(): Double {
        try {
            val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)

            return expenses.filter {
                try {
                    val expenseDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it.date)
                    val calendar = Calendar.getInstance()
                    calendar.time = expenseDate!!
                    calendar.get(Calendar.MONTH) == currentMonth && calendar.get(Calendar.YEAR) == currentYear
                } catch (e: Exception) {
                    false
                }
            }.sumOf { it.amount }
        } catch (e: Exception) {
            Log.e("ExpenseManager", "Error calculating monthly expenses: ${e.message}", e)
            return 0.0
        }
    }
}
