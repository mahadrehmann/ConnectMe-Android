package com.example.connectme

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val posts: List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPost: ImageView = itemView.findViewById(R.id.ivPostImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_post_grid, parent, false)
        )

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        post.postImage?.let { data ->
            try {
                val bytes = Base64.decode(data, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                holder.ivPost.setImageBitmap(bmp)
            } catch (_: Exception) { /* fall‑back if you want */ }
        }
    }
}
