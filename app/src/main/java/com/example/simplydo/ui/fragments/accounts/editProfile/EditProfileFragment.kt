package com.example.simplydo.ui.fragments.accounts.editProfile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R
import com.example.simplydo.databinding.EditProfileFragmentBinding

class EditProfileFragment : Fragment(R.layout.edit_profile_fragment) {

    private lateinit var binding: EditProfileFragmentBinding
    private lateinit var viewModel: EditProfileViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = EditProfileFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this)[EditProfileViewModel::class.java]
    }

}