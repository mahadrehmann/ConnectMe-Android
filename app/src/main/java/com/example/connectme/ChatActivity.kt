package com.example.connectme

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

class ChatActivity : AppCompatActivity(), MessageActionListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var chatMessages: MutableList<Message>
    private val chatDbRef = FirebaseDatabase.getInstance().getReference("Chats")
        .child("ChatID_12345").child("messages")
    // For media sharing:
    private var selectedMediaUri: Uri? = null
    // Register for media picker result
    private val mediaPicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val uri = result.data?.data
        if (uri != null) {
            selectedMediaUri = uri
            Toast.makeText(this, "Media attached", Toast.LENGTH_SHORT).show()
            // Optionally, you can display a thumbnail or update UI here.
        }
    }

    // Helper function to copy URI content to a temporary file
    private fun copyUriToTempFile(uri: Uri): Uri? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(cacheDir, "temp_media_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            inputStream.close()
            Uri.fromFile(tempFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        val userName = intent.getStringExtra("userName")
        val profileImageResId = intent.getIntExtra("profileImage", R.drawable.asim)

        findViewById<TextView>(R.id.tvUserName).text = userName
        findViewById<ImageView>(R.id.ivProfilePicture).setImageResource(profileImageResId)

        chatMessages = mutableListOf()
        recyclerView = findViewById(R.id.rvChatMessages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageAdapter = MessageAdapter(chatMessages, profileImageResId, this)
        recyclerView.adapter = messageAdapter

        // Attach Firebase listener to load messages
        chatDbRef.addChildEventListener(object : com.google.firebase.database.ChildEventListener {
            override fun onChildAdded(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    chatMessages.add(message)
                    messageAdapter.notifyDataSetChanged()
                }
            }
            override fun onChildChanged(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) { }
            override fun onChildRemoved(snapshot: com.google.firebase.database.DataSnapshot) { }
            override fun onChildMoved(snapshot: com.google.firebase.database.DataSnapshot, previousChildName: String?) { }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) { }
        })

        findViewById<ImageView>(R.id.ivBack).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.ivCall).setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("userName", userName)  // Pass the user's name
            intent.putExtra("profileImage", profileImageResId)  // Pass the user's profile image
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.ivCall).setOnClickListener {
            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("profileImage", profileImageResId)
            startActivity(intent)
        }

        val videoButton = findViewById<ImageView>(R.id.ivVideoCall)
        videoButton.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("profileImage", profileImageResId)
            startActivity(intent)
        }

        // Vanish mode via profile picture click (as before)
        val vanish = findViewById<ImageView>(R.id.ivProfilePicture)
        vanish.setOnClickListener {
            val intent = Intent(this, VanishChat::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("profileImage", profileImageResId)
            startActivity(intent)
        }

        // New: Attach button for media sharing (ensure your layout has an ImageView with id ivAttach)
        val attachButton = findViewById<ImageView>(R.id.ivSendMedia)
        attachButton?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            mediaPicker.launch(intent)
        }

        val sendButton = findViewById<ImageView>(R.id.ivSend)
        val messageInput = findViewById<EditText>(R.id.etMessage)
        sendButton.setOnClickListener {
            val text = messageInput.text.toString().trim()
            // If media is attached, send media message
            if (selectedMediaUri != null) {
                val tempUri = copyUriToTempFile(selectedMediaUri!!)
                if (tempUri == null) {
                    Toast.makeText(this, "Failed to process the selected media", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val storageRef = FirebaseStorage.getInstance().reference
                val mediaRef = storageRef.child("chat_media/${System.currentTimeMillis()}.jpg")
                mediaRef.putFile(tempUri).addOnSuccessListener { taskSnapshot ->
                    mediaRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                        val newMessage = Message(
                            id = "",
                            text = if (text.isNotEmpty()) text else null,  // Optional caption
                            imageUrl = downloadUrl.toString(),
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
                                    selectedMediaUri = null // Clear media attachment after sending
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to send media message: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to get download URL: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Media upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else if (text.isNotEmpty()) {
                // Send a text message if there's no media attached
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

    // Check if the message is within the 5-minute edit/delete window
    private fun canEditOrDelete(message: Message): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - message.time
        return timeDifference <= 5 * 60 * 1000  // 5 minutes in milliseconds
    }

    override fun onEditMessage(message: Message) {
        if (canEditOrDelete(message)) {
            // Show a dialog with an EditText pre-filled with the message text
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Edit Message")

            val input = EditText(this)
            input.setText(message.text)
            builder.setView(input)

            builder.setPositiveButton("Save") { dialog, _ ->
                val newText = input.text.toString().trim()
                if (newText.isNotEmpty()) {
                    // Update the message in Firebase
                    chatDbRef.child(message.id).child("text").setValue(newText)
                    chatDbRef.child(message.id).child("lastEdited").setValue(System.currentTimeMillis())

                    // Update the local message object and UI
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
