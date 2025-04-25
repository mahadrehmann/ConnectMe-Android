package com.example.connectme

data class Post(
    var id: String = "",
    var userName: String = "",
    var userProfilePic: String = "", // Base64 or URL string
    var caption: String = "",
    var postImage: String? = null,   // Optional Base64 image string or URL
    var timestamp: Long = 0
)
