package com.example.simplydo.ui.fragments.gallaryListView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.databinding.GalleryListFragmentBinding
import com.example.simplydo.utli.SimpleViewModelFactory

class GalleryListFragment : Fragment() {

    companion object {
        fun newInstance() = GalleryListFragment()
    }

    private lateinit var viewModel: GalleryListViewModel
    lateinit var binding: GalleryListFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = GalleryListFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel

        setupObserver()
        setupViewModel()

        
        viewModel.getGalleryItemCount(requireActivity())
    }

    private fun setupObserver() {

    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, SimpleViewModelFactory(requireContext())).get(GalleryListViewModel::class.java)
        binding.apply {
            lifecycleOwner = this@GalleryListFragment
            executePendingBindings()
        }
    }
}