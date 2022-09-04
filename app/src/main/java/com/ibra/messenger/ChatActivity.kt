package com.ibra.messenger

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ibra.messenger.adapter.MessageAdapter
import com.ibra.messenger.dataClass.ImageMessage
import com.ibra.messenger.dataClass.TextMessage
import com.ibra.messenger.glide.GlideApp
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*

class ChatActivity : AppCompatActivity() {

    private val auth : FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val storageInstance : FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val firebaseFirestore : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentImageRef : StorageReference
    get() = storageInstance.reference


    private val chatChannelCollectionRef = firebaseFirestore.collection("chatChannel")
    private val mSenderId = auth.currentUser!!.uid
    private lateinit var mReceiverId :String
    private lateinit var mChannelId : String
    private lateinit var bitmap :ByteArray
    private lateinit var messageList : ArrayList<TextMessage>
    private lateinit var messageAdapter :MessageAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mReceiverId = intent.getStringExtra("user_uid").toString()
        val userName = intent.getStringExtra("user_name")
        val userImage = intent.getStringExtra("user_image")
        messageList = arrayListOf()

        txtNameChatActivity.text = userName
        if (userImage!!.isNotEmpty()){
            GlideApp.with(this@ChatActivity)
                .load(storageInstance.getReference(userImage))
                .into(ToolBarImageChatActivity)
        }
        else{
            ToolBarImageChatActivity.setImageResource(R.drawable.ic_person)
        }

        imageBackChatActivity.setOnClickListener {
            finish()
        }

        createChatChannel{channelId ->
            mChannelId = channelId
            eventChangeList(mChannelId)
            messageAdapter = MessageAdapter(messageList,mSenderId,this@ChatActivity)
            recyclerViewChatActivity.adapter = messageAdapter
            imgSend.setOnClickListener {
                val edtMessage = edtChatActivity.text.toString()
                if (edtMessage.isNotEmpty()){
                    val messageSend = TextMessage(edtMessage,mSenderId,mReceiverId,Calendar.getInstance().time,"TEXT")
                    sendMessage(mChannelId ,messageSend)
                    edtChatActivity.setText("")
                }

            }
        }

        val action = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            lifecycleScope.launch {
                bitmap = getBitmapArray(uri)
                uploadImage(bitmap){path->
                    val imageMessage = ImageMessage(path,mSenderId,mReceiverId,Calendar.getInstance().time)
                    chatChannelCollectionRef.document(mChannelId).collection("message").add(imageMessage)

                }
            }
        }

        fab.setOnClickListener {
            action.launch("image/*")
        }

    }

    //Convert Uri To ByteArray
    private suspend fun getBitmapArray(image: Uri?):ByteArray{
        val loading = ImageLoader(this@ChatActivity)
        val request = ImageRequest.Builder(this@ChatActivity)
            .data(image).build()
        val result = (loading.execute(request) as SuccessResult).drawable
        val bitmap = (result as BitmapDrawable).bitmap
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,outputStream)
        return outputStream.toByteArray()
    }

    //Upload Image
    private fun uploadImage(image: ByteArray, onSuccess:(imagePath:String)->Unit) {
            val ref =currentImageRef.child("${FirebaseAuth.getInstance().currentUser!!.uid}/Images/${UUID.nameUUIDFromBytes(image)}")
                ref.putBytes(image).addOnCompleteListener {
                    if (it.isComplete){
                        Toast.makeText(this@ChatActivity,"Done",Toast.LENGTH_SHORT).show()
                        onSuccess(ref.path)
                    }
                    else{
                        Toast.makeText(this@ChatActivity,"error",Toast.LENGTH_SHORT).show()
                    }
                }

        }

    @SuppressLint("NotifyDataSetChanged")
    private fun eventChangeList(channelId: String) {
       val query = chatChannelCollectionRef.document(channelId).collection("message")
           .orderBy("date", Query.Direction.DESCENDING)
        query.addSnapshotListener { value, error ->
            messageList.clear()
            value!!.documents.forEach { documwnt->
                var text =""
                if (documwnt.get("type") == "TEXT") {
                     text = documwnt.get("text").toString()
                }
                else{
                     text = documwnt.get("imagePath").toString()
                }
                val senderId = documwnt.get("senderId").toString()
                val receiveId = documwnt.get("recipientId").toString()
                val date = documwnt.get("date")
                messageList.add(TextMessage(text, senderId, receiveId, date,documwnt.get("type").toString()))
            }
            messageAdapter.notifyDataSetChanged()
        }
    }


    private fun sendMessage(channelId: String , message: TextMessage) {
        chatChannelCollectionRef.document(channelId).collection("message").add(message)

    }

    private fun createChatChannel(onComplete : (channelId :String) -> Unit) {

        firebaseFirestore.collection("Users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("sharedChat").document(mReceiverId.toString())
            .get().addOnSuccessListener { document ->
                if (document.exists()){
                    onComplete(document["channelId"] as String)
                    return@addOnSuccessListener
                }

                val newChatChannel = firebaseFirestore.collection("Users").document()
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

                firebaseFirestore.collection("Users")
                    .document(mReceiverId.toString())
                    .collection("sharedChat").document(currentUserId)
                    .set(mapOf("channelId" to newChatChannel.id))
                firebaseFirestore.collection("Users").document(currentUserId)
                    .collection("sharedChat").document(mReceiverId.toString())
                    .set(mapOf("channelId" to newChatChannel.id))

                onComplete(newChatChannel.id)

            }
    }


}