package com.example.wallet_thepersonalfinancialtracker.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.adapter.NotificationAdapter
import com.example.wallet_thepersonalfinancialtracker.utils.NotificationHelper

class NotificationsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var btnClearAll: TextView
    private lateinit var rvNotifications: RecyclerView
    private lateinit var tvNoNotifications: TextView
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var adapter: NotificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        try {
            // Initialize NotificationHelper
            notificationHelper = NotificationHelper(applicationContext)

            // Initialize views
            initViews()

            // Set up RecyclerView
            setupRecyclerView()

            // Load notifications
            loadNotifications()

            // Set click listeners
            setClickListeners()
        } catch (e: Exception) {
            Log.e("NotificationsActivity", "Error in onCreate: ${e.message}", e)
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        btnClearAll = findViewById(R.id.btnClearAll)
        rvNotifications = findViewById(R.id.rvNotifications)
        tvNoNotifications = findViewById(R.id.tvNoNotifications)
    }

    private fun setupRecyclerView() {
        rvNotifications.layoutManager = LinearLayoutManager(this)
        adapter = NotificationAdapter(this, emptyList()) { notificationId ->
            notificationHelper.markNotificationAsRead(notificationId)
            loadNotifications() // Reload to update UI
        }
        rvNotifications.adapter = adapter
    }

    private fun loadNotifications() {
        val notifications = notificationHelper.getAllNotifications()
        adapter.updateData(notifications)

        // Show "No notifications" message if the list is empty
        if (notifications.isEmpty()) {
            tvNoNotifications.visibility = View.VISIBLE
            rvNotifications.visibility = View.GONE
            btnClearAll.visibility = View.GONE
        } else {
            tvNoNotifications.visibility = View.GONE
            rvNotifications.visibility = View.VISIBLE
            btnClearAll.visibility = View.VISIBLE
        }

        // Mark all as read when opened
        notificationHelper.markAllNotificationsAsRead()
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnClearAll.setOnClickListener {
            notificationHelper.clearAllNotifications()
            loadNotifications()
        }
    }
}
