package com.example.chatproject.model

import java.util.*

data class TextMessage (val text:String,
                        override val time: Date,
                        override val senderId:String,
                        override val type:String = MessageType.TEXT) : Message2{
constructor() :this("",Date(0), "")
}