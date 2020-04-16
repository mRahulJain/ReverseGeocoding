package com.android.reversegeocoding.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import com.android.reversegeocoding.Adapter.TabPagerAdapter
import com.android.reversegeocoding.R
import com.google.android.material.tabs.TabLayout

class CreateAccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        //Set the tabLayout Adapter
        val profilePagerAdapter = TabPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager1)
        viewPager.adapter = profilePagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs1)
        tabs.setupWithViewPager(viewPager)
    }
}
