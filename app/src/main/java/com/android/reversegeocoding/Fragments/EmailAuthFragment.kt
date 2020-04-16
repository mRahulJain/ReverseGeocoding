package com.android.reversegeocoding.Fragments

import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold

import com.android.reversegeocoding.R
import kotlinx.android.synthetic.main.fragment_email_auth.view.*

class EmailAuthFragment : Fragment() {

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



        return view
    }

}
