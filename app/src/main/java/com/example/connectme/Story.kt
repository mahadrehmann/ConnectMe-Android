package com.example.connectme

data class Story(
    var userName: String = "",
    var userProfilePic: String = "", // Download URL for the user's profile picture
    var storyImage: String = "",      // Download URL for the story image (or video)
    var timestamp: Long = 0L          // You can use this to manage 24-hour expiration
)
