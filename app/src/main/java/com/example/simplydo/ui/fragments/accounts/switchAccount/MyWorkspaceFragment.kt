package com.example.simplydo.ui.fragments.accounts.linkAccount

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.MyWorkspaceAdapter
import com.example.simplydo.databinding.AvailableWorkspaceFragmentBinding
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.model.WorkspaceModel
import com.example.simplydo.ui.fragments.accounts.switchAccount.MyWorkspaceViewModel
import com.example.simplydo.utils.AppConstant
import com.example.simplydo.utils.AppPreference
import com.example.simplydo.utils.AppRepository
import com.example.simplydo.utils.ViewModelFactory

class MyWorkspaceFragment : Fragment(R.layout.available_workspace_fragment) {


    private lateinit var observerWorkspace: Observer<ArrayList<WorkspaceModel>>

    private lateinit var viewModel: MyWorkspaceViewModel
    private lateinit var binding: AvailableWorkspaceFragmentBinding
    private lateinit var myWorkspaceAdapter: MyWorkspaceAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AvailableWorkspaceFragmentBinding.bind(view)
        setupBinding()

        myWorkspaceAdapter = MyWorkspaceAdapter(null)
        binding.recyclerViewWorkspaceAccounts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myWorkspaceAdapter
        }

        viewModel.getAvailableWorkspace()

        binding.buttonCreateWorkspace.setOnClickListener {
            if (AppPreference.getPreferences(
                    AppConstant.Preferences.IS_PAID_USER,
                    false,
                    requireContext()
                )
            ) {
//                findNavController().navigate(R.id.action_myWorkspaceFragment_to_createWorkspaceFragment)
            } else {
                Toast.makeText(requireContext(), "You don't have any plan", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }


    private fun setupBinding() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val repository = AppRepository.getInstance(requireContext(), appDatabase)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), repository)
        )[MyWorkspaceViewModel::class.java]

        binding.apply {
            lifecycleOwner = this@MyWorkspaceFragment.viewLifecycleOwner
            viewModel = this@MyWorkspaceFragment.viewModel
            executePendingBindings()
        }

        observerWorkspace = Observer {
            it?.let {
                loadWorkspace(it)
            }
        }
        viewModel.mutableWorkspace.observe(viewLifecycleOwner, observerWorkspace)
        viewModel.mutableWorkspace.postValue(null)
    }

    private fun loadWorkspace(it: ArrayList<WorkspaceModel>) {
        myWorkspaceAdapter.loadData(it)
    }
}