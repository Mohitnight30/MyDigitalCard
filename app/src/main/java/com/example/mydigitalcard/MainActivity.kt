package com.example.mydigitalcard

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.mydigitalcard.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var Userid: String
    lateinit var name: String
    lateinit var email: String
    lateinit var Dbname: String
    lateinit var Dbpnumber: String
    lateinit var Dbqrur: String
    private val STORAGE_PERMISSION_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            var i = Intent(this, Login::class.java)
            startActivity(i)
        }

        val user = Firebase.auth.currentUser
        user?.let {
            name = it.displayName.toString()
            email = it.email.toString()
            Userid = it.uid
        }
        getuserdata()

        if (!name.isEmpty()) {
            binding.disname.setText(name)
        }
        binding.disemail.setText(email)

        binding.btnshare.setOnClickListener(View.OnClickListener {
            try {
                val qrCodeUri = Uri.parse(Dbqrur)
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "image/JPEG"
                shareIntent.putExtra(Intent.EXTRA_STREAM, qrCodeUri)
                startActivity(Intent.createChooser(shareIntent, "Share QR code"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
        binding.btnsave.setOnClickListener(View.OnClickListener {
            saveimage()
        })
        binding.edit.setOnClickListener(View.OnClickListener {
            var i = Intent(this, Edit_profile::class.java)
            startActivity(i)
        })
        binding.btnlogout.setOnClickListener(View.OnClickListener {
            Firebase.auth.signOut()
            var i = Intent(this, Login::class.java)
            startActivity(i)
        })
        binding.btnqrscan.setOnClickListener(View.OnClickListener {
            var i = Intent(this, scanner::class.java)
            startActivity(i)
        })

    }

    private fun saveimage() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            downloadImage(Dbqrur)
        }
    }

    private fun getuserdata() {
        val reference = FirebaseDatabase.getInstance().getReference("users")
        reference.child(Userid).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Dbname = snapshot.child("name").value.toString()
                    Dbpnumber = snapshot.child("pnumber").value.toString()
                    Dbqrur = snapshot.child("qruri").value.toString()
                    binding.disphone.text = Dbpnumber
                    binding.disname.text = Dbname
                    Glide.with(this@MainActivity).load(Dbqrur).into(binding.qrShow)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            var i = Intent(this, Login::class.java)
            startActivity(i)
        }
        getuserdata()
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) { if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val imageUri = Dbqrur
                downloadImage(imageUri)
            } else {
                finish()
            }
        }
    }

    private fun downloadImage(imageUri: String) {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(imageUri))
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "My_QR_Code.jpg")

        downloadManager.enqueue(request)
    }

}