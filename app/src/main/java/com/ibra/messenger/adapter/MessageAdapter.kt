package com.ibra.messenger.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.ibra.messenger.R
import com.ibra.messenger.dataClass.TextMessage
import com.ibra.messenger.glide.GlideApp
import kotlin.collections.ArrayList
import android.widget.ImageView
import android.widget.Toast

class MessageAdapter(private val messageList : ArrayList<TextMessage>,
                     private val userId : String,
                     private val context: Context  ):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val itemReceiveText = 1
    private val  itemSendText = 2
    private val itemReceiveImage = 3
    private val itemSendImage = 4

   class SentTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtSentMessage = itemView.findViewById<TextView>(R.id.txtMessage)
        val txtSentTime = itemView.findViewById<TextView>(R.id.txtTime)
   }

    class ReceiveTextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtReceiveMessage = itemView.findViewById<TextView>(R.id.txtReceiveMessage)
        val txtReceiveTime = itemView.findViewById<TextView>(R.id.txtReceiveTime)
    }
    class SentImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgSendMessage = itemView.findViewById<ImageView>(R.id.imgSendMessage)
        val txtSendImageTime = itemView.findViewById<TextView>(R.id.txtSendImageTime)
    }
    class ReceiveImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imgReceiveMessage = itemView.findViewById<ImageView>(R.id.imgReceiveMessage)
        val txtReceiveImageTime = itemView.findViewById<TextView>(R.id.txtReceiveImageTime)
    }



    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (currentMessage.senderId == userId && currentMessage.type == "TEXT"){
            return itemSendText
        }
        else  if (currentMessage.senderId == userId && currentMessage.type == "IMAGE")
            { return itemSendImage}

        else if (currentMessage.senderId != userId && currentMessage.type == "IMAGE") {
            return itemReceiveImage
        }
        else{
                return itemReceiveText
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            1 ->{
                val view = LayoutInflater.from(context).inflate(R.layout.recipient_item_text_message,parent,false)
                return ReceiveTextViewHolder(view)
            }
            2 ->{
                val view = LayoutInflater.from(context).inflate(R.layout.item_text_message,parent,false)
                return SentTextViewHolder(view)
            }
            3 ->{
                val view = LayoutInflater.from(context).inflate(R.layout.receive_item_image_message,parent,false)
                return ReceiveImageViewHolder(view)
            }
            else ->{
                val view = LayoutInflater.from(context).inflate(R.layout.send_item_image_message,parent,false)
                return SentImageViewHolder(view)
            }
        }

    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        when(holder.javaClass){
            SentTextViewHolder::class.java->{
                val viewHolder = holder as SentTextViewHolder
                viewHolder.txtSentMessage.text = messageList[position].text
                viewHolder.txtSentTime.text = currentMessage.date.toString()
            }
            ReceiveTextViewHolder::class.java->{
                val viewHolder = holder as ReceiveTextViewHolder
                viewHolder.txtReceiveMessage.text=currentMessage.text
                viewHolder.txtReceiveTime.text = currentMessage.date.toString()
            }
            SentImageViewHolder::class.java->{
                val viewHolder = holder as SentImageViewHolder
               viewHolder.txtSendImageTime.text = currentMessage.date.toString()
                GlideApp.with(context)
                    .load(FirebaseStorage.getInstance().getReference(currentMessage.text))
                    .placeholder(R.drawable.ic_image)
                    .into(viewHolder.imgSendMessage)
            }
            else->{
                val viewHolder = holder as ReceiveImageViewHolder
                viewHolder.txtReceiveImageTime.text = currentMessage.date.toString()
                GlideApp.with(context)
                    .load(FirebaseStorage.getInstance().getReference(currentMessage.text))
                    .placeholder(R.drawable.ic_image)
                    .into(viewHolder.imgReceiveMessage)
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}


