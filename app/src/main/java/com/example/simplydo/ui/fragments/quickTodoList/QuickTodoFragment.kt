package com.example.simplydo.ui.fragments.quickTodoList

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.databinding.TodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.adapters.QuickTodoListAdapter
import com.example.simplydo.utli.bottomSheetDialogs.basicAddTodoDialog.AddTodoBasic
import com.example.simplydo.utli.bottomSheetDialogs.calenderOptions.TodoOptions
import com.example.simplydo.utli.bottomSheetDialogs.todoOptions.TodoOptionsFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import kotlin.collections.ArrayList


internal val TAG = QuickTodoFragment::class.java.canonicalName

class QuickTodoFragment : Fragment(R.layout.todo_fragment) {

    companion object {
        fun newInstance() = QuickTodoFragment()
    }

    private lateinit var todoModelObserver: Observer<List<TodoModel>>
    private lateinit var todoObserver: Observer<CommonResponseModel>
    private lateinit var noNetworkObserver: Observer<String>

    private lateinit var viewModel: QuickTodoViewModel

    private lateinit var _binding: TodoFragmentBinding
    private val binding get() = _binding

    private lateinit var quickTodoListAdapter: QuickTodoListAdapter
    private lateinit var todoModel: ArrayList<TodoModel>

    private lateinit var recentSelectedItem: TodoModel

    private val todoOptionDialogFragments = object : TodoOptionDialogFragments {
        override fun onDelete(item: TodoModel) {
            viewModel.removeTaskById(item)
            AppFunctions.showMessage("Task Removed", requireContext())
        }

        override fun onEdit(item: TodoModel) {
        }

        override fun onRestore(item: TodoModel) {
        }
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
            absoluteAdapterPosition: Int,
            extras: FragmentNavigator.Extras
        ) {
            val bundle = Bundle()
            bundle.putSerializable("todo", item)

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
            eventTime: String,
            isPriority: Boolean
        ) {
            viewModel.createNewTodo(
                title,
                task,
                eventDate,
                eventTime = eventTime,
                isPriority,
                ArrayList(),
                ArrayList()
            )
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = TodoFragmentBinding.bind(view)

        setObserver()
        setupViewModel()
        // init

        quickTodoListAdapter = QuickTodoListAdapter(todoAdapterInterface, requireContext())
        binding.recyclerViewTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = quickTodoListAdapter
        }


        val livePagingData =
            viewModel.getQuickTodoList(AppFunctions.getCurrentDayStartInMilliSeconds())

        lifecycleScope.launchWhenResumed {
            livePagingData.collectLatest {
                Log.i(TAG, "onViewCreated: Paging Data $it")
                quickTodoListAdapter.submitData(it)
            }
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
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return false
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.absoluteAdapterPosition
                if (todoModel.isNotEmpty()) {
                    viewModel.completeTaskByID(todoModel[position].dtId)
                    quickTodoListAdapter.notifyItemChanged(position)

                    Snackbar.make(
                        binding.root,
                        getString(R.string.task_completed_label),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTodoList)
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
    }
}