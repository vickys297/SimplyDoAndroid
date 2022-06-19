package com.example.simplydo.ui.fragments.addOrEditTodoTask.addItem

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.databinding.AddNewTaskItemFragmentBinding
import com.example.simplydo.dialog.bottomSheetDialogs.addTodoItem.AddTodoItemDialog
import com.example.simplydo.utils.AppConstant
import com.example.simplydo.utils.AppInterface

internal val TAG = AddNewTaskItemFragment::class.java.canonicalName
class AddNewTaskItemFragment : Fragment(R.layout.add_new_task_item_fragment) {

    private val array: ArrayList<String> = ArrayList()
    private lateinit var _binding: AddNewTaskItemFragmentBinding
    private val binding: AddNewTaskItemFragmentBinding get() = _binding

    private lateinit var viewModel: AddNewTaskItemViewModel

    private lateinit var addListItemAdapter: AddListItemAdapter

    private lateinit var addTaskContentBottomSheetDialog: AddTodoItemDialog

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

        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            AppConstant.Key.NAVIGATION_ADD_TASK_LIST,
            array
        )

        addTaskContentBottomSheetDialog = AddTodoItemDialog.newInstance(
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
            findNavController().popBackStack()
        }
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}