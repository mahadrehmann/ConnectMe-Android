package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var postAdapter: PostAdapter
    private lateinit var postList: List<Int> // Replace Int with image resource ID type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set profile info
        findViewById<TextView>(R.id.tvUserName).text = "Mahad Rehman"
        findViewById<TextView>(R.id.tvBio).text = "two bananas for a pound, three bananas for a euro"

        // Post list (replace these with actual images from resources or URLs)
        postList = listOf(
            R.drawable.grass, R.drawable.islamia, R.drawable.awan,
            R.drawable.highrise, R.drawable.masab, R.drawable.mahad
        )

        // Set up RecyclerView for the post grid
        val recyclerView = findViewById<RecyclerView>(R.id.rvPosts)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        postAdapter = PostAdapter(postList)
        recyclerView.adapter = postAdapter

        // Edit profile button (optional action)
        findViewById<ImageView>(R.id.ivEditProfile).setOnClickListener {
            // Handle profile edit action
        }

        findViewById<BottomNavigationView>(R.id.bottomNavigation).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    val intent = Intent(this, homePage::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        val followersLayout = findViewById<LinearLayout>(R.id.followers)
        followersLayout.setOnClickListener {
            val intent = Intent(this@ProfileActivity, FollowersActivity::class.java)
            startActivity(intent)
        }
        val followingLayout = findViewById<LinearLayout>(R.id.following)
        followingLayout.setOnClickListener {
            val intent = Intent(this@ProfileActivity, FollowingActivity::class.java)
            startActivity(intent)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Find the Add item in the BottomNavigationView
        val addItemView = bottomNavigationView.findViewById<View>(R.id.add)

        // Set the background for the Add button
        addItemView.background = ContextCompat.getDrawable(this, R.drawable.brown_circle_background)

        // Add padding to prevent the circle from looking too large
        addItemView.setPadding(10, 10, 10, 10) // Adjust padding as needed


        val editProfileButton = findViewById<ImageView>(R.id.ivEditProfile)
        editProfileButton.setOnClickListener {
            val intent = Intent(this@ProfileActivity, EditProfileActivity::class.java)
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
                R.id.home -> {
                    val intent = Intent(this, homePage::class.java)
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

    }
}
