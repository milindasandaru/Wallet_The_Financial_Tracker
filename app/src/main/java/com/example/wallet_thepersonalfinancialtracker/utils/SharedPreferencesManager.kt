package com.example.wallet_thepersonalfinancialtracker.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.wallet_thepersonalfinancialtracker.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.wallet_thepersonalfinancialtracker.model.Notification
import java.util.Date

class SharedPreferencesManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "wallet_preferences"
        private const val TRANSACTIONS_KEY = "transactions"
        private const val ID_COUNTER_KEY = "id_counter"
        private const val BUDGET_KEY = "monthly_budget"
        private const val CURRENCY_KEY = "currency_type"
        private const val NOTIFICATIONS_KEY = "notifications"
        private const val NOTIFICATIONS_ENABLED_KEY = "notifications_enabled"
        private const val THEME_KEY = "app_theme"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    // Add these methods for notifications
    fun saveNotifications(notifications: List<Notification>) {
        try {
            val json = gson.toJson(notifications)
            sharedPreferences.edit().putString(NOTIFICATIONS_KEY, json).apply()
        } catch (e: Exception) {
            Log.e("SharedPreferencesManager", "Error saving notifications: ${e.message}", e)
        }
    }

    fun getNotifications(): List<Notification> {
        val json = sharedPreferences.getString(NOTIFICATIONS_KEY, null)
        return if (json != null) {
            try {
                val type = object : TypeToken<List<Notification>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                Log.e("SharedPreferencesManager", "Error loading notifications: ${e.message}", e)
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    // Budget methods
    fun setMonthlyBudget(budget: Double) {
        sharedPreferences.edit().putFloat(BUDGET_KEY, budget.toFloat()).apply()
    }

    fun getMonthlyBudget(): Double {
        return sharedPreferences.getFloat(BUDGET_KEY, 0f).toDouble()
    }

    // Currency methods
    fun setCurrencyType(currency: String) {
        sharedPreferences.edit().putString(CURRENCY_KEY, currency).apply()
    }

    fun getCurrencyType(): String {
        return sharedPreferences.getString(CURRENCY_KEY, "$") ?: "$"
    }

    // Transaction storage methods
    fun saveTransactions(transactions: List<Transaction>) {
        try {
            val json = gson.toJson(transactions)
            sharedPreferences.edit().putString(TRANSACTIONS_KEY, json).apply()
        } catch (e: Exception) {
            Log.e("SharedPreferencesManager", "Error saving transactions: ${e.message}", e)
        }
    }

    fun getTransactions(): List<Transaction> {
        val json = sharedPreferences.getString(TRANSACTIONS_KEY, null)
        return if (json != null) {
            try {
                val type = object : TypeToken<List<Transaction>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                Log.e("SharedPreferencesManager", "Error loading transactions: ${e.message}", e)
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    // ID counter for transactions
    fun saveIdCounter(counter: Long) {
        sharedPreferences.edit().putLong(ID_COUNTER_KEY, counter).apply()
    }

    fun getIdCounter(): Long {
        return sharedPreferences.getLong(ID_COUNTER_KEY, 1)
    }

    fun getNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean(NOTIFICATIONS_ENABLED_KEY, true)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(NOTIFICATIONS_ENABLED_KEY, enabled).apply()
    }

    fun getTheme(): String {
        return sharedPreferences.getString(THEME_KEY, "Light") ?: "Light"
    }

    fun setTheme(theme: String) {
        sharedPreferences.edit().putString(THEME_KEY, theme).apply()
    }

}
