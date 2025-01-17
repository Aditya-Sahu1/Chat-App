package com.example.chatapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import java.util.ArrayList

class UserAdapter(val context:Context, private val userList:ArrayList<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view:View=LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        var curr = userList[position]
        holder.textName.text=curr.name
        holder.itemView.setOnClickListener {
            var intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("name", curr.name)
            intent.putExtra("uid", curr.uid)
            context.startActivity(intent)
        }
    }

    class UserViewHolder(itemView: View): ViewHolder(itemView){
        val textName =itemView.findViewById<TextView>(R.id.txt_name)
    }

}