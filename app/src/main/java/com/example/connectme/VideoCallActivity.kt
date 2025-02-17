package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class VideoCallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        // Get the user info from the intent passed from the DM page
        val userName = intent.getStringExtra("userName") ?: "Unknown"
        val profileImageResId = intent.getIntExtra("profileImage", R.drawable.asim)

        // Set the user info in the UI
        findViewById<TextView>(R.id.tvUserName).text = userName

        // Handle call actions (e.g., mute, video, speaker, end call)
        val endCallButton = findViewById<ImageView>(R.id.ivEndCall)
        endCallButton.setOnClickListener {
            // Finish activity to simulate ending the call
            finish()
        }

        // Mute button functionality
        val muteButton = findViewById<ImageView>(R.id.ivMute)
        muteButton.setOnClickListener {
            Toast.makeText(this, "Mute", Toast.LENGTH_SHORT).show()
        }

        // Speaker button functionality
        val speakerButton = findViewById<ImageView>(R.id.ivSpeaker)
        speakerButton.setOnClickListener {
            Toast.makeText(this, "Speaker", Toast.LENGTH_SHORT).show()
        }

        // Video button functionality (toggle video view or options)
        val videoButton = findViewById<ImageView>(R.id.ivVideo)
        videoButton.setOnClickListener {
            finish()

        }
    }
}