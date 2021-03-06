package com.example.chatapplication.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapplication.R
import com.example.chatapplication.adapter.UserAdapter
import com.example.chatapplication.firebase.FirebaseService
import com.example.chatapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.FirebaseDatabase.getInstance
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessaging.getInstance
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList
import java.util.Calendar.getInstance

class UsersActivity : AppCompatActivity() {

    private lateinit var userRecyclerView:RecyclerView
    private lateinit var logout:ImageView
    private lateinit var imgProfile:CircleImageView
    var userList = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        if (FirebaseAuth.getInstance().currentUser == null){
            startLoginActivity()
        }


        FirebaseService.sharedPref = getSharedPreferences("sharedPref",Context.MODE_PRIVATE)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
        }

//        FirebaseMessaging.getInstance().token.addOnSuccessListener {
//
//        }

        userRecyclerView = findViewById(R.id.userRecyclerView)
        userRecyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        logout = findViewById(R.id.logout)
        imgProfile = findViewById(R.id.imgProfile)

        userRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startLoginActivity()
        }

        imgProfile.setOnClickListener {
            val intent:Intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        getUserList()

    }

    private fun startLoginActivity() {
        var intent:Intent = Intent(this,LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun getUserList(){
        val firebase : FirebaseUser = FirebaseAuth.getInstance().currentUser!!


        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")

        val databaseReference : DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()

                val currentUser = snapshot.getValue(User::class.java)

                if (currentUser!!.profileImage == ""){
                    imgProfile.setImageResource(R.drawable.dp)
                }else{
                    Glide.with(this@UsersActivity).load(currentUser!!.profileImage).into(imgProfile)
                }

                for (dataSnapShot:DataSnapshot in snapshot.children){
                    val user = dataSnapShot.getValue(User::class.java)

                    if (!user!!.userId.equals(firebase.uid)){
                        userList.add(user)
                    }
                }

                val userAdapter = UserAdapter(this@UsersActivity,userList)

                userRecyclerView.adapter = userAdapter

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UsersActivity,error.message,Toast.LENGTH_LONG).show()
            }

        })
    }

}