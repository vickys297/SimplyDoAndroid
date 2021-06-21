package com.example.simplydo.ui.fragments.quickTodoList

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.adapters.QuickTodoListAdapter
import com.example.simplydo.bottomSheetDialogs.basicAddTodoDialog.AddTodoBasic
import com.example.simplydo.bottomSheetDialogs.basicAddTodoDialog.EditTodoBasic
import com.example.simplydo.bottomSheetDialogs.calenderOptions.TodoOptions
import com.example.simplydo.bottomSheetDialogs.todoOptions.TodoOptionsFragment
import com.example.simplydo.databinding.TodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.ui.activity.SettingsActivity
import com.example.simplydo.utli.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


internal val TAG = QuickTodoFragment::class.java.canonicalName
internal const val CHANNEL_ID = "task_channel_id"
internal const val NOTIFICATION_ID = 8888

class QuickTodoFragment : Fragment(R.layout.todo_fragment) {

    companion object {
        fun newInstance() = QuickTodoFragment()
    }

    private lateinit var todoObserver: Observer<CommonResponseModel>
    private lateinit var totalTaskCountObserver: Observer<Int>

    private lateinit var noNetworkObserver: Observer<String>

    private lateinit var viewModel: QuickTodoViewModel

    private lateinit var _binding: TodoFragmentBinding
    private val binding get() = _binding

    private lateinit var quickTodoListAdapter: QuickTodoListAdapter

    private lateinit var recentSelectedItem: TodoModel

    private val undoInterface = object : UndoInterface {
        override fun onUndo(task: TodoModel, type: Int) {
            when (type) {
                AppConstant.TASK_ACTION_RESTORE -> {
                    viewModel.undoTaskRemove(task)
                }
            }
        }
    }

    private val editBasicTodoInterface = object : EditBasicTodoInterface {
        override fun onUpdateDetails(todoModel: TodoModel) {
            viewModel.updateTaskModel(todoModel)
        }

        override fun onAddMoreDetails(todoModel: TodoModel) {
            // show edit fragment
            val bundle = Bundle()
            bundle.putSerializable(AppConstant.NAVIGATION_TASK_DATA_KEY, todoModel)
            findNavController().navigate(R.id.action_toDoFragment_to_editFragment, bundle)
        }
    }


