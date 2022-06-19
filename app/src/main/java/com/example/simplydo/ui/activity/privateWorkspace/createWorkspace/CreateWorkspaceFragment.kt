package com.example.simplydo.ui.activity.privateWorkspace.createWorkspace

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.simplydo.R
import com.example.simplydo.databinding.CreateWorkspaceFragmentBinding
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.utils.AppRepository
import com.example.simplydo.utils.ViewModelFactory

class CreateWorkspaceFragment : Fragment(R.layout.create_workspace_fragment) {

    private lateinit var binding: CreateWorkspaceFragmentBinding
    private lateinit var viewModel: CreateWorkspaceViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CreateWorkspaceFragmentBinding.bind(view)
        setupViewModel()

        val accentText = "<font color='#6200EE'><br>Workspace</font>"
        val create = "<font color='#6200EE'>Create</font>"

        val workspaceTextTitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(
                String.format("Hi, %s your %s", create, accentText),
                Html.FROM_HTML_MODE_LEGACY
            )
        } else {
            Html.fromHtml(String.format("Hi, %s your %s", create, accentText))
        }

        binding.textViewCreateWorkspaceTitle.text = workspaceTextTitle
        binding.linearLayoutWorkspace.setOnClickListener {
            binding.editTextWorkspace.performClick()
        }
        binding.imageButtonClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.buttonCreateNewWorkspace.setOnClickListener {

            viewModel.createNewWorkSpace(
                binding.editTextWorkspace.text.toString(),
                requireContext()
            )

            findNavController().navigateUp()

//            findNavController().navigate(R.id.action_createWorkspaceFragment_to_plansFragment)
            /*if (AppPreference.getPreferences(
                    AppConstant.Preferences.IS_PAID_USER,
                    false,
                    requireContext()
                )
            ) {
                viewModel.createNewWorkSpace(
                    binding.editTextWorkspace.text.toString(),
                    requireContext()
                )
            } else {
                // open subscription plan
                findNavController().navigate(R.id.action_createWorkspaceFragment_to_plansFragment)
            }*/
        }
    }

    private fun setupViewModel() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val repository = AppRepository.getInstance(requireContext(), appDatabase)
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), repository)
        )[CreateWorkspaceViewModel::class.java]
        binding.apply {
            lifecycleOwner = this@CreateWorkspaceFragment.viewLifecycleOwner
            viewModel = this@CreateWorkspaceFragment.viewModel
            executePendingBindings()
        }
    }


}