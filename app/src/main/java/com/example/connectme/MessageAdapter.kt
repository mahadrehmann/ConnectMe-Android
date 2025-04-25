package com.example.connectme

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
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

    // View Types
    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    // ViewHolder for Sent Messages
    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        // ImageView for media message, if any
        val ivMessageImage: ImageView = itemView.findViewById(R.id.ivMessageImage)
    }

    // ViewHolder for Received Messages
    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val ivProfileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        val ivMessageImage: ImageView = itemView.findViewById(R.id.ivMessageImage)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSender) VIEW_TYPE_SENT else VIEW_TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_sent_message, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_received_message, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        val formattedTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(message.time)

        if (holder is SentMessageViewHolder) {
            holder.tvMessage.text = message.text ?: ""
            holder.tvTime.text = formattedTime

            // Setup media view: if message.imageUrl is not empty, decode it
            if (!message.imageUrl.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(message.imageUrl, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    holder.ivMessageImage.visibility = View.VISIBLE
                    holder.ivMessageImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    holder.ivMessageImage.visibility = View.GONE
                }
            } else {
                holder.ivMessageImage.visibility = View.GONE
            }

            // Set long-click listener for editing/deletion if within allowed time
            if (System.currentTimeMillis() - message.time <= 5 * 60 * 1000) {
                holder.itemView.setOnLongClickListener {
                    val options = arrayOf("Edit", "Delete")
                    val builder = android.app.AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("Message Options")
                        .setItems(options) { dialog, which ->
                            when (which) {
                                0 -> listener.onEditMessage(message)
                                1 -> listener.onDeleteMessage(message)
                            }
                        }.show()
                    true
                }
            } else {
                holder.itemView.setOnLongClickListener(null)
            }
        } else if (holder is ReceivedMessageViewHolder) {
            holder.tvMessage.text = message.text ?: ""
            holder.tvTime.text = formattedTime
            holder.ivProfileImage.setImageResource(profileImageResId)

            if (!message.imageUrl.isNullOrEmpty()) {
                try {
                    val imageBytes = Base64.decode(message.imageUrl, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    holder.ivMessageImage.visibility = View.VISIBLE
                    holder.ivMessageImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                    holder.ivMessageImage.visibility = View.GONE
                }
            } else {
                holder.ivMessageImage.visibility = View.GONE
            }
        }
    }
}
