package com.example.chatproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.chatproject.util.FirestoreUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_signin.*

class Signin : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContentView(R.layout.activity_signin)

        textView6.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        button_first.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
            if (editTextTextEmailAddress.text.toString().isEmpty() || editTextTextPassword2.text.toString().isEmpty())
                textView15.text = "Email Address or Password is not provided"
            else {
                auth.signInWithEmailAndPassword(
                    editTextTextEmailAddress.text.toString(),
                    editTextTextPassword2.text.toString()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        textView15.text = "Succesfull"
                        val user = auth.currentUser
                        UpdateUI(user, editTextTextEmailAddress.text.toString())
                    } else {
                        textView15.text = "Invalid Email or Password"
                    }
                }
            }
        }

    }
    private fun UpdateUI(currentUser: FirebaseUser?, emailAdd: String){
        if(currentUser!=null){
            if(currentUser.isEmailVerified) {
                val intent = Intent(this, NavDrawer::class.java)
                intent.putExtra("emailAddress", emailAdd);
                FirestoreUtil.initCurrentUserIfFirstTime {
                    startActivity(intent)
                    finish()
                }


            } else
                Toast.makeText(this,"Email Address Is not Verified. Please verify your email address",
                    Toast.LENGTH_LONG).show()
        }
        textViewForgotPassword.setOnClickListener {
            val intent = Intent(this, PasswordReset::class.java)
            startActivity(intent)
            finish()
        }
    }
    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        UpdateUI(currentUser, currentUser?.email.toString())
    }



}