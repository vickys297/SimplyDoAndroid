package com.example.simplydo.ui.activity

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import java.util.*

class OnBoardViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(
        fragmentManager,
        lifecycle
    ) {

    var arrayList = ArrayList<Fragment>()

    override fun createFragment(position: Int): Fragment {
        return arrayList[position]
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun addFragment(fragment: Fragment) {
        arrayList.add(fragment)
    }
}