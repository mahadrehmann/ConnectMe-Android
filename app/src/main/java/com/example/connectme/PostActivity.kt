package com.example.connectme

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PostActivity : AppCompatActivity() {

    private lateinit var ivClosePost: ImageView
    private lateinit var gvImages: GridView
    private lateinit var etCaption: EditText
    private lateinit var btnShare: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // Initialize views
        ivClosePost = findViewById(R.id.ivClosePost)
        gvImages = findViewById(R.id.gvImages)
        etCaption = findViewById(R.id.etCaption)
        btnShare = findViewById(R.id.btnShare)

        val imageList = listOf(
            R.drawable.room,
            R.drawable.street
        )

        // Set up ImageAdapter
        val imageAdapter = ImageAdapter(this, imageList)
        gvImages.adapter = imageAdapter

        // Handle Close button click
        ivClosePost.setOnClickListener {
            finish() // Close the activity
        }

        // Handle Share button click
        btnShare.setOnClickListener {
            val caption = etCaption.text.toString().trim()
            if (caption.isEmpty()) {
                Toast.makeText(this, "Please enter a caption", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Post Shared!", Toast.LENGTH_SHORT).show()
                finish() // Close activity after sharing
            }
        }
    }
}
