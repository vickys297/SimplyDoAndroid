package com.example.simplydo.screens.todoList.addTodoBasic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.databinding.FragmentAddTodoBasicListDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    AddTodoBasic.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class AddTodoBasic : BottomSheetDialogFragment() {

    private var _binding: FragmentAddTodoBasicListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentAddTodoBasicListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }





    companion object {

        fun newInstance(itemCount: Int): AddTodoBasic =
            AddTodoBasic().apply {
                arguments = Bundle().apply {
                    putInt(ARG_ITEM_COUNT, itemCount)
                }
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}