package com.example.mydigitalcard

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mydigitalcard.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var user_email: String
    lateinit var user_pass: String
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        binding.LoginprogressBar.visibility = View.GONE

        binding.btnlogin.setOnClickListener(View.OnClickListener {
            binding.LoginprogressBar.visibility = View.VISIBLE
            UserSignin()
        })
        binding.btnfrogot.setOnClickListener(View.OnClickListener {
            Toast.makeText(this,"Forgot Password",Toast.LENGTH_SHORT).show()
        })
        binding.btnregister.setOnClickListener(View.OnClickListener {
            var i = Intent(this,Signup::class.java)
            startActivity(i)
        })
    }

    private fun UserSignin() {

        user_email = binding.TextLoginEmail.text.toString().trim()
        user_pass = binding.TextLoginPassword.text.toString().trim()

        if (TextUtils.isEmpty(user_email)) {
            binding.LoginprogressBar.visibility = View.GONE
            Toast.makeText(applicationContext, "Please enter email!!", Toast.LENGTH_LONG).show()
            return
        }
        if (TextUtils.isEmpty(user_pass)) {
            binding.LoginprogressBar.visibility = View.GONE
            Toast.makeText(applicationContext, "Please enter password!!", Toast.LENGTH_LONG).show()
            return
        }

        auth.signInWithEmailAndPassword(user_email, user_pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var i = Intent(this, MainActivity::class.java)
                    i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
                    i.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
                    i.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                } else {
                    Log.w("Login ", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this,
                        "Login failed Please Check E-mail and Password.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            var i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }
}