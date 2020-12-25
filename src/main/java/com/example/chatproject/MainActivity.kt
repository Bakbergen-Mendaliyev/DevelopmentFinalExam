package com.example.chatproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView14.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            //val intent = Intent(this, GroupChat::class.java)
            startActivity(intent)
        }

        button_first2.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
            if (editTextTextEmailAddress2.text.toString().isEmpty() || editTextTextPassword.text.toString().isEmpty())
                textView13.text = "Email Address or Password is not provided"
            else {
                auth.createUserWithEmailAndPassword(
                    editTextTextEmailAddress2.text.toString(),
                    editTextTextPassword.text.toString()
                ).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    textView13.text = "Succesfull"
                                    val user = auth.currentUser
                                    val intent = Intent(this, Signin::class.java)
                                    startActivity(intent)
                                }
                            }
                    } else {
                        textView13.text = "Failed"
                    }
                }
            }
            }
                }
            }

