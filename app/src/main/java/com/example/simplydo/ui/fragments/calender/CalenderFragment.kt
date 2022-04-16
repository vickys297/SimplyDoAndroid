package com.example.simplydo.ui.fragments.calender

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.adapters.CalenderViewAdapter
import com.example.simplydo.adapters.PersonalTaskListAdapter
import com.example.simplydo.databinding.CalenderFragmentBinding
import com.example.simplydo.dialog.bottomSheetDialogs.basicAddTodoDialog.AddNewRemainder
import com.example.simplydo.dialog.bottomSheetDialogs.todoOptions.TodoOptionsFragment
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.SmallCalenderModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.CalenderDateSelectorModel
import com.example.simplydo.utlis.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


internal val TAG = CalenderFragment::class.java.canonicalName

class CalenderFragment : Fragment() {

    companion object {
        fun newInstance() = CalenderFragment()
    }

    private lateinit var selectedEventDateTotalItemCount: Observer<Int>
    private lateinit var nextAvailableDateObserver: Observer<TodoModel>

    private lateinit var viewModel: CalenderViewModel

    private lateinit var binding: CalenderFragmentBinding
    private lateinit var arrayListSmallCalenderModels: ArrayList<SmallCalenderModel>
    private lateinit var calenderViewAdapter: CalenderViewAdapter

    private lateinit var personalTaskListAdapter: PersonalTaskListAdapter

    private var selectedEventDate: CalenderDateSelectorModel = CalenderDateSelectorModel(
        startEventDate = AppFunctions.getCurrentDayStartInMilliSeconds(),
        endEventDate = AppFunctions.getCurrentDayEndInMilliSeconds()
    )

    var contactInfo: ArrayList<ContactModel> = ArrayList()

    private lateinit var calenderLinearLayoutManager: LinearLayoutManager

    private val undoInterface = object : UndoInterface {
        override fun onUndo(task: Any, type: Int) {
            when (type) {
                AppConstant.Task.TASK_ACTION_RESTORE -> {
                    viewModel.undoTaskRemove(task as TodoModel)
                }
                AppConstant.Task.TASK_ACTION_DELETE -> {
                    viewModel.undoTaskRemove(task as TodoModel)
                }
            }
        }
    }

    // calender time line date selector
    private val calenderAdapterInterface = object : CalenderAdapterInterface {
        override fun onDateSelect(layoutPosition: Int, smallCalenderModel: SmallCalenderModel) {

            Log.i(TAG, "onDateSelect: $layoutPosition")
            calenderLinearLayoutManager.scrollToPositionWithOffset(layoutPosition-1, 0)

            // update current selected date
            selectedEventDate.apply {
                startEventDate = smallCalenderModel.startEventDate
                endEventDate = smallCalenderModel.endEventDate
            }

            // update calender time line to current date
            calenderViewAdapter.setActiveDate(layoutPosition)

            // get the task by current date from start morning to night
            setupPagingDataObserverForSelectedDate()

//            viewModel.requestDataFromCloud(selectedEventDate)
        }

    }

    private val todoOptionDialogFragments = object : TodoOptionDialogFragments {
        override fun onDelete(item: TodoModel) {
            viewModel.removeTask(item)
            AppFunctions.showSnackBar(
                task = item,
                view = binding.root,
                message = "Task Removed",
                actionButtonName = "Undo",
                type = AppConstant.Task.TASK_ACTION_DELETE,
                undoInterface = undoInterface
            )
        }

        override fun onEdit(item: TodoModel) {
            findNavController().navigate(R.id.action_calenderFragment_to_editFragment)
        }

        override fun onRestore(item: TodoModel) {
            viewModel.restoreTask(item.dtId)
            AppFunctions.showSnackBar(
                task = item,
                view = binding.root,
                message = "Task Restored",
                actionButtonName = "Undo",
                type = AppConstant.Task.TASK_ACTION_RESTORE,
                undoInterface = undoInterface
            )
        }

    }

    private var todoAdapterInterface = object : TodoItemInterface {
        override fun onLongClick(item: TodoModel) {
            TodoOptionsFragment.newInstance(requireContext(),todoOptionDialogFragments, item = item)
                .show(requireActivity().supportFragmentManager, "dialog")
        }

        override fun onTaskClick(
            item: TodoModel,
            absoluteAdapterPosition: Int
        ) {
            val bundle = Bundle()
            bundle.putLong(AppConstant.NAVIGATION_TASK_KEY, item.dtId)
            findNavController().navigate(
                R.id.action_calenderFragment_to_todoFullDetailsFragment,
                bundle
            )
        }

    }

