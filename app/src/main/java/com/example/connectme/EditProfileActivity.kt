package com.example.connectme

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.ByteArrayOutputStream
import java.io.InputStream

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etContactNumber: EditText
    private lateinit var etBio: EditText
    private lateinit var ivProfileImage: ImageView
    private lateinit var tvDone: TextView
    private lateinit var tvUserName: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private lateinit var userId: String

    private var selectedImageUri: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                ivProfileImage.setImageURI(uri)

                val bitmap = uriToBitmap(uri)
                if (bitmap != null) {
                    val base64Image = bitmapToBase64(bitmap)

                    userRef.child("profilePic").setValue(base64Image)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to update picture", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // View initialization
        etName = findViewById(R.id.etName)
        etUsername = findViewById(R.id.etUsername)
        etContactNumber = findViewById(R.id.etContactNumber)
        etBio = findViewById(R.id.etBio)
        ivProfileImage = findViewById(R.id.ivProfileImage)
        tvDone = findViewById(R.id.tvDone)
        tvUserName = findViewById(R.id.tvUserName)

        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        loadUserData()

        // ✅ Select Profile Image
        ivProfileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            galleryLauncher.launch(intent)
        }

        // ✅ Save Button Logic
        tvDone.setOnClickListener {
            val updatedData = mapOf(
                "name" to etName.text.toString(),
                "username" to etUsername.text.toString(),
                "contact" to etContactNumber.text.toString(),
                "bio" to etBio.text.toString()
            )

            userRef.updateChildren(updatedData).addOnSuccessListener {
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 🔁 Load profile data into views
    private fun loadUserData() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                val username = snapshot.child("username").getValue(String::class.java) ?: ""
                val contact = snapshot.child("contact").getValue(String::class.java) ?: ""
                val bio = snapshot.child("bio").getValue(String::class.java) ?: ""
                val profilePicBase64 = snapshot.child("profilePic").getValue(String::class.java)

                etName.setText(name)
                etUsername.setText(username)
                etContactNumber.setText(contact)
                etBio.setText(bio)
                tvUserName.text = username

                if (!profilePicBase64.isNullOrEmpty()) {
                    val decodedBytes = Base64.decode(profilePicBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    ivProfileImage.setImageBitmap(bitmap)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProfileActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 📦 Helper: URI to Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // 📦 Helper: Bitmap to Base64
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 5, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
