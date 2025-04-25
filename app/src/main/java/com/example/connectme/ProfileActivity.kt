package com.example.connectme

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.Base64

object Utilities {
    fun base64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvUserName: TextView
    private lateinit var tvBio: TextView
    private lateinit var tvFollowers: TextView
    private lateinit var tvFollowing: TextView
    private lateinit var ivProfilePicture: ImageView
    private lateinit var rvPosts: RecyclerView

    private lateinit var postAdapter: PostAdapter
    private val postList = mutableListOf<Post>()

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvUserName = findViewById(R.id.tvUserName)
        tvBio = findViewById(R.id.tvBio)
        tvFollowers = findViewById(R.id.tvFollowers)
        tvFollowing = findViewById(R.id.tvFollowing)
        ivProfilePicture = findViewById(R.id.ivProfilePicture)
        rvPosts = findViewById(R.id.rvPosts)

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""
        database = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        fetchUserProfile()
        fetchUserPosts()

        rvPosts.layoutManager = GridLayoutManager(this, 3)
        postAdapter = PostAdapter(postList)
        rvPosts.adapter = postAdapter

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> startActivity(Intent(this, homePage::class.java))
                R.id.search -> startActivity(Intent(this, SearchActivity::class.java))
                R.id.add -> startActivity(Intent(this, UploadActivity::class.java))
                R.id.contacts -> startActivity(Intent(this, ContactsActivity::class.java))
            }
            true
        }

        findViewById<ImageView>(R.id.ivEditProfile).setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.followers).setOnClickListener {
            startActivity(Intent(this, FollowersActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.following).setOnClickListener {
            startActivity(Intent(this, FollowingActivity::class.java))
        }
    }

    private fun fetchUserProfile() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvUserName.text = snapshot.child("name").getValue(String::class.java) ?: "Unknown"
                tvBio.text = snapshot.child("bio").getValue(String::class.java) ?: ""
                tvFollowers.text = snapshot.child("followers").childrenCount.toString()
                tvFollowing.text = snapshot.child("following").childrenCount.toString()

                val base64ProfilePic = snapshot.child("profilePic").getValue(String::class.java)
                if (!base64ProfilePic.isNullOrEmpty()) {
                    val bitmap = Utilities.base64ToBitmap(base64ProfilePic)
                    if (bitmap != null) {
                        ivProfilePicture.setImageBitmap(bitmap)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProfileActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchUserPosts() {
        val postsRef = FirebaseDatabase.getInstance().getReference("Posts")
        postsRef.orderByChild("userId").equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    postList.clear()
                    for (postSnap in snapshot.children) {
                        val post = postSnap.getValue(Post::class.java)
                        post?.let { postList.add(it) }
                    }
                    postAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ProfileActivity, "Failed to load posts", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
