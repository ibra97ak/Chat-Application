package com.ibra.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.ibra.messenger.dataClass.Users
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity(),TextWatcher {


    private val auth :FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    private val cloudFireStore : FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val newUserDocRef : DocumentReference
    get() = cloudFireStore.document("Users/${auth.currentUser?.uid.toString()}/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)


        edtNameSignUp.addTextChangedListener(this@SignUpActivity)
        edtEmailSignUp.addTextChangedListener(this@SignUpActivity)
        edtPasswordSignUp.addTextChangedListener(this@SignUpActivity)

        btnSignUpSignUp.setOnClickListener {
            val name =edtNameSignUp.text.toString().trim()
            val email =edtEmailSignUp.text.toString().trim()
            val password =edtPasswordSignUp.text.toString().trim()

            if (name.isEmpty()){
                edtNameSignUp.error="Enter Your Name"
                edtNameSignUp.requestFocus()
                return@setOnClickListener
            }
            if (email.isEmpty()){
                edtEmailSignUp.error = "Enter Your Email"
                edtEmailSignUp.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edtEmailSignUp.error = "Your Email is incorrect"
                edtEmailSignUp.requestFocus()
                return@setOnClickListener
            }
            if (password.length<6){
                edtPasswordSignUp.error = "Password must be more than 5 digit"
                edtPasswordSignUp.requestFocus()
                return@setOnClickListener
            }

            createNewAccount(name,email,password)

        }
    }

    private fun createNewAccount(Name: String, Email: String, Password: String) {
        signUpProgressBar.visibility = View.VISIBLE
        auth.createUserWithEmailAndPassword(Email,Password).addOnCompleteListener {
            if (it.isSuccessful){
                val newUser = Users(Name,"")
                newUserDocRef.set(newUser)

                val intentMainActivity = Intent(this@SignUpActivity,MainActivity::class.java)
                intentMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK)
                signUpProgressBar.visibility = View.INVISIBLE
                startActivity(intentMainActivity)
            }
            else{
                signUpProgressBar.visibility = View.INVISIBLE
                Toast.makeText(this@SignUpActivity,it.exception?.message,Toast.LENGTH_LONG).show()
            }

        }
    }


    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        btnSignUpSignUp.isEnabled = edtNameSignUp.text.trim().isNotEmpty()
                && edtEmailSignUp.text.trim().isNotEmpty()
                &&edtPasswordSignUp.text.trim().isNotEmpty()

    }
}