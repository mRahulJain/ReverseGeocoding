package com.android.reversegeocoding.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.reversegeocoding.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_geo_location.*

class GeoLocationActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geo_location)

        val type = intent.getStringExtra("type")

        logout.setOnClickListener {
            if(auth.currentUser!=null) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("source", "auth")
                startActivity(intent)
                auth.signOut()
                finish()
                return@setOnClickListener
            }

            if(type == "login") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("source", "login")
                startActivity(intent)
                finish()
                return@setOnClickListener
            }
        }
    }
}
