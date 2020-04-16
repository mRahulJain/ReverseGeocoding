package com.android.reversegeocoding.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.core.text.bold
import com.android.reversegeocoding.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //setText for create and forgotPassword textViews
        var create = SpannableStringBuilder()
            .append("Don't have an account? ")
            .bold {
                append("Create here!")
            }
        var forgot = SpannableStringBuilder()
            .append("Forgot your password? ")
            .bold {
                append("Get Help!")
            }
        createAccount.text = create
        forgotPassword.text = forgot

        //code to execute when user clicks on create account text
        createAccount.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }
}
