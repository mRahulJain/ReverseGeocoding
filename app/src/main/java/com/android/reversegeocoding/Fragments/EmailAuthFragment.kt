package com.android.reversegeocoding.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.bold
import androidx.core.view.isVisible
import com.android.reversegeocoding.Activities.GeoLocationActivity

import com.android.reversegeocoding.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_email_auth.view.*

class EmailAuthFragment : Fragment() {

    var count = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_email_auth, container, false)

        //setHelp
        var query = SpannableStringBuilder()
            .append("Have query? ")
            .bold {
                append("Click here!")
            }
        view.queryEmail.text = query
        view.queryEmail.setOnClickListener {
            var queryString = "Enter your credentials above. \n A verification mail will be sent on your email " +
                    "(Check spam if you don't receive notification).\n" +
                    " Verify that and come back to hit login button for successful creation of your account."
            view.queryEmail.text = queryString
        }

        view!!.sendMail.setOnClickListener {
            //count=0 when user sends verification mail
            //count=1 when user sign in the verified mail
            if(count == 0) {
                //creadentials missing
                if(view!!.emailEL.text.toString() == "" ||
                    view!!.passwordEL.text.toString() == "") {
                    Toast.makeText(view!!.context,
                        "Enter all credentials",
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                //password of small length
                if(view!!.passwordEL.text.toString().length < 6) {
                    Toast.makeText(view!!.context,
                        "Password should be at least 6 characters long",
                        Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                view!!.progressEL.isVisible = true
                view!!.sendMail.isVisible = false
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(view!!.emailEL.text.toString(),
                    view!!.passwordEL.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        FirebaseAuth.getInstance().currentUser!!.sendEmailVerification().addOnSuccessListener {
                            view!!.sendMail.setText("Login")
                            count = 1
                            Toast.makeText(view!!.context,
                                "Email Sent Successfully",
                                Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(view!!.context,
                                "Something went wrong",
                                Toast.LENGTH_SHORT).show()
                        }
                        view!!.progressEL.isVisible = false
                        view!!.sendMail.isVisible = true
                    } else {
                        Toast.makeText(view!!.context,
                            "${it.exception!!.localizedMessage}",
                            Toast.LENGTH_SHORT).show()
                        Log.d("myCHECK", "${it.exception!!.localizedMessage}")
                    }
                    view!!.progressEL.isVisible = false
                    view!!.sendMail.isVisible = true
                }
            } else {
                view!!.progressEL.isVisible = true
                view!!.sendMail.isVisible = false
                FirebaseAuth.getInstance().signInWithEmailAndPassword(view!!.emailEL.text.toString(),
                    view!!.passwordEL.text.toString()).addOnCompleteListener {
                    if(it.isSuccessful) {
                        if(FirebaseAuth.getInstance().currentUser!!.isEmailVerified) {
                            val intent = Intent(view!!.context, GeoLocationActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            activity!!.finish()
                        } else {
                            Toast.makeText(view!!.context,
                                "Please verify your mail first",
                                Toast.LENGTH_SHORT).show()
                            view!!.progressEL.isVisible = false
                            view!!.sendMail.isVisible = true
                        }
                    } else {
                        Toast.makeText(view!!.context,
                            "${it.exception!!.localizedMessage}",
                            Toast.LENGTH_SHORT).show()
                        view!!.progressEL.isVisible = false
                        view!!.sendMail.isVisible = true
                    }
                }
            }
        }
        return view
    }

}
