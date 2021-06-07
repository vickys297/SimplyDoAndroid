package com.example.simplydo.ui.fragments.todoList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.databinding.TodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.adapters.TodoAdapter
import com.example.simplydo.utli.bottomSheetDialogs.addTodoBasic.AddTodoBasic
import com.example.simplydo.utli.bottomSheetDialogs.todoOptions.TodoOptionsFragment
import java.util.*
import kotlin.collections.ArrayList


internal val TAG = ToDoFragment::class.java.canonicalName

class ToDoFragment : Fragment() {

    companion object {
        fun newInstance() = ToDoFragment()
    }

    private lateinit var todoModelObserver: Observer<List<TodoModel>>
    private lateinit var todoObserver: Observer<CommonResponseModel>
    private lateinit var noNetworkObserver: Observer<String>


    private lateinit var viewModel: ToDoViewModel
    private lateinit var binding: TodoFragmentBinding
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var todoModel: ArrayList<TodoModel>

    lateinit var contactInfo: ArrayList<ContactModel>
    lateinit var imagesList: ArrayList<String>

    lateinit var recentSelectedItem: TodoModel


    private val todoOptionDialogFragments = object : TodoOptionDialogFragments {
        override fun onDelete(item: TodoModel) {
            viewModel.removeTaskById(item)
            AppConstant.showMessage("Task Removed", requireContext())
        }

        override fun onEdit(item: TodoModel) {
        }

        override fun onRestore(item: TodoModel) {
        }

    }


    private var todoAdapterInterface = object : TodoAdapterInterface {
        override fun onLongClick(item: TodoModel) {
            recentSelectedItem = item
            val options = TodoOptionsFragment.newInstance(todoOptionDialogFragments, item)
            options.show(requireActivity().supportFragmentManager, "dialog")
        }

    }

    private var appInterface = object : CreateBasicTodoInterface {
        override fun onAddMoreDetails(eventDate: String) {
            val bundle = Bundle()
            bundle.putString(getString(R.string.eventDateString), eventDate)
            findNavController().navigate(R.id.action_toDoFragment_to_addNewTodo, bundle)
        }

        override fun onCreateTodo(
            title: String,
            task: String,
            eventDate: String,
            isPriority: Boolean,
        ) {
            viewModel.createNewTodo(
                title,
                task,
                eventDate,
                isPriority,
                contactInfo,
                imagesList
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = TodoFragmentBinding.inflate(layoutInflater, container, false)
        setObserver()
        setupViewModel()
        return binding.root
    }

    private fun setObserver() {

        todoModelObserver = Observer {
            todoModel = it as ArrayList<TodoModel>
            todoAdapter.updateDataSet(it)
        }

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
            ViewModelProvider(this,
                ViewModelFactory(requireContext(), AppRepository.getInstance(requireContext(),
                    AppDatabase.getInstance(context = requireContext())))).get(
                ToDoViewModel::class.java
            )
        binding.apply {
            todoViewModel = this@ToDoFragment.viewModel
            lifecycleOwner = this@ToDoFragment
            executePendingBindings()
        }


        viewModel.todoListObserver(AppConstant.dateFormatter(AppConstant.DATE_PATTERN_COMMON)
            .format(Date().time)).observe(viewLifecycleOwner, todoModelObserver)

        viewModel.todoListResponse.observe(viewLifecycleOwner, todoObserver)
        viewModel.noNetworkMessage.observe(viewLifecycleOwner, noNetworkObserver)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel

        // init
        contactInfo = ArrayList()
        imagesList = ArrayList()
        todoAdapter = TodoAdapter(todoAdapterInterface)


        binding.recyclerViewTodoList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewTodoList.adapter = todoAdapter

        binding.btnNewTodo.setOnClickListener {
            AddTodoBasic.newInstance(appInterface,
                AppConstant.dateFormatter(AppConstant.DATE_PATTERN_COMMON).format(Date()))
                .show(requireActivity().supportFragmentManager, "dialog")
        }

        binding.btnCalenderView.setOnClickListener {
            val extra = FragmentNavigatorExtras(binding.todoToolbar to "tn_toolbar")
            findNavController().navigate(R.id.action_toDoFragment_to_calenderFragment,
                null,
                null,
                extra)
        }


        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
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
                    todoAdapter.notifyItemChanged(position)
                    Toast.makeText(requireContext(),
                        getString(R.string.task_completed_label),
                        Toast.LENGTH_LONG).show()
                }

            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTodoList)


    }


}