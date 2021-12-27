package com.example.simplydo.adapters.viewFullScreenContent

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.simplydo.components.FullScreenImageFragment

class ImageSlideAdapter(
    activity: FragmentActivity,
    val dataSet: ArrayList<FullScreenImageFragment>,
    val imageStartPosition: Int
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun createFragment(position: Int): Fragment {
        return dataSet[position]
    }

}