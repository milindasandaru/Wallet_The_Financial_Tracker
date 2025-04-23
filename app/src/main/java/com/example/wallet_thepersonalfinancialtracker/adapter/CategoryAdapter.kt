package com.example.wallet_thepersonalfinancialtracker.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.model.Category

class CategoryAdapter(
    private val context: Context,
    private var categories: List<Category>,
    private val onDelete: (Category) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    private var groupedCategories: List<Pair<String, List<Category>>> = listOf(
        "Income Categories" to categories.filter { it.type == Category.Type.INCOME },
        "Expenses Categories" to categories.filter { it.type == Category.Type.EXPENSE }
    )

    override fun getItemViewType(position: Int): Int {
        var itemCount = 0
        for ((_, items) in groupedCategories) {
            if (position == itemCount) return TYPE_HEADER
            itemCount++
            if (position < itemCount + items.size) return TYPE_ITEM
            itemCount += items.size
        }
        return TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category_header, parent, false)
            HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
            CategoryViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        // Count headers + all items
        return groupedCategories.sumOf { it.second.size + 1 }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var itemCount = 0
        for ((header, items) in groupedCategories) {
            if (position == itemCount) {
                (holder as HeaderViewHolder).bind(header)
                return
            }
            itemCount++
            if (position < itemCount + items.size) {
                val item = items[position - itemCount]
                (holder as CategoryViewHolder).bind(item)
                return
            }
            itemCount += items.size
        }
    }

    fun updateData(newCategories: List<Category>) {
        categories = newCategories
        // Recreate grouped categories
        val newGrouped = listOf(
            "Income Categories" to newCategories.filter { it.type == Category.Type.INCOME },
            "Expenses Categories" to newCategories.filter { it.type == Category.Type.EXPENSE }
        )
        groupedCategories = newGrouped
        notifyDataSetChanged()
    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHeader: TextView = itemView.findViewById(R.id.tvHeader)

        fun bind(header: String) {
            tvHeader.text = header
        }
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCategoryIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)
        private val tvCategoryName: TextView = itemView.findViewById(R.id.tvCategoryName)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(category: Category) {
            ivCategoryIcon.setImageResource(category.iconRes)
            tvCategoryName.text = category.name

            btnDelete.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Delete Category")
                    .setMessage("Are you sure you want to delete this category?")
                    .setPositiveButton("Delete") { _, _ -> onDelete(category) }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }
}
