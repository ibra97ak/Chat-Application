package com.ibra.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Patterns
import android.widget.Toast
import android.text.TextWatcher
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() ,TextWatcher {

    private val auth :FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        edtEmailSignIn.addTextChangedListener(this@SignInActivity)
        edtPasswordSignIn.addTextChangedListener(this@SignInActivity)


        btnToSignUp.setOnClickListener {
            val signInIntent = Intent(this,SignUpActivity::class.java)
            startActivity(signInIntent)
        }

        btnSignInSignIn.setOnClickListener {
            val email = edtEmailSignIn.text.trim().toString()
            val password = edtPasswordSignIn.text.trim().toString()

            if (email.isEmpty()){
                edtEmailSignIn.error = "Enter Your Email"
                edtEmailSignIn.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edtEmailSignIn.error = "Your Email is incorrect"
                edtEmailSignIn.requestFocus()
                return@setOnClickListener
            }
            if (password.length<6){
                edtPasswordSignIn.error = "Password must be more than 5 digit"
                edtPasswordSignIn.requestFocus()
                return@setOnClickListener
            }

            signIn(email, password)
        }


    }

    override fun onStart() {
        super.onStart()
        if (auth.currentUser?.uid != null){
            val mainActivityIntent = Intent(this@SignInActivity,MainActivity::class.java)
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(mainActivityIntent)

        }
    }

    private fun signIn(Email: String, Password: String) {
        signInProgressBar.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(Email,Password).addOnCompleteListener {
            if (it.isSuccessful){
                val mainActivityIntent = Intent(this@SignInActivity,MainActivity::class.java)
                mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                signInProgressBar.visibility = View.INVISIBLE
                startActivity(mainActivityIntent)
            }
            else{
                signInProgressBar.visibility = View.INVISIBLE
                Toast.makeText(this@SignInActivity,it.exception?.message,Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun afterTextChanged(p0: Editable?) {
        btnSignInSignIn.isEnabled=edtEmailSignIn.text.trim().isNotEmpty()
                &&edtPasswordSignIn.text.trim().isNotEmpty()
    }


}