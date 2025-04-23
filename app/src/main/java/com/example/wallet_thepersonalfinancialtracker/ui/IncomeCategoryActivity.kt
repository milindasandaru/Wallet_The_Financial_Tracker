package com.example.wallet_thepersonalfinancialtracker.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.utils.TransactionManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class IncomeCategoryActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvDate: TextView
    private lateinit var tvAmount: TextView
    private lateinit var tvNote: TextView
    private lateinit var btnClear: ImageButton
    private lateinit var navHome: LinearLayout
    private lateinit var navTransfer: LinearLayout
    private lateinit var navCategory: LinearLayout
    private lateinit var navSettings: LinearLayout
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var btnDelete: Button

    // Category cards
    private lateinit var categorySalary: CardView
    private lateinit var categoryBonus: CardView
    private lateinit var categoryInvestment: CardView
    private lateinit var categoryDeposit: CardView
    private lateinit var categorySavings: CardView
    private lateinit var categoryGift: CardView
    private lateinit var categoryRefund: CardView
    private lateinit var categoryOtherIncome: CardView
    private lateinit var categoryAdd: CardView

    // Transaction manager
    private lateinit var transactionManager: TransactionManager

    private var amount = 0.0
    private var note = ""
    private var date = ""
    private var selectedCategory = ""
    private var transactionId: Long = -1 // -1 means new transaction, otherwise updating existing
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income_category)

        try {
            // Initialize TransactionManager
            transactionManager = TransactionManager(applicationContext)

            // Initialize views
            initViews()

            // Get data from intent
            getDataFromIntent()

            // Set click listeners
            setClickListeners()

            // Set category click listeners
            setCategoryClickListeners()

            // Configure delete button visibility
            setupDeleteButton()
        } catch (e: Exception) {
            Log.e("IncomeCategoryActivity", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun initViews() {
        try {
            btnBack = findViewById(R.id.btnBack)
            tvDate = findViewById(R.id.tvDate)
            tvAmount = findViewById(R.id.tvAmount)
            tvNote = findViewById(R.id.tvNote)
            btnClear = findViewById(R.id.btnClear)
            navHome = findViewById(R.id.navHome)
            navTransfer = findViewById(R.id.navTransfer)
            navCategory = findViewById(R.id.navCategory)
            navSettings = findViewById(R.id.navSettings)
            fabAdd = findViewById(R.id.fabAdd)
            btnDelete = findViewById(R.id.btnDelete)

            // Initialize category cards
            categorySalary = findViewById(R.id.categorySalary)
            categoryBonus = findViewById(R.id.categoryBonus)
            categoryInvestment = findViewById(R.id.categoryInvestment)
            categoryDeposit = findViewById(R.id.categoryDeposit)
            categorySavings = findViewById(R.id.categorySavings)
            categoryGift = findViewById(R.id.categoryGift)
            categoryRefund = findViewById(R.id.categoryRefund)
            categoryOtherIncome = findViewById(R.id.categoryOtherIncome)
            categoryAdd = findViewById(R.id.categoryAdd)
        } catch (e: Exception) {
            Log.e("IncomeCategoryActivity", "Error initializing views: ${e.message}", e)
            throw e
        }
    }

    private fun setupDeleteButton() {
        if (isEditing) {
            btnDelete.visibility = View.VISIBLE
        } else {
            btnDelete.visibility = View.GONE
        }
    }

    private fun getDataFromIntent() {
        try {
            // Get data passed from IncomeActivity
            amount = intent.getDoubleExtra("amount", 0.0)
            note = intent.getStringExtra("note") ?: ""
            date = intent.getStringExtra("date") ?: getCurrentDate()
            transactionId = intent.getLongExtra("TRANSACTION_ID", -1)

            isEditing = transactionId != -1L

            Log.d("IncomeCategoryActivity", "Received amount: $amount, note: $note, date: $date, transactionId: $transactionId")

            // If updating, get the current category
            if (isEditing) {
                val transaction = transactionManager.getTransactionById(transactionId)
                if (transaction != null) {
                    selectedCategory = transaction.category
                }
            }

            // Display the data
            tvAmount.text = String.format(Locale.getDefault(), "%.2f", amount)
            if (note.isNotEmpty()) {
                tvNote.text = note
            } else {
                tvNote.text = getString(R.string.notedot)
            }

            // Format and display the date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val displayFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())

            try {
                val parsedDate = dateFormat.parse(date)
                if (parsedDate != null) {
                    tvDate.text = displayFormat.format(parsedDate)
                } else {
                    tvDate.text = displayFormat.format(Calendar.getInstance().time)
                }
            } catch (e: Exception) {
                tvDate.text = displayFormat.format(Calendar.getInstance().time)
            }
        } catch (e: Exception) {
            Log.e("IncomeCategoryActivity", "Error getting data from intent: ${e.message}", e)
            Toast.makeText(this, "Error loading data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Calendar.getInstance().time)
    }

    private fun setClickListeners() {
        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Clear button
        btnClear.setOnClickListener {
            // Go back to IncomeActivity to re-enter amount
            finish()
        }

        // Navigation buttons
        navHome.setOnClickListener {
            navigateToHome()
        }

        navTransfer.setOnClickListener {
            val intent = Intent(this, TransactionHistoryActivity::class.java)
            startActivity(intent)
        }

        navCategory.setOnClickListener {
            showToastAtTop("Category feature coming soon")
        }

        navSettings.setOnClickListener {
            showToastAtTop("Settings feature coming soon")
        }

        // Add Category button
        categoryAdd.setOnClickListener {
            showAddCategoryDialog()
        }

        // Delete button
        btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        // FAB button
        fabAdd.setOnClickListener {
            // Navigate to IncomeActivity for new income
            val intent = Intent(this, IncomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setCategoryClickListeners() {
        try {
            // Set click listeners for all category cards
            val categoryPairs = arrayOf(
                Pair(categorySalary, "Salary"),
                Pair(categoryBonus, "Bonus"),
                Pair(categoryInvestment, "Investment"),
                Pair(categoryDeposit, "Deposit"),
                Pair(categorySavings, "Savings"),
                Pair(categoryGift, "Gift"),
                Pair(categoryRefund, "Refund"),
                Pair(categoryOtherIncome, "Other Income")
            )

            for ((categoryCard, categoryName) in categoryPairs) {
                categoryCard.setOnClickListener {
                    selectedCategory = categoryName
                    saveTransaction(categoryName)
                }
            }
        } catch (e: Exception) {
            Log.e("IncomeCategoryActivity", "Error setting category listeners: ${e.message}", e)
            Toast.makeText(this, "Error setting up categories: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this income transaction?")
            .setPositiveButton("Delete") { _, _ ->
                if (transactionManager.deleteTransaction(transactionId)) {
                    Toast.makeText(this, "Transaction deleted", Toast.LENGTH_SHORT).show()
                    navigateToHome()
                } else {
                    Toast.makeText(this, "Failed to delete transaction", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveTransaction(category: String) {
        try {
            if (amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return
            }

            val success: Boolean
            val message: String

            // Parse date
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = try {
                dateFormat.parse(date) ?: Date()
            } catch (e: Exception) {
                Date()
            }

            // Income transactions are not expenses
            val isExpense = false

            // Check if we're updating or adding new
            if (isEditing) {
                // Update existing transaction
                success = transactionManager.updateTransaction(
                    transactionId, amount, note, category, parsedDate, isExpense
                )
                message = if (success) {
                    "Income transaction updated"
                } else {
                    "Failed to update income transaction"
                }
            } else {
                // Add new transaction
                transactionId = transactionManager.addTransaction(
                    amount, note, category, parsedDate, isExpense
                )
                success = transactionId > 0
                message = if (success) {
                    "Income transaction saved"
                } else {
                    "Failed to save income transaction"
                }
            }

            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

            if (success) {
                // Navigate to home
                navigateToHome()
            }
        } catch (e: Exception) {
            Log.e("IncomeCategoryActivity", "Error saving transaction: ${e.message}", e)
            Toast.makeText(this, "Error saving transaction: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddCategoryDialog() {
        // This would be implemented in a future version
        showToastAtTop("Add category feature coming soon")
    }

    private fun navigateToHome() {
        try {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } catch (e: Exception) {
            Log.e("IncomeCategoryActivity", "Error navigating to home: ${e.message}", e)
            Toast.makeText(this, "Error navigating to home: ${e.message}", Toast.LENGTH_SHORT).show()
            finish() // At least try to close this activity
        }
    }

    private fun showToastAtTop(message: String) {
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 100)
        toast.show()
    }
}
