package com.android.reversegeocoding.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.text.bold
import com.android.reversegeocoding.R
import com.android.reversegeocoding.models.user
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val KEY_DIRECT_OPEN = "app_open"
    var direct = false
    var list : ArrayList<user> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //fetching existing usernames
        val ref = FirebaseDatabase.getInstance().getReference("myUsers")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()) {
                    for(snap in p0.children) {
                        val getUser = snap.getValue(user::class.java)
                        list.add(getUser!!)
                    }
                }
            }

        })

        //shared preferences for existing user entry to the app
        //this will help in logout process
        val prefs = getPreferences(Context.MODE_PRIVATE)

        val source = intent.getStringExtra("source")

        //source = null when LoginActivity is not opened through GeoLocationActivity
        //source != null when LoginActivity is opened through GeoLocationActivity and we set our
        //shared preference as false.
        if(source != null) {
            if(source == "login") {
                prefs.edit() {
                    putBoolean(KEY_DIRECT_OPEN, false)
                }
            }
        }

        //fetch the shared preference value
        direct = prefs.getBoolean(KEY_DIRECT_OPEN, false)


        //check whether any type of user is logged in
        if(auth.currentUser != null) {
            val intent = Intent(this, GeoLocationActivity::class.java)
            intent.putExtra("type", "details")
            startActivity(intent)
            finish()
        }
        if(direct) {
            val intent = Intent(this, GeoLocationActivity::class.java)
            intent.putExtra("type", "login")
            startActivity(intent)
            finish()
        }

        //setText for create textView
        var create = SpannableStringBuilder()
            .append("Don't have an account? ")
            .bold {
                append("Create here!")
            }
        createAccount.text = create

        //code to execute when user clicks on create account text
        createAccount.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        //code to execute when user clicks on login button
        //here username is matched first
        //later we check for password
        login.setOnClickListener {
            if(usernameE.text.toString() == "" ||
                    passwordE.text.toString() == "") {
                Toast.makeText(this, "Enter all credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            var check = false
            for(item in list) {
                if(item.username == usernameE.text.toString()) {
                    check = true
                    if(item.password == passwordE.text.toString()) {
                        prefs.edit {
                            putBoolean(KEY_DIRECT_OPEN, true)
                        }
                        val intent = Intent(this, GeoLocationActivity::class.java)
                        intent.putExtra("type", "login")
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Password is wrong", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                }
            }

            //if username is not found
            if(!check) {
                Toast.makeText(this, "Username doesn't exist", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
        }
    }
}
