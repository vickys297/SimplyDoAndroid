package com.example.simplydo.ui.fragments.calender

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.databinding.CalenderFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.SmallCalenderModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.CalenderDateSelectorModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.adapters.CalenderViewAdapter
import com.example.simplydo.utli.adapters.TodoAdapter
import com.example.simplydo.utli.bottomSheetDialogs.basicAddTodoDialog.AddTodoBasic
import com.example.simplydo.utli.bottomSheetDialogs.todoOptions.TodoOptionsFragment
import java.util.*
import kotlin.collections.ArrayList


internal val TAG = CalenderFragment::class.java.canonicalName

class CalenderFragment : Fragment() {

    companion object {
        fun newInstance() = CalenderFragment()
    }

    private lateinit var nextAvailableDateObserver: Observer<List<TodoModel>>
    private lateinit var todoByDateObserver: Observer<List<TodoModel>>
    private lateinit var viewModel: CalenderViewModel

    private lateinit var binding: CalenderFragmentBinding
    private lateinit var smallCalenderModels: ArrayList<SmallCalenderModel>
    private lateinit var calenderViewAdapter: CalenderViewAdapter

    private lateinit var todoAdapter: TodoAdapter

    private var selectedEventDate: CalenderDateSelectorModel = CalenderDateSelectorModel(
        startEventDate = AppFunctions.getCurrentDayMinInMilliSeconds(),
        endEventDate = AppFunctions.getCurrentDayMaxInMilliSeconds()
    )

    private var todoModel = ArrayList<TodoModel>()

    var contactInfo: ArrayList<ContactModel> = ArrayList()
    var imagesList: ArrayList<String> = ArrayList()


    private val calenderAdapterInterface = object : CalenderAdapterInterface {
        override fun onDateSelect(layoutPosition: Int, smallCalenderModel: SmallCalenderModel) {

            // update current selected date
            selectedEventDate.apply {
                startEventDate = smallCalenderModel.startEventDate
                endEventDate = smallCalenderModel.endEventDate
            }

            // update calender time line to current date
            calenderViewAdapter.setActiveDate(layoutPosition)

            // get the task by current date from start morning to night
            viewModel.getTodoListByEventDate(
                smallCalenderModel.startEventDate,
                smallCalenderModel.endEventDate
            ).observe(viewLifecycleOwner, todoByDateObserver)

//            viewModel.requestDataFromCloud(selectedEventDate)
        }

    }

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

