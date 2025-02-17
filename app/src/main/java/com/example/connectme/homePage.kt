package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class homePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        // Set click listener to navigate to Register Page
        val dm: ImageView = findViewById(R.id.ivEditProfile)
        dm.setOnClickListener {
            val intent = Intent(this, dmPage::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        findViewById<BottomNavigationView>(R.id.bottomNavigation).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.add -> {
                    val intent = Intent(this, UploadActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }



        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Find the Add item in the BottomNavigationView
        val addItemView = bottomNavigationView.findViewById<View>(R.id.add)

        // Set the background for the Add button
        addItemView.background = ContextCompat.getDrawable(this, R.drawable.brown_circle_background)

        // Add padding to prevent the circle from looking too large
        addItemView.setPadding(10, 10, 10, 10) // Adjust padding as needed


    }
}