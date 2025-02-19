package com.example.connectme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageView
import android.widget.TextView
import android.widget.EditText

class VanishChat : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatMessages: MutableList<Message>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vanish_chat)

        // Get the intent data (User info passed from DM page)
        val userName = intent.getStringExtra("userName")
        val profileImageResId = intent.getIntExtra("profileImage", R.drawable.asim)

        // Set the user info in the UI
        findViewById<TextView>(R.id.tvUserName).text = userName
        findViewById<ImageView>(R.id.ivProfilePicture).setImageResource(profileImageResId)

        // Initialize chat messages with preloaded messages
        chatMessages = mutableListOf(
            Message("Hello, How are you?", isSender = false, time = "10:30"),
            Message("Hi, I am great, Wbu?", isSender = true, time = "10:32"),
            Message("I am doing well", isSender = false, time = "01:30"),
            Message("Good to know", isSender = true, time = "01:30")
        )

        // RecyclerView setup for chat messages
        recyclerView = findViewById(R.id.rvChatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(chatMessages, profileImageResId)
        recyclerView.adapter = messageAdapter

        val sendButton = findViewById<ImageView>(R.id.ivSend)
        val messageInput = findViewById<EditText>(R.id.etMessage)

        sendButton.setOnClickListener {
            val message = messageInput.text.toString()
            if (message.isNotEmpty()) {
                // Add the sent message to the chat list
                chatMessages.add(Message(message, isSender = true, time = "Now"))
                messageAdapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(chatMessages.size - 1)
                messageInput.text.clear()
            }
        }

        // Back button to close chat
        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.ivCall).setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("userName", userName)  // Pass the user's name
            intent.putExtra("profileImage", profileImageResId)  // Pass the user's profile image
            startActivity(intent)
        }

        val videoButton = findViewById<ImageView>(R.id.ivVideoCall)
        videoButton.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("userName", userName)  // Pass the user's name
            intent.putExtra("profileImage", profileImageResId)  // Pass the user's profile image
            startActivity(intent)
        }

    }
}
