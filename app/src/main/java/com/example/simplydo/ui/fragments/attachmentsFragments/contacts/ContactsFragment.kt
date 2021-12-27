package com.example.simplydo.ui.fragments.attachmentsFragments.contacts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.attachment.SelectedListContactAdapter
import com.example.simplydo.adapters.attachment.SelectionListContactAdapter
import com.example.simplydo.databinding.ContactsListViewBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.utlis.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


internal val TAG = ContactsFragment::class.java.canonicalName

class ContactsFragment :
    Fragment(R.layout.contacts_list_view), View.OnClickListener {


    private lateinit var _binding: ContactsListViewBinding
    private val binding: ContactsListViewBinding get() = _binding


    companion object {
        fun newInstance() = ContactsFragment()
    }

    private lateinit var viewModel: ContactsViewModel
    private lateinit var selectionListContactAdapter: SelectionListContactAdapter
    private lateinit var selectedListContactAdapter: SelectedListContactAdapter

    private var selectedContact = ArrayList<ContactModel>()

    private val requiredPermission = Manifest.permission.READ_CONTACTS

    private val contactAdapterInterface = object : ContactAdapterInterface {
        override fun onContactSelect(item: ContactModel) {
            if (isContactNotAvailable(item)) {
                selectedContact.add(item)
                selectedListContactAdapter.updateDatSet(selectedContact)
            } else {
                selectedContact.remove(item)
                selectedListContactAdapter.updateDatSet(selectedContact)
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
                selectedListContactAdapter.updateDatSet(selectedContact)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ContactsListViewBinding.bind(view)
        setUpViewModel()
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            AppConstant.Key.NAVIGATION_CONTACT_DATA_KEY,
            selectedContact
        )

        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    onPermissionGranted()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Permission required to view contact",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        if (requireContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        } else {
            onPermissionGranted()
        }


    }

    private fun onPermissionGranted() {

        selectedListContactAdapter = SelectedListContactAdapter(selectedContactInterFace)
        binding.recyclerViewSelectedContact.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = selectedListContactAdapter
        }

        selectionListContactAdapter = SelectionListContactAdapter(contactAdapterInterface, requireContext())
        binding.recyclerViewContactList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = selectionListContactAdapter
        }

        binding.btnAddContact.setOnClickListener(this)
        binding.btnClose.setOnClickListener(this)

        getContactList()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btnAddContact -> {
                if (selectedContact.isNotEmpty()) {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        AppConstant.Key.NAVIGATION_CONTACT_DATA_KEY,
                        selectedContact
                    )
                    findNavController().popBackStack()
                } else {
                    AppFunctions.showMessage("No contacts selected", requireContext())
                }
            }
            R.id.btnClose -> {
                findNavController().navigateUp()
            }
        }
    }


    private fun setUpViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                requireContext(),
                appRepository = AppRepository.getInstance(
                    requireContext(),
                    AppDatabase.getInstance(requireContext())
                )
            )
        )[ContactsViewModel::class.java]
        binding.apply {
            lifecycleOwner = this@ContactsFragment
            executePendingBindings()
        }
    }


    private fun getContactList() {
        lifecycleScope.launch {
            viewModel.getContactList(requireContext()).collectLatest { pagingData ->
                Log.i(TAG, "getContactList: pagingData $pagingData")
                selectionListContactAdapter.submitData(pagingData)
            }
        }
    }
}
