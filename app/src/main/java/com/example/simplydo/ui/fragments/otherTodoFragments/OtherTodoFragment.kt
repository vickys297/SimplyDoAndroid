package com.example.simplydo.ui.fragments.otherTodoFragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.databinding.OtherTodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.*
import com.example.simplydo.adapters.TodoListPagingAdapter
import com.example.simplydo.bottomSheetDialogs.todoOptions.TodoOptionsFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OtherTodoFragment : Fragment(R.layout.other_todo_fragment), LifecycleObserver {

    companion object {
        fun newInstance() = OtherTodoFragment()
    }

    private lateinit var viewModel: OtherTodoViewModel

    private lateinit var _binding: OtherTodoFragmentBinding
    private val binding get() = _binding

    private lateinit var todoListPagingAdapter: TodoListPagingAdapter
    lateinit var recentSelectedItem: TodoModel


    private val undoInterface = object : UndoInterface {
        override fun onUndo(task: TodoModel, type: Int) {
            when (type) {
                AppConstant.TASK_ACTION_RESTORE -> {
                    viewModel.undoTaskRemove(task)
                }
            }
        }
    }


    private val todoOptionDialogFragments = object : TodoOptionDialogFragments {
        override fun onDelete(item: TodoModel) {
            recentSelectedItem = item
            viewModel.removeTask(item)
            AppFunctions.showSnackBar(
                task = item,
                view = binding.root,
                message = "Task Removed",
                actionButtonName = "Undo",
                type = AppConstant.TASK_ACTION_RESTORE,
                undoInterface = undoInterface
            )
        }

        override fun onEdit(item: TodoModel) {
            recentSelectedItem = item
            findNavController().navigate(R.id.action_toDoFragment_to_editFragment)
        }

        override fun onRestore(item: TodoModel) {
            recentSelectedItem = item
            viewModel.restoreTask(item.dtId)
            AppFunctions.showSnackBar(
                task = item,
                view = binding.root,
                message = "Task Restored",
                actionButtonName = "Undo",
                type = AppConstant.TASK_ACTION_RESTORE,
                undoInterface = undoInterface
            )
        }
    }
    private val todoItemInterface = object : TodoItemInterface {
        override fun onLongClick(item: TodoModel) {
            recentSelectedItem = item
            val options = TodoOptionsFragment.newInstance(todoOptionDialogFragments, item)
            options.show(requireActivity().supportFragmentManager, "dialog")
        }

        override fun onTaskClick(
            item: TodoModel,
            absoluteAdapterPosition: Int
        ) {
            val bundle = Bundle()
            bundle.putLong(getString(R.string.TODO_ITEM_KEY), item.dtId)
            findNavController().navigate(
                R.id.action_otherTodoFragment_to_todoFullDetailsFragment,
                bundle
            )
        }

    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                requireContext(),
                AppRepository.getInstance(
                    requireContext(),
                    AppDatabase.getInstance(requireContext())
                )
            )
        ).get(OtherTodoViewModel::class.java)
        binding.apply {
            viewModel = this@OtherTodoFragment.viewModel
            lifecycleOwner = this@OtherTodoFragment
            executePendingBindings()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = OtherTodoFragmentBinding.bind(view)
        setupViewModel()

        todoListPagingAdapter = TodoListPagingAdapter(requireContext(), todoItemInterface)

        binding.recyclerViewTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todoListPagingAdapter
        }

        arguments?.let {
            when (it.getInt(getString(R.string.view_type))) {
                AppConstant.TODO_VIEW_PAST -> {
                    loadPastViewType()
                }
                AppConstant.TODO_VIEW_COMPLETED -> {
                    loadCompletedType()
                }
                else -> {
                    throw Exception("Type not added")
                }
            }
        }

        recyclerViewHelperClass()

    }

    private fun recyclerViewHelperClass() {
        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {

            override fun isLongPressDragEnabled(): Boolean {
                return true
            }


            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {


                val from = viewHolder.absoluteAdapterPosition
                val to = target.absoluteAdapterPosition

                val item1 = todoListPagingAdapter.getItemAtPosition(from)
                val item2 = todoListPagingAdapter.getItemAtPosition(to)


                if (item1 != null && item2 != null) {

//                    quickTodoListAdapter.notifyItemMoved(from, to)
                    return true
                }


                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.absoluteAdapterPosition
                todoListPagingAdapter.getItemAtPosition(position)?.let {
                    todoListPagingAdapter.notifyItemRemoved(position)
                    todoListPagingAdapter.notifyDataSetChanged()
                    if (it.eventDateTime < AppFunctions.getCurrentDayStartInMilliSeconds()) {
                        todoListPagingAdapter.notifyItemRemoved(position)
                    }

                    if (!it.isCompleted)
                        viewModel.completeTaskByID(it.dtId).let {
                            AppFunctions.showSnackBar(
                                binding.root,
                                getString(R.string.task_completed_label)
                            )
                        }

                    if (it.isCompleted) {
                        viewModel.removeTask(it)
                        AppFunctions.showSnackBar(
                            view = binding.root,
                            message = "Task Removed",
                            actionButtonName = "Undo",
                            type = AppConstant.TASK_ACTION_DELETE,
                            task = it,
                            undoInterface = undoInterface
                        )
                    }
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                viewHolder.itemView.alpha = 1.0f

                // Don't need to do anything here
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTodoList)
    }

    private fun loadCompletedType() {

        // Change the title
        binding.textViewTitle.text = getString(R.string.completed)

        // check for availability
        val count = viewModel.getCompletedCount(AppFunctions.getCurrentDayEndInMilliSeconds())
        if (count > 0) {
            binding.recyclerViewTodoList.visibility = View.VISIBLE
            binding.includeNoTask.linearLayoutEmptyTask.visibility = View.GONE

            lifecycleScope.launch {
                viewModel.flowGetCompletedTask.collectLatest { pagingData ->
                    todoListPagingAdapter.submitData(pagingData)
                }
            }

        } else {
            binding.recyclerViewTodoList.visibility = View.GONE
            binding.includeNoTask.linearLayoutEmptyTask.visibility = View.VISIBLE
        }


    }

    private fun loadPastViewType() {
        binding.textViewTitle.text = getString(R.string.past)

        val count = viewModel.getPastTaskCount(AppFunctions.getCurrentDayStartInMilliSeconds())
        if (count > 0) {
            binding.recyclerViewTodoList.visibility = View.VISIBLE
            binding.includeNoTask.linearLayoutEmptyTask.visibility = View.GONE

            lifecycleScope.launch {
                viewModel.flowGetPastTask.collectLatest { pagingData ->
                    todoListPagingAdapter.submitData(pagingData)
                }
            }

        } else {
            binding.recyclerViewTodoList.visibility = View.GONE
            binding.includeNoTask.linearLayoutEmptyTask.visibility = View.VISIBLE
        }
    }


}