package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

        // Get references to the input fields and buttons
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginText = findViewById<TextView>(R.id.loginText)


        // Navigate to Login page if the user already has an account
        loginText.setOnClickListener {
            val intent = Intent(this, loginPage::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Handle registration
        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val username = usernameInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the user with Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Registration succeeded, now save additional user data in Realtime Database
                        val userId = auth.currentUser?.uid
                        val dbRef = FirebaseDatabase.getInstance().getReference("Users")
                        val userData = hashMapOf(
                            "name" to name,
                            "username" to username,
                            "phone" to phone,
                            "email" to email
                        )

                        if (userId != null) {
                            dbRef.child(userId).setValue(userData)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                                        // Direct first-time users to Profile Setup screen
                                        val intent = Intent(this, loginPage::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Failed to save user data: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}