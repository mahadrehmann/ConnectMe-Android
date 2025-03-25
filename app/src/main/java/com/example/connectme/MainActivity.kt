package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val splashDelay: Long = 5000 // 5 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Automatically navigate after 5 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            navigateToLogin()
        }, splashDelay)
    }

    private fun navigateToLogin() {
        // Avoid navigating twice if finish() was already called.
        if (!isFinishing) {
            val intent = Intent(this, loginPage::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }
}
