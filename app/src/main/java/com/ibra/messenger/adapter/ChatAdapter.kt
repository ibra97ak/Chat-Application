package com.ibra.messenger.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ibra.messenger.R
import com.ibra.messenger.dataClass.ChatUser
import com.ibra.messenger.glide.GlideApp
import com.mikhaellopez.circularimageview.CircularImageView

class ChatAdapter(private val myList: ArrayList<ChatUser>, val context:Context) :
    RecyclerView.Adapter<ChatAdapter.myViewHolder>() {

    private lateinit var mListener :OnItemClickListener

    private val auth : FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val fireStoreInstance : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val storageInstance : FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }


    interface OnItemClickListener  {
        fun onItemClick(position: Int)

    }
    fun setOnItemClickListener(listener : OnItemClickListener){
        mListener=listener
    }

    class myViewHolder(itemView: View , listener : OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val nameUser = itemView.findViewById<TextView>(R.id.txtNameRecyclerViewChat)
        val lastMessge = itemView.findViewById<TextView>(R.id.txtLastMessageRecyclerViewChat)
        val date = itemView.findViewById<TextView>(R.id.txtDateRecyclerViewChat)
        val image = itemView.findViewById<CircularImageView>(R.id.imgItemViewRecyclerViewChat)
        init {
            itemView.setOnClickListener {
                listener.onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
        val itmView =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false)
        return myViewHolder(itmView,mListener)
    }

    override fun onBindViewHolder(holder:myViewHolder, position: Int) {
        val user: ChatUser = myList[position]
//        fireStoreInstance.collection("Users")
//            .document(myList[position]).addSnapshotListener { value, error ->
//                val user: ChatUser = value?.toObject(ChatUser::class.java)!!
//                Toast.makeText(context, user.profileName, Toast.LENGTH_LONG).show()
//                holder.nameUser.text = user.profileName
//                holder.lastMessge.text = user.lastMessage
//                holder.date.text = user.Date
//                fireStoreInstance.collection("Users").get()
//                    .addOnSuccessListener {
//                        if (user.image.toString().isNotEmpty()) {
//                            GlideApp.with(context)
//                                .load(storageInstance.getReference(user.image.toString()))
//                                .into(holder.image)
//                        } else {
//                            holder.image.setImageResource(R.drawable.ic_person)
//                        }
//                    }
//            }

        holder.nameUser.text = user.profileName
        holder.lastMessge.text = user.lastMessage
        holder.date.text = user.Date
        fireStoreInstance.collection("Users").get()
            .addOnSuccessListener {
                if (user.image.toString().isNotEmpty()) {
           GlideApp.with(context)
               .load(storageInstance.getReference(user.image.toString()))
               .into(holder.image)
                }else{
                        holder.image.setImageResource(R.drawable.ic_person)
                    }
       }
    }

    override fun getItemCount(): Int {
        return myList.size
    }


}