    private val createBasicTodoInterface = object : NewRemainderInterface {
        override fun onAddMoreDetails(eventDate: Long) {
            val bundle = Bundle()
            bundle.putString(getString(R.string.eventDateString), eventDate.toString())
            findNavController().navigate(R.id.action_calenderFragment_to_addNewTodo, bundle)
        }

        override fun onCreateTodo(
            title: String,
            task: String,
            eventDate: Long,
            isPriority: Boolean,
            allDayTask: Boolean
        ) {
            viewModel.createNewTodo(
                title,
                task,
                eventDate = eventDate,
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


        selectedEventDateTotalItemCount = Observer {

            // task available in the selected or current event date
            if (it != 0) {
//                todoAdapter.updateDataSet(it)
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


        nextAvailableDateObserver = Observer { task ->

            if (task == null) {
                binding.tvNextEventAvailable.text = String.format("You do not have upcoming task")
                binding.btnViewEventOnNextDate.visibility = View.GONE
            }


            task?.let {
                binding.tvNextEventAvailable.text =
                    String.format(
                        "Next task available @ %s",
                        AppFunctions.convertTimeInMillsecToPattern(
                            task.eventDateTime,
                            AppConstant.DATE_PATTERN_EVENT_DATE
                        )
                    )

                binding.btnViewEventOnNextDate.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = task.eventDateTime

                        calendar.set(Calendar.HOUR_OF_DAY, 0)
                        calendar.set(Calendar.MINUTE, 0)
                        calendar.set(Calendar.SECOND, 0)
                        calendar.set(Calendar.MILLISECOND, 0)

                        val startEventDate = calendar.timeInMillis

                        calendar.set(Calendar.HOUR_OF_DAY, 23)
                        calendar.set(Calendar.MINUTE, 59)
                        calendar.set(Calendar.SECOND, 59)
                        calendar.set(Calendar.MILLISECOND, 59)
                        val endEventDate = calendar.timeInMillis

                        selectedEventDate.startEventDate = startEventDate
                        selectedEventDate.endEventDate = endEventDate

                        setupPagingDataObserverForSelectedDate()

                        var itemPosition = 0
                        for (item in arrayListSmallCalenderModels) {
                            if (item.startEventDate == startEventDate) {
                                itemPosition = item.id
                                break
                            }
                        }
                        calenderViewAdapter.setActiveDate(itemPosition)
                        calenderLinearLayoutManager.scrollToPositionWithOffset(itemPosition-1, 0)
                    }
                }
            }


        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO: Use the ViewModel
        clickListener(binding)
        arrayListSmallCalenderModels = ArrayList()


        // calender timeline view

        calenderLinearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        calenderLinearLayoutManager.isSmoothScrollbarEnabled = true

        calenderViewAdapter =
            CalenderViewAdapter(requireContext(), calenderAdapterInterface)
        binding.recyclerViewCalenderView.apply {
            layoutManager = calenderLinearLayoutManager
            adapter = calenderViewAdapter
        }


        // using quick task list adapter
        personalTaskListAdapter = PersonalTaskListAdapter(todoAdapterInterface, requireContext())
        binding.recyclerViewTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = personalTaskListAdapter
        }


        setupPagingDataObserverForSelectedDate()

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.absoluteAdapterPosition
                personalTaskListAdapter.getItemAtPosition(position)?.let {
                    personalTaskListAdapter.notifyItemRemoved(position)

                    if (it.eventDateTime < AppFunctions.getCurrentDayStartInMilliSeconds()) {
                        personalTaskListAdapter.notifyItemRemoved(position)
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
                            type = AppConstant.Task.TASK_ACTION_DELETE,
                            task = it,
                            undoInterface = undoInterface
                        )
                    }
                }
            }

        }).apply {
            attachToRecyclerView(binding.recyclerViewTodoList)
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

                val item1 = personalTaskListAdapter.getItemAtPosition(from)
                val item2 = personalTaskListAdapter.getItemAtPosition(to)

                if (item1 != null && item2 != null) {
//                    personalTaskListAdapter.notifyItemMoved(from, to)
                    return true
                }
                return false
            }



            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                //Remove swiped item from list and notify the RecyclerView
                val position = viewHolder.absoluteAdapterPosition
                val item = personalTaskListAdapter.getItemAtPosition(position)

                item?.let {

                    viewModel.completeTaskByID(item.dtId)

                    if (item.eventDateTime < AppFunctions.getCurrentDayStartInMilliSeconds()) {
                        personalTaskListAdapter.notifyItemRemoved(position)
                    }

                    Snackbar.make(
                        binding.root,
                        getString(R.string.task_completed_label),
                        Snackbar.LENGTH_SHORT
                    ).show()

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
    }

    private fun setupPagingDataObserverForSelectedDate() {
        if (personalTaskListAdapter.itemCount > 0) {
            personalTaskListAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
        }
        val pagingDataSource = viewModel.getTodoListByEventDate(
            selectedEventDate.startEventDate,
            selectedEventDate.endEventDate
        )
        lifecycleScope.launch {
            pagingDataSource.collectLatest {
                personalTaskListAdapter.submitData(it)
            }
        }
        viewModel.getSelectedEventDateItemCount(
            selectedEventDate.startEventDate,
            selectedEventDate.endEventDate
        )
    }


    private fun clickListener(binding: CalenderFragmentBinding) {
        binding.buttonAddNewTask.setOnClickListener {
            AddNewRemainder.newInstance(
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

        viewModel.getSelectedEventDateItemCount(
            selectedEventDate.startEventDate,
            selectedEventDate.endEventDate
        )

        viewModel.selectedEventDateTotalCount.observe(
            viewLifecycleOwner,
            selectedEventDateTotalItemCount
        )

        viewModel.nextAvailableDate.observe(
            viewLifecycleOwner,
            nextAvailableDateObserver
        )

        // next task available date

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
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.DAY_OF_MONTH, i)

            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)

            val startEventDate = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 0)

            val endEventDate = calendar.timeInMillis

            arrayListSmallCalenderModels.add(
                SmallCalenderModel(
                    id = i,
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
        arrayListSmallCalenderModels[0].isActive = true
        calenderViewAdapter.updateList(arrayListSmallCalenderModels)

        calenderLinearLayoutManager.scrollToPositionWithOffset(0, 0)
    }
}