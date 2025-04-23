package com.example.wallet_thepersonalfinancialtracker.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.model.Transaction
import com.example.wallet_thepersonalfinancialtracker.model.TransactionGroup
import com.example.wallet_thepersonalfinancialtracker.ui.ExpenseActivity
import com.example.wallet_thepersonalfinancialtracker.ui.IncomeActivity

class TransactionAdapter(
    private val context: Context,
    private var transactionGroups: List<TransactionGroup>,
    private var isGroupByCategory: Boolean
) : RecyclerView.Adapter<TransactionAdapter.GroupViewHolder>() {

    private val expandedGroups = mutableSetOf<Int>()

    init {
        // Initially expand all groups
        for (i in transactionGroups.indices) {
            expandedGroups.add(i)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction_group, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = transactionGroups[position]
        holder.bind(group, position, isGroupByCategory)
    }

    override fun getItemCount(): Int = transactionGroups.size

    fun updateData(newGroups: List<TransactionGroup>, groupByCategory: Boolean) {
        transactionGroups = newGroups
        isGroupByCategory = groupByCategory
        expandedGroups.clear()
        for (i in transactionGroups.indices) {
            expandedGroups.add(i)
        }
        notifyDataSetChanged()
    }

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvGroupTitle: TextView = itemView.findViewById(R.id.tvGroupTitle)
        private val tvGroupTotal: TextView = itemView.findViewById(R.id.tvGroupTotal)
        private val layoutHeader: LinearLayout = itemView.findViewById(R.id.layoutHeader)
        private val layoutItems: LinearLayout = itemView.findViewById(R.id.layoutItems)
        private val ivCategoryIcon: ImageView = itemView.findViewById(R.id.ivCategoryIcon)

        fun bind(group: TransactionGroup, position: Int, isGroupByCategory: Boolean) {
            try {
                tvGroupTitle.text = group.title
                tvGroupTotal.text = group.getFormattedTotal()

                // Set category icon visibility based on view type
                if (isGroupByCategory) {
                    ivCategoryIcon.visibility = View.VISIBLE
                    // Set appropriate icon based on category name
                    setCategoryIcon(group.title)
                } else {
                    ivCategoryIcon.visibility = View.GONE
                }

                // Toggle group expansion on header click
                layoutHeader.setOnClickListener {
                    if (expandedGroups.contains(position)) {
                        expandedGroups.remove(position)
                    } else {
                        expandedGroups.add(position)
                    }
                    notifyItemChanged(position)
                }

                // Clear previous items
                layoutItems.removeAllViews()

                // Add transaction items if group is expanded
                if (expandedGroups.contains(position)) {
                    for (transaction in group.transactions) {
                        val itemView = LayoutInflater.from(context)
                            .inflate(R.layout.item_transaction, layoutItems, false)

                        val viewTransactionType = itemView.findViewById<View>(R.id.viewTransactionType)
                        val tvTransactionTitle = itemView.findViewById<TextView>(R.id.tvTransactionTitle)
                        val tvTransactionSubtitle = itemView.findViewById<TextView>(R.id.tvTransactionSubtitle)
                        val tvTransactionAmount = itemView.findViewById<TextView>(R.id.tvTransactionAmount)

                        // Set transaction type indicator color with enhanced drawables
                        viewTransactionType.setBackgroundResource(
                            if (transaction.isExpense) R.drawable.circle_red else R.drawable.circle_green
                        )

                        // Set transaction details
                        tvTransactionTitle.text = transaction.note

                        // Set subtitle based on view type (without time)
                        if (isGroupByCategory) {
                            tvTransactionSubtitle.text = transaction.getShortDate()
                        } else {
                            tvTransactionSubtitle.text = transaction.category
                        }

                        tvTransactionAmount.text = transaction.getFormattedAmount()

                        // Set click listener to open transaction details
                        itemView.setOnClickListener {
                            openTransactionForEditing(transaction)
                        }

                        layoutItems.addView(itemView)

                        // Add divider except for the last item
                        if (transaction != group.transactions.last()) {
                            val divider = View(context)
                            val params = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1
                            )
                            params.setMargins(12, 0, 12, 0)
                            divider.layoutParams = params
                            divider.setBackgroundColor(context.getColor(R.color.divider_color))
                            layoutItems.addView(divider)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("TransactionAdapter", "Error binding view holder: ${e.message}", e)
            }
        }

        private fun setCategoryIcon(categoryTitle: String) {
            try {
                val categoryName = categoryTitle.split(" ")[0].lowercase()

                when {
                    // Expense categories
                    categoryName.contains("food") -> {
                        ivCategoryIcon.setImageResource(R.drawable.foodicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.food_color))
                    }
                    categoryName.contains("eating") -> {
                        ivCategoryIcon.setImageResource(R.drawable.cuttleryicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.eating_out_color))
                    }
                    categoryName.contains("bill") -> {
                        ivCategoryIcon.setImageResource(R.drawable.billicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.bill_color))
                    }
                    categoryName.contains("car") -> {
                        ivCategoryIcon.setImageResource(R.drawable.caricon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.car_color))
                    }
                    categoryName.contains("clothes") -> {
                        ivCategoryIcon.setImageResource(R.drawable.clotheicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.clothes_color))
                    }
                    categoryName.contains("health") -> {
                        ivCategoryIcon.setImageResource(R.drawable.healthicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.health_color))
                    }
                    categoryName.contains("entertainment") -> {
                        ivCategoryIcon.setImageResource(R.drawable.entertainigicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.entertainment_color))
                    }
                    categoryName.contains("communication") -> {
                        ivCategoryIcon.setImageResource(R.drawable.communicationicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.communication_color))
                    }
                    categoryName.contains("house") -> {
                        ivCategoryIcon.setImageResource(R.drawable.houseicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.house_color))
                    }
                    categoryName.contains("pets") -> {
                        ivCategoryIcon.setImageResource(R.drawable.peticon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.pets_color))
                    }
                    categoryName.contains("sports") -> {
                        ivCategoryIcon.setImageResource(R.drawable.sporticon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.sports_color))
                    }
                    categoryName.contains("taxi") -> {
                        ivCategoryIcon.setImageResource(R.drawable.taxiicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.taxi_color))
                    }
                    categoryName.contains("toiletry") -> {
                        ivCategoryIcon.setImageResource(R.drawable.toiloteryicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.toiletry_color))
                    }
                    categoryName.contains("transport") -> {
                        ivCategoryIcon.setImageResource(R.drawable.transporticon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.transport_color))
                    }

                    // Income categories
                    categoryName.contains("salary") -> {
                        ivCategoryIcon.setImageResource(R.drawable.salaryicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.salary_color))
                    }
                    categoryName.contains("bonus") -> {
                        ivCategoryIcon.setImageResource(R.drawable.bonusicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.bonus_color))
                    }
                    categoryName.contains("investment") -> {
                        ivCategoryIcon.setImageResource(R.drawable.investmenticon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.investment_color))
                    }
                    categoryName.contains("deposit") -> {
                        ivCategoryIcon.setImageResource(R.drawable.depositeicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.deposit_color))
                    }
                    categoryName.contains("savings") -> {
                        ivCategoryIcon.setImageResource(R.drawable.savingicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.savings_color))
                    }
                    categoryName.contains("gift") -> {
                        ivCategoryIcon.setImageResource(R.drawable.gifticon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.gift_color))
                    }
                    categoryName.contains("refund") -> {
                        ivCategoryIcon.setImageResource(R.drawable.refundicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.refund_color))
                    }
                    categoryName.contains("other") -> {
                        ivCategoryIcon.setImageResource(R.drawable.otherincomeicon)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.other_income_color))
                    }

                    // Default
                    else -> {
                        ivCategoryIcon.setImageResource(R.drawable.category)
                        ivCategoryIcon.setColorFilter(context.getColor(R.color.default_category_color))
                    }
                }
            } catch (e: Exception) {
                Log.e("TransactionAdapter", "Error setting category icon: ${e.message}", e)
                ivCategoryIcon.setImageResource(R.drawable.category)
                ivCategoryIcon.setColorFilter(context.getColor(R.color.default_category_color))
            }
        }

        private fun openTransactionForEditing(transaction: Transaction) {
            try {
                val intent = if (transaction.isExpense) {
                    Intent(context, ExpenseActivity::class.java)
                } else {
                    Intent(context, IncomeActivity::class.java)
                }
                intent.putExtra("TRANSACTION_ID", transaction.id)
                context.startActivity(intent)
            } catch (e: Exception) {
                Log.e("TransactionAdapter", "Error opening transaction for editing: ${e.message}", e)
            }
        }
    }
}
