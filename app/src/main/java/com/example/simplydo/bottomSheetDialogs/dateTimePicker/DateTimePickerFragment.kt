package com.example.simplydo.bottomSheetDialogs.dateTimePicker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.simplydo.R
import com.example.simplydo.bottomSheetDialogs.dateTimePicker.layout.DatePickerFragment
import com.example.simplydo.bottomSheetDialogs.dateTimePicker.layout.TimePickerFragment
import com.example.simplydo.databinding.DateTimePickerFragmentBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DateTimePickerFragment : DialogFragment() {

    companion object {
        fun newInstance() = DateTimePickerFragment()
    }

    private lateinit var _binding: DateTimePickerFragmentBinding
    private val binding: DateTimePickerFragmentBinding get() = _binding

    private lateinit var viewModel: DateTimePickerViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DateTimePickerFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(DateTimePickerViewModel::class.java)

        val tabLayoutAdapter = TabLayoutAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewpager) { tab, position ->
        }.attach()

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Date"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Time"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewpager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        binding.viewpager.adapter = tabLayoutAdapter

    }


}

private class TabLayoutAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2


    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> DatePickerFragment.newInstance()
            else -> TimePickerFragment.newInstance()
        }
    }


}