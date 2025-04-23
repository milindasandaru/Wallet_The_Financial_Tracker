package com.example.wallet_thepersonalfinancialtracker.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.wallet_thepersonalfinancialtracker.R

class CategoryIconsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    init {
        orientation = HORIZONTAL
        gravity = android.view.Gravity.CENTER
    }

    fun setData(categoryData: Map<String, Double>, total: Double) {
        removeAllViews()
        for ((category, amount) in categoryData) {
            val percent = if (total > 0) (amount / total * 100) else 0.0

            val item = LayoutInflater.from(context)
                .inflate(R.layout.activity_category_icons_view, this, false) as LinearLayout

            val iconView = item.findViewById<ImageView>(R.id.ivCategoryIcon)
            val percentView = item.findViewById<TextView>(R.id.tvCategoryPercent)

            iconView.setImageResource(getCategoryIconRes(category))
            percentView.text = String.format("%.1f%%", percent)
            percentView.setTextColor(getCategoryColor(category))

            addView(item)
        }
    }

    private fun getCategoryIconRes(category: String): Int {
        return when (category.lowercase()) {
            "food" -> R.drawable.foodicon
            "eating out" -> R.drawable.cuttleryicon
            "bill" -> R.drawable.billicon
            "car" -> R.drawable.caricon
            "clothes" -> R.drawable.clotheicon
            "health" -> R.drawable.healthicon
            "entertainment" -> R.drawable.entertainigicon
            "communication" -> R.drawable.communicationicon
            "house" -> R.drawable.houseicon
            "pets" -> R.drawable.peticon
            "sports" -> R.drawable.sporticon
            "taxi" -> R.drawable.taxiicon
            "toiletry" -> R.drawable.toiloteryicon
            "transport" -> R.drawable.transporticon
            else -> R.drawable.category
        }
    }

    private fun getCategoryColor(category: String): Int {
        return when (category.lowercase()) {
            "food" -> resources.getColor(R.color.food_color, null)
            "eating out" -> resources.getColor(R.color.eating_out_color, null)
            "bill" -> resources.getColor(R.color.bill_color, null)
            "car" -> resources.getColor(R.color.car_color, null)
            "clothes" -> resources.getColor(R.color.clothes_color, null)
            "health" -> resources.getColor(R.color.health_color, null)
            "entertainment" -> resources.getColor(R.color.entertainment_color, null)
            "communication" -> resources.getColor(R.color.communication_color, null)
            "house" -> resources.getColor(R.color.house_color, null)
            "pets" -> resources.getColor(R.color.pets_color, null)
            "sports" -> resources.getColor(R.color.sports_color, null)
            "taxi" -> resources.getColor(R.color.taxi_color, null)
            "toiletry" -> resources.getColor(R.color.toiletry_color, null)
            "transport" -> resources.getColor(R.color.transport_color, null)
            else -> resources.getColor(R.color.default_category_color, null)
        }
    }
}
