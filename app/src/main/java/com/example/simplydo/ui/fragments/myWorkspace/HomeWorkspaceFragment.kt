package com.example.simplydo.ui.fragments.myWorkspace

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R
import com.example.simplydo.databinding.HomeWorkspaceFragmentBinding

class HomeWorkspaceFragment : Fragment(R.layout.home_workspace_fragment) {

    companion object {
        fun newInstance() = HomeWorkspaceFragment()
    }

    private lateinit var viewModel: HomeWorkspaceViewModel
    private lateinit var binding: HomeWorkspaceFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = HomeWorkspaceFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this)[HomeWorkspaceViewModel::class.java]




    }


}