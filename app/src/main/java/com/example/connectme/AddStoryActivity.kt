package com.example.connectme

// Inside AddStoryActivity.kt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileOutputStream

class AddStoryActivity : AppCompatActivity() {

    private var selectedStoryUri: Uri? = null

    // Helper function to copy URI content to a temporary file
    private fun copyUriToTempFile(uri: Uri): Uri? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val tempFile = File(cacheDir, "temp_story_${System.currentTimeMillis()}.jpg")
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
        setContentView(R.layout.activity_add_story) // Create this layout as needed

        val imageView = findViewById<ImageView>(R.id.storyImageView)
        val captionInput = findViewById<EditText>(R.id.captionInput) // optional
        val uploadButton = findViewById<Button>(R.id.uploadStoryButton)

        // Set up image picker for story image
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

        uploadButton.setOnClickListener {
            if (selectedStoryUri == null) {
                Toast.makeText(this, "Please select a story image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Copy the file to a temporary file for better accessibility
            val tempUri = copyUriToTempFile(selectedStoryUri!!)
            if (tempUri == null) {
                Log.e("AddStoryActivity", "------||--------Failed to create temporary file from selected URI: $selectedStoryUri")
                Toast.makeText(this, "Failed to process the selected image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                Log.d("AddStoryActivity", "------||--------Temporary file created at: ${tempUri.path}")
            }

            val tempFile = File(tempUri.path!!)
            Log.d("AddStoryActivity", "-----------||--------Temporary file exists: ${tempFile.exists()}, size: ${tempFile.length()} bytes, path: ${tempFile.absolutePath}")

            val storageRef = FirebaseStorage.getInstance().reference
            val storyRef = storageRef.child("stories/${System.currentTimeMillis()}.jpg")

            storyRef.putFile(tempUri).addOnSuccessListener { taskSnapshot ->
                storyRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    // Create a Story object
                    val story = Story(
                        userName = "Mahad Rehman", // Replace with the current user's name
                        userProfilePic = "img", // Replace with current user's profile picture URL
                        storyImage = downloadUrl.toString(),
                        timestamp = System.currentTimeMillis()
                    )

                    // Save the story in Realtime Database under "Stories"
                    val dbRef = FirebaseDatabase.getInstance().getReference("Stories")
                    val key = dbRef.push().key
                    if (key != null) {
                        dbRef.child(key).setValue(story).addOnSuccessListener {
                            Toast.makeText(this, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                            finish() // Close the activity after upload
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save story: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Image upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddStoryActivity", "Image upload failed", e)
            }
        }
    }
}
