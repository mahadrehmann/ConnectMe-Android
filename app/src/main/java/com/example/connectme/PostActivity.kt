package com.example.connectme

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream
import java.io.InputStream

class PostActivity : AppCompatActivity() {

    private lateinit var ivSelectedImage: ImageView
    private lateinit var etCaption: EditText
    private lateinit var btnShare: Button
    private lateinit var ivClosePost: ImageView

    private var selectedImageUri: Uri? = null
    private var userName: String = ""
    private var userProfilePic: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // Binding views
        ivSelectedImage = findViewById(R.id.ivSelectedImage)
        etCaption = findViewById(R.id.etCaption)
        btnShare = findViewById(R.id.btnShare)
        ivClosePost = findViewById(R.id.ivClosePost)

        // Get image URI from intent
        val imageUriStr = intent.getStringExtra("selectedImageUri")
        if (imageUriStr != null) {
            selectedImageUri = Uri.parse(imageUriStr)
            val bitmap = uriToBitmap(this, selectedImageUri!!)
            if (bitmap != null) {
                ivSelectedImage.setImageBitmap(bitmap)
            }
        }

        // Fetch user info from Firebase
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userName = snapshot.child("name").getValue(String::class.java) ?: ""
                    userProfilePic = snapshot.child("profilePic").getValue(String::class.java) ?: ""
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PostActivity, "Failed to load user info", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Share button logic
        btnShare.setOnClickListener {
            if (selectedImageUri == null) {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val caption = etCaption.text.toString().trim()
            if (caption.isEmpty()) {
                Toast.makeText(this, "Please enter a caption", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bitmap = uriToBitmap(this, selectedImageUri!!)
            if (bitmap != null) {
                val base64Image = bitmapToBase64(bitmap)

                val post = Post(
                    userName = userName,
                    userProfilePic = userProfilePic,
                    postImage = base64Image,
                    caption = caption,
                    timestamp = System.currentTimeMillis()
                )

                val postRef = FirebaseDatabase.getInstance().getReference("Posts").push()
                postRef.setValue(post)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Post shared successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to share post", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        // Close button
        ivClosePost.setOnClickListener {
            finish()
        }
    }

    // Helper to convert Uri to Bitmap
    private fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Helper to convert Bitmap to Base64 String
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
