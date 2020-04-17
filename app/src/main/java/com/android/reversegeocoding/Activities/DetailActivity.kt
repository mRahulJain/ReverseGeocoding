package com.android.reversegeocoding.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.reversegeocoding.R
import com.android.reversegeocoding.models.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    var usernames : ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        //fetching data from google firebase database
        var ref = FirebaseDatabase.getInstance().getReference("myUsers")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    for(snap in p0.children) {
                        val getUser = snap.getValue(user::class.java)
                        usernames.add(getUser!!.username)
                    }
                }
            }

        })

        proceed_details.setOnClickListener {
            if(username.text.toString() == "" ||
                    password.text.toString() == "" ||
                    passwordC.text.toString() == "") {
                Toast.makeText(this, "Please fill all credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(passwordC.text.toString() != password.text.toString()) {
                Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //if username exists
            if(usernames.contains(username.text.toString())) {
                Toast.makeText(this, "Username taken", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //adding data to google firebase database
            var ref = FirebaseDatabase.getInstance().getReference(
                "myUsers/${auth.currentUser!!.uid}")
            var hashMap =HashMap<String, String>()
            hashMap.put("username","${username.text}")
            hashMap.put("password", "${password.text}")
            hashMap.put("uid", "${auth.currentUser!!.uid}")
            ref.setValue(hashMap)
            val intent = Intent(this, GeoLocationActivity::class.java)
            intent.putExtra("type", "details")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
    }
}
