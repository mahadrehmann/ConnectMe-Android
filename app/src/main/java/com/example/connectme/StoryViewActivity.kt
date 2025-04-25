package com.example.connectme

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StoryViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_story_view)

        val storyImageBase64 = intent.getStringExtra("storyImage")
        val userProfilePicBase64 = intent.getStringExtra("userProfilePic")
        val userName = intent.getStringExtra("userName")

        val ivStoryImage = findViewById<ImageView>(R.id.ivStoryImage)
        val ivUserProfile = findViewById<ImageView>(R.id.ivUserProfile)
        val tvUserName = findViewById<TextView>(R.id.tvUserName)

        tvUserName.text = userName

        // Decode and display Story Image
        if (!storyImageBase64.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(storyImageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ivStoryImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Decode and display Profile Picture
        if (!userProfilePicBase64.isNullOrEmpty() && userProfilePicBase64 != "img") {
            try {
                val profileBytes = Base64.decode(userProfilePicBase64, Base64.DEFAULT)
                val profileBitmap = BitmapFactory.decodeByteArray(profileBytes, 0, profileBytes.size)
                ivUserProfile.setImageBitmap(profileBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
