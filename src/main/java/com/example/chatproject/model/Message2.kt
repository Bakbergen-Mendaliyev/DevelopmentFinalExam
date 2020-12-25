package com.example.chatproject.model

import java.util.*

object  MessageType {
    const val TEXT = "TEXT"
    const val IMAGE = "IMAGE"
}

interface Message2 {

    val time: Date
    val senderId: String
    val type: String
}