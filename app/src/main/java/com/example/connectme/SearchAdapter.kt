package com.example.connectme

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


// Adapter class for the DM list
class SearchAdapter(private val dmList: List<DM>) : RecyclerView.Adapter<SearchAdapter.DMViewHolder>() {

    // ViewHolder to hold the views for each DM item
    class DMViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }

    // Inflates the item layout (dm_item_layout.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DMViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_item_layout, parent, false)
        return DMViewHolder(view)
    }

    // Binds the DM data to the item views
    override fun onBindViewHolder(holder: DMViewHolder, position: Int) {
        val dm = dmList[position]
        holder.tvName.text = dm.name

        // Handle click on DM item
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userName", dm.name)
            context.startActivity(intent)
        }

    }

    // Returns the total number of DMs in the list
    override fun getItemCount(): Int {
        return dmList.size
    }
}
