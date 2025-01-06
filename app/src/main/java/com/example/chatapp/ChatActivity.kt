package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler.Value
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {


    private lateinit var chatRecylceriew:RecyclerView
    private lateinit var messageBox:TextView
    private lateinit var sendButton:ImageView
    private  lateinit var messageList:ArrayList<Message>
    private lateinit var  mesageAdapter: MessageAdapter
    var senderRoom :String?=null
    var receiverRoom :String?=null
    private lateinit var mDBref:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name=intent.getStringExtra("name")
        val receiver_uid=intent.getStringExtra("uid")
        var sender_uid=FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiver_uid+sender_uid
        receiverRoom=sender_uid+receiver_uid
        supportActionBar?.title=name
        mDBref=FirebaseDatabase.getInstance().getReference()
        chatRecylceriew=findViewById(R.id.ChatRecyclerView)
        messageBox=findViewById(R.id.messageBox)
        sendButton=findViewById(R.id.sent_button)
        messageList=ArrayList()
         mesageAdapter=MessageAdapter(this,messageList)
        chatRecylceriew.layoutManager=LinearLayoutManager(this)
        chatRecylceriew.adapter=mesageAdapter

        mDBref.child("chats").child(senderRoom!!).child("messages").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()

                for(postSnapshot in snapshot.children){
                    val message=postSnapshot.getValue(Message::class.java)
                    if (message != null) {
                        messageList.add(message)
                    }
                }
                mesageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
        sendButton.setOnClickListener{
            val msg=messageBox.text.toString().trim()
            if(msg != "") {
                val messageObject = Message(msg, sender_uid)
                mDBref.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                    mDBref.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
                messageBox.text = ""
            }
        }
    }
}