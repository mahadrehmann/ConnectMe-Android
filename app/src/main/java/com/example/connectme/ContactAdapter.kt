package com.example.connectme

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ContactAdapter(private val context: Context, private val contacts: List<Contact>) : BaseAdapter() {

    override fun getCount(): Int = contacts.size

    override fun getItem(position: Int): Any = contacts[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.contact_item, parent, false)
        val contact = contacts[position]

        val imageView = view.findViewById<ImageView>(R.id.ivProfileImage)
        val nameView = view.findViewById<TextView>(R.id.tvContactName)
        val chatIcon = view.findViewById<ImageView>(R.id.ivChatIcon)

        imageView.setImageResource(contact.profileImage)
        nameView.text = contact.name

        chatIcon.setOnClickListener {
            // Handle the click to chat
        }

        return view
    }
}
