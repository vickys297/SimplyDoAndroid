package com.example.simplydo.dialog.bottomSheetDialogs.tags

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R
import com.example.simplydo.databinding.TagsBottomSheetDialogFragmentBinding
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.model.TagModel
import com.example.simplydo.utils.AppInterface
import com.example.simplydo.utils.AppRepository
import com.example.simplydo.utils.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip

class TagsBottomSheetDialog(
    private val parentContext: Context,
    private val callback: AppInterface.TagDialog.Callback
) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(parentContext: Context, callback: AppInterface.TagDialog.Callback) =
            TagsBottomSheetDialog(parentContext, callback)
    }


    private lateinit var viewModel: TagsBottomSheetDialogViewModel

    private lateinit var _binding: TagsBottomSheetDialogFragmentBinding
    private val binding: TagsBottomSheetDialogFragmentBinding get() = _binding


    private var availableTags: ArrayList<TagModel> = ArrayList()
    private var selectedTag: ArrayList<TagModel> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TagsBottomSheetDialogFragmentBinding.inflate(
            LayoutInflater.from(parentContext),
            container,
            false
        )
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        availableTags = viewModel.getAvailableTags()
        loadAvailableTags()

        binding.buttonDone.setOnClickListener {
            callback.onDone(selectedTag)
            dismiss()
        }
    }

    private fun loadAvailableTags() {
        binding.chipGroupAvailableTags.removeAllViews()
        for (tag in availableTags) {
            val chipTag = Chip(requireContext())
            chipTag.text = tag.tagName
            chipTag.setChipBackgroundColorResource(R.color.colorPrimary)
            chipTag.isCloseIconVisible = true
            chipTag.setTextColor(
                requireContext().resources.getColor(
                    R.color.white,
                    requireContext().theme
                )
            )
            chipTag.closeIcon = ResourcesCompat.getDrawable(
                requireContext().resources,
                R.drawable.white_add,
                requireContext().theme
            )
            chipTag.setOnClickListener {
                addToSelectedTag(tag)
                binding.chipGroupAvailableTags.removeView(it)
                binding.textViewNotTagsAvailable.isVisible = availableTags.isEmpty()
            }
            binding.chipGroupAvailableTags.addView(chipTag)
        }
        binding.textViewNotTagsAvailable.isVisible = availableTags.isEmpty()

    }

    private fun addToSelectedTag(tagName: TagModel) {
        availableTags.remove(tagName)
        selectedTag.add(tagName)
        loadSelectedTag()
    }

    private fun loadSelectedTag() {
        binding.chipGroupSelectedTags.removeAllViews()
        for (tag in selectedTag) {
            val chipTag = Chip(requireContext())
            chipTag.text = tag.tagName
            chipTag.setChipBackgroundColorResource(R.color.colorPrimary)
            chipTag.isCloseIconVisible = true
            chipTag.setTextColor(
                requireContext().resources.getColor(
                    R.color.white,
                    requireContext().theme
                )
            )
            chipTag.setOnClickListener {
                addToAvailableTag(tag)
                binding.chipGroupSelectedTags.removeView(it)
                binding.textViewTagsNotSelected.isVisible = selectedTag.isEmpty()
            }
            binding.chipGroupSelectedTags.addView(chipTag)
        }
        binding.textViewTagsNotSelected.isVisible = selectedTag.isEmpty()
    }

    private fun addToAvailableTag(tagName: TagModel) {
        availableTags.add(tagName)
        selectedTag.remove(tagName)
        loadAvailableTags()
    }

    private fun setupViewModel() {
        val viewModelFactory = ViewModelFactory(
            requireContext(),
            AppRepository.getInstance(requireContext(), AppDatabase.getInstance(requireContext()))
        )
        viewModel =
            ViewModelProvider(this, viewModelFactory)[TagsBottomSheetDialogViewModel::class.java]
        binding.apply {
            viewModel = this@TagsBottomSheetDialog.viewModel
            lifecycleOwner = this@TagsBottomSheetDialog
            executePendingBindings()
        }
    }

}