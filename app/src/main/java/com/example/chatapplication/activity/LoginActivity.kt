package com.example.chatapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chatapplication.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

//    private lateinit var auth: FirebaseAuth
//    private lateinit var firebaseUser: FirebaseUser

    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: Button

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)

        edtEmail = findViewById(R.id.edtEmail)
        edtPassword = findViewById(R.id.edtPassword)

//        auth = FirebaseAuth.getInstance().currentUser

        //check if user login then navigate to user screen

//        firebaseUser = auth.currentUser

        if (FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this,UsersActivity::class.java)
            startActivity(intent)
            finish()
        }

//        if (FirebaseAuth.getInstance().currentUser != null){
//            val intent:Intent = Intent(this, UsersActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(intent)
//            finish()
//        }
    }

    override fun onStart() {
        super.onStart()
        btnLogin.setOnClickListener {
            performLogin()
        }
        btnSignUp.setOnClickListener {
            performRegister()
        }
    }

    private fun performRegister() {
        val intent:Intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun performLogin() {
        val email = edtEmail.text.toString()
        val password = edtPassword.text.toString()

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)){
            Toast.makeText(this,"Email and Password are Required.",Toast.LENGTH_LONG).show()
        }else{
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this) {
                    if (it.isSuccessful){
                        edtEmail.setText("")
                        edtPassword.setText("")
                        val intent:Intent = Intent(this, UsersActivity::class.java)
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this,"Email and Password Invalid.",Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}