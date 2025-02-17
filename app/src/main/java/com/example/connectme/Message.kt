package com.example.connectme

data class Message(
    val text: String,
    val isSender: Boolean,  // true if the message is sent by the user, false if received
    val time: String        // Add time for the messages
)