    private val todoOptionDialogFragments = object : TodoOptionDialogFragments {
        override fun onDelete(item: TodoModel) {
            deleteSingleTask(item)
        }

        override fun onEdit(item: TodoModel) {
            recentSelectedItem = item

            if (item.taskType == AppConstant.TASK_TYPE_BASIC) {
                // show basic edit
                EditTodoBasic.newInstance(editBasicTodoInterface, item)
                    .show(requireActivity().supportFragmentManager, "dialog")
            }

            if (item.taskType == AppConstant.TASK_TYPE_EVENT) {
                // show edit fragment
                val bundle = Bundle()
                bundle.putSerializable(AppConstant.NAVIGATION_TASK_DATA_KEY, item)
                findNavController().navigate(R.id.action_toDoFragment_to_editFragment, bundle)
            }

//            findNavController().navigate(R.id.action_toDoFragment_to_editFragment)
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

    private fun deleteSingleTask(item: TodoModel) {
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

    private val todoTaskOptionsInterface = object : TodoTaskOptionsInterface {
        override fun onCalenderView() {
            findNavController().navigate(R.id.action_toDoFragment_to_calenderFragment)
        }

        override fun onCompletedView() {
            val bundle = Bundle()
            bundle.putInt(getString(R.string.view_type), AppConstant.TODO_VIEW_COMPLETED)
            findNavController().navigate(R.id.action_toDoFragment_to_otherTodoFragment, bundle)
        }

        override fun onPastTaskView() {
            val bundle = Bundle()
            bundle.putInt(getString(R.string.view_type), AppConstant.TODO_VIEW_PAST)
            findNavController().navigate(R.id.action_toDoFragment_to_otherTodoFragment, bundle)
        }

    }
    private var todoAdapterInterface = object : TodoItemInterface {
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
                R.id.action_toDoFragment_to_todoFullDetailsFragment,
                bundle
            )
        }

    }
    private var appInterface = object : CreateBasicTodoInterface {

        override fun onAddMoreDetails(eventDate: Long) {
            val bundle = Bundle()
            bundle.putLong(getString(R.string.eventDateString), eventDate)
            findNavController().navigate(R.id.action_toDoFragment_to_addNewTodo, bundle)
        }

        override fun onCreateTodo(
            title: String,
            task: String,
            eventDate: Long,
            isPriority: Boolean
        ) {
            val newInert = viewModel.createNewTodo(
                title,
                task,
                eventDate,
                isPriority,
            )

            newInert.let {
                val bundle = Bundle()
                bundle.putLong("dtId", it)
                bundle.putString("title", title)
                bundle.putString("task", task)
                bundle.putBoolean("priority", isPriority)
                AppFunctions.showSnackBar(binding.root, "New task added")
                AppFunctions.setupNotification(it, eventDate, bundle, requireActivity())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = TodoFragmentBinding.bind(view)

//        (activity as MainActivity).setSupportActionBar(binding.todoToolbar)
//        (activity as MainActivity).supportActionBar!!.setDisplayShowTitleEnabled(false)
//        setHasOptionsMenu(true)

        setObserver()
        setupViewModel()
        // init

        quickTodoListAdapter = QuickTodoListAdapter(todoAdapterInterface, requireContext())
        binding.recyclerViewTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = quickTodoListAdapter
        }


        binding.btnNewTodo.setOnClickListener {
            AddTodoBasic.newInstance(
                appInterface,
                System.currentTimeMillis()
            ).show(requireActivity().supportFragmentManager, "dialog")
        }

        binding.buttonTodoOption.setOnClickListener {
            TodoOptions.newInstance(todoTaskOptionsInterface)
                .show(requireActivity().supportFragmentManager, "dialog")
        }


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

                val item1 = quickTodoListAdapter.getItemAtPosition(from)
                val item2 = quickTodoListAdapter.getItemAtPosition(to)

                if (item1 != null && item2 != null) {
//                    quickTodoListAdapter.notifyItemMoved(from, to)
                    return true
                }
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.absoluteAdapterPosition
                quickTodoListAdapter.getItemAtPosition(position)?.let {
                    quickTodoListAdapter.notifyItemRemoved(position)

                    if (it.eventDateTime < AppFunctions.getCurrentDayStartInMilliSeconds()) {
                        quickTodoListAdapter.notifyItemRemoved(position)
                    }

                    if (!it.isCompleted)
                        viewModel.completeTaskByID(it.dtId).let {
                            AppFunctions.showSnackBar(
                                binding.root,
                                getString(R.string.task_completed_label)
                            )
                        }
                    if (it.isCompleted) {
                        deleteSingleTask(it)
                    }
                }
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)
                if (actionState == ACTION_STATE_DRAG) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.todo_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSettings -> {
                val intent = Intent(requireContext(), SettingsActivity::class.java)
                requireContext().startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setObserver() {


        todoObserver = Observer {
            if (it.result == AppConstant.API_RESULT_OK) {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        }

        noNetworkObserver = Observer {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        totalTaskCountObserver = Observer {
            if (it > 0) {
                binding.recyclerViewTodoList.visibility = View.VISIBLE
                binding.noTaskAvailable.linearLayoutEmptyTask.visibility = View.GONE
            } else {
                binding.noTaskAvailable.linearLayoutEmptyTask.visibility = View.VISIBLE
                binding.recyclerViewTodoList.visibility = View.GONE
            }
        }

    }

    private fun setupViewModel() {
        viewModel =
            ViewModelProvider(
                this,
                ViewModelFactory(
                    requireActivity(),
                    AppRepository.getInstance(
                        requireActivity(),
                        AppDatabase.getInstance(
                            context = requireActivity()
                        )
                    )
                )
            ).get(
                QuickTodoViewModel::class.java
            )
        binding.apply {
            todoViewModel = this@QuickTodoFragment.viewModel
            lifecycleOwner = this@QuickTodoFragment
            executePendingBindings()
        }


        viewModel.todoListResponse.observe(viewLifecycleOwner, todoObserver)
        viewModel.noNetworkMessage.observe(viewLifecycleOwner, noNetworkObserver)
        viewModel.getTotalTaskCount(AppFunctions.getCurrentDayStartInMilliSeconds())
            .observe(viewLifecycleOwner, totalTaskCountObserver)


    }


    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getQuickTodoList(AppFunctions.getCurrentDayStartInMilliSeconds())
                .collect {
                    quickTodoListAdapter.submitData(it)
                }
        }

    }
}