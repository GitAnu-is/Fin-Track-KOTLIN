package com.example.fintrack

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.fintrack.model.Transaction
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Dashboard : AppCompatActivity() {

    private var incomeTotal = 0.0
    private var expenseTotal = 0.0
    private var totalBalance = 0.0

    private lateinit var txtIncome: TextView
    private lateinit var txtExpense: TextView
    private lateinit var txtBalance: TextView
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Bind views
        txtIncome = findViewById(R.id.txtIncome)
        txtExpense = findViewById(R.id.txtExpense)
        txtBalance = findViewById(R.id.txtBalance)
        pieChart = findViewById(R.id.pieChart)

        loadTransactionData()
        updateDashboard()


        findViewById<ImageButton>(R.id.btnTransaction1).setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnBudget1).setOnClickListener {
            val intent = Intent(this, Budget::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnSetting1).setOnClickListener {
            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
        }
    }

    private fun loadTransactionData() {
        val prefs = getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("transaction_list", null)
        if (!json.isNullOrEmpty()) {
            val type = object : TypeToken<MutableList<Transaction>>() {}.type
            val list: List<Transaction> = Gson().fromJson(json, type)

            for (tx in list) {
                if (tx.type == "INCOME") {
                    incomeTotal += tx.amount
                } else if (tx.type == "EXPENSE") {
                    expenseTotal += tx.amount
                }
            }
            totalBalance = incomeTotal - expenseTotal
        }
    }

    private fun updateDashboard() {
        txtIncome.text = "LKR %.2f".format(incomeTotal)
        txtExpense.text = "LKR %.2f".format(expenseTotal)
        txtBalance.text = "LKR %.2f".format(totalBalance)

        val pieEntries = ArrayList<PieEntry>()
        if (expenseTotal > 0) pieEntries.add(PieEntry(expenseTotal.toFloat(), "Expense"))
        if (incomeTotal > 0) pieEntries.add(PieEntry(incomeTotal.toFloat(), "Income"))

        val pieDataSet = PieDataSet(pieEntries, "Income vs Expense")
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        pieDataSet.valueTextSize = 14f

        val pieData = PieData(pieDataSet)

        pieChart.data = pieData
        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false
        pieChart.centerText = "Income vs Expense"
        pieChart.animateY(1000)
        pieChart.invalidate()
    }
}
