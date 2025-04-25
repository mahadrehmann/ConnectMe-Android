package com.example.connectme

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class dmPage : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dmAdapter: DMAdapter
    private val dmList = mutableListOf<DM>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dm_page)

        recyclerView = findViewById(R.id.rvDMList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        dmAdapter = DMAdapter(dmList)
        recyclerView.adapter = dmAdapter

        fetchUsersFromFirebase()
    }

    private fun fetchUsersFromFirebase() {
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dmList.clear()
                for (userSnap in snapshot.children) {
                    val name = userSnap.child("name").getValue(String::class.java) ?: continue
                    val profilePic = userSnap.child("profilePic").getValue(String::class.java) ?: ""
                    dmList.add(DM(name, profilePic))
                }
                dmAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@dmPage, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
