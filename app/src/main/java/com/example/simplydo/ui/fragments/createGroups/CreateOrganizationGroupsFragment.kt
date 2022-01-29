package com.example.simplydo.ui.fragments.createGroups

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R
import com.example.simplydo.databinding.CreateOrganizationGroupsFragmentBinding

class CreateOrganizationGroupsFragment : Fragment(R.layout.create_organization_groups_fragment) {

    companion object {
        fun newInstance() = CreateOrganizationGroupsFragment()
    }

    private lateinit var viewModel: CreateOrganizationGroupsViewModel
    private lateinit var binding: CreateOrganizationGroupsFragmentBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CreateOrganizationGroupsFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this)[CreateOrganizationGroupsViewModel::class.java]

        val accentText = "<font color='#6200EE'><br>Group</font>"
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
    }


}