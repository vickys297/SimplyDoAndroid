package com.example.simplydo.ui.fragments.fullScreenContent

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R
import com.example.simplydo.adapters.viewFullScreenContent.ImageSlideAdapter
import com.example.simplydo.components.FullScreenImageFragment
import com.example.simplydo.databinding.ViewContentFullScreenFragmentBinding
import com.example.simplydo.model.attachmentModel.GalleryModel

class ImageSliderFullScreenFragment : Fragment(R.layout.view_content_full_screen_fragment) {

    private lateinit var _binding: ViewContentFullScreenFragmentBinding
    private val binding: ViewContentFullScreenFragmentBinding get() = _binding
    lateinit var imageSlideAdapter: ImageSlideAdapter

    private lateinit var viewModel: ViewContentFullScreenViewModel
    private var imageStartPosition = 0
    private lateinit var arrayListGalleryDataSet: ArrayList<GalleryModel>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = ViewContentFullScreenFragmentBinding.bind(view)
        setupViewModel()
        arguments?.let {
            imageStartPosition = it.getInt("currentPosition",0)
            val dataSet = it.getSerializable("ImageKey")
            arrayListGalleryDataSet = dataSet as ArrayList<GalleryModel>
        }

        val dataSet = loadViewPagerAdapter(arrayListGalleryDataSet)
        imageSlideAdapter = ImageSlideAdapter(requireActivity(), dataSet, imageStartPosition)
        binding.viewPager.apply {
            adapter = imageSlideAdapter
        }

        Handler(Looper.getMainLooper()).postDelayed({
            binding.viewPager.setCurrentItem(imageStartPosition, true)
        }, 100)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[ViewContentFullScreenViewModel::class.java]

        binding.apply {
            lifecycleOwner = this@ImageSliderFullScreenFragment.viewLifecycleOwner
            executePendingBindings()
        }
    }

    private fun loadViewPagerAdapter(arrayListGalleryDataSet: ArrayList<GalleryModel>): ArrayList<FullScreenImageFragment> {
        val arrayList = ArrayList<FullScreenImageFragment>()
        for ((index, item) in arrayListGalleryDataSet.withIndex()) {
            arrayList.add(FullScreenImageFragment.newInstance(item.name, item.contentUri, index))
        }
        return arrayList
    }

}