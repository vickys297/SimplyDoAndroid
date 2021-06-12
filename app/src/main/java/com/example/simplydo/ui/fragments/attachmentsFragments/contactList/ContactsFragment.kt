package com.example.simplydo.ui.fragments.attachmentsFragments.contactList

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.databinding.ContactsListViewBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.adapters.ContactAdapter
import com.example.simplydo.utli.adapters.SelectedContactAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


internal val TAG = ContactsFragment::class.java.canonicalName

class ContactsFragment :
    Fragment() {

    lateinit var binding: ContactsListViewBinding


    companion object {
        fun newInstance() = ContactsFragment()
    }

    private lateinit var viewModel: ContactsViewModel
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var selectedContactAdapter: SelectedContactAdapter

    private var selectedContact = ArrayList<ContactModel>()

    private val contactAdapterInterface = object : ContactAdapterInterface {
        override fun onContactSelect(item: ContactModel) {
            if (isContactNotAvailable(item)) {
                selectedContact.add(item)
                selectedContactAdapter.updateDatSet(selectedContact)
            } else {
                selectedContact.remove(item)
                selectedContactAdapter.updateDatSet(selectedContact)
            }
        }
    }

    private fun isContactNotAvailable(item: ContactModel): Boolean {
        selectedContact.forEach {
            if (it.mobile == item.mobile) {
                return false
            }
        }
        return true
    }


    private val selectedContactInterFace = object : SelectedContactInterface {
        override fun onContactRemove(item: ContactModel) {
            if (selectedContact.contains(item)) {
                selectedContact.remove(item)
                selectedContactAdapter.updateDatSet(selectedContact)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ContactsListViewBinding.inflate(inflater, container, false)
        setUpViewModel()
        return binding.root
    }

    private fun setUpViewModel() {
        viewModel = ViewModelProvider(this,
            ViewModelFactory(requireContext(),
                appRepository = AppRepository.getInstance(requireContext(),
                    AppDatabase.getInstance(requireContext())))).get(ContactsViewModel::class.java)
        binding.apply {
            lifecycleOwner = this@ContactsFragment
            executePendingBindings()
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        selectedContactAdapter = SelectedContactAdapter(selectedContactInterFace)
        binding.recyclerViewSelectedContact.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = selectedContactAdapter
        }

        contactAdapter = ContactAdapter(contactAdapterInterface, requireContext())

        binding.recyclerViewContactList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }

        binding.btnAddContact.setOnClickListener {
            if (selectedContact.isNotEmpty()) {
                findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.NAVIGATION_CONTACT_DATA_KEY,
                    selectedContact)
                findNavController().popBackStack()
            } else {
                AppFunctions.showMessage("No contacts selected", requireContext())
            }
        }

        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }


    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }


    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                100)
        } else {
            getContactList()
        }
    }

    private fun getContactList() {
        lifecycleScope.launch {
            viewModel.getContactList(requireContext()).collectLatest { pagingData ->
                Log.i(TAG, "getContactList: pagingData $pagingData")
                contactAdapter.submitData(pagingData)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContactList()
        } else {
            Toast.makeText(requireContext(),
                "Permission required to view contact",
                Toast.LENGTH_LONG).show()
        }

    }

}