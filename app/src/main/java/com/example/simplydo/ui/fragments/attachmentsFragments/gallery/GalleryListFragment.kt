package com.example.simplydo.ui.fragments.attachmentsFragments.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.attachment.SelectionListGalleryAdapter
import com.example.simplydo.databinding.FragmentGalleryViewBinding
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions
import com.example.simplydo.utlis.GalleryInterface
import com.example.simplydo.utlis.SimpleViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal val GALLERY_TAG = GalleryListFragment::class.java.canonicalName

class GalleryListFragment() : Fragment(R.layout.fragment_gallery_view) {

    companion object {
        fun newInstance() = GalleryListFragment()
    }

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private lateinit var viewModel: GalleryListViewModel
    lateinit var binding: FragmentGalleryViewBinding

    private lateinit var selectionListGalleryAdapter: SelectionListGalleryAdapter

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentGalleryViewBinding.bind(view)
        setupViewModel()

        val requestFileStoragePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    onPermissionGranted()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Required Permission to View Images",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        if (requireContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            // request Permission
            requestFileStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            onPermissionGranted()
        }
    }

    private fun onPermissionGranted() {
        selectionListGalleryAdapter = SelectionListGalleryAdapter(requireContext(), galleryInterface)
        binding.recyclerViewGalleryView.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = selectionListGalleryAdapter

            lifecycleScope.launch {
                viewModel.getGalleryDataSource().collectLatest { pagingData ->
                    selectionListGalleryAdapter.submitData(pagingData)
                }
            }
        }

        binding.buttonAddImages.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                AppConstant.Key.NAVIGATION_GALLERY_DATA_KEY,
                selectedGalleryArrayList
            )
            findNavController().popBackStack()
        }
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            SimpleViewModelFactory(requireContext())
        )[GalleryListViewModel::class.java]
        binding.apply {
            lifecycleOwner = this@GalleryListFragment
            executePendingBindings()
        }
    }
}