package com.example.connectme

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.ByteArrayOutputStream

class ChatActivity : AppCompatActivity(), MessageActionListener {

    private lateinit var tvStatus: TextView
    private lateinit var otherUserId: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatMessages: MutableList<Message>

    private val chatDbRef = FirebaseDatabase.getInstance().getReference("Chats")
        .child("ChatID_12345").child("messages")

    private var selectedMediaUri: Uri? = null

    private val mediaPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
        if (uri != null) {
            selectedMediaUri = uri
            Toast.makeText(this, "Media attached", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uriToBitmap(context: android.content.Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        // Pull from Intent
        val userName       = intent.getStringExtra("userName") ?: "Unknown"
        val profileBase64  = intent.getStringExtra("profileImageBase64").orEmpty()
        otherUserId        = intent.getStringExtra("userId") ?: ""        // new

        // Wire up views
        tvStatus          = findViewById(R.id.tvStatus)
        findViewById<TextView>(R.id.tvUserName).text = userName
        // … load profile pic …

        // 2) PRESENCE LISTENER
        if (otherUserId.isNotBlank()) {
            val statusRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(otherUserId)
                .child("online")
            statusRef.addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isOnline = snapshot.getValue(Boolean::class.java) ?: false
                    tvStatus.text = if (isOnline) "Online" else "Offline"
                }
                override fun onCancelled(error: DatabaseError) { /* no‑op */ }
            })
        }

//        val userName = intent.getStringExtra("userName") ?: "Unknown"
//        val profileBase64 = intent.getStringExtra("profileImageBase64").orEmpty()

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

        chatMessages = mutableListOf()
        recyclerView = findViewById(R.id.rvChatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(chatMessages, R.drawable.circle_background, this)
        recyclerView.adapter = messageAdapter

        chatDbRef.addChildEventListener(object : com.google.firebase.database.ChildEventListener {
            override fun onChildAdded(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    chatMessages.add(message)
                    messageAdapter.notifyDataSetChanged()
                }
            }
            override fun onChildChanged(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: com.google.firebase.database.DataSnapshot) {}
            override fun onChildMoved(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
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

        ivProfile.setOnClickListener {
            val intent = Intent(this, VanishChat::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("profileImageBase64", profileBase64)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.ivSendMedia)?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            mediaPicker.launch(intent)
        }

        val sendButton = findViewById<ImageView>(R.id.ivSend)
        val messageInput = findViewById<EditText>(R.id.etMessage)
        sendButton.setOnClickListener {
            val text = messageInput.text.toString().trim()
            if (selectedMediaUri != null) {
                val bitmap = uriToBitmap(this, selectedMediaUri!!)
                if (bitmap == null) {
                    Toast.makeText(this, "Failed to process the selected media", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val base64Image = bitmapToBase64(bitmap)
                val newMessage = Message(
                    id = "",
                    text = if (text.isNotEmpty()) text else null,
                    imageUrl = base64Image,
                    isSender = true,
                    time = System.currentTimeMillis(),
                    lastEdited = null,
                    isRead = false
                )
                val messageKey = chatDbRef.push().key
                if (messageKey != null) {
                    newMessage.id = messageKey
                    chatDbRef.child(messageKey).setValue(newMessage)
                        .addOnSuccessListener {
                            chatMessages.add(newMessage)
                            messageAdapter.notifyDataSetChanged()
                            recyclerView.scrollToPosition(chatMessages.size - 1)
                            messageInput.text.clear()
                            selectedMediaUri = null
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to send media message: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else if (text.isNotEmpty()) {
                val newMessage = Message(
                    id = "",
                    text = text,
                    imageUrl = null,
                    isSender = true,
                    time = System.currentTimeMillis(),
                    lastEdited = null,
                    isRead = false
                )
                val messageKey = chatDbRef.push().key
                if (messageKey != null) {
                    newMessage.id = messageKey
                    chatDbRef.child(messageKey).setValue(newMessage)
                        .addOnSuccessListener {
                            chatMessages.add(newMessage)
                            messageAdapter.notifyDataSetChanged()
                            recyclerView.scrollToPosition(chatMessages.size - 1)
                            messageInput.text.clear()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            } else {
                Toast.makeText(this, "Please enter a message or attach media", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun canEditOrDelete(message: Message): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - message.time
        return timeDifference <= 5 * 60 * 1000
    }

    override fun onEditMessage(message: Message) {
        if (canEditOrDelete(message)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Edit Message")
            val input = EditText(this)
            input.setText(message.text)
            builder.setView(input)
            builder.setPositiveButton("Save") { dialog, _ ->
                val newText = input.text.toString().trim()
                if (newText.isNotEmpty()) {
                    chatDbRef.child(message.id).child("text").setValue(newText)
                    chatDbRef.child(message.id).child("lastEdited").setValue(System.currentTimeMillis())
                    message.text = newText
                    message.lastEdited = System.currentTimeMillis()
                    messageAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Message updated", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            builder.show()
        } else {
            Toast.makeText(this, "You can only edit messages within 5 minutes", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeleteMessage(message: Message) {
        if (canEditOrDelete(message)) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Delete Message")
            builder.setMessage("Are you sure you want to delete this message?")
            builder.setPositiveButton("Yes") { dialog, _ ->
                chatDbRef.child(message.id).removeValue().addOnSuccessListener {
                    chatMessages.remove(message)
                    messageAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to delete message: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        } else {
            Toast.makeText(this, "You can only delete messages within 5 minutes", Toast.LENGTH_SHORT).show()
        }
    }
}
