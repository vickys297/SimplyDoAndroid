package com.example.simplydo.ui.fragments.accounts.addAccount

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R
import com.example.simplydo.databinding.AddAccountFragmentBinding

class AddAccountFragment : Fragment(R.layout.add_account_fragment) {

    companion object {
        fun newInstance() = AddAccountFragment()
    }

    private lateinit var binding: AddAccountFragmentBinding
    private lateinit var viewModel: AddAccountViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddAccountFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this)[AddAccountViewModel::class.java]
    }

}