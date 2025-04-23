package com.example.wallet_thepersonalfinancialtracker.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.*
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.model.Category
import java.text.SimpleDateFormat
import java.util.*

class AddCategoryDialog(
    context: Context,
    private val onCategoryAdded: (Category) -> Unit
) : Dialog(context) {

    private lateinit var btnIncome: Button
    private lateinit var btnExpenses: Button
    private lateinit var etCategoryName: EditText
    private lateinit var ivIcon: ImageView
    private lateinit var etBudget: EditText
    private lateinit var etStartDate: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var selectedType = Category.Type.EXPENSE
    private var selectedIconRes = R.drawable.categoryicon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = this.window
        val displayMetrics = context.resources.displayMetrics
        val width = (displayMetrics.widthPixels * 0.9).toInt()

        window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        val view = LayoutInflater.from(context).inflate(R.layout.activity_add_category_dialog, null, false)
        setContentView(view)


        btnIncome = findViewById(R.id.btnIncome)
        btnExpenses = findViewById(R.id.btnExpenses)
        etCategoryName = findViewById(R.id.etCategoryName)
        ivIcon = findViewById(R.id.ivCategoryIcon)  // Changed to match XML ID
        etBudget = findViewById(R.id.etBudget)
        etStartDate = findViewById(R.id.etStartDate)
        btnSave = findViewById(R.id.btnSaveCategory)
        btnCancel = findViewById(R.id.btnCancel)

        btnIncome.setOnClickListener {
            selectedType = Category.Type.INCOME
            btnIncome.setBackgroundResource(R.drawable.bg_button_selected_green)
            btnExpenses.setBackgroundResource(R.drawable.bg_button_unselected)
        }
        btnExpenses.setOnClickListener {
            selectedType = Category.Type.EXPENSE
            btnIncome.setBackgroundResource(R.drawable.bg_button_unselected)
            btnExpenses.setBackgroundResource(R.drawable.bg_button_selected_red)
        }

        ivIcon.setOnClickListener {
            // TODO: Show icon picker dialog if you want
            // selectedIconRes = ...
            // ivIcon.setImageResource(selectedIconRes)
        }

        etStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(context, { _, y, m, d ->
                val date = Calendar.getInstance()
                date.set(y, m, d)
                etStartDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnSave.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            val budget = etBudget.text.toString().toDoubleOrNull() ?: 0.0
            val startDate = etStartDate.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(context, "Enter category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onCategoryAdded(
                Category(
                    name = name,
                    type = selectedType,
                    iconRes = selectedIconRes,
                    budget = budget,
                    startDate = startDate
                )
            )
            dismiss()
        }
        btnCancel.setOnClickListener { dismiss() }
    }
}
