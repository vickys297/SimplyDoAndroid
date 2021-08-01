package com.example.simplydo.ui.fragments.attachmentsFragments.gallery

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplydo.adapters.GalleryAdapter
import com.example.simplydo.databinding.GalleryListFragmentBinding
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.GalleryInterface
import com.example.simplydo.utli.SimpleViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal val GALLERY_TAG = GalleryListFragment::class.java.canonicalName

class GalleryListFragment : Fragment() {

    companion object {
        fun newInstance() = GalleryListFragment()
    }

    private lateinit var viewModel: GalleryListViewModel
    lateinit var binding: GalleryListFragmentBinding

    private lateinit var galleryAdapter: GalleryAdapter

    private val selectedGalleryArrayList = ArrayList<GalleryModel>()

    private val galleryInterface = object : GalleryInterface {
        override fun onGallerySelect(galleryModel: GalleryModel) {
            if (isNotDuplicate(galleryModel)) {
                selectedGalleryArrayList.add(galleryModel)
            } else {
                selectedGalleryArrayList.removeAt(selectedGalleryArrayList.indexOf(galleryModel))
            }

            if (selectedGalleryArrayList.isEmpty()) {
                binding.buttonAddImages.visibility = View.GONE
                binding.imageButtonClose.visibility = View.GONE
            } else {
                binding.buttonAddImages.visibility = View.VISIBLE
                binding.imageButtonClose.visibility = View.VISIBLE
            }
        }

        override fun onViewItem(galleryModel: GalleryModel) {
            AppFunctions.showMessage("Show Image Full Screen ", requireContext())
        }
    }

    private fun isNotDuplicate(galleryModel: GalleryModel): Boolean {
        selectedGalleryArrayList.forEach {
            if (it.contentUri == galleryModel.contentUri)
                return false
        }
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = GalleryListFragmentBinding.inflate(inflater, container, false)
        setupViewModel()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        galleryAdapter = GalleryAdapter(requireContext(), galleryInterface)

        binding.recyclerViewGalleryView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = galleryAdapter

            lifecycleScope.launch {
                viewModel.getGalleryDataSource().collectLatest { pagingData ->
                    galleryAdapter.submitData(pagingData)
                }
            }

        }

        binding.buttonAddImages.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                AppConstant.NAVIGATION_GALLERY_DATA_KEY,
                selectedGalleryArrayList
            )
            findNavController().popBackStack()
        }
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this, SimpleViewModelFactory(requireContext())).get(
            GalleryListViewModel::class.java
        )
        binding.apply {
            lifecycleOwner = this@GalleryListFragment
            executePendingBindings()
        }
    }
}