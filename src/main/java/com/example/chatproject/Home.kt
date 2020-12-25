package com.example.chatproject

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*

class Home : AppCompatActivity() {
    var myAuth = FirebaseAuth.getInstance()
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val intent = intent
        val receivedEmail = intent.getStringExtra("emailAddress")
        loggedInTextView.text = "Welcome $receivedEmail"

        logout.setOnClickListener { signOut() }
        myAuth.addAuthStateListener {
            if (myAuth.currentUser == null){
                this.finish()
            }
        }

    }
    fun signOut(){
        myAuth.signOut()
    }
}