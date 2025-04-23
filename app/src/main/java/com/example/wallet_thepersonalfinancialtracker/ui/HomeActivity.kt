package com.example.wallet_thepersonalfinancialtracker.ui

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.receivers.ExpenseReminderReceiver
import com.example.wallet_thepersonalfinancialtracker.utils.NotificationHelper
import com.example.wallet_thepersonalfinancialtracker.utils.SharedPreferencesManager
import com.example.wallet_thepersonalfinancialtracker.utils.TransactionManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.NumberFormat
import java.util.*

class HomeActivity : AppCompatActivity() {

    private lateinit var tvGreeting: TextView
    private lateinit var tvWelcome: TextView
    private lateinit var btnNotification: ImageButton
    private lateinit var viewNotificationBadge: View
    private lateinit var tvDaily: TextView
    private lateinit var tvWeekly: TextView
    private lateinit var tvMonthly: TextView
    private lateinit var tvAnnually: TextView
    private lateinit var pieChart: PieChart
    private lateinit var tvExpensesAmount: TextView
    private lateinit var tvIncomeAmount: TextView
    private lateinit var tvBalanceAmount: TextView
    private lateinit var btnAddMoney: Button
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var btnBudget: Button
    private lateinit var notificationHelper: NotificationHelper
    private lateinit var categoryIconContainer: CategoryIconsView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var transactionManager: TransactionManager
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    private var currentTimePeriod = TimePeriod.MONTHLY
    private var userName = "User"

    enum class TimePeriod { DAILY, WEEKLY, MONTHLY, ANNUALLY }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        try {
            sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            userName = sharedPreferences.getString("fullName", "User") ?: "User"

            transactionManager = TransactionManager(applicationContext)
            sharedPreferencesManager = SharedPreferencesManager(applicationContext)
            notificationHelper = NotificationHelper(applicationContext)

            initViews()
            setGreeting()
            setClickListeners()
            updateTimePeriodSelection(currentTimePeriod)
            updateDashboard()
            requestNotificationPermission()
            scheduleExpenseReminder()
            updateNotificationBadge()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error initializing home screen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        tvGreeting = findViewById(R.id.tvGreeting)
        tvWelcome = findViewById(R.id.tvWelcome)
        btnNotification = findViewById(R.id.btnNotification)
        viewNotificationBadge = findViewById(R.id.viewNotificationBadge)
        tvDaily = findViewById(R.id.tvDaily)
        tvWeekly = findViewById(R.id.tvWeekly)
        tvMonthly = findViewById(R.id.tvMonthly)
        tvAnnually = findViewById(R.id.tvAnnually)
        pieChart = findViewById(R.id.pieChart)
        tvExpensesAmount = findViewById(R.id.tvExpensesAmount)
        tvIncomeAmount = findViewById(R.id.tvIncomeAmount)
        tvBalanceAmount = findViewById(R.id.tvBalanceAmount)
        btnAddMoney = findViewById(R.id.btnAddMoney)
        fabAdd = findViewById(R.id.fabAdd)
        btnBudget = findViewById(R.id.btnBudget)
        categoryIconContainer = findViewById(R.id.categoryIconContainer)
    }

