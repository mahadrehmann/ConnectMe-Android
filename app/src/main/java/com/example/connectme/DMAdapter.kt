package com.example.connectme

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DMAdapter(private val dmList: List<DM>) : RecyclerView.Adapter<DMAdapter.DMViewHolder>() {

    class DMViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DMViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dm_item_layout, parent, false)
        return DMViewHolder(view)
    }

    override fun onBindViewHolder(holder: DMViewHolder, position: Int) {
        val dm = dmList[position]
        holder.tvName.text = dm.name

        // Decode Base64 profilePic and display
        if (dm.profilePicBase64.isNotEmpty()) {
            try {
                val decodedBytes = Base64.decode(dm.profilePicBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                holder.ivProfileImage.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // On click
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userName", dm.name)
            intent.putExtra("profileImageBase64", dm.profilePicBase64)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = dmList.size
}
