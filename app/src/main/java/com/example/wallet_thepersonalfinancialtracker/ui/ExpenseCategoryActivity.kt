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

class ExpenseCategoryActivity : AppCompatActivity() {

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
    private lateinit var categoryBill: CardView
    private lateinit var categoryCar: CardView
    private lateinit var categoryClothes: CardView
    private lateinit var categoryCommunication: CardView
    private lateinit var categoryEatingOut: CardView
    private lateinit var categoryEntertainment: CardView
    private lateinit var categoryFood: CardView
    private lateinit var categoryGifts: CardView
    private lateinit var categoryHealth: CardView
    private lateinit var categoryHouse: CardView
    private lateinit var categoryPets: CardView
    private lateinit var categorySports: CardView
    private lateinit var categoryTaxi: CardView
    private lateinit var categoryToiletry: CardView
    private lateinit var categoryTransport: CardView
    private lateinit var categoryAdd: CardView

    // Transaction manager
    private lateinit var transactionManager: TransactionManager

    private var amount = 0.0
    private var note = ""
    private var date = ""
    private var selectedCategory = ""
    private var isExpense = true
    private var transactionId: Long = -1 // -1 means new transaction, otherwise updating existing
    private var isEditing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_category)

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
            Log.e("ExpenseCategoryActivity", "Error in onCreate: ${e.message}", e)
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
            categoryBill = findViewById(R.id.categoryBill)
            categoryCar = findViewById(R.id.categoryCar)
            categoryClothes = findViewById(R.id.categoryClothes)
            categoryCommunication = findViewById(R.id.categoryCommunication)
            categoryEatingOut = findViewById(R.id.categoryEatingOut)
            categoryEntertainment = findViewById(R.id.categoryEntertainment)
            categoryFood = findViewById(R.id.categoryFood)
            categoryGifts = findViewById(R.id.categoryGifts)
            categoryHealth = findViewById(R.id.categoryHealth)
            categoryHouse = findViewById(R.id.categoryHouse)
            categoryPets = findViewById(R.id.categoryPets)
            categorySports = findViewById(R.id.categorySports)
            categoryTaxi = findViewById(R.id.categoryTaxi)
            categoryToiletry = findViewById(R.id.categoryToiletry)
            categoryTransport = findViewById(R.id.categoryTransport)
            categoryAdd = findViewById(R.id.categoryAdd)
        } catch (e: Exception) {
            Log.e("ExpenseCategoryActivity", "Error initializing views: ${e.message}", e)
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
            // Get data passed from ExpenseActivity
            amount = intent.getDoubleExtra("amount", 0.0)
            note = intent.getStringExtra("note") ?: ""
            date = intent.getStringExtra("date") ?: getCurrentDate()
            isExpense = intent.getBooleanExtra("isExpense", true)
            transactionId = intent.getLongExtra("TRANSACTION_ID", -1)

            isEditing = transactionId != -1L

            Log.d("ExpenseCategoryActivity", "Received data: amount=$amount, note=$note, date=$date, isExpense=$isExpense, transactionId=$transactionId")

            // If updating, get the current category
            if (isEditing) {
                val transaction = transactionManager.getTransactionById(transactionId)
                if (transaction != null) {
                    selectedCategory = transaction.category
                    isExpense = transaction.isExpense
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
                Log.e("ExpenseCategoryActivity", "Error parsing date: ${e.message}", e)
                tvDate.text = displayFormat.format(Calendar.getInstance().time)
            }
        } catch (e: Exception) {
            Log.e("ExpenseCategoryActivity", "Error getting data from intent: ${e.message}", e)
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
            // Go back to ExpenseActivity to re-enter amount
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
            // Navigate to ExpenseActivity for new expense
            val intent = Intent(this, ExpenseActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setCategoryClickListeners() {
        try {
            // Set click listeners for all category cards
            val categoryPairs = arrayOf(
                Pair(categoryBill, "Bill"),
                Pair(categoryCar, "Car"),
                Pair(categoryClothes, "Clothes"),
                Pair(categoryCommunication, "Communication"),
                Pair(categoryEatingOut, "Eating Out"),
                Pair(categoryEntertainment, "Entertainment"),
                Pair(categoryFood, "Food"),
                Pair(categoryGifts, "Gifts"),
                Pair(categoryHealth, "Health"),
                Pair(categoryHouse, "House"),
                Pair(categoryPets, "Pets"),
                Pair(categorySports, "Sports"),
                Pair(categoryTaxi, "Taxi"),
                Pair(categoryToiletry, "Toiletry"),
                Pair(categoryTransport, "Transport")
            )

            for ((categoryCard, categoryName) in categoryPairs) {
                categoryCard.setOnClickListener {
                    selectedCategory = categoryName
                    saveTransaction(categoryName)
                }
            }
        } catch (e: Exception) {
            Log.e("ExpenseCategoryActivity", "Error setting category listeners: ${e.message}", e)
            Toast.makeText(this, "Error setting up categories: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction?")
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

            // Check if we're updating or adding new
            if (isEditing) {
                // Update existing transaction
                success = transactionManager.updateTransaction(
                    transactionId, amount, note, category, parsedDate, isExpense
                )
                message = if (success) {
                    "Transaction updated"
                } else {
                    "Failed to update transaction"
                }
            } else {
                // Add new transaction
                transactionId = transactionManager.addTransaction(
                    amount, note, category, parsedDate, isExpense
                )
                success = transactionId > 0
                message = if (success) {
                    "Transaction saved"
                } else {
                    "Failed to save transaction"
                }
            }

            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()

            if (success) {
                // Check budget status and show notifications if needed
                checkBudgetStatus()

                // Navigate to home
                navigateToHome()
            }
        } catch (e: Exception) {
            Log.e("ExpenseCategoryActivity", "Error saving transaction: ${e.message}", e)
            Toast.makeText(this, "Error saving transaction: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkBudgetStatus() {
        try {
            // In a real app, you would get this from SharedPreferences
            val monthlyBudget = 1000.0 // Example budget amount
            val monthlyExpenses = transactionManager.getCurrentMonthExpensesTotal()

            // Check if budget is exceeded
            if (monthlyExpenses > monthlyBudget) {
                // Notification would go here
                Toast.makeText(this, "Budget exceeded!", Toast.LENGTH_SHORT).show()
            }
            // Check if we're approaching budget (80% or more)
            else if (monthlyExpenses >= monthlyBudget * 0.8) {
                // Warning notification would go here
                Toast.makeText(this, "Approaching budget limit!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("ExpenseCategoryActivity", "Error checking budget: ${e.message}", e)
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
            Log.e("ExpenseCategoryActivity", "Error navigating to home: ${e.message}", e)
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
