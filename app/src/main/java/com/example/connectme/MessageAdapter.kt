package com.example.connectme

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class MessageAdapter(private val messages: List<Message>, private val profileImageResId: Int) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // ViewHolder for sent messages
    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    // ViewHolder for received messages
    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sent_message, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_received_message, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is SentMessageViewHolder) {
            holder.tvMessage.text = message.text
            holder.tvTime.text = message.time
        } else if (holder is ReceivedMessageViewHolder) {
            holder.tvMessage.text = message.text
            holder.tvTime.text = message.time
            holder.ivProfileImage.setImageResource(profileImageResId)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSender) 1 else 0
    }
}
