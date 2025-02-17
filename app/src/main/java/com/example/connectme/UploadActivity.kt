package com.example.connectme

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.GridView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class UploadActivity : AppCompatActivity() {

    private lateinit var ivSelectedImage: ImageView
    private lateinit var gridViewImages: GridView
    private lateinit var tvNext: TextView

    private val images = listOf(
        R.drawable.highrise, R.drawable.islamia, R.drawable.street,
        R.drawable.room, R.drawable.grass, R.drawable.highrise,
        R.drawable.street, R.drawable.masab, R.drawable.ahmed
    )

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        // Initialize views
        ivSelectedImage = findViewById(R.id.ivSelectedImage)
        gridViewImages = findViewById(R.id.gridViewImages)
        tvNext = findViewById(R.id.tvNext)

        // Set adapter to GridView
        val imageAdapter = ImageAdapter(this, images)
        gridViewImages.adapter = imageAdapter

        // Set an item click listener to the grid view
        gridViewImages.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            // Update the selected image on top when an image is clicked
            ivSelectedImage.setImageResource(images[position])
        }


        findViewById<BottomNavigationView>(R.id.bottomNavigation).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
                    val intent = Intent(this, SearchActivity::class.java)
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

        val cam: ImageView = findViewById(R.id.openCamera)
        cam.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }

        val next: TextView = findViewById(R.id.tvNext)
        next.setOnClickListener {
            val intent = Intent(this, PostActivity::class.java)
            startActivity(intent)
        }

    }
}
