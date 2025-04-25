package com.example.connectme

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var adapter: SearchAdapter
    private val allUsers = mutableListOf<DM>()  // Full list of users
    private val filteredUsers = mutableListOf<DM>()  // Filtered list shown

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.searchList)
        etSearch = findViewById(R.id.etSearch)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SearchAdapter(filteredUsers)
        recyclerView.adapter = adapter

        fetchUsersFromFirebase()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterUsers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchUsersFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("Users")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allUsers.clear()
                for (userSnapshot in snapshot.children) {
                    val name = userSnapshot.child("name").getValue(String::class.java) ?: continue
                    val profilePic = userSnapshot.child("profilePic").getValue(String::class.java) ?: ""
                    allUsers.add(DM(name, profilePic))
                }
                filteredUsers.clear()
                filteredUsers.addAll(allUsers)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun filterUsers(query: String) {
        val lowerQuery = query.lowercase()
        val filtered = allUsers.filter {
            it.name.lowercase().contains(lowerQuery)
        }
        filteredUsers.clear()
        filteredUsers.addAll(filtered)
        adapter.notifyDataSetChanged()
    }
}
