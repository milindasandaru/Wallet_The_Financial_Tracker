package com.example.wallet_thepersonalfinancialtracker.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.model.Category
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CategoryManager(private val context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("category_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private var categories: MutableList<Category> = loadCategories()

    init {
        if (categories.isEmpty()) {
            addDefaultCategories()
        }
    }

    private fun loadCategories(): MutableList<Category> {
        val json = sharedPreferences.getString("categories", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<Category>>() {}.type
            gson.fromJson(json, type)
        } else {
            mutableListOf()
        }
    }

    private fun saveCategories() {
        val json = gson.toJson(categories)
        sharedPreferences.edit().putString("categories", json).apply()
    }

    private fun addDefaultCategories() {
        // Income categories
        addCategory(Category(name = "Salary", type = Category.Type.INCOME, iconRes = R.drawable.salaryicon))
        addCategory(Category(name = "Bonus", type = Category.Type.INCOME, iconRes = R.drawable.bonusicon))
        addCategory(Category(name = "Investment", type = Category.Type.INCOME, iconRes = R.drawable.investmenticon))
        addCategory(Category(name = "Deposit", type = Category.Type.INCOME, iconRes = R.drawable.depositeicon))
        addCategory(Category(name = "Savings", type = Category.Type.INCOME, iconRes = R.drawable.savingicon))
        addCategory(Category(name = "Gift", type = Category.Type.INCOME, iconRes = R.drawable.gifticonn))
        addCategory(Category(name = "Refund", type = Category.Type.INCOME, iconRes = R.drawable.refundicon))
        addCategory(Category(name = "Other", type = Category.Type.INCOME, iconRes = R.drawable.othericonnnn))

        // Expense categories
        addCategory(Category(name = "Bill", type = Category.Type.EXPENSE, iconRes = R.drawable.billiconnnn))
        addCategory(Category(name = "Car", type = Category.Type.EXPENSE, iconRes = R.drawable.cariconnn))
        addCategory(Category(name = "Clothes", type = Category.Type.EXPENSE, iconRes = R.drawable.lotheiconnn))
        addCategory(Category(name = "Communication", type = Category.Type.EXPENSE, iconRes = R.drawable.communicationiconnn))
        addCategory(Category(name = "Eating Out", type = Category.Type.EXPENSE, iconRes = R.drawable.eatingouticonn))
        addCategory(Category(name = "Entertainment", type = Category.Type.EXPENSE, iconRes = R.drawable.entertaimenticonnn))
        addCategory(Category(name = "Food", type = Category.Type.EXPENSE, iconRes = R.drawable.foodiconnn))
        addCategory(Category(name = "Gift", type = Category.Type.EXPENSE, iconRes = R.drawable.gifticonn))
        addCategory(Category(name = "Health", type = Category.Type.EXPENSE, iconRes = R.drawable.healthiconnn))
        addCategory(Category(name = "House", type = Category.Type.EXPENSE, iconRes = R.drawable.houseiconn))
        addCategory(Category(name = "Pets", type = Category.Type.EXPENSE, iconRes = R.drawable.peticonnn))
        addCategory(Category(name = "Sports", type = Category.Type.EXPENSE, iconRes = R.drawable.sporticonn))
        addCategory(Category(name = "Taxi", type = Category.Type.EXPENSE, iconRes = R.drawable.taxiiconnm))
        addCategory(Category(name = "Toiletry", type = Category.Type.EXPENSE, iconRes = R.drawable.toiletryiconn))
        addCategory(Category(name = "Transport", type = Category.Type.EXPENSE, iconRes = R.drawable.ransporticonn))
    }

    fun getAllCategories(): List<Category> = categories

    fun getIncomeCategories(): List<Category> = categories.filter { it.type == Category.Type.INCOME }

    fun getExpenseCategories(): List<Category> = categories.filter { it.type == Category.Type.EXPENSE }

    fun addCategory(category: Category) {
        categories.add(category)
        saveCategories()
    }

    fun deleteCategory(category: Category) {
        categories.remove(category)  // Use remove instead of removeIf
        saveCategories()
    }
}
