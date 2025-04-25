package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class registerPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_page)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // UI references
        val nameInput     = findViewById<EditText>(R.id.nameInput)
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val phoneInput    = findViewById<EditText>(R.id.phoneInput)
        val emailInput    = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginText      = findViewById<TextView>(R.id.loginText)

        // If they already have an account
        loginText.setOnClickListener {
            startActivity(Intent(this, loginPage::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        registerButton.setOnClickListener {
            val name     = nameInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val phone    = phoneInput.text.toString().trim()
            val email    = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the user with Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        return@addOnCompleteListener
                    }

                    // Auth success → now save the rest of the user info
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val dbRef = FirebaseDatabase.getInstance().getReference("Users").child(uid)

                    // Build the full profile map
                    val userData = mapOf(
                        "name"            to name,
                        "username"        to username,
                        "phone"           to phone,
                        "email"           to email,
                        "bio"             to "Hey there, I'm using Connect Me",
                        "contact"         to "",
                        "followersCount"  to 0,
                        "followingCount"  to 0,
                        "online"  to false,
                        "profilePic"      to ""  // Base64 or URL string goes here
                    )

                    dbRef.setValue(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                            // Send them to login (or directly to profile setup)
                            startActivity(Intent(this, loginPage::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
        }
    }
}
