package com.example.simplydo.dialog.bottomSheetDialogs.workspaceDialog.workspaceDialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.example.simplydo.R
import com.example.simplydo.databinding.FragmentWorkspaceTaskBottomSheetDialogBinding
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppPreference
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class WorkspaceTaskBottomSheetDialog(private val requireContext: Context, val callback: Callback) :
    BottomSheetDialogFragment() {

    private lateinit var binding: FragmentWorkspaceTaskBottomSheetDialogBinding

    /*
    * Order By
    * 1 - Ascending
    * 2 - Descending
    * */
    private var orderBy: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkspaceTaskBottomSheetDialogBinding.inflate(
            LayoutInflater.from(requireContext),
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderBy =
            AppPreference.getPreferences(AppConstant.Key.PREFERRED_ORDER_BY, 1, requireContext)
        updatePreferredOrderBy()

        binding.buttonByDate.setOnClickListener {
            callback.filterByDate(orderBy)
            dismiss()
        }

        binding.buttonByPriority.setOnClickListener {
            callback.filterByPriority(orderBy)
            dismiss()
        }

        binding.buttonByStage.setOnClickListener {
            callback.filterByStage(orderBy)
            dismiss()
        }


        binding.textViewOrderByAscending.setOnClickListener {
            orderBy = 1
            updatePreferredOrderBy()
            AppPreference.storePreferences(
                AppConstant.Key.PREFERRED_ORDER_BY,
                orderBy,
                requireContext
            )
        }
        binding.textViewOrderByDescending.setOnClickListener {
            orderBy = 2
            updatePreferredOrderBy()
            AppPreference.storePreferences(
                AppConstant.Key.PREFERRED_ORDER_BY,
                orderBy,
                requireContext
            )
        }


        binding.buttonClearFilter.setOnClickListener {
            callback.filterByStage(1)
            AppPreference.storePreferences(
                AppConstant.Key.PREFERRED_ORDER_BY,
                1,
                requireContext
            )
            dismiss()
        }

        binding.imageButtonClose.setOnClickListener {
            dismiss()
        }
    }

    private fun updatePreferredOrderBy() {
        if (orderBy == 1) {
            binding.textViewOrderByAscending.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.ic_checked,
                    null
                ),
                null
            )
            binding.textViewOrderByDescending.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
        } else {
            binding.textViewOrderByAscending.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
            binding.textViewOrderByDescending.setCompoundDrawablesWithIntrinsicBounds(
                null,
                null,
                ResourcesCompat.getDrawable(
                    requireContext().resources,
                    R.drawable.ic_checked,
                    null
                ),
                null
            )
        }
    }

    companion object {
        fun newInstance(
            requireContext: Context,
            callback: Callback
        ) = WorkspaceTaskBottomSheetDialog(requireContext, callback)
    }

    interface Callback {
        fun filterByDate(orderBy: Int)
        fun filterByPriority(orderBy: Int)
        fun filterByStage(orderBy: Int)
    }
}