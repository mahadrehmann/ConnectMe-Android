package com.example.connectme

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import java.io.ByteArrayOutputStream
import java.io.InputStream

class AddStoryActivity : AppCompatActivity() {

    private var selectedStoryUri: Uri? = null
    private var userName: String = ""
    private var userProfilePic: String = ""

    // Helper: Convert URI to Bitmap
    fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
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

    // Helper: Convert Bitmap to Base64
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_story)

        val imageView = findViewById<ImageView>(R.id.storyImageView)
        val captionInput = findViewById<EditText>(R.id.captionInput) // Optional
        val uploadButton = findViewById<Button>(R.id.uploadStoryButton)

        // Set up Firebase Auth and DB
        val auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userName = snapshot.child("name").getValue(String::class.java) ?: ""
                    userProfilePic = snapshot.child("profilePic").getValue(String::class.java) ?: ""
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@AddStoryActivity, "Failed to load user info", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Image Picker
        val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val uri = activityResult.data?.data
            if (uri != null) {
                imageView.setImageURI(uri)
                selectedStoryUri = uri
                Log.d("AddStoryActivity", "Selected story URI: $uri")
            }
        }

        imageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            result.launch(intent)
        }

        // Upload Button Click
        uploadButton.setOnClickListener {
            if (selectedStoryUri == null) {
                Toast.makeText(this, "Please select a story image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bitmap = uriToBitmap(this, selectedStoryUri!!)
            if (bitmap == null) {
                Toast.makeText(this, "Failed to process the selected image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val base64Image = bitmapToBase64(bitmap)
            Log.d("AddStoryActivity", "Base64 String Length: ${base64Image.length}")

            val story = Story(
                userName = userName,
                userProfilePic = userProfilePic,
                storyImage = base64Image,
                timestamp = System.currentTimeMillis()
            )

            val dbRef = FirebaseDatabase.getInstance().getReference("Stories")
            val key = dbRef.push().key
            if (key != null) {
                dbRef.child(key).setValue(story).addOnSuccessListener {
                    Toast.makeText(this, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }.addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save story: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
