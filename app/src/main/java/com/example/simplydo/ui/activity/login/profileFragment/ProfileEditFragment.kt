package com.example.simplydo.ui.activity.login.profileFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.simplydo.R
import com.example.simplydo.databinding.FragmentProfileEditBinding

class ProfileEditFragment : Fragment(R.layout.fragment_profile_edit), View.OnClickListener {

    companion object {
        fun newInstance() = ProfileEditFragment()
    }

    private lateinit var viewModel: ProfileEditViewModel
    private lateinit var binding: FragmentProfileEditBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileEditBinding.bind(view)
        viewModel = ViewModelProvider(this)[ProfileEditViewModel::class.java]

        binding.buttonCreateProfile.setOnClickListener(this@ProfileEditFragment)
        binding.imageButtonBack.setOnClickListener(this@ProfileEditFragment)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.buttonCreateProfile.id -> {

            }
            binding.imageButtonBack.id -> {
                findNavController().navigateUp()
            }
        }
    }
}