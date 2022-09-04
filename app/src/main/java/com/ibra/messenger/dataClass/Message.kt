package com.ibra.messenger.dataClass

import java.util.*

interface Message {
    val senderId :String
    val recipientId : String
    val date : Any?
    val type : String
}