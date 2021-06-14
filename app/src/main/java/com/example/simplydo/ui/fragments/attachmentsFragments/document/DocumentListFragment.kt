package com.example.simplydo.ui.fragments.attachmentsFragments.document

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.databinding.DocumentListFragmentBinding
import com.example.simplydo.utli.adapters.DocumentAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DocumentListFragment : Fragment(R.layout.document_list_fragment) {

    companion object {
        fun newInstance() = DocumentListFragment()
    }

    private lateinit var _binding: DocumentListFragmentBinding

    private lateinit var documentAdapter: DocumentAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    private lateinit var viewModel: DocumentListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DocumentListFragmentBinding.bind(view)

        viewModel = ViewModelProvider(this).get(DocumentListViewModel::class.java)

        documentAdapter = DocumentAdapter()
        binding.recyclerViewDocumentList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = documentAdapter
        }

        checkPermission()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_CONTACTS),
                100
            )
        } else {
            getDocument()
        }
    }

    private fun getDocument() {
        lifecycleScope.launch {
            viewModel.getDocument(requireActivity()).collectLatest {
                documentAdapter.submitData(it)
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
            getDocument()
        } else {
            Toast.makeText(
                requireContext(),
                "Permission required to view contact",
                Toast.LENGTH_LONG
            ).show()
        }

    }

}