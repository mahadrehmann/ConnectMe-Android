package com.example.connectme

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Find the button
        val continueButton: Button = findViewById(R.id.continuebutton)

        // Set a click listener on the button
        continueButton.setOnClickListener {
            // Open SecondActivity
            val intent = Intent(this, loginPage::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        }
    }
}