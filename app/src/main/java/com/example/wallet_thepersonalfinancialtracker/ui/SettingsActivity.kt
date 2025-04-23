package com.example.wallet_thepersonalfinancialtracker.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.wallet_thepersonalfinancialtracker.R
import com.example.wallet_thepersonalfinancialtracker.utils.BackupManager
import com.example.wallet_thepersonalfinancialtracker.utils.SharedPreferencesManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var tvCurrentCurrency: TextView
    private lateinit var tvCurrentTheme: TextView
    private lateinit var switchNotifications: SwitchCompat

    private lateinit var settingProfile: LinearLayout
    private lateinit var settingNotifications: LinearLayout
    private lateinit var settingCurrency: LinearLayout
    private lateinit var settingTheme: LinearLayout
    private lateinit var settingBackup: LinearLayout
    private lateinit var settingRestore: LinearLayout
    private lateinit var settingExport: LinearLayout
    private lateinit var settingAbout: LinearLayout

    private lateinit var navHome: LinearLayout
    private lateinit var navTransfer: LinearLayout
    private lateinit var navCategory: LinearLayout
    private lateinit var navSettings: LinearLayout

    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var backupManager: BackupManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        try {
            // Initialize managers
            sharedPreferencesManager = SharedPreferencesManager(applicationContext)
            backupManager = BackupManager(applicationContext)

            // Initialize views
            initViews()

            // Load settings
            loadSettings()

            // Set click listeners
            setClickListeners()
        } catch (e: Exception) {
            Log.e("SettingsActivity", "Error in onCreate: ${e.message}", e)
            Toast.makeText(this, "Error initializing settings", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        tvCurrentCurrency = findViewById(R.id.tvCurrentCurrency)
        tvCurrentTheme = findViewById(R.id.tvCurrentTheme)
        switchNotifications = findViewById(R.id.switchNotifications)

        settingProfile = findViewById(R.id.settingProfile)
        settingNotifications = findViewById(R.id.settingNotifications)
        settingCurrency = findViewById(R.id.settingCurrency)
        settingTheme = findViewById(R.id.settingTheme)
        settingBackup = findViewById(R.id.settingBackup)
        settingRestore = findViewById(R.id.settingRestore)
        settingExport = findViewById(R.id.settingExport)
        settingAbout = findViewById(R.id.settingAbout)

        navHome = findViewById(R.id.navHome)
        navTransfer = findViewById(R.id.navTransfer)
        navCategory = findViewById(R.id.navCategory)
        navSettings = findViewById(R.id.navSettings)
    }

    private fun loadSettings() {
        // Load currency
        tvCurrentCurrency.text = sharedPreferencesManager.getCurrencyType()

        // Load theme (default is Light)
        tvCurrentTheme.text = sharedPreferencesManager.getTheme()

        // Load notification preference
        switchNotifications.isChecked = sharedPreferencesManager.getNotificationsEnabled()
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener { finish() }

        // Account settings
        settingProfile.setOnClickListener {
            Toast.makeText(this, "Profile feature coming soon", Toast.LENGTH_SHORT).show()
        }

        settingNotifications.setOnClickListener {
            switchNotifications.toggle()
        }

        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferencesManager.setNotificationsEnabled(isChecked)
            Toast.makeText(this, "Notifications ${if (isChecked) "enabled" else "disabled"}", Toast.LENGTH_SHORT).show()
        }

        // Preferences
        settingCurrency.setOnClickListener {
            showCurrencyDialog()
        }

        settingTheme.setOnClickListener {
            showThemeDialog()
        }

        // Data management
        settingBackup.setOnClickListener {
            backupData()
        }

        settingRestore.setOnClickListener {
            showRestoreDialog()
        }

        settingExport.setOnClickListener {
            exportData()
        }

        // About
        settingAbout.setOnClickListener {
            showAboutDialog()
        }

        // Navigation
        navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        navTransfer.setOnClickListener {
            startActivity(Intent(this, TransactionHistoryActivity::class.java))
            finish()
        }

        navCategory.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
            finish()
        }

        navSettings.setOnClickListener {
            // Already on settings
        }
    }

    private fun showCurrencyDialog() {
        val currencies = arrayOf("$", "€", "£", "¥", "₹", "₩", "₽")
        val currentCurrency = sharedPreferencesManager.getCurrencyType()
        val currentIndex = currencies.indexOf(currentCurrency).takeIf { it >= 0 } ?: 0

        AlertDialog.Builder(this)
            .setTitle("Select Currency")
            .setSingleChoiceItems(currencies, currentIndex) { dialog, which ->
                val selectedCurrency = currencies[which]
                sharedPreferencesManager.setCurrencyType(selectedCurrency)
                tvCurrentCurrency.text = selectedCurrency
                dialog.dismiss()
                Toast.makeText(this, "Currency changed to $selectedCurrency", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showThemeDialog() {
        val themes = arrayOf("Light", "Dark", "System Default")
        val currentTheme = sharedPreferencesManager.getTheme()
        val currentIndex = themes.indexOf(currentTheme).takeIf { it >= 0 } ?: 0

        AlertDialog.Builder(this)
            .setTitle("Select Theme")
            .setSingleChoiceItems(themes, currentIndex) { dialog, which ->
                val selectedTheme = themes[which]
                sharedPreferencesManager.setTheme(selectedTheme)
                tvCurrentTheme.text = selectedTheme
                dialog.dismiss()
                Toast.makeText(this, "Theme changed to $selectedTheme (restart app to apply)", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun backupData() {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "wallet_backup_$timestamp.json"

            if (backupManager.createBackup(fileName)) {
                Toast.makeText(this, "Backup created successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to create backup", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("SettingsActivity", "Error creating backup: ${e.message}", e)
            Toast.makeText(this, "Error creating backup: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showRestoreDialog() {
        val backupFiles = backupManager.getBackupFiles()

        if (backupFiles.isEmpty()) {
            Toast.makeText(this, "No backup files found", Toast.LENGTH_SHORT).show()
            return
        }

        // Format the file names to be more readable
        val displayNames = backupFiles.map {
            val name = it.replace("wallet_backup_", "").replace(".json", "")
            try {
                val date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).parse(name)
                SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault()).format(date)
            } catch (e: Exception) {
                name
            }
        }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Backup to Restore")
            .setItems(displayNames) { _, which ->
                val selectedFile = backupFiles[which]
                confirmRestore(selectedFile)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmRestore(fileName: String) {
        AlertDialog.Builder(this)
            .setTitle("Restore Data")
            .setMessage("This will replace all your current data. Continue?")
            .setPositiveButton("Restore") { _, _ ->
                try {
                    if (backupManager.restoreBackup(fileName)) {
                        Toast.makeText(this, "Data restored successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to restore data", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.e("SettingsActivity", "Error restoring data: ${e.message}", e)
                    Toast.makeText(this, "Error restoring data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun exportData() {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "wallet_export_$timestamp.csv"

            if (backupManager.exportToCsv(fileName)) {
                Toast.makeText(this, "Data exported successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to export data", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("SettingsActivity", "Error exporting data: ${e.message}", e)
            Toast.makeText(this, "Error exporting data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Wallet")
            .setMessage("Personal Finance Tracker\nVersion 1.0.0\n\nDeveloped by: Your Name\n\nTrack your income and expenses with ease.")
            .setPositiveButton("OK", null)
            .show()
    }
}
