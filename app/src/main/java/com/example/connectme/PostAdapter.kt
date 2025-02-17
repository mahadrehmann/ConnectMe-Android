package com.example.connectme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val posts: List<Int>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.postImage.setImageResource(post) // Replace with image loading for dynamic content
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.ivPostImage)
    }
}
