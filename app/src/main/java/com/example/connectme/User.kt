package com.example.connectme

data class User(
    var name: String = "",
    var profilePic: String = "",  // base64 or URL
    var bio: String = "",
    var followersCount: Long = 0,
    var followingCount: Long = 0
)