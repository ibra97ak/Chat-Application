package com.ibra.messenger

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ibra.messenger.fragments.ChatFragment
import com.ibra.messenger.glide.GlideApp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val fireStoreInstance : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val storageInstance : FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val currentUserDocRef : DocumentReference
        get() = fireStoreInstance.document("Users/${auth.currentUser?.uid.toString()}/")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val controllerFragment = findNavController(R.id.fragment)
        bottomNavigation.setupWithNavController(controllerFragment)
        setSupportActionBar(mainToolBar)
        supportActionBar?.title = ""

        ToolBarImageFragment.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }

        currentUserDocRef.get().addOnSuccessListener { document ->
                if (document.data?.get("image").toString().isNotEmpty()){
                    GlideApp.with(this@MainActivity)
                        .load(storageInstance.getReference(document.data?.get("image").toString()))
                        .placeholder(R.drawable.ic_person)
                        .into(ToolBarImageFragment)
                }
            }




    }

    override fun onStart() {
        super.onStart()
        currentUserDocRef.get().addOnSuccessListener { document ->
            if (document.data?.get("image").toString().isNotEmpty()){
                GlideApp.with(this@MainActivity)
                    .load(storageInstance.getReference(document.data?.get("image").toString()))
                    .placeholder(R.drawable.ic_person)
                    .into(ToolBarImageFragment)
            }

        }
    }


}