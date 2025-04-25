package com.example.connectme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener

class homePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)

        // Navigate to DM page when the profile icon is clicked
        val dm: ImageView = findViewById(R.id.ivEditProfile)
        dm.setOnClickListener {
            val intent = Intent(this, dmPage::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Open AddStoryActivity when the addStory button is clicked
        val addStory = findViewById<ImageView>(R.id.addStory)
        addStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        // Bottom navigation listeners
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

        // e.g. in your Application.onCreate() or MainActivity.onCreate()
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userStatusRef = FirebaseDatabase.getInstance()
            .getReference("Users/$uid/online")
        val lastSeenRef = FirebaseDatabase.getInstance()
            .getReference("Users/$uid/lastSeen")

        // Listen to connection state
        FirebaseDatabase.getInstance()
            .getReference(".info/connected")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(s: DataSnapshot) {
                    val connected = s.getValue(Boolean::class.java) ?: false
                    if (connected) {
                        // Mark online
                        userStatusRef.setValue(true)
                        // When this client disconnects, set online=false and update lastSeen
                        userStatusRef.onDisconnect().setValue(false)
                        lastSeenRef.onDisconnect().setValue(ServerValue.TIMESTAMP)
                    }
                }
                override fun onCancelled(e: DatabaseError) { /* … */ }
            })


        // Styling the bottom navigation add button
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val addItemView = bottomNavigationView.findViewById<View>(R.id.add)
        addItemView.background = ContextCompat.getDrawable(this, R.drawable.brown_circle_background)
        addItemView.setPadding(10, 10, 10, 10) // Adjust padding as needed

        // =============================
        // Load Stories from Firebase
        // =============================
        val storiesLayout = findViewById<LinearLayout>(R.id.stories)
        val storiesRef = FirebaseDatabase.getInstance().getReference("Stories")
        storiesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val story = snapshot.getValue(Story::class.java)
                if (story != null) {
                    // Inflate the item_story layout which now only contains a circular profile image.
                    val storyView = layoutInflater.inflate(R.layout.item_story, storiesLayout, false)
                    val profileOverlay = storyView.findViewById<ImageView>(R.id.ivProfileOverlay)

                    // Decode and display the user's profile picture (Base64 string)
                    try {
                        if (!story.userProfilePic.isNullOrEmpty()) {
                            val profileBytes = Base64.decode(story.userProfilePic, Base64.DEFAULT)
                            val profileBitmap = BitmapFactory.decodeByteArray(profileBytes, 0, profileBytes.size)
                            profileOverlay.setImageBitmap(profileBitmap)
                        } else {
                            profileOverlay.setImageResource(R.drawable.circle_background) // Fallback image
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        profileOverlay.setImageResource(R.drawable.circle_background) // Fallback image
                    }

                    // Handle story click: open StoryViewActivity and pass necessary extras
                    storyView.setOnClickListener {
                        val intent = Intent(this@homePage, StoryViewActivity::class.java)
                        intent.putExtra("storyImage", story.storyImage)
                        intent.putExtra("userProfilePic", story.userProfilePic)
                        intent.putExtra("userName", story.userName)
                        startActivity(intent)
                    }
                    storiesLayout.addView(storyView)
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }
            override fun onChildRemoved(snapshot: DataSnapshot) { }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }
            override fun onCancelled(error: DatabaseError) { }
        })

        // =============================
        // Load Posts from Firebase
        // =============================
        val postsRef = FirebaseDatabase.getInstance().getReference("Posts")
        val rvPosts = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rvPosts)
        val postsList = mutableListOf<Post>()
        // PostsAdapter is assumed to be defined in a separate file (PostsAdapter.kt)
        val postsAdapter = PostsAdapter(postsList)
        rvPosts.layoutManager = LinearLayoutManager(this)
        rvPosts.adapter = postsAdapter

        postsRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val post = snapshot.getValue(Post::class.java)
                if (post != null) {
                    postsList.add(post)
                    postsAdapter.notifyDataSetChanged()
                }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                postsAdapter.notifyDataSetChanged()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                postsAdapter.notifyDataSetChanged()
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                postsAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) { }
        })

    }
}
