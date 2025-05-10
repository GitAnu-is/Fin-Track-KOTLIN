package com.example.fintrack

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONObject
import java.io.File

class Setting : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var exportButton: Button
    private lateinit var restoreButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        findViewById<ImageButton>(R.id.btnTransaction3).setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnDashboard3).setOnClickListener {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.btnBudget3).setOnClickListener {
            val intent = Intent(this, Budget::class.java)
            startActivity(intent)
        }

        // Initialize SharedPreferences
        prefs = getSharedPreferences("fintrack_prefs", Context.MODE_PRIVATE)

        // Bind views
        exportButton = findViewById(R.id.btnExport)
        restoreButton = findViewById(R.id.btnRestore)

        // Export Button Click
        exportButton.setOnClickListener {
            val backupPrefs = prefs.all
            val backupJson = JSONObject(backupPrefs).toString()

            val file = File(filesDir, "backup.json")
            file.writeText(backupJson)

            Toast.makeText(this, "Data exported successfully!", Toast.LENGTH_SHORT).show()


        }

        // Restore Button Click
        restoreButton.setOnClickListener {
            val file = File(filesDir, "backup.json")
            if (file.exists()) {
                val jsonStr = file.readText()
                val jsonObject = JSONObject(jsonStr)
                val editor = prefs.edit()
                for (key in jsonObject.keys()) {
                    val value = jsonObject.get(key)
                    when (value) {
                        is Int -> editor.putInt(key, value)
                        is Boolean -> editor.putBoolean(key, value)
                        is Float -> editor.putFloat(key, value)
                        is Long -> editor.putLong(key, value)
                        is String -> editor.putString(key, value)
                        else -> { /* Ignore other types */ }
                    }
                }
                editor.apply()
                Toast.makeText(this, "Data restored successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No backup file found!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
