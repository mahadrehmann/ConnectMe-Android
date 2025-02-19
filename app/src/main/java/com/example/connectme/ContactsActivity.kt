package com.example.connectme

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class ContactsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        val inContactsList = findViewById<ListView>(R.id.listInContacts)
        val inviteFriendsList = findViewById<ListView>(R.id.listInviteFriends)

        val contacts = listOf(
            Contact("Henry School", R.drawable.masab),
            Contact("Emily James", R.drawable.asim),
            Contact("Lily Thomas", R.drawable.grass)
        )

        val inviteContacts = listOf(
            Contact("Amy Wesley", R.drawable.ahmed),
            Contact("Laura Ryan", R.drawable.mahad),
            Contact("Christopher", R.drawable.highrise)
        )

        // Set the adapters
        inContactsList.adapter = ContactAdapter(this, contacts)
        inviteFriendsList.adapter = ContactAdapter(this, inviteContacts)

        findViewById<BottomNavigationView>(R.id.bottomNavigation).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search -> {
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.add -> {
                    val intent = Intent(this, UploadActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.home -> {
                    val intent = Intent(this, homePage::class.java)
                    startActivity(intent)
                    true
                }
                R.id.profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }
}
