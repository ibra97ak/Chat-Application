package com.ibra.messenger.dataClass

import java.util.*

class ImageMessage (val imagePath : String ,
                    override val senderId : String,
                    override val recipientId : String,
                    override val date : Any?,
                    override val type: String = MessageType.IMAGE
                    ):Message {
   // constructor(): this ( "","","",Date(),"")
}