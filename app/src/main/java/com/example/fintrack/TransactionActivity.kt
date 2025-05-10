package com.example.fintrack

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fintrack.adapter.TransactionAdapter
import com.example.fintrack.model.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class TransactionActivity : AppCompatActivity() {

    private lateinit var adapter: TransactionAdapter
    private val transactionList = mutableListOf<Transaction>()
    private var editingPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transaction)


        findViewById<ImageButton>(R.id.btnDashboard).setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnBudget).setOnClickListener {
            val intent = Intent(this, Budget::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnSetting).setOnClickListener {
            val intent = Intent(this, Setting::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val prefs = getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)

        // Bind views
        val spinnerType: Spinner = findViewById(R.id.spinnerType)
        val spinnerCategory: Spinner = findViewById(R.id.spinnerCategory)
        val editAmount: EditText = findViewById(R.id.editAmount)
        val editTitle: EditText = findViewById(R.id.editTitle)
        val editDate: EditText = findViewById(R.id.editDate)
        val btnPickDate: ImageButton = findViewById(R.id.btnPickDate)
        val addButton: Button = findViewById(R.id.btnAddTransaction)
        val recyclerView: RecyclerView = findViewById(R.id.transactionRecyclerView)

        val types = listOf("Select type", "INCOME", "EXPENSE")
        val incomeCategories = listOf("Salary", "Investments", "Part-Time", "Bonus", "Others")
        val expenseCategories = listOf("Shopping", "Food", "Transport", "Gifts", "Sports", "Education", "Utilities")

        // Load saved data
        loadTransactionList()

        // Setup RecyclerView
        adapter = TransactionAdapter(transactionList,
            onEdit = { position -> loadTransactionToEdit(position) },
            onDelete = { position ->
                transactionList.removeAt(position)
                adapter.notifyItemRemoved(position)
                saveTransactionList()
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Spinner setup
        spinnerType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types)

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val categoryList = when (types[position]) {
                    "INCOME" -> incomeCategories
                    "EXPENSE" -> expenseCategories
                    else -> listOf("Select Category")
                }

                spinnerCategory.adapter = ArrayAdapter(
                    this@TransactionActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    categoryList
                )

                val savedCategory = prefs.getString("lastCategory", "Select Category")
                val catIndex = categoryList.indexOf(savedCategory)
                if (catIndex >= 0) {
                    spinnerCategory.setSelection(catIndex)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Restore saved type
        val savedType = prefs.getString("lastType", "Select type")
        val typeIndex = types.indexOf(savedType)
        if (typeIndex >= 0) {
            spinnerType.setSelection(typeIndex)
        }

        // Restore saved date or default to today
        val savedDate = prefs.getString("lastDate", "")
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        editDate.setText(if (savedDate.isNullOrEmpty()) todayDate else savedDate)

        // Date picker
        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val formatted = String.format("%04d-%02d-%02d", y, m + 1, d)
                editDate.setText(formatted)
            }, year, month, day)

            datePicker.show()
        }

        // Add transaction logic
        addButton.setOnClickListener {
            val type = spinnerType.selectedItem.toString()
            val category = spinnerCategory.selectedItem.toString()
            val title = editTitle.text.toString().trim()
            val date = editDate.text.toString().trim()
            val amount = editAmount.text.toString().toDoubleOrNull()

            if (title.isEmpty()) {
                Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Enter a valid positive amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val transaction = Transaction(amount, type, category, title, date)

                if (editingPosition >= 0) {
                    transactionList[editingPosition] = transaction
                    adapter.notifyItemChanged(editingPosition)
                    editingPosition = -1
                } else {
                    transactionList.add(0, transaction)
                    adapter.notifyItemInserted(0)
                }

                saveTransactionList()

                prefs.edit()
                    .putString("lastType", type)
                    .putString("lastCategory", category)
                    .putString("lastDate", date)
                    .apply()

                Toast.makeText(this, "Transaction saved successfully!", Toast.LENGTH_SHORT).show()
                clearFields(editAmount, editTitle, editDate, spinnerType, spinnerCategory)

            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    private fun loadTransactionToEdit(position: Int) {
        val transaction = transactionList[position]
        findViewById<EditText>(R.id.editAmount).setText(transaction.amount.toString())
        findViewById<EditText>(R.id.editTitle).setText(transaction.title)
        findViewById<EditText>(R.id.editDate).setText(transaction.date)

        val spinnerType = findViewById<Spinner>(R.id.spinnerType)
        spinnerType.setSelection(if (transaction.type == "INCOME") 1 else 2)

        editingPosition = position
    }

    private fun clearFields(amount: EditText, title: EditText, date: EditText, type: Spinner, category: Spinner) {
         amount.text.clear()
        title.text.clear()
        date.setText(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
        type.setSelection(0)
        category.setSelection(0)
    }

    private fun saveTransactionList() {
        val prefs = getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)
        val json = Gson().toJson(transactionList)
        prefs.edit().putString("transaction_list", json).apply()
    }

    private fun loadTransactionList() {
        val prefs = getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("transaction_list", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<Transaction>>() {}.type
            val savedList: MutableList<Transaction> = Gson().fromJson(json, type)
            transactionList.addAll(savedList)
        }
    }
}
