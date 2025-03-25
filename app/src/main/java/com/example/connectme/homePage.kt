package com.example.connectme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ChildEventListener

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

        // Find the addStory ImageView and set a click listener
        val addStory = findViewById<ImageView>(R.id.addStory)
        addStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
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
                R.id.contacts -> {
                    val intent = Intent(this, ContactsActivity::class.java)
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

        val storiesLayout = findViewById<LinearLayout>(R.id.stories)
        val storiesRef = FirebaseDatabase.getInstance().getReference("Stories")

        storiesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val story = snapshot.getValue(Story::class.java)
                if (story != null) {
                    // Inflate a layout for the story
                    val storyView = layoutInflater.inflate(R.layout.item_story, storiesLayout, false)
                    val storyImageView = storyView.findViewById<ImageView>(R.id.storyImageView)
                    // Optionally, set profile picture and name overlays here

                    // Load the story image
                    storyImageView.setImageURI(Uri.parse(story.storyImage))
                    storiesLayout.addView(storyView)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }

            override fun onChildRemoved(snapshot: DataSnapshot) { }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }

            override fun onCancelled(error: DatabaseError) { }
        })



    }
}