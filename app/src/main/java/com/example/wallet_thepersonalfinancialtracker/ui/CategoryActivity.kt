package com.example.wallet_thepersonalfinancialtracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.adapter.CategoryAdapter
import com.example.wallet_thepersonalfinancialtracker.model.Category
import com.example.wallet_thepersonalfinancialtracker.utils.CategoryManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoryActivity : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var fabAddCategory: FloatingActionButton
    private lateinit var navHome: LinearLayout
    private lateinit var navTransfer: LinearLayout
    private lateinit var navCategory: LinearLayout
    private lateinit var navSettings: LinearLayout

    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var categoryManager: CategoryManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        categoryManager = CategoryManager(this)
        rvCategories = findViewById(R.id.rvCategories)
        fabAddCategory = findViewById(R.id.fabAdd)
        navHome = findViewById(R.id.navHome)
        navTransfer = findViewById(R.id.navTransfer)
        navCategory = findViewById(R.id.navCategory)
        navSettings = findViewById(R.id.navSettings)

        rvCategories.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(
            this,  // Add this context parameter
            categoryManager.getAllCategories(),
            onDelete = { category ->
                categoryManager.deleteCategory(category)
                refreshList()
            }
        )
        rvCategories.adapter = categoryAdapter

        fabAddCategory.setOnClickListener {
            AddCategoryDialog(this) { newCategory ->
                categoryManager.addCategory(newCategory)
                refreshList()
            }.show()
        }

        navHome.setOnClickListener { startActivity(Intent(this, HomeActivity::class.java)) }
        navTransfer.setOnClickListener { startActivity(Intent(this, TransactionHistoryActivity::class.java)) }
        navCategory.setOnClickListener { /* Already here */ }
        navSettings.setOnClickListener { startActivity(Intent(this, SettingsActivity::class.java)) }
    }

    private fun refreshList() {
        categoryAdapter.updateData(categoryManager.getAllCategories())
    }
}
