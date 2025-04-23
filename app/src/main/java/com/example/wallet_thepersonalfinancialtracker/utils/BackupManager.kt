package com.example.wallet_thepersonalfinancialtracker.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.wallet_thepersonalfinancialtracker.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Locale

class BackupManager(private val context: Context) {

    private val transactionManager = TransactionManager(context)

    fun createBackup(fileName: String): Boolean {
        try {
            val transactions = transactionManager.getAllTransactions()
            val json = Gson().toJson(transactions)

            context.openFileOutput(fileName, Context.MODE_PRIVATE).use { stream ->
                stream.write(json.toByteArray())
            }

            return true
        } catch (e: Exception) {
            Log.e("BackupManager", "Error creating backup: ${e.message}", e)
            return false
        }
    }

    fun restoreBackup(fileName: String): Boolean {
        try {
            context.openFileInput(fileName).bufferedReader().use { reader ->
                val json = reader.readText()
                val type = object : TypeToken<List<Transaction>>() {}.type
                val transactions = Gson().fromJson<List<Transaction>>(json, type)

                // Clear existing transactions and add restored ones
                // This would require a method in TransactionManager to clear all transactions
                // For now, we'll just add the restored transactions

                for (transaction in transactions) {
                    transactionManager.addTransaction(
                        transaction.amount,
                        transaction.note,
                        transaction.category,
                        transaction.date,
                        transaction.isExpense
                    )
                }
            }

            return true
        } catch (e: Exception) {
            Log.e("BackupManager", "Error restoring backup: ${e.message}", e)
            return false
        }
    }

    fun exportToCsv(fileName: String): Boolean {
        try {
            val transactions = transactionManager.getAllTransactions()
            // Use external storage directory that's visible to users
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, fileName)

            FileWriter(file).use { writer ->
                // Write header
                writer.append("Date,Category,Note,Amount,Type\n")

                // Write data
                for (transaction in transactions) {
                    writer.append(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(transaction.date))
                        .append(",")
                        .append(transaction.category)
                        .append(",")
                        .append(transaction.note.replace(",", " ")) // Escape commas
                        .append(",")
                        .append(transaction.amount.toString())
                        .append(",")
                        .append(if (transaction.isExpense) "Expense" else "Income")
                        .append("\n")
                }
            }

            return true
        } catch (e: Exception) {
            Log.e("BackupManager", "Error exporting to CSV: ${e.message}", e)
            return false
        }
    }

    fun getBackupFiles(): List<String> {
        return context.fileList().filter { it.startsWith("wallet_backup_") && it.endsWith(".json") }
    }
}
