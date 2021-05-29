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
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.adapters.TodoAdapter
import com.example.simplydo.utli.adapters.options.TodoOptionsFragment
import com.example.simplydo.utli.bottomSheetDialogs.addTodoBasic.AddTodoBasic
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


    private var todoAdapterInterface = object : TodoAdapterInterface {
        override fun onLongClick(item: TodoModel) {
            val options = TodoOptionsFragment.newInstance()
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
            todoAdapter.updateItem(it)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel

        // init
        contactInfo = ArrayList()
        imagesList = ArrayList()
        todoAdapter = TodoAdapter(requireContext(), todoAdapterInterface)


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
                viewModel.completeTaskByID(todoModel[position].dtId)

                todoAdapter.notifyItemRemoved(position)
                todoAdapter.notifyDataSetChanged()

                Toast.makeText(requireContext(),
                    getString(R.string.task_completed_label),
                    Toast.LENGTH_LONG).show()
            }
        }

        // pre load sample tasks
//        for (i in 0..200) {
//
//            val calendar = Calendar.getInstance()
//            calendar.time = Date()
//            calendar.add(Calendar.DAY_OF_MONTH, i)
//
//            viewModel.createNewTodo(
//                title = "Title $i",
//                task = "Sample test task with populated data $i",
//                eventDate = Constant.dateFormatter(Constant.DATE_PATTERN_COMMON)
//                    .format(calendar.time),
//                priority = i % 10 == 0,
//                contactInfo,
//                imagesList
//            )
//        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTodoList)


    }

    override fun onResume() {
        super.onResume()

        viewModel.syncDataWithDatabase(SimpleDateFormat(AppConstant.DATE_PATTERN_COMMON,
            Locale.getDefault()).format(Date().time))
    }


}