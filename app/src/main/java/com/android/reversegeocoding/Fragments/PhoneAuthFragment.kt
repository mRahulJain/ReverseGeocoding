package com.android.reversegeocoding.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.android.reversegeocoding.Activities.CreateAccountActivity
import com.android.reversegeocoding.Activities.DetailActivity
import com.android.reversegeocoding.Activities.GeoLocationActivity

import com.android.reversegeocoding.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_phone_auth.*
import kotlinx.android.synthetic.main.fragment_phone_auth.view.*
import java.util.concurrent.TimeUnit

class PhoneAuthFragment : Fragment() {

    lateinit var verificationId : String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_phone_auth, container, false)

        //creating a phoneAuth callback
        val callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                otp!!.setValue(p0!!.smsCode.toString())
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                view!!.reqOTP.isVisible = true
                Toast.makeText(view!!.context, "${p0!!.localizedMessage}", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                view!!.frameSimple1.isVisible = false
                view!!.frameSimple.isVisible = true
                view!!.otp.isVisible = true
                view!!.textSimple.isVisible = true
                view!!.proceed.isVisible = true
                verificationId = p0.toString()
            }
        }

        view!!.reqOTP.setOnClickListener {
            //return on invalid phoneNumber
            if(view!!.phoneNumber.text.toString().length < 10) {
                Toast.makeText(view!!.context, "Phone number not valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val number = "+${phoneNumber.text}"
            view!!.reqOTP.isVisible = false
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                activity!!,
                callback
            )
        }

        view!!.proceed.setOnClickListener {
            if(view!!.otp.value.toString() == "") {
                Toast.makeText(view!!.context, "Enter OTP first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            view!!.proceed.isVisible = false
            val credential = PhoneAuthProvider.getCredential(verificationId, otp.value.toString())
            signInWithCredential(credential)
        }

        return view
    }

    private fun signInWithCredential(credential: PhoneAuthCredential?) {
        val auth = FirebaseAuth.getInstance()
        auth.signInWithCredential(credential as AuthCredential)
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    val intent = Intent(view!!.context, DetailActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(view!!.context, "${it.exception!!.localizedMessage}", Toast.LENGTH_LONG).show()
                    view!!.proceed.isVisible = true
                }
            }
    }

}
