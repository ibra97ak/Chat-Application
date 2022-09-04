package com.ibra.messenger.dataClass

import java.util.*


class TextMessage(
    val text: String,
    override val senderId: String,
    override val recipientId: String,
    override val date: Any?,
    override val type: String
        ) : Message{
    constructor():this("" , "","", Date(),"")
}