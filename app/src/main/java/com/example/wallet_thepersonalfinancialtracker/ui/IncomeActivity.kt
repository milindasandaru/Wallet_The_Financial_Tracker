package com.example.wallet_thepersonalfinancialtracker.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.utils.TransactionManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class IncomeActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvDate: TextView
    private lateinit var tvAmount: TextView
    private lateinit var etNote: EditText
    private lateinit var btnBackspace: ImageButton
    private lateinit var btnChooseCategory: Button
    private lateinit var navHome: LinearLayout
    private lateinit var navTransfer: LinearLayout

    // Calculator buttons
    private lateinit var btn0: TextView
    private lateinit var btn1: TextView
    private lateinit var btn2: TextView
    private lateinit var btn3: TextView
    private lateinit var btn4: TextView
    private lateinit var btn5: TextView
    private lateinit var btn6: TextView
    private lateinit var btn7: TextView
    private lateinit var btn8: TextView
    private lateinit var btn9: TextView
    private lateinit var btnDot: TextView
    private lateinit var btnPlus: TextView
    private lateinit var btnMinus: TextView
    private lateinit var btnMultiply: TextView
    private lateinit var btnDivide: TextView
    private lateinit var btnEquals: TextView

    // CardViews for keypad buttons
    private lateinit var cardBtn0: CardView
    private lateinit var cardBtn1: CardView
    private lateinit var cardBtn2: CardView
    private lateinit var cardBtn3: CardView
    private lateinit var cardBtn4: CardView
    private lateinit var cardBtn5: CardView
    private lateinit var cardBtn6: CardView
    private lateinit var cardBtn7: CardView
    private lateinit var cardBtn8: CardView
    private lateinit var cardBtn9: CardView
    private lateinit var cardBtnDot: CardView
    private lateinit var cardBtnPlus: CardView
    private lateinit var cardBtnMinus: CardView
    private lateinit var cardBtnMultiply: CardView
    private lateinit var cardBtnDivide: CardView
    private lateinit var cardBtnEquals: CardView

    private var selectedDate = ""
    private var currentAmount = "0"
    private var hasDecimalPoint = false
    private var isOperatorAdded = false
    private var lastOperation = ""
    private var firstOperand = 0.0

    // Transaction editing variables
    private var transactionId: Long = -1
    private var isEditing = false

    // Initialize TransactionManager
    private lateinit var transactionManager: TransactionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_income)

        transactionManager = TransactionManager(applicationContext)

        // Initialize views
        initViews()

        // Check if we're editing an existing transaction
        transactionId = intent.getLongExtra("TRANSACTION_ID", -1)
        if (transactionId != -1L) {
            isEditing = true
            loadTransactionData()
        } else {
            // Set default date to today
            setDefaultDate()
        }

        // Set click listeners
        setClickListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvDate = findViewById(R.id.tvDate)
        tvAmount = findViewById(R.id.tvAmount)
        etNote = findViewById(R.id.etNote)
        btnBackspace = findViewById(R.id.btnBackspace)
        btnChooseCategory = findViewById(R.id.btnChooseCategory)
        navHome = findViewById(R.id.navHome)
        navTransfer = findViewById(R.id.navTransfer)

        // Initialize calculator buttons
        btn0 = findViewById(R.id.btn0)
        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)
        btn5 = findViewById(R.id.btn5)
        btn6 = findViewById(R.id.btn6)
        btn7 = findViewById(R.id.btn7)
        btn8 = findViewById(R.id.btn8)
        btn9 = findViewById(R.id.btn9)
        btnDot = findViewById(R.id.btnDot)
        btnPlus = findViewById(R.id.btnPlus)
        btnMinus = findViewById(R.id.btnMinus)
        btnMultiply = findViewById(R.id.btnMultiply)
        btnDivide = findViewById(R.id.btnDivide)
        btnEquals = findViewById(R.id.btnEquals)

        // Initialize CardViews for keypad buttons
        cardBtn0 = btn0.parent as CardView
        cardBtn1 = btn1.parent as CardView
        cardBtn2 = btn2.parent as CardView
        cardBtn3 = btn3.parent as CardView
        cardBtn4 = btn4.parent as CardView
        cardBtn5 = btn5.parent as CardView
        cardBtn6 = btn6.parent as CardView
        cardBtn7 = btn7.parent as CardView
        cardBtn8 = btn8.parent as CardView
        cardBtn9 = btn9.parent as CardView
        cardBtnDot = btnDot.parent as CardView
        cardBtnPlus = btnPlus.parent as CardView
        cardBtnMinus = btnMinus.parent as CardView
        cardBtnMultiply = btnMultiply.parent as CardView
        cardBtnDivide = btnDivide.parent as CardView
        cardBtnEquals = btnEquals.parent as CardView
    }

    private fun loadTransactionData() {
        try {
            val transaction = transactionManager.getTransactionById(transactionId)
            if (transaction != null && !transaction.isExpense) {
                // Set amount
                currentAmount = transaction.amount.toString()
                tvAmount.text = String.format(Locale.getDefault(), "%.2f", transaction.amount)

                // Set note
                etNote.setText(transaction.note)

                // Set date
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = dateFormat.format(transaction.date)

                // Format for display
                val displayFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
                tvDate.text = displayFormat.format(transaction.date)

                Log.d("IncomeActivity", "Loaded transaction: $transaction")
            } else {
                Log.e("IncomeActivity", "Transaction not found or is not income: $transactionId")
                Toast.makeText(this, "Income transaction not found", Toast.LENGTH_SHORT).show()
                isEditing = false
                setDefaultDate()
            }
        } catch (e: Exception) {
            Log.e("IncomeActivity", "Error loading transaction: ${e.message}", e)
            Toast.makeText(this, "Error loading transaction: ${e.message}", Toast.LENGTH_SHORT).show()
            isEditing = false
            setDefaultDate()
        }
    }

    private fun setDefaultDate() {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = dateFormat.format(calendar.time)

        // Format for display
        val displayFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        tvDate.text = displayFormat.format(calendar.time)
    }

    private fun setClickListeners() {
        // Back button
        btnBack.setOnClickListener {
            finish()
        }

        // Date selection
        tvDate.setOnClickListener {
            showDatePicker()
        }

        // Backspace button
        btnBackspace.setOnClickListener {
            if (currentAmount.length > 1) {
                // If removing a decimal point, update flag
                if (currentAmount[currentAmount.length - 1] == '.') {
                    hasDecimalPoint = false
                }
                currentAmount = currentAmount.substring(0, currentAmount.length - 1)
            } else {
                currentAmount = "0"
            }
            updateAmountDisplay()
        }

        // Choose Category button
        btnChooseCategory.setOnClickListener {
            if (validateInput()) {
                navigateToCategoryActivity()
            }
        }

        // Home navigation
        navHome.setOnClickListener {
            finish()
        }

        // Transaction history navigation
        navTransfer.setOnClickListener {
            val intent = Intent(this, TransactionHistoryActivity::class.java)
            startActivity(intent)
        }

        // Set up number buttons with hover effect
        setupNumberButton(cardBtn0, btn0, "0")
        setupNumberButton(cardBtn1, btn1, "1")
        setupNumberButton(cardBtn2, btn2, "2")
        setupNumberButton(cardBtn3, btn3, "3")
        setupNumberButton(cardBtn4, btn4, "4")
        setupNumberButton(cardBtn5, btn5, "5")
        setupNumberButton(cardBtn6, btn6, "6")
        setupNumberButton(cardBtn7, btn7, "7")
        setupNumberButton(cardBtn8, btn8, "8")
        setupNumberButton(cardBtn9, btn9, "9")

        // Set up decimal point button with hover effect
        setupNumberButton(cardBtnDot, btnDot, ".") {
            if (!hasDecimalPoint) {
                hasDecimalPoint = true
                appendToAmount(".")
            }
        }

        // Set up operator buttons with hover effect
        setupOperatorButton(cardBtnPlus, btnPlus, "+")
        setupOperatorButton(cardBtnMinus, btnMinus, "-")
        setupOperatorButton(cardBtnMultiply, btnMultiply, "×")
        setupOperatorButton(cardBtnDivide, btnDivide, "÷")

        // Set up equals button with hover effect
        setupEqualsButton(cardBtnEquals, btnEquals)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupNumberButton(cardView: CardView, textView: TextView, value: String, customAction: (() -> Unit)? = null) {
        cardView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Change background color to green when pressed
                    cardView.setCardBackgroundColor(Color.parseColor("#A5D86E"))
                    textView.setTextColor(Color.WHITE)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Restore original color when released
                    cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
                    textView.setTextColor(Color.BLACK)

                    // Execute custom action if provided, otherwise default behavior
                    if (customAction != null) {
                        customAction.invoke()
                    } else {
                        appendToAmount(value)
                    }
                }
            }
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupOperatorButton(cardView: CardView, textView: TextView, operator: String) {
        cardView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Change background color to green when pressed
                    cardView.setCardBackgroundColor(Color.parseColor("#A5D86E"))
                    textView.setTextColor(Color.WHITE)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Restore original color when released
                    cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
                    textView.setTextColor(Color.BLACK)

                    handleOperator(operator)
                }
            }
            true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupEqualsButton(cardView: CardView, textView: TextView) {
        cardView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Change background color to green when pressed
                    cardView.setCardBackgroundColor(Color.parseColor("#A5D86E"))
                    textView.setTextColor(Color.WHITE)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Restore original color when released
                    cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
                    textView.setTextColor(Color.BLACK)

                    calculateResult()
                }
            }
            true
        }
    }

    private fun handleOperator(operator: String) {
        try {
            if (isOperatorAdded) {
                // Calculate previous operation first
                calculateResult()
            }

            firstOperand = currentAmount.toDouble()
            lastOperation = operator
            isOperatorAdded = true
            currentAmount = "0"
            hasDecimalPoint = false

            // Show the current operation in the display
            tvAmount.text = String.format("%.2f %s", firstOperand, operator)
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid operation", Toast.LENGTH_SHORT).show()
        }
    }


    private fun appendToAmount(value: String) {
        if (currentAmount == "0" && value != ".") {
            currentAmount = value
        } else {
            currentAmount += value
        }
        updateAmountDisplay()
    }

    /*private fun handleOperator(operator: String) {
        try {
            val currentValue = currentAmount.toDouble()

            if (isOperatorAdded) {
                // Calculate previous operation first
                calculateResult()
            }

            firstOperand = currentAmount.toDouble()
            lastOperation = operator
            isOperatorAdded = true
            currentAmount = "0"
            hasDecimalPoint = false

            // Show the current operation in the display
            tvAmount.text = String.format("%.2f %s", firstOperand, operator)
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid operation", Toast.LENGTH_SHORT).show()
        }
    }*/

    private fun calculateResult() {
        if (isOperatorAdded) {
            try {
                val secondOperand = currentAmount.toDouble()
                var result = 0.0

                when (lastOperation) {
                    "+" -> result = firstOperand + secondOperand
                    "-" -> result = firstOperand - secondOperand
                    "×" -> result = firstOperand * secondOperand
                    "÷" -> {
                        if (secondOperand == 0.0) {
                            Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show()
                            return
                        }
                        result = firstOperand / secondOperand
                    }
                }

                currentAmount = result.toString()
                isOperatorAdded = false

                // Check if result has decimal part
                hasDecimalPoint = currentAmount.contains(".")

                updateAmountDisplay()
            } catch (e: Exception) {
                Toast.makeText(this, "Error in calculation", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateAmountDisplay() {
        try {
            val amount = currentAmount.toDouble()
            tvAmount.text = String.format(Locale.getDefault(), "%.2f", amount)
        } catch (e: Exception) {
            tvAmount.text = currentAmount
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        // If we have a selected date, parse it
        if (selectedDate.isNotEmpty()) {
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = dateFormat.parse(selectedDate)
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)

                // Format date for storage
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = dateFormat.format(calendar.time)

                // Format date for display
                val displayFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
                tvDate.text = displayFormat.format(calendar.time)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun validateInput(): Boolean {
        try {
            val amount = tvAmount.text.toString().replace(",", "").toDouble()
            if (amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return false
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun navigateToCategoryActivity() {
        try {
            // Get the amount from the display
            val amountStr = tvAmount.text.toString().replace(",", "")
            val amount = amountStr.toDouble()

            // Get the note
            val note = etNote.text.toString().trim()

            // Create intent to IncomeCategoryActivity
            val intent = Intent(this, IncomeCategoryActivity::class.java)

            // Pass data as extras
            intent.putExtra("amount", amount)
            intent.putExtra("note", note)
            intent.putExtra("date", selectedDate)

            // Pass transaction ID if editing
            if (isEditing) {
                intent.putExtra("TRANSACTION_ID", transactionId)
            }

            // Start the activity
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
