package com.example.mydigitalcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mydigitalcard.databinding.ActivityContactBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class Contact : AppCompatActivity() {

    lateinit var binding: ActivityContactBinding
    lateinit var Userid:String
    lateinit var cDetails: contactDetails
    lateinit var list: ArrayList<contactDetails>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)

        val user = Firebase.auth.currentUser
        user?.let {
            Userid = it.uid
        }

        val reference = FirebaseDatabase.getInstance().getReference("users")
        reference.child(Userid).child("contact").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (db in snapshot.children){
                        cDetails= db.getValue(contactDetails::class.java)!!
                        list.add(cDetails!!)
                    }
                    binding.recyclerView.adapter = ContactAdapter(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }
}