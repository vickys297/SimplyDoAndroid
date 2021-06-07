package com.example.simplydo.ui.fragments.gallaryListView

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplydo.databinding.GalleryListFragmentBinding
import com.example.simplydo.utli.SimpleViewModelFactory
import com.example.simplydo.utli.adapters.GalleryAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal val GALLERY_TAG = GalleryListFragment::class.java.canonicalName
class GalleryListFragment : Fragment() {

    companion object {
        fun newInstance() = GalleryListFragment()
    }

    private lateinit var viewModel: GalleryListViewModel
    lateinit var binding: GalleryListFragmentBinding

    lateinit var galleryAdapter: GalleryAdapter

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


        galleryAdapter = GalleryAdapter(requireContext())
        binding.recyclerViewGalleryView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = galleryAdapter
        }
        setupViewModel()
        setupObserver()


    }

    private fun setupObserver() {
        lifecycleScope.launch {
            viewModel.flow.collectLatest { pagingData ->
                Log.i(GALLERY_TAG, "setupObserver: ")
                galleryAdapter.submitData(pagingData)
            }
        }
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, SimpleViewModelFactory(requireContext())).get(
            GalleryListViewModel::class.java)
        binding.apply {
            lifecycleOwner = this@GalleryListFragment
            executePendingBindings()
        }


    }
}