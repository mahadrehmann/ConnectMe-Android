package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class FollowingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FollowersAdapter
    private lateinit var followersList: List<Follower>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)

        // Initialize followers data
        followersList = listOf(
            Follower("Ahmed Tahir", R.drawable.masab),
            Follower("Asim Iqbal", R.drawable.awan),
            Follower("Apple Inc.", R.drawable.apple_icon),
            // Add more followers here...
        )

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerViewFollowers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FollowersAdapter(followersList)
        recyclerView.adapter = adapter

        val followersLayout = findViewById<TextView>(R.id.tvFollowers)
        followersLayout.setOnClickListener {
            val intent = Intent(this, FollowersActivity::class.java)
            startActivity(intent)
        }

        val backButton = findViewById<ImageView>(R.id.ivBack)
        backButton.setOnClickListener {
            // Finish activity to simulate ending the call
            finish()
        }
    }
}