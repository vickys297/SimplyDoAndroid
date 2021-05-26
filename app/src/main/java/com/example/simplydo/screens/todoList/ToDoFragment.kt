package com.example.simplydo.screens.todoList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.databinding.TodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.CommonResponseModel
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.model.TodoModel
import com.example.simplydo.screens.todoList.adapter.TodoAdapter
import com.example.simplydo.screens.todoList.addTodoBasic.AddTodoBasic
import com.example.simplydo.utli.AppInterface
import com.example.simplydo.utli.Constant
import com.example.simplydo.utli.Repository
import com.example.simplydo.utli.ViewModelFactory
import java.text.SimpleDateFormat
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

    lateinit var contactInfo: ArrayList<ContactInfo>
    lateinit var imagesList: ArrayList<String>


    private var appInterface = object : AppInterface {
        override fun onAddMoreDetails() {
            findNavController().navigate(R.id.action_toDoFragment_to_addNewTodo)
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
        binding = DataBindingUtil.inflate(inflater, R.layout.todo_fragment, container, false)
        setObserver()
        setupViewModel()
        return binding.root
    }

    private fun setObserver() {

        todoModelObserver = Observer {
            println("getObserver $it")
            todoModel = it as ArrayList<TodoModel>
            todoAdapter.updateItem(it)
        }

        todoObserver = Observer {
            if (it.result == Constant.API_RESULT_OK) {
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
                ViewModelFactory(requireContext(), Repository.getInstance(requireContext(),
                    AppDatabase.getInstance(context = requireContext())))).get(
                ToDoViewModel::class.java
            )
        binding.apply {
            todoViewModel = this@ToDoFragment.viewModel
            lifecycleOwner = this@ToDoFragment
            executePendingBindings()
        }


        viewModel.todoListObserver().observe(viewLifecycleOwner, todoModelObserver)
        viewModel.todoListResponse.observe(viewLifecycleOwner, todoObserver)
        viewModel.noNetworkMessage.observe(viewLifecycleOwner, noNetworkObserver)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel

        // init
        contactInfo = ArrayList()
        imagesList = ArrayList()
        todoAdapter = TodoAdapter(requireContext())


        viewModel.syncDataWithDatabase(SimpleDateFormat("dd-MM-yyyy").format(Date()))

        binding.recyclerViewTodoList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewTodoList.adapter = todoAdapter

        binding.btnNewTodo.setOnClickListener {
            AddTodoBasic.newInstance(appInterface)
                .show(requireActivity().supportFragmentManager, "dialog")
        }


        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT or ItemTouchHelper.DOWN or ItemTouchHelper.UP) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                Toast.makeText(requireContext(), "on Move", Toast.LENGTH_SHORT).show()
                return false
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.absoluteAdapterPosition
                viewModel.removeTaskById(todoModel[position].dtId)
                todoAdapter.notifyItemRemoved(position)
                todoAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Task Removed", Toast.LENGTH_SHORT).show()

            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTodoList)


    }

    override fun onResume() {
        super.onResume()

    }


}