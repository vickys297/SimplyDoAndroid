package com.example.simplydo.ui.activity.screens

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.simplydo.R
import com.example.simplydo.databinding.FragmentPage4Binding
import com.example.simplydo.utlis.Page4Interface


class Page4(val page4Interface: Page4Interface) : Fragment(R.layout.fragment_page4) {
    private lateinit var _binding: FragmentPage4Binding
    private val binding: FragmentPage4Binding get() = _binding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPage4Binding.bind(view)


        binding.buttonStart.setOnClickListener {

            page4Interface.onStart()

        }
    }

    companion object {
        @JvmStatic
        fun newInstance(page4Interface: Page4Interface) = Page4(page4Interface)
    }
}