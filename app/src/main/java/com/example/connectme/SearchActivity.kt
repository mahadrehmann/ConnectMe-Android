package com.example.connectme

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.searchList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Sample data for DM list
        val dmList = listOf(
            DM("Hashim Awan", R.drawable.awan),
            DM("Asim Iqbal", R.drawable.asim),
            DM("Masab Hammad", R.drawable.masab),
            // Add more DMs here...
        )

        // Set up the adapter with the DM list
        val adapter = SearchAdapter(dmList)
        recyclerView.adapter = adapter

    }
}