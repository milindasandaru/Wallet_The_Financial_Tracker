package com.example.wallet_thepersonalfinancialtracker.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.model.Notification

class NotificationAdapter(
    private val context: Context,
    private var notifications: List<Notification>,
    private val onNotificationClicked: (Int) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.bind(notification)
    }

    override fun getItemCount(): Int = notifications.size

    fun updateData(newNotifications: List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardNotification: CardView = itemView.findViewById(R.id.cardNotification)
        private val ivIcon: ImageView = itemView.findViewById(R.id.ivNotificationIcon)
        private val tvTitle: TextView = itemView.findViewById(R.id.tvNotificationTitle)
        private val tvMessage: TextView = itemView.findViewById(R.id.tvNotificationMessage)
        private val tvDate: TextView = itemView.findViewById(R.id.tvNotificationDate)
        private val ivUnread: View = itemView.findViewById(R.id.viewUnread)

        fun bind(notification: Notification) {
            tvTitle.text = notification.title
            tvMessage.text = notification.message
            tvDate.text = notification.getFormattedDate()

            // Set unread indicator
            ivUnread.visibility = if (notification.isRead) View.GONE else View.VISIBLE

            // Set text style based on read status
            val typeface = if (notification.isRead) Typeface.NORMAL else Typeface.BOLD
            tvTitle.setTypeface(null, typeface)

            // Set icon and color based on notification type
            when (notification.type) {
                Notification.Type.WARNING -> {
                    ivIcon.setImageResource(R.drawable.warningicon)
                    ivIcon.setColorFilter(Color.parseColor("#FFA500")) // Orange
                }
                Notification.Type.ALERT -> {
                    ivIcon.setImageResource(R.drawable.remindericon)
                    ivIcon.setColorFilter(Color.RED)
                }
                Notification.Type.REMINDER -> {
                    ivIcon.setImageResource(R.drawable.remindericon)
                    ivIcon.setColorFilter(Color.GREEN)
                }
            }

            // Set click listener
            itemView.setOnClickListener {
                onNotificationClicked(notification.id)
                ivUnread.visibility = View.GONE
                tvTitle.setTypeface(null, Typeface.NORMAL)
            }
        }
    }
}
