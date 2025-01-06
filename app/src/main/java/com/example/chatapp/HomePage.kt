package com.example.chatapp


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import java.util.ArrayList

class HomePage : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userRecyclerView:RecyclerView
    private lateinit var userList:ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var mDBref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.title ="Chats"
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        mAuth=FirebaseAuth.getInstance()
        mDBref=FirebaseDatabase.getInstance().getReference()
        userList= ArrayList()
        adapter= UserAdapter(this,userList)
        userRecyclerView=findViewById(R.id.user_recycler)
        userRecyclerView.layoutManager=LinearLayoutManager(this)
        userRecyclerView.adapter=adapter

        mDBref.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser=postSnapshot.getValue(User::class.java)
                    if(mAuth.currentUser?.uid !=currentUser?.uid) {
                        userList.add(currentUser!!)
                        Log.d("RecyclerView", "User list size: ${currentUser.name}\n")
                    }
                }
                Log.d("RecyclerView", "User list size: ${userList.size}")
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.loogOut){
            mAuth.signOut()
            intent=Intent(this@HomePage, LoginActivity::class.java)
            finish()
            startActivity(intent)
            return true;
        }
        return true;
    }
}