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
import com.example.simplydo.databinding.CalenderFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.SmallCalenderModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.CalenderDateSelectorModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.adapters.CalenderViewAdapter
import com.example.simplydo.utli.adapters.QuickTodoListAdapter
import com.example.simplydo.utli.bottomSheetDialogs.basicAddTodoDialog.AddTodoBasic
import com.example.simplydo.utli.bottomSheetDialogs.todoOptions.TodoOptionsFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


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

    private lateinit var quickTodoListAdapter: QuickTodoListAdapter

    private var selectedEventDate: CalenderDateSelectorModel = CalenderDateSelectorModel(
        startEventDate = AppFunctions.getCurrentDayStartInMilliSeconds(),
        endEventDate = AppFunctions.getCurrentDayEndInMilliSeconds()
    )

    var contactInfo: ArrayList<ContactModel> = ArrayList()

    private lateinit var calenderLinearLayoutManager: LinearLayoutManager

    private val undoInterface = object : UndoInterface {
        override fun onUndo(task: TodoModel, type: Int) {
            when (type) {
                AppConstant.TASK_ACTION_RESTORE -> {
                    viewModel.undoTaskRemove(task)
                }
                AppConstant.TASK_ACTION_DELETE -> {
                    viewModel.undoTaskRemove(task)
                }
            }
        }
    }

    // calender time line date selector
    private val calenderAdapterInterface = object : CalenderAdapterInterface {
        override fun onDateSelect(layoutPosition: Int, smallCalenderModel: SmallCalenderModel) {

            Log.i(TAG, "onDateSelect: $layoutPosition")
            calenderLinearLayoutManager.scrollToPositionWithOffset(layoutPosition, 0)

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
            viewModel.removeTaskById(item)
            AppFunctions.showSnackBar(
                task = item,
                view = binding.root,
                message = "Task Removed",
                actionButtonName = "Undo",
                type = AppConstant.TASK_ACTION_DELETE,
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
                type = AppConstant.TASK_ACTION_RESTORE,
                undoInterface = undoInterface
            )
        }

    }

    private var todoAdapterInterface = object : TodoItemInterface {
        override fun onLongClick(item: TodoModel) {
            TodoOptionsFragment.newInstance(todoOptionDialogFragments, item = item)
                .show(requireActivity().supportFragmentManager, "dialog")
        }

        override fun onTaskClick(
            item: TodoModel,
            absoluteAdapterPosition: Int
        ) {
            val bundle = Bundle()
            bundle.putSerializable("todo", item)

            findNavController().navigate(
                R.id.action_calenderFragment_to_todoFullDetailsFragment,
                bundle
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


        selectedEventDateTotalItemCount = Observer {

            Log.i(TAG, "setObserver: selectedEventDateTotalCount $it")


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
                        AppFunctions.getDateStringFromMilliseconds(
                            task.eventDate,
                            AppConstant.DATE_PATTERN_EVENT_DATE
                        )
                    )

                binding.btnViewEventOnNextDate.apply {
                    visibility = View.VISIBLE
                    setOnClickListener {
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = task.eventDate

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
                        calenderLinearLayoutManager.scrollToPositionWithOffset(itemPosition, 0)
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
                val position = viewHolder.absoluteAdapterPosition
                quickTodoListAdapter.getItemAtPosition(position)?.let {
                    quickTodoListAdapter.notifyItemRemoved(position)
                    quickTodoListAdapter.notifyDataSetChanged()
                    if (it.eventDate < AppFunctions.getCurrentDayStartInMilliSeconds()) {
                        quickTodoListAdapter.notifyItemRemoved(position)
                    }
                    viewModel.completeTaskByID(it.dtId).let {
                        AppFunctions.showSnackBar(
                            binding.root,
                            getString(R.string.task_completed_label)
                        )
                    }
                }
            }

        }).apply {
            attachToRecyclerView(binding.recyclerViewCalenderView)
        }


        // using quick task list adapter
        quickTodoListAdapter = QuickTodoListAdapter(todoAdapterInterface, requireContext())
        binding.recyclerViewTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = quickTodoListAdapter
        }


        setupPagingDataObserverForSelectedDate()


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
                val item = quickTodoListAdapter.getItemAtPosition(position)

                item?.let {

                    viewModel.completeTaskByID(item.dtId)

                    if (item.eventDate < AppFunctions.getCurrentDayStartInMilliSeconds()) {
                        quickTodoListAdapter.notifyItemRemoved(position)
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

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTodoList)
    }

    private fun setupPagingDataObserverForSelectedDate() {
        if (quickTodoListAdapter.itemCount > 0) {
            quickTodoListAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
        }
        val pagingDataSource = viewModel.getTodoListByEventDate(
            selectedEventDate.startEventDate,
            selectedEventDate.endEventDate
        )
        lifecycleScope.launch {
            pagingDataSource.collectLatest {
                quickTodoListAdapter.submitData(it)
            }
        }
        viewModel.getSelectedEventDateItemCount(
            selectedEventDate.startEventDate,
            selectedEventDate.endEventDate
        )
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
    }
}