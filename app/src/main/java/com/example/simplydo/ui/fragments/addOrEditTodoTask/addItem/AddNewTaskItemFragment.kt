package com.example.simplydo.ui.fragments.addOrEditTodoTask.addItem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.dialog.bottomSheetDialogs.AddTaskItemBottomSheetModel
import com.example.simplydo.databinding.AddNewTaskItemFragmentBinding
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppInterface

class AddNewTaskItemFragment : Fragment(R.layout.add_new_task_item_fragment) {

    private val array: ArrayList<String> = ArrayList()
    private lateinit var _binding: AddNewTaskItemFragmentBinding
    private val binding: AddNewTaskItemFragmentBinding get() = _binding

    private lateinit var viewModel: AddNewTaskItemViewModel

    private lateinit var addListItemAdapter: AddListItemAdapter

    private lateinit var addTaskContentBottomSheetDialog: AddTaskItemBottomSheetModel

    private var addItemInterface = object : AppInterface.TaskNoteTextItemListener {
        override fun onAdd(content: String) {
            array.add(content)
            addListItemAdapter.notifyDataSetChanged()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AddNewTaskItemFragmentBinding.bind(view)
        viewModel = ViewModelProvider(this).get(AddNewTaskItemViewModel::class.java)

        addTaskContentBottomSheetDialog =
            AddTaskItemBottomSheetModel.newInstance(
                taskNoteTextItemListener = addItemInterface,
                context = requireContext()
            )

        addListItemAdapter = AddListItemAdapter(array)

        binding.recyclerViewNewList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = addListItemAdapter
        }

        binding.buttonAddItem.setOnClickListener {
            addTaskContentBottomSheetDialog.show(
                requireActivity().supportFragmentManager,
                "add_content_dialog"
            )
        }

//        default show
        addTaskContentBottomSheetDialog.show(
            requireActivity().supportFragmentManager,
            "add_content_dialog"
        )

        binding.buttonAddToTask.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                AppConstant.Key.NAVIGATION_ADD_TASK_LIST,
                array
            )
            findNavController().navigateUp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}