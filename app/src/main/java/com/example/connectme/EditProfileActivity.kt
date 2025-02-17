package com.example.connectme

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditProfileActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etContactNumber: EditText
    private lateinit var etBio: EditText
    private lateinit var ivProfileImage: ImageView
    private lateinit var tvDone: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        // Initialize views
        etName = findViewById(R.id.etName)
        etUsername = findViewById(R.id.etUsername)
        etContactNumber = findViewById(R.id.etContactNumber)
        etBio = findViewById(R.id.etBio)
        ivProfileImage = findViewById(R.id.ivProfileImage)
        tvDone = findViewById(R.id.tvDone)

        // Handle Done button click
        tvDone.setOnClickListener {
            // Logic to save changes
            // You can capture the text from edit texts and save them as needed
            val name = etName.text.toString()
            val username = etUsername.text.toString()
            val contact = etContactNumber.text.toString()
            val bio = etBio.text.toString()

            // For example, showing a toast with the updated details
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()

            // Optionally, finish the activity to go back to the profile page
            finish()
        }

        // You can add more functionality here, such as image upload or validation logic
    }
}
