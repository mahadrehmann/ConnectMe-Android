package com.example.connectme

interface MessageActionListener {
    fun onEditMessage(message: Message)
    fun onDeleteMessage(message: Message)
}
