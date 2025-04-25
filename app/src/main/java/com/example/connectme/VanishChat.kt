package com.example.connectme

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class VanishChat : AppCompatActivity(), MessageActionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatMessages: MutableList<Message>
    private var selectedMediaUri: Uri? = null

    // Register for media picker result
    private val mediaPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
        if (uri != null) {
            selectedMediaUri = uri
            val newMessage = Message(
                id = System.currentTimeMillis().toString(),
                text = null,
                imageUrl = uri.toString(),
                isSender = true,
                time = System.currentTimeMillis(),
                lastEdited = null,
                isRead = false
            )
            chatMessages.add(newMessage)
            messageAdapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(chatMessages.size - 1)
            Toast.makeText(this, "Image added to chat", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vanish_chat)

        val userName = intent.getStringExtra("userName") ?: "Unknown"
        val profileBase64 = intent.getStringExtra("profileImageBase64").orEmpty()

        findViewById<TextView>(R.id.tvUserName).text = userName

        val ivProfile = findViewById<ImageView>(R.id.ivProfilePicture)
        if (profileBase64.isNotEmpty()) {
            try {
                val decodedBytes = Base64.decode(profileBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                ivProfile.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                ivProfile.setImageResource(R.drawable.circle_background)
            }
        } else {
            ivProfile.setImageResource(R.drawable.circle_background)
        }

        chatMessages = mutableListOf(
            Message(
                id = "1",
                text = "This is Vanish Mode. Messages won't be saved!",
                imageUrl = null,
                isSender = false,
                time = System.currentTimeMillis(),
                lastEdited = null,
                isRead = false
            )
        )

        recyclerView = findViewById(R.id.rvChatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(chatMessages, R.drawable.circle_background, this)
        recyclerView.adapter = messageAdapter

        val attachButton = findViewById<ImageView>(R.id.ivSendMedia)
        attachButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            mediaPicker.launch(intent)
        }

        val sendButton = findViewById<ImageView>(R.id.ivSend)
        val messageInput = findViewById<EditText>(R.id.etMessage)
        sendButton.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (text.isNotEmpty()) {
                val newMessage = Message(
                    id = System.currentTimeMillis().toString(),
                    text = text,
                    imageUrl = null,
                    isSender = true,
                    time = System.currentTimeMillis(),
                    lastEdited = null,
                    isRead = false
                )
                chatMessages.add(newMessage)
                messageAdapter.notifyDataSetChanged()
                recyclerView.scrollToPosition(chatMessages.size - 1)
                messageInput.text.clear()
            } else {
                Toast.makeText(this, "Please enter a message or attach media", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish() // Messages are lost after closing chat
        }

        findViewById<ImageView>(R.id.ivCall).setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("profileImageBase64", profileBase64)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.ivVideoCall).setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("profileImageBase64", profileBase64)
            startActivity(intent)
        }
    }

    override fun onEditMessage(message: Message) {
        Toast.makeText(this, "Editing disabled in Vanish Mode", Toast.LENGTH_SHORT).show()
    }

    override fun onDeleteMessage(message: Message) {
        chatMessages.remove(message)
        messageAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show()
    }
}
