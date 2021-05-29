package com.example.simplydo.ui.fragments.calender

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.databinding.CalenderFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.model.SmallCalenderModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.adapters.CalenderViewAdapter
import com.example.simplydo.utli.adapters.TodoAdapter
import com.example.simplydo.utli.adapters.options.TodoOptionsFragment
import com.example.simplydo.utli.bottomSheetDialogs.addTodoBasic.AddTodoBasic
import com.example.simplydo.utli.bottomSheetDialogs.todoOptions.TodoOptions
import java.util.*
import kotlin.collections.ArrayList


internal val TAG = CalenderViewAdapter::class.java.canonicalName

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

    private var selectedEventDate: String


    var contactInfo: ArrayList<ContactInfo> = ArrayList()
    var imagesList: ArrayList<String> = ArrayList()


    private val calenderAdapterInterface = object : CalenderAdapterInterface {
        override fun onDateSelect(position: Int, dateEvent: String) {
            selectedEventDate = dateEvent
            calenderViewAdapter.setActiveDate(position)
            viewModel.getTodoListByEventDate(selectedEventDate)
                .observe(viewLifecycleOwner, todoByDateObserver)
            viewModel.requestDataFromCloud(selectedEventDate)
        }

    }

    private var todoAdapterInterface = object : TodoAdapterInterface {
        override fun onLongClick(item: TodoModel) {
            TodoOptionsFragment.newInstance()
                .show(requireActivity().supportFragmentManager, "dialog")
        }

    }

    private val createBasicTodoInterface = object : CreateBasicTodoInterface {
        override fun onAddMoreDetails(eventDate: String) {
            val bundle = Bundle()
            bundle.putString(getString(R.string.eventDateString), eventDate)
            findNavController().navigate(R.id.action_calenderFragment_to_addNewTodo, bundle)
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
                eventDate = eventDate,
                priority = isPriority,
                contactInfo,
                imagesList
            )
        }
    }


    init {
        selectedEventDate = AppConstant.dateFormatter(AppConstant.DATE_PATTERN_COMMON).format(Date().time)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = CalenderFragmentBinding.inflate(inflater, container, false)
        setViewModel()
        setObserver()
        return binding.root
    }

    private fun setObserver() {

        todoByDateObserver = Observer {

            Log.i(TAG, "todoList -> $it")
            todoAdapter.updateItem(it as ArrayList<TodoModel>)

            if (it.isNotEmpty()) {
                binding.llNoEventAvailable.visibility = View.GONE
                binding.recyclerViewTodoList.visibility = View.VISIBLE
            }

            if (it.isEmpty()) {
                binding.llNoEventAvailable.visibility = View.VISIBLE
                binding.recyclerViewTodoList.visibility = View.GONE
                viewModel.getNextTaskAvailability(selectedEventDate)
            }

        }

        nextAvailableDateObserver = Observer {

            Log.i(TAG, "Next available date observer $it")
            it as ArrayList<TodoModel>

            if (it.isNotEmpty()) {
                binding.btnViewEventOnNextDate.visibility = View.VISIBLE
                binding.tvNextEventAvailable.text =
                    String.format("You have %d on %s", it.size, it[0].eventDate)
            } else {
                binding.tvNextEventAvailable.text = String.format("You do not have upcoming task")
                binding.btnViewEventOnNextDate.visibility = View.GONE
            }


        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // TODO: Use the ViewModel



        smallCalenderModels = ArrayList()

        calenderViewAdapter =
            CalenderViewAdapter(requireContext(), calenderAdapterInterface)

        binding.recyclerViewCalenderView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewCalenderView.adapter = calenderViewAdapter


        todoAdapter = TodoAdapter(requireContext(), todoAdapterInterface)
        binding.recyclerViewTodoList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTodoList.adapter = todoAdapter

        clickListener(binding)

        populateRecyclerView()


        viewModel.nextAvailableDate.observe(viewLifecycleOwner, nextAvailableDateObserver)

        viewModel.getTodoListByEventDate(selectedEventDate)
            .observe(viewLifecycleOwner, todoByDateObserver)
    }


    private fun clickListener(binding: CalenderFragmentBinding) {
        binding.btnNewTodo.setOnClickListener {
            AddTodoBasic.newInstance(
                createBasicTodoInterface = createBasicTodoInterface,
                eventDate = selectedEventDate
            ).show(requireActivity().supportFragmentManager, "dialog")
        }
        binding.ivOptions.setOnClickListener {
            TodoOptions.newInstance("", "").show(requireActivity().supportFragmentManager, "dialog")
        }
    }

    private fun populateRecyclerView() {
        for (i in 0..60) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, i)
            smallCalenderModels.add(
                SmallCalenderModel(
                    calendar.get(Calendar.DAY_OF_MONTH).toString(),
                    AppConstant.dateFormatter(AppConstant.DATE_PATTERN_MONTH_TEXT).format(calendar.time)
                        .uppercase(Locale.getDefault()),
                    AppConstant.dateFormatter(AppConstant.DATE_PATTERN_COMMON).format(calendar.time)))
        }

        smallCalenderModels[0].isActive = true

        calenderViewAdapter.updateList(smallCalenderModels)
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this,
            ViewModelFactory(requireContext(),
                AppRepository.getInstance(requireContext(),
                    AppDatabase.getInstance(requireContext()
                    )
                )
            )
        ).get(CalenderViewModel::class.java)

        binding.apply {
            viewModel = this@CalenderFragment.viewModel
            lifecycleOwner = this@CalenderFragment
            executePendingBindings()
        }
    }


}