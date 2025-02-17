package com.example.connectme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class CallActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        // Get the user info from the intent passed from the DM page
        val userName = intent.getStringExtra("userName") ?: "Unknown"
        val profileImageResId = intent.getIntExtra("profileImage", R.drawable.asim)

        // Set the user info in the UI
        findViewById<TextView>(R.id.tvUserName).text = userName
        findViewById<ImageView>(R.id.ivProfilePicture).setImageResource(profileImageResId)

        val endCallButton = findViewById<ImageView>(R.id.ivEndCall)
        endCallButton.setOnClickListener {
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

        // Video button functionality
        val videoButton = findViewById<ImageView>(R.id.ivVideo)
        videoButton.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("userName", userName)  // Pass the user's name
            intent.putExtra("profileImage", profileImageResId)  // Pass the user's profile image
            startActivity(intent)
        }

    }
}
