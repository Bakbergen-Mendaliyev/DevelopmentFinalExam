package com.example.chatproject.model

data class ChatChannel(val userIds: MutableList<String>){
constructor() : this(mutableListOf())
}