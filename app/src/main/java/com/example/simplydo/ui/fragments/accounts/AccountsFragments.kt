package com.example.simplydo.ui.fragments.accounts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.SettingsItemAdapter
import com.example.simplydo.databinding.AccountsFragmentBinding
import com.example.simplydo.model.SettingsItemModel

class AccountsFragments : Fragment(R.layout.accounts_fragment), View.OnClickListener {

    companion object {
        fun newInstance() = AccountsFragments()
    }

    private lateinit var binding: AccountsFragmentBinding

    private lateinit var settingAdapter: SettingsItemAdapter
    private lateinit var viewModel: AccountsViewModel
    private val settingArrayItem: ArrayList<SettingsItemModel> = ArrayList()

    init {
        settingArrayItem.add(
            SettingsItemModel(
                R.drawable.ic_notification,
                "Accounts",
                "Personal, Organization accounts",
                R.id.action_settings_accountsFragments_to_myAccountsFragment
            )
        )

        settingArrayItem.add(
            SettingsItemModel(
                R.drawable.ic_notification,
                "Notification",
                "Notifications, Alerts & Tasks",
                R.id.action_settings_accountsFragments_to_notificationSettingsFragment
            )
        )

        settingArrayItem.add(
            SettingsItemModel(
                R.drawable.ic_verified,
                "Privacy Policy",
                "Help center, Contact Us",
                -1
            )
        )

        settingArrayItem.add(
            SettingsItemModel(
                R.drawable.ic_terms_of_service,
                "Terms of Services",
                "Downloads, Media access, Data Usage and Storage",
                -1
            )
        )

        settingArrayItem.add(
            SettingsItemModel(
                R.drawable.ic_support,
                "Supports",
                "Downloads, Media access, Data Usage and Storage",
                -1
            )
        )
    }

    private val settingCallback: SettingsItemAdapter.SettingsCallback =
        object : SettingsItemAdapter.SettingsCallback {
            override fun onSettingsClick(item: SettingsItemModel) {
                val indexOf = settingArrayItem.indexOf(item)
                settingArrayItem[indexOf].let {
                    if (it.action != -1) {
                        findNavController().navigate(it.action)
                    }
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AccountsFragmentBinding.bind(view)
        setupViewModel()

        settingAdapter =
            SettingsItemAdapter(dataSet = settingArrayItem, settingsCallback = settingCallback)
        binding.recyclerViewSettings.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = settingAdapter
        }

        binding.buttonEditProfile.setOnClickListener(this)
        binding.textViewSwitchAccount.setOnClickListener(this)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[AccountsViewModel::class.java]
        binding.apply {
            executePendingBindings()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            binding.buttonEditProfile.id -> {
                findNavController().navigate(R.id.action_settings_accountsFragments_to_editProfileFragment)
            }
            binding.textViewSwitchAccount.id -> {
                findNavController().navigate(R.id.action_settings_accountsFragments_to_myWorkspaceFragment)
            }
        }
    }
}