    private fun setGreeting() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hourOfDay < 12 -> "Good Morning"
            hourOfDay < 18 -> "Good Afternoon"
            else -> "Good Evening"
        }
        val firstName = userName.split(" ").firstOrNull() ?: userName
        tvGreeting.text = "$greeting, $firstName"
    }

    private fun setClickListeners() {
        btnNotification.setOnClickListener {
            startActivity(Intent(this, NotificationsActivity::class.java))
        }
        btnAddMoney.setOnClickListener {
            startActivity(Intent(this, IncomeActivity::class.java))
        }
        fabAdd.setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }
        btnBudget.setOnClickListener {
            startActivity(Intent(this, BudgetSettingsActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navHome).setOnClickListener { /* Already here */ }
        findViewById<LinearLayout>(R.id.navTransfer).setOnClickListener {
            startActivity(Intent(this, TransactionHistoryActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navCategory).setOnClickListener {
            Toast.makeText(this, "Category feature coming soon", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, CategoryActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.navSettings).setOnClickListener {
            Toast.makeText(this, "Settings feature coming soon", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        tvDaily.setOnClickListener { updateTimePeriodSelection(TimePeriod.DAILY); updateDashboard() }
        tvWeekly.setOnClickListener { updateTimePeriodSelection(TimePeriod.WEEKLY); updateDashboard() }
        tvMonthly.setOnClickListener { updateTimePeriodSelection(TimePeriod.MONTHLY); updateDashboard() }
        tvAnnually.setOnClickListener { updateTimePeriodSelection(TimePeriod.ANNUALLY); updateDashboard() }
    }

    private fun updateTimePeriodSelection(period: TimePeriod) {
        val black = resources.getColor(R.color.black, null)
        val gray = resources.getColor(R.color.text_secondary, null)
        val selectedBg = R.drawable.time_period_selector_bg_selected
        val unselectedBg = R.drawable.time_period_selector_bg

        tvDaily.setBackgroundResource(if (period == TimePeriod.DAILY) selectedBg else unselectedBg)
        tvWeekly.setBackgroundResource(if (period == TimePeriod.WEEKLY) selectedBg else unselectedBg)
        tvMonthly.setBackgroundResource(if (period == TimePeriod.MONTHLY) selectedBg else unselectedBg)
        tvAnnually.setBackgroundResource(if (period == TimePeriod.ANNUALLY) selectedBg else unselectedBg)

        tvDaily.setTextColor(if (period == TimePeriod.DAILY) black else gray)
        tvWeekly.setTextColor(if (period == TimePeriod.WEEKLY) black else gray)
        tvMonthly.setTextColor(if (period == TimePeriod.MONTHLY) black else gray)
        tvAnnually.setTextColor(if (period == TimePeriod.ANNUALLY) black else gray)

        currentTimePeriod = period
    }


    private fun updateDashboard() {
        val (startDate, endDate) = getPeriodRange(currentTimePeriod)
        val currency = sharedPreferencesManager.getCurrencyType()

        val expenses = transactionManager.getTotalExpenses(startDate, endDate)
        val income = transactionManager.getTotalIncome(startDate, endDate)
        val balance = income - expenses

        tvExpensesAmount.text = "$currency ${String.format("%.2f", expenses)}"
        tvIncomeAmount.text = "$currency ${String.format("%.2f", income)}"
        tvBalanceAmount.text = "$currency ${String.format("%.2f", balance)}"

        updatePieChart(startDate, endDate)
    }

    private fun updatePieChart(startDate: Date, endDate: Date) {
        val categoryMap = transactionManager.getExpensesByCategory(startDate, endDate)
        val total = categoryMap.values.sum()
        val entries = categoryMap.map { (category, amount) ->
            PieEntry(amount.toFloat(), "${String.format("%.1f", (amount/total)*100)}%")
        }
        val dataSet = PieDataSet(entries, "").apply {
            sliceSpace = 2f
            selectionShift = 5f
            colors = getChartColors()
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            valueLinePart1Length = 0.3f
            valueLinePart2Length = 0.3f
            valueLineColor = Color.GRAY
            valueLineWidth = 2f
        }
        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(pieChart))
            setValueTextSize(14f)
            setValueTextColor(Color.BLACK)
        }
        pieChart.data = data
        pieChart.legend.isEnabled = false
        pieChart.setDrawEntryLabels(false)
        pieChart.holeRadius = 80f
        pieChart.transparentCircleRadius = 85f
        pieChart.invalidate()

    }

        private fun getPeriodRange(period: TimePeriod): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        val end = calendar.time
        when (period) {
            TimePeriod.DAILY -> {
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            TimePeriod.WEEKLY -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            TimePeriod.MONTHLY -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            TimePeriod.ANNUALLY -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }
        val start = calendar.time
        return Pair(start, end)
    }

    private fun getChartColors(): List<Int> {
        return listOf(
            resources.getColor(R.color.food_color, null),
            resources.getColor(R.color.eating_out_color, null),
            resources.getColor(R.color.bill_color, null),
            resources.getColor(R.color.car_color, null),
            resources.getColor(R.color.clothes_color, null),
            resources.getColor(R.color.health_color, null),
            resources.getColor(R.color.entertainment_color, null),
            resources.getColor(R.color.communication_color, null),
            resources.getColor(R.color.house_color, null),
            resources.getColor(R.color.pets_color, null),
            resources.getColor(R.color.sports_color, null),
            resources.getColor(R.color.taxi_color, null),
            resources.getColor(R.color.toiletry_color, null),
            resources.getColor(R.color.transport_color, null),
            resources.getColor(R.color.other_income_color, null)
        )
    }

    private fun getCategoryIconRes(category: String): Int {
        return when (category.lowercase()) {
            "food" -> R.drawable.foodiconnn
            "eating out" -> R.drawable.eatingouticonn
            "bill" -> R.drawable.billiconnnn
            "car" -> R.drawable.cariconnn
            "clothes" -> R.drawable.lotheiconnn
            "health" -> R.drawable.healthiconnn
            "entertainment" -> R.drawable.entertaimenticonnn
            "communication" -> R.drawable.communicationiconnn
            "house" -> R.drawable.houseiconn
            "pets" -> R.drawable.peticonnn
            "sports" -> R.drawable.sporticonn
            "taxi" -> R.drawable.taxiiconnm
            "toiletry" -> R.drawable.toiletryiconn
            "transport" -> R.drawable.ransporticonn
            else -> R.drawable.othericonnnn
        }
    }

    private fun requestNotificationPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error requesting notification permission: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleExpenseReminder() {
        try {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, ExpenseReminderReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 20)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                if (timeInMillis < System.currentTimeMillis()) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error scheduling reminder: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNotificationBadge() {
        try {
            val unreadCount = notificationHelper.getUnreadNotificationCount()
            viewNotificationBadge.visibility = if (unreadCount > 0) View.VISIBLE else View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("HomeActivity", "Error updating notification badge: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            updateNotificationBadge()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 123
    }
}
