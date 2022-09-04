package com.ibra.messenger

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ibra.messenger.glide.GlideApp
import kotlinx.android.synthetic.main.activity_profile.*
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private val storageInstance : FirebaseStorage by lazy {
        FirebaseStorage.getInstance()
    }
    private val auth :FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val currentUserRef : StorageReference
    get() = storageInstance.reference.child(auth.currentUser?.uid.toString())

    private val fireStoreInstance : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
    private val currentUserDocRef : DocumentReference
        get() = fireStoreInstance.document("Users/${auth.currentUser?.uid.toString()}/")

    private lateinit var userName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        setSupportActionBar(ToolBarProfileActivity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Me"

        getUserInfo()

        val action = registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            uploadImage(uri){path->
               val userFieldMAp = mutableMapOf<String,Any>()
                userFieldMAp["name"] = userName
               userFieldMAp["image"] = path
              currentUserDocRef.update(userFieldMAp)


            }
        }

        circularImageProfileActivity.setOnClickListener {
            action.launch("image/*")
        }

        txtSignOutProfileActivity.setOnClickListener {
            auth.signOut()
            val signOutIntent =Intent(this@ProfileActivity,SignInActivity::class.java)
            startActivity(signOutIntent)

            finish()

        }


    }



    private fun uploadImage(image: Uri?,onSuccess:(imagePath:String)->Unit) {
        if (image != null) {
            val ref = currentUserRef.child("profileImage/${UUID.randomUUID()}")
            ref.putFile(image).addOnCompleteListener {
                if (it.isComplete){
                    circularImageProfileActivity.setImageURI(image)
                    onSuccess(ref.path)
                }
                else{
                    Toast.makeText(this@ProfileActivity,it.exception.toString(),Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun getUserInfo() {
        currentUserDocRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    userName  = document.data?.get("profileName").toString()
                    txtNameProfileActivity.text = userName
                     if (document.data?.get("image").toString().isNotEmpty()) {
                         GlideApp.with(this@ProfileActivity)
                             .load(storageInstance.getReference(document.data?.get("image").toString()))
                             .placeholder(R.drawable.ic_person)
                             .into(circularImageProfileActivity)
                     }

                }
            }
    }


}


