package com.example.connectme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Activity for the DM Page
class dmPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dm_page)

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rvDMList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Sample data for DM list
        val dmList = listOf(
            DM("Hashim Awan", R.drawable.awan),
            DM("Asim Iqbal", R.drawable.asim),
            DM("Masab Hammad", R.drawable.masab),
            DM("Ahmed Tahir", R.drawable.ahmed),
            DM("Islamia College", R.drawable.islamia)
            // Add more DMs here...
        )

        // Set up the adapter with the DM list
        val adapter = DMAdapter(dmList)
        recyclerView.adapter = adapter

    }
}
