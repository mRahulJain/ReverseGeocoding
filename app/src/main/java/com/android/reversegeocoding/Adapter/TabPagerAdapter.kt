package com.android.reversegeocoding.Adapter

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.android.reversegeocoding.Fragments.EmailAuthFragment
import com.android.reversegeocoding.Fragments.PhoneAuthFragment
import com.android.reversegeocoding.R

private val TAB_TITLES = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_2
)

class TabPagerAdapter(private val context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        lateinit var fragment : Fragment
        when(position) {
            0 -> fragment = PhoneAuthFragment()
            1 -> fragment = EmailAuthFragment()
            else -> {
                Toast.makeText(context, "Select a fragment", Toast.LENGTH_SHORT).show()
            }
        }
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 2 total pages.
        return 2
    }
}