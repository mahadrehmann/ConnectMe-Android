package com.example.connectme

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class PostsAdapter(
    private val posts: MutableList<Post>
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProfilePic: ImageView = itemView.findViewById(R.id.ivProfilePic)
        val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        val tvCaption: TextView = itemView.findViewById(R.id.tvCaption)
        val ivPostImage: ImageView = itemView.findViewById(R.id.ivPostImage)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.tvUserName.text = post.userName
        holder.tvCaption.text = post.caption
        val formattedTime = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(post.timestamp)
        holder.tvTimestamp.text = formattedTime

        // Decode and display profile picture
        if (!post.userProfilePic.isNullOrEmpty()) {
            try {
                val profileBytes = Base64.decode(post.userProfilePic, Base64.DEFAULT)
                val profileBitmap = BitmapFactory.decodeByteArray(profileBytes, 0, profileBytes.size)
                holder.ivProfilePic.setImageBitmap(profileBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                // set fallback image if needed
            }
        }

        // Check if the post contains an image
        if (!post.postImage.isNullOrEmpty()) {
            try {
                val postBytes = Base64.decode(post.postImage, Base64.DEFAULT)
                val postBitmap = BitmapFactory.decodeByteArray(postBytes, 0, postBytes.size)
                holder.ivPostImage.visibility = View.VISIBLE
                holder.ivPostImage.setImageBitmap(postBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                holder.ivPostImage.visibility = View.GONE
            }
        } else {
            holder.ivPostImage.visibility = View.GONE
        }
    }
}
