package com.example.connectme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class FollowersAdapter(private val followersList: List<Follower>) :
    RecyclerView.Adapter<FollowersAdapter.FollowersViewHolder>() {

    // ViewHolder to hold the views for each Follower item
    class FollowersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
//        val ivChat: ImageView = itemView.findViewById(R.id.ivChat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowersViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dm_item_layout, parent, false)
        return FollowersViewHolder(view)
    }

    override fun onBindViewHolder(holder: FollowersViewHolder, position: Int) {
        val follower = followersList[position]
        holder.tvName.text = follower.name
        holder.ivProfileImage.setImageResource(follower.profileImageResId)

    }

    override fun getItemCount(): Int {
        return followersList.size
    }
}