    private var todoAdapterInterface = object : TodoItemInterface {
        override fun onLongClick(item: TodoModel) {
            TodoOptionsFragment.newInstance(todoOptionDialogFragments, item = item)
                .show(requireActivity().supportFragmentManager, "dialog")
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
                bundle,
                null,
                extras
            )
        }

    }

    private val createBasicTodoInterface = object : CreateBasicTodoInterface {
        override fun onAddMoreDetails(eventDate: Long) {
            val bundle = Bundle()
            bundle.putString(getString(R.string.eventDateString), eventDate.toString())
            findNavController().navigate(R.id.action_calenderFragment_to_addNewTodo, bundle)
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
                eventDate = eventDate,
                eventTime = eventTime,
                priority = isPriority,
                contactInfo,
                ArrayList()
            )
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = CalenderFragmentBinding.inflate(inflater, container, false)
        setObserver()
        setViewModel()
        return binding.root
    }

    private fun setObserver() {

        // observer for task for the current or selected event date from morning till night
        todoByDateObserver = Observer {

            todoModel = it as ArrayList<TodoModel>

            // task available in the selected or current event date
            if (it.isNotEmpty()) {
                todoAdapter.updateDataSet(it)
                binding.llNoEventAvailable.visibility = View.GONE
                binding.recyclerViewTodoList.visibility = View.VISIBLE
            }
            // task not available in the selected or current event date
            else {
                binding.llNoEventAvailable.visibility = View.VISIBLE
                binding.recyclerViewTodoList.visibility = View.GONE
                viewModel.getNextTaskAvailability(selectedEventDate.endEventDate)
            }

        }

        nextAvailableDateObserver = Observer { todoList ->

            Log.d(TAG, "Next available date observer $todoList")
            todoList as ArrayList<TodoModel>

            if (todoList.isNotEmpty()) {
                binding.btnViewEventOnNextDate.visibility = View.VISIBLE
                binding.tvNextEventAvailable.text =
                    String.format(
                        "You have %d on %s",
                        todoList.size,
                        AppFunctions.getDateStringFromMilliseconds(
                            todoList[0].eventDate,
                            AppConstant.DATE_PATTERN_EVENT_DATE
                        )
                    )

                binding.btnViewEventOnNextDate.setOnClickListener {

                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = todoList[0].eventDate

                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)

                    val startEventDate = calendar.timeInMillis

                    calendar.set(Calendar.HOUR_OF_DAY, 23)
                    calendar.set(Calendar.MINUTE, 59)
                    calendar.set(Calendar.SECOND, 59)
                    val endEventDate = calendar.timeInMillis

                    viewModel.getTodoListByEventDate(startEventDate, endEventDate)
                        .observe(viewLifecycleOwner, todoByDateObserver)
                }
            } else {
                binding.tvNextEventAvailable.text = String.format("You do not have upcoming task")
                binding.btnViewEventOnNextDate.visibility = View.GONE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: Use the ViewModel
        clickListener(binding)
        smallCalenderModels = ArrayList()

        calenderViewAdapter =
            CalenderViewAdapter(requireContext(), calenderAdapterInterface)
        todoAdapter = TodoAdapter(todoAdapterInterface, requireContext())

        binding.recyclerViewCalenderView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = calenderViewAdapter
        }

        binding.recyclerViewTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todoAdapter
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
                val position = viewHolder.layoutPosition

                if (todoModel.isNotEmpty()) {

                    Log.d(TAG, "onSwiped: $todoModel")
                    Log.d(TAG, "onSwiped: position $position")

                    viewModel.completeTaskByID(todoModel[position].dtId)
                    todoAdapter.notifyItemChanged(position)
                    AppFunctions.showMessage("Task Completed", requireContext())
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTodoList)
    }


    private fun clickListener(binding: CalenderFragmentBinding) {
        binding.btnNewTodo.setOnClickListener {
            AddTodoBasic.newInstance(
                createBasicTodoInterface = createBasicTodoInterface,
                eventDate = 0L
            ).show(requireActivity().supportFragmentManager, "dialog")
        }

    }


    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                requireContext(),
                AppRepository.getInstance(
                    requireContext(),
                    AppDatabase.getInstance(
                        requireContext()
                    )
                )
            )
        ).get(CalenderViewModel::class.java)

        binding.apply {
            viewModel = this@CalenderFragment.viewModel
            lifecycleOwner = this@CalenderFragment
            executePendingBindings()
        }

        // next task available date
        viewModel.nextAvailableDate.observe(viewLifecycleOwner, nextAvailableDateObserver)


        // get task list by event date
        viewModel.getTodoListByEventDate(
            selectedEventDate.startEventDate,
            selectedEventDate.endEventDate
        )
            .observe(viewLifecycleOwner, todoByDateObserver)

        // check if there is any new data in cloud database
//        viewModel.requestDataFromCloud(selectedEventDate)
    }


    override fun onResume() {
        super.onResume()
        populateRecyclerView()
    }

    private fun populateRecyclerView() {
        for (i in 0..60) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, i)

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)

            val startEventDate = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)

            val endEventDate = calendar.timeInMillis

            Log.i(TAG, "populateRecyclerView: ${calendar.time}")
            smallCalenderModels.add(
                SmallCalenderModel(
                    calendar.get(Calendar.DAY_OF_MONTH).toString(),
                    AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_MONTH_TEXT)
                        .format(calendar.time)
                        .uppercase(Locale.getDefault()),
                    AppFunctions.dateFormatter(AppConstant.DATE_PATTERN_COMMON)
                        .format(calendar.time),
                    startEventDate = startEventDate,
                    endEventDate = endEventDate
                )
            )
        }

        smallCalenderModels[0].isActive = true

        calenderViewAdapter.updateList(smallCalenderModels)
    }
}