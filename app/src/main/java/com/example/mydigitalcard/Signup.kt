package com.example.mydigitalcard

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mydigitalcard.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


class Signup : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    lateinit var Username: String
    lateinit var UserEmail: String
    lateinit var UserPassword: String
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        binding.progressBarSignup.visibility = View.GONE

        binding.btnCreateAccount.setOnClickListener(View.OnClickListener {
            binding.progressBarSignup.visibility = View.VISIBLE
            Createuser()
        })

        binding.InputName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.LayoutName.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })
        binding.InputEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.LayoutEmail.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
        binding.InputPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.LayoutPassword.isErrorEnabled = false
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    private fun Createuser() {

        Username = binding.InputName.text.toString().trim()
        UserEmail = binding.InputEmail.text.toString().trim()
        UserPassword = binding.InputPassword.text.toString().trim()

        if (TextUtils.isEmpty(UserPassword)) {
            binding.progressBarSignup.visibility = View.GONE
            binding.LayoutPassword.isErrorEnabled = true
            binding.LayoutPassword.error = "Please Enter Password"
            return
        }
        if (UserPassword.length < 6) {
            binding.progressBarSignup.visibility = View.GONE
            binding.LayoutPassword.isErrorEnabled = true
            binding.LayoutPassword.error = "Please Enter Atlest 8 Digit Password"
            return
        }

        if (TextUtils.isEmpty(UserEmail)) {
            binding.progressBarSignup.visibility = View.GONE
            binding.LayoutEmail.isErrorEnabled = true
            binding.LayoutEmail.error = "Please Enter Email"
            return
        }

        if (TextUtils.isEmpty(Username)) {
            binding.progressBarSignup.visibility = View.GONE
            binding.LayoutName.isErrorEnabled = true
            binding.LayoutName.error = "Please Enter Name"
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(UserEmail).matches()) {
            binding.progressBarSignup.visibility = View.GONE
            binding.LayoutEmail.isErrorEnabled = true
            binding.LayoutEmail.error = "Enter Correct E-mail"
            return
        }

        auth.createUserWithEmailAndPassword(UserEmail, UserPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.progressBarSignup.visibility = View.GONE
                    updateuser()
                    var i = Intent(this, Edit_profile::class.java)
                    i.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK
                    i.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP
                    i.flags=Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                } else {
                    binding.progressBarSignup.visibility = View.GONE
                    Log.w("Account Fail", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateuser() {
        val changeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(Username)
            .build()
        auth.currentUser?.updateProfile(changeRequest)
    }
}