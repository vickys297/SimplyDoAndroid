package com.example.simplydo.ui.fragments.addOrEditTodoTask.addItem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.bottomSheetDialogs.AddItemBottomSheetModel
import com.example.simplydo.databinding.AddNewTaskItemFragmentBinding
import com.example.simplydo.utli.AppInterface

class AddNewTaskItemFragment : Fragment(R.layout.add_new_task_item_fragment) {

    private lateinit var array: ArrayList<String>
    private lateinit var _binding: AddNewTaskItemFragmentBinding
    private val binding: AddNewTaskItemFragmentBinding get() = _binding

    private lateinit var viewModel: AddNewTaskItemViewModel

    private lateinit var addListItemAdapter: AddListItemAdapter

    private lateinit var addContentBottomSheetDialog: AddItemBottomSheetModel

    private var addItemInterface = object : AppInterface.AddContent {
        override fun onAdd(content: String) {
            array.add(content)
            addListItemAdapter.notifyDataSetChanged()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AddNewTaskItemFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(AddNewTaskItemViewModel::class.java)

        addContentBottomSheetDialog =
            AddItemBottomSheetModel.newInstance(addItemInterface = addItemInterface)

        array = ArrayList<String>()
        for (item in 1..2) {
            array.add("New Item $item")
        }

        addListItemAdapter = AddListItemAdapter(array)

        binding.recyclerViewNewList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addListItemAdapter
        }


        binding.buttonAddItem.setOnClickListener {
            addContentBottomSheetDialog.show(
                requireActivity().supportFragmentManager,
                "add_content_dialog"
            )
        }

    }


}