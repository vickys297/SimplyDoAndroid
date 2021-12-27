package com.example.simplydo.components

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.simplydo.R
import com.example.simplydo.databinding.FullScreenImageFragmentBinding

class FullScreenImageFragment(val title: String, private val contentUri: String) :
    Fragment(R.layout.full_screen_image_fragment) {

    companion object {
        fun newInstance(title: String, contentUri: String, index: Int) =
            FullScreenImageFragment(title, contentUri)
    }

    private lateinit var viewModel: FullScreenImageViewModel

    private lateinit var binding: FullScreenImageFragmentBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FullScreenImageFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this)[FullScreenImageViewModel::class.java]
        binding.textViewTitle.text = title
        Glide.with(requireContext())
            .load(contentUri)
            .into(binding.imageView)
    }

}