package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CameraActivity : AppCompatActivity() {

    private lateinit var ivClose: ImageView
    private lateinit var ivShutterButton: ImageView
    private lateinit var tvNext: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Initialize views
        ivClose = findViewById(R.id.ivClose)
        ivShutterButton = findViewById(R.id.ivShutterButton)
        tvNext = findViewById(R.id.tvNext)

        // Handle the close button click
        ivClose.setOnClickListener {
            finish() // Close the activity
        }

        // Handle the shutter button click
        ivShutterButton.setOnClickListener {
            println("Shutter button clicked!")
        }

    }
}
