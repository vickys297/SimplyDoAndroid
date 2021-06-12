package com.example.simplydo.ui.fragments.attachmentsFragments.documentListView

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R
import com.example.simplydo.databinding.DocumentListFragmentBinding

class DocumentListFragment : Fragment(R.layout.document_list_fragment) {

    companion object {
        fun newInstance() = DocumentListFragment()
    }

    private lateinit var _binding: DocumentListFragmentBinding

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding

    private lateinit var viewModel: DocumentListViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = DocumentListFragmentBinding.bind(view)

        viewModel = ViewModelProvider(this).get(DocumentListViewModel::class.java)

    }

}