package com.example.fintrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Onboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_onboard)

            val nextBtn = findViewById<Button>(R.id.button)
            nextBtn.setOnClickListener {
                val intent = Intent(this, TransactionActivity::class.java)
                startActivity(intent)
                finish() // Optional: prevent going back to Onboard
            }

        } catch (e: Exception) {
            Log.e("Onboard", "Error in onCreate: ${e.message}")
            Toast.makeText(this, "Error loading onboarding screen", Toast.LENGTH_LONG).show()
        }
    }
}
