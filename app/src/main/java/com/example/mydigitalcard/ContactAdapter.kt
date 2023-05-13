package com.example.mydigitalcard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(private val contactList: ArrayList<contactDetails>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contactview, parent, false)
        return ContactViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contactitm = contactList[position]
        holder.nameTextView.text=contactitm.cname
        holder.phoneTextView.text=contactitm.cnumber
        holder.emailTextView.text=contactitm.cemail
        holder.githubTextView.text=contactitm.cgithub
        holder.addressTextView.text=contactitm.caddresh

    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    class ContactViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView)
    {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val phoneTextView: TextView = itemView.findViewById(R.id.number)
        val emailTextView: TextView = itemView.findViewById(R.id.email)
        val githubTextView: TextView = itemView.findViewById(R.id.github)
        val addressTextView: TextView = itemView.findViewById(R.id.address)

        fun bind(contact: ContactAdapter) {
//            nameTextView.text = contact.name
//            phoneTextView.text = contact.phone
//            emailTextView.text = contact.name
//            githubTextView.text = contact.name
//            addressTextView.text = contact.name
        }

   }
}