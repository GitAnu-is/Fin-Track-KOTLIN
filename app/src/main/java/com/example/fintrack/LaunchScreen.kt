package com.example.fintrack

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LaunchScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_launch_screen)
            
            // Delay and move to the next activity
            Handler(Looper.getMainLooper()).postDelayed({
                try {
                    val intent = Intent(this@LaunchScreen, Onboard::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.e("LaunchScreen", "Error navigating to Onboard: ${e.message}")
                    Toast.makeText(this, "Error launching app. Please try again.", Toast.LENGTH_LONG).show()
                }
            }, 3000)
        } catch (e: Exception) {
            Log.e("LaunchScreen", "Error in onCreate: ${e.message}")
            Toast.makeText(this, "Error launching app. Please try again.", Toast.LENGTH_LONG).show()
        }
    }
}

