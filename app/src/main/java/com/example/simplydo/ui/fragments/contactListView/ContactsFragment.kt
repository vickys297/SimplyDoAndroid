package com.example.simplydo.ui.fragments.contactListView

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.databinding.ContactsListViewBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.attachmentModel.ContactModel
import com.example.simplydo.utli.AppRepository
import com.example.simplydo.utli.ContactAdapterInterface
import com.example.simplydo.utli.ViewModelFactory
import com.example.simplydo.utli.adapters.ContactAdapter
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

    private var selectedContact = ArrayList<ContactModel>()

    private val contactAdapterInterface = object :ContactAdapterInterface{
        override fun onContactSelect(item: ContactModel) {
            selectedContact.add(item)
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

        binding.recyclerViewContactList.layoutManager = LinearLayoutManager(requireContext())
        contactAdapter = ContactAdapter(contactAdapterInterface)
        binding.recyclerViewContactList.adapter = contactAdapter

        binding.recyclerViewSelectedContact.layoutManager = LinearLayoutManager(requireContext())


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
            viewModel.flow.collectLatest { pagingData ->
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