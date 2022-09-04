package com.ibra.messenger.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.ibra.messenger.ChatActivity
import com.ibra.messenger.R
import com.ibra.messenger.adapter.ChatAdapter
import com.ibra.messenger.dataClass.ChatUser
import kotlinx.android.synthetic.main.fragment_chat.*


class ChatFragment : Fragment() {

    private val fireStoreInstance: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val auth : FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private lateinit var userArrayList: ArrayList<ChatUser>
    private lateinit var idUserArrayList: ArrayList<String>
    private lateinit var myAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val nameFragment = activity?.findViewById<TextView>(R.id.ToolBarNameFragment)
        nameFragment?.text = "Chat"

        return inflater.inflate(R.layout.fragment_chat, container, false)
        // Inflate the layout for this fragment
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        ChatRecyclerView.layoutManager = LinearLayoutManager(activity)
        ChatRecyclerView.setHasFixedSize(true)
        userArrayList = arrayListOf()
        idUserArrayList = arrayListOf()
        myAdapter = ChatAdapter(userArrayList, requireActivity())
        eventChangeList()
        ChatRecyclerView.adapter = myAdapter
        myAdapter.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val chatIntent = Intent(activity, ChatActivity::class.java)
                chatIntent.putExtra("user_uid", idUserArrayList[position])
                chatIntent.putExtra("user_name", userArrayList[position].profileName)
                chatIntent.putExtra("user_image", userArrayList[position].image)
                activity!!.startActivity(chatIntent)
            }

        })

    }

    private fun eventChangeList() {
        fireStoreInstance.collection("Users")
        .addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if (error != null) {
                    Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
                    return
                }
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        userArrayList.add(dc.document.toObject(ChatUser::class.java))
                        idUserArrayList.add(dc.document.id)
                    }
                }
                myAdapter.notifyDataSetChanged()
            }

        })





//            .addSnapshotListener { value, error ->
//                value!!.documents.forEach { document->
//                    if (error != null) {
//                        Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
//                        return@addSnapshotListener
//                    }
//                    value!!.documents.forEach {
//                            idUserArrayList.add(it.id)
//
//                    }
//                }
//
//            }
//        myAdapter.notifyDataSetChanged()


    }




}