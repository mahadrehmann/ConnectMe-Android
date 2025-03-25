package com.example.connectme

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class MessageAdapter(
    private val messages: MutableList<Message>,
    private val profileImageResId: Int,
    private val listener: MessageActionListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Assume you have two view types: Sent and Received.
    // For brevity, I’ll define two simple ViewHolders:

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSender) 1 else 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sent_message, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_received_message, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        // Format the time (you can adjust the format as needed)
        val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.time)

        if (holder is SentMessageViewHolder) {
            holder.tvMessage.text = message.text ?: ""
            holder.tvTime.text = formattedTime

            // Allow editing and deletion if within 5 minutes
            if (System.currentTimeMillis() - message.time <= 5 * 60 * 1000) {
                holder.itemView.setOnLongClickListener {
                    // Show options for editing or deleting
                    val options = arrayOf("Edit", "Delete")
                    val builder = android.app.AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Message Options")
                        .setItems(options) { dialog, which ->
                            when (which) {
                                0 -> listener.onEditMessage(message)
                                1 -> listener.onDeleteMessage(message)
                            }
                        }
                        .show()
                    true
                }
            }
        } else if (holder is ReceivedMessageViewHolder) {
            holder.tvMessage.text = message.text ?: ""
            holder.tvTime.text = formattedTime
            holder.ivProfileImage.setImageResource(profileImageResId)
        }
    }
}
