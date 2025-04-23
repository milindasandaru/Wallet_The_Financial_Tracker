package com.example.wallet_thepersonalfinancialtracker.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.adapter.TransactionAdapter
import com.example.wallet_thepersonalfinancialtracker.model.TransactionGroup
import com.example.wallet_thepersonalfinancialtracker.utils.TransactionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionHistoryActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvBalanceTitle: TextView
    private lateinit var tvBalance: TextView
    private lateinit var ivViewType: ImageView
    private lateinit var ivViewTypeCategory: ImageView
    private lateinit var viewToggleLayout: LinearLayout
    private lateinit var rvTransactions: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var transactionManager: TransactionManager

    // Navigation elements
    private lateinit var navHome: LinearLayout
    private lateinit var navTransfer: LinearLayout
    private lateinit var navCategory: LinearLayout
    private lateinit var navSettings: LinearLayout
    private lateinit var fabAdd: com.google.android.material.floatingactionbutton.FloatingActionButton

    private var isGroupByCategory = false
    private var transactionGroups: List<TransactionGroup> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_history)

        try {
            // Initialize TransactionManager with context
            transactionManager = TransactionManager(applicationContext)

            initViews()
            setupRecyclerView()
            loadTransactions()
            setClickListeners()
        } catch (e: Exception) {
            Log.e("TransactionHistoryActivity", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing app", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        try {
            btnBack = findViewById(R.id.btnBack)
            tvBalanceTitle = findViewById(R.id.tvBalanceTitle)
            tvBalance = findViewById(R.id.tvBalance)
            ivViewType = findViewById(R.id.ivViewType)
            ivViewTypeCategory = findViewById(R.id.ivViewTypeCategory)
            viewToggleLayout = findViewById(R.id.viewToggleLayout)
            rvTransactions = findViewById(R.id.rvTransactions)

            // Initialize navigation elements
            navHome = findViewById(R.id.navHome)
            navTransfer = findViewById(R.id.navTransfer)
            navCategory = findViewById(R.id.navCategory)
            navSettings = findViewById(R.id.navSettings)
            fabAdd = findViewById(R.id.fabAdd)

            // Set current month in balance title
            val calendar = Calendar.getInstance()
            val monthFormat = SimpleDateFormat("MMMM", Locale.getDefault())
            val currentMonth = monthFormat.format(calendar.time)
            tvBalanceTitle.text = "Total Balance ($currentMonth)"

            // Set current balance using instance method
            val balance = transactionManager.getCurrentMonthTotal()
            tvBalance.text = "$ ${String.format("%.2f", balance)}"
        } catch (e: Exception) {
            Log.e("TransactionHistoryActivity", "Error initializing views: ${e.message}", e)
        }
    }

    private fun setupRecyclerView() {
        try {
            rvTransactions.layoutManager = LinearLayoutManager(this)
            transactionAdapter = TransactionAdapter(this, emptyList(), isGroupByCategory)
            rvTransactions.adapter = transactionAdapter
        } catch (e: Exception) {
            Log.e("TransactionHistoryActivity", "Error setting up RecyclerView: ${e.message}", e)
        }
    }

    private fun loadTransactions() {
        try {
            transactionGroups = if (isGroupByCategory) {
                transactionManager.groupTransactionsByCategory()
            } else {
                transactionManager.groupTransactionsByDate()
            }
            transactionAdapter.updateData(transactionGroups, isGroupByCategory)
        } catch (e: Exception) {
            Log.e("TransactionHistoryActivity", "Error loading transactions: ${e.message}", e)
            Toast.makeText(this, "Error loading transactions", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        viewToggleLayout.setOnClickListener {
            toggleViewType()
        }

        // Set navigation click listeners
        navHome.setOnClickListener {
            navigateToHome()
        }

        navTransfer.setOnClickListener {
            // Already on transaction page, do nothing
        }

        navCategory.setOnClickListener {
            // Navigate to category page
            Toast.makeText(this, "Category feature coming soon", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        navSettings.setOnClickListener {
            navigateToSettings()
        }

        fabAdd.setOnClickListener {
            showAddTransactionOptions()
        }
    }

    private fun navigateToHome() {
        try {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("TransactionHistoryActivity", "Error navigating to home: ${e.message}", e)
            Toast.makeText(this, "Error navigating to home", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToSettings() {
        try {
            // Replace SettingsActivity with your actual settings activity class
            Toast.makeText(this, "Settings feature coming soon", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("TransactionHistoryActivity", "Error navigating to settings: ${e.message}", e)
        }
    }

    private fun showAddTransactionOptions() {
        try {
            // Show a dialog to choose between adding expense or income
            val options = arrayOf("Add Expense", "Add Income")
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Add Transaction")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> startActivity(Intent(this, ExpenseActivity::class.java))
                        1 -> startActivity(Intent(this, IncomeActivity::class.java))
                    }
                }
                .show()
        } catch (e: Exception) {
            Log.e("TransactionHistoryActivity", "Error showing options dialog: ${e.message}", e)
            Toast.makeText(this, "Error showing options", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleViewType() {
        try {
            isGroupByCategory = !isGroupByCategory

            // Update view type icon
            if (isGroupByCategory) {
                ivViewType.visibility = View.GONE
                ivViewTypeCategory.visibility = View.VISIBLE
            } else {
                ivViewType.visibility = View.VISIBLE
                ivViewTypeCategory.visibility = View.GONE
            }

            // Reload transactions with new grouping
            loadTransactions()
        } catch (e: Exception) {
            Log.e("TransactionHistoryActivity", "Error toggling view: ${e.message}", e)
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            // Reload data when returning to this activity
            loadTransactions()

            // Update balance using instance method
            val balance = transactionManager.getCurrentMonthTotal()
            tvBalance.text = "$ ${String.format("%.2f", balance)}"
        } catch (e: Exception) {
            Log.e("TransactionHistoryActivity", "Error in onResume: ${e.message}", e)
        }
    }
}
