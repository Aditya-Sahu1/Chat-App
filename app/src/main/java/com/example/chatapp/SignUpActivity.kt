package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtName: EditText
    private lateinit var btnSignUp: Button
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDBref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edt_name)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mAuth = FirebaseAuth.getInstance()
        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        edtName = findViewById(R.id.edt_name)
        btnSignUp = findViewById(R.id.btnSignUp)

        btnSignUp.setOnClickListener {

            val name = edtName.text.toString()
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            if (validateInputs(name, email, password)) {
                signup(email, password,name)
            }
        }
    }

    private fun signup(email: String, password: String, name: String) {
        // logic of creating user
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    addUserToDataBase(name,email, mAuth.currentUser?.uid!!)
                    val intent=Intent(this@SignUpActivity, HomePage::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this,"User Already Exists",Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDataBase(name: String, email: String, uid: String) {
        mDBref=FirebaseDatabase.getInstance().getReference()
        mDBref.child("users").child(uid).setValue(User(name,email,uid))
    }

    private fun validateInputs(name: String, email: String, password: String): Boolean {
        if (name.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
