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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.databinding.TodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.TodoList
import com.example.simplydo.screens.todoList.adapter.TodoAdapter
import com.example.simplydo.screens.todoList.addTodoBasic.AddTodoBasic
import com.example.simplydo.utli.Repository
import com.example.simplydo.utli.ViewModelFactory


internal val TAG = ToDoFragment::class.java.canonicalName

class ToDoFragment : Fragment() {

    companion object {
        fun newInstance() = ToDoFragment()
    }

    private lateinit var todoListObserver: Observer<List<TodoList>>
    private lateinit var viewModel: ToDoViewModel
    private lateinit var binding: TodoFragmentBinding
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var todoList: ArrayList<TodoList>

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

        todoListObserver = Observer {
            println("getObserver $it")
            todoList = it as ArrayList<TodoList>
            todoAdapter.updateItem(it)
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


        viewModel.todoListObserver().observe(viewLifecycleOwner, todoListObserver)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel

        todoAdapter = TodoAdapter(requireContext())

        binding.recyclerViewTodoList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewTodoList.adapter = todoAdapter

        binding.btnNewTodo.setOnClickListener {
//            findNavController().navigate(R.id.action_toDoFragment_to_addNewTodo)
            AddTodoBasic.newInstance(30).show(requireActivity().supportFragmentManager, "dialog")
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
                viewModel.removeTaskById(todoList[position].id)
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