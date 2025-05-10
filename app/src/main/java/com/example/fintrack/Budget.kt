package com.example.fintrack

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.fintrack.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Budget : AppCompatActivity() {

    private lateinit var txtInput: EditText
    private lateinit var txtSavedBudget: TextView
    private lateinit var txtTotalExpense: TextView

    private var totalExpenses = 0.0
    private var monthlyBudget = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget)

        // Bottom navigation buttons
        findViewById<ImageButton>(R.id.btnTransaction2).setOnClickListener {
            startActivity(Intent(this, TransactionActivity::class.java))
        }
        findViewById<ImageButton>(R.id.btnDashboard2).setOnClickListener {
            startActivity(Intent(this, Dashboard::class.java))
        }
        findViewById<ImageButton>(R.id.btnSetting2).setOnClickListener {
            startActivity(Intent(this, Setting::class.java))
        }

        // Bind Views
        txtInput = findViewById(R.id.editBudget)
        txtSavedBudget = findViewById(R.id.txtSavedBudget)
        txtTotalExpense = findViewById(R.id.txtExpenseTotal)

        val btnAdd = findViewById<Button>(R.id.btnAddBudget)
        val btnSave = findViewById<Button>(R.id.btnSaveBudget)

        loadTransactionData()
        loadSavedBudget()

        btnAdd.setOnClickListener {
            val input = txtInput.text.toString().toDoubleOrNull()
            if (input != null && input > 0) {
                txtInput.setText("LKR %.2f".format(input))
            } else {
                Toast.makeText(this, "Enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }

        btnSave.setOnClickListener {
            val cleanValue = txtInput.text.toString().replace("LKR", "").trim()
            val input = cleanValue.toDoubleOrNull()
            if (input != null && input > 0) {
                monthlyBudget = input
                saveBudget()
                updateUI()
                Toast.makeText(this, "Budget saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Invalid budget amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadTransactionData() {
        val prefs = getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("transaction_list", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Transaction>>() {}.type
            val transactions: List<Transaction> = Gson().fromJson(json, type)
            totalExpenses = transactions.filter { it.type == "EXPENSE" }
                .sumOf { it.amount }
        }
    }

    private fun saveBudget() {
        val prefs = getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("monthly_budget_str", monthlyBudget.toString()).apply()
    }

    private fun loadSavedBudget() {
        val prefs = getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)
        val budgetStr = prefs.getString("monthly_budget_str", "0.0")
        monthlyBudget = budgetStr?.toDoubleOrNull() ?: 0.0
        updateUI()
    }

    private fun updateUI() {
        txtSavedBudget.text = "Monthly Budget : LKR %.2f".format(monthlyBudget)
        txtTotalExpense.text = "Total Expenses : LKR %.2f".format(totalExpenses)

        //  Check if expenses exceeded budget
        if (totalExpenses > monthlyBudget && monthlyBudget > 0) {
            showBudgetExceededNotification()
        }
    }

    private fun showBudgetExceededNotification() {
        val channelId = "budget_channel"
        val channelName = "Budget Notifications"
        val notificationId = 101

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android 8.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // ⚡ Replace with your app icon if needed
            .setContentTitle("🚨 Budget Exceeded!")
            .setContentText("Your expenses are higher than your monthly budget.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
