package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class loginPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val loginButton: TextView = findViewById(R.id.loginButton)

        // Set click listener to navigate to Register Page
        loginButton.setOnClickListener {
            val intent = Intent(this, homePage::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        val registerText: TextView = findViewById(R.id.registerText)

        // Set click listener to navigate to Register Page
        registerText.setOnClickListener {
            val intent = Intent(this, registerPage::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

    }
}