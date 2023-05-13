package com.example.mydigitalcard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mydigitalcard.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var user_email: String
    lateinit var user_pass: String
    lateinit var auth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient

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
            Toast.makeText(this, "Forgot Password", Toast.LENGTH_SHORT).show()
        })
        binding.btnregister.setOnClickListener(View.OnClickListener {
            var i = Intent(this, Signup::class.java)
            startActivity(i)
        })

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btngoogle.setOnClickListener(View.OnClickListener {
            googlesignup()
        })
    }

    private fun googlesignup() {
        var signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            }
        }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                var i = Intent(this, Edit_profile::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            }
        } else {
            Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
        }
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
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
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