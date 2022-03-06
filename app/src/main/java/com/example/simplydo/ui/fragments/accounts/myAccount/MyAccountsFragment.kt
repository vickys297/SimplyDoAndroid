package com.example.simplydo.ui.fragments.accounts.myAccount

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.myAccounts.MyAccountAdapter
import com.example.simplydo.databinding.MyAccountsFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.AccountModel
import com.example.simplydo.utlis.AppRepository
import com.example.simplydo.utlis.ViewModelFactory

class MyAccountsFragment : Fragment(R.layout.my_accounts_fragment) {

    companion object {
        fun newInstance() = MyAccountsFragment()
    }

    private lateinit var viewModel: MyAccountsViewModel
    private lateinit var binding: MyAccountsFragmentBinding
    private lateinit var myAccountAdapter: MyAccountAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = MyAccountsFragmentBinding.bind(view)
        setupViewModel()
        setupObservers()

        loadAccountList()
        binding.buttonAddNewAccount.setOnClickListener {
            findNavController().navigate(R.id.action_settings_myAccountsFragment_to_addAccountFragment)
        }
    }

    private fun loadAccountList() {
        val dataset = arrayListOf(
            AccountModel(
                profilePicture = "https://picsum.photos/200",
                firstName = "Vignesh",
                middleName = "",
                lastName = "Selvam",
                email = "vignesh297@gmail.com",
                phone = "8667353860",
                uKey = "2893179847872169462913649",
                title = "Personal"
            ),
            AccountModel(
                profilePicture = "https://picsum.photos/200",
                firstName = "Vignesh",
                middleName = "",
                lastName = "Selvam",
                email = "vignesh297@gmail.com",
                phone = "8667353860",
                uKey = "2893179847872169462913649",
                title = "Simply Do"
            ),
            AccountModel(
                profilePicture = "https://picsum.photos/200",
                firstName = "Vignesh",
                middleName = "",
                lastName = "Selvam",
                email = "vignesh297@gmail.com",
                phone = "8667353860",
                uKey = "2893179847872169462913649",
                title = "Oxcy"
            )
        )
        myAccountAdapter = MyAccountAdapter(dataset = dataset)
        binding.recyclerViewMyAccount.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myAccountAdapter
        }
    }

    private fun setupObservers() {

    }

    private fun setupViewModel() {
        val repository =
            AppRepository.getInstance(requireContext(), AppDatabase.getInstance(requireContext()))
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(requireContext(), repository)
        )[MyAccountsViewModel::class.java]
    }


}