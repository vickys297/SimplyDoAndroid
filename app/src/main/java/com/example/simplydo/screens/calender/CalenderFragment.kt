package com.example.simplydo.screens.calender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.databinding.CalenderFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.SmallCalenderModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.screens.calender.adapter.CalenderViewAdapter
import com.example.simplydo.screens.todoList.adapter.TodoAdapter
import com.example.simplydo.utli.CalenderAdapterInterface
import com.example.simplydo.utli.Repository
import com.example.simplydo.utli.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


internal val TAG = CalenderViewAdapter::class.java.canonicalName

class CalenderFragment : Fragment() {

    companion object {
        fun newInstance() = CalenderFragment()
    }

    private lateinit var viewModel: CalenderViewModel

    private lateinit var binding: CalenderFragmentBinding
    private lateinit var smallCalenderModels: ArrayList<SmallCalenderModel>
    private lateinit var calenderViewAdapter: CalenderViewAdapter

    private lateinit var todoAdapter: TodoAdapter

    lateinit var selectedEventDate: String

    private val calenderAdapterInterface = object : CalenderAdapterInterface {
        override fun onDateSelect(position: Int, dateEvent: String) {
            selectedEventDate = dateEvent
            calenderViewAdapter.setActiveDate(position)
            viewModel.getTodoListByEventDate(selectedEventDate)
        }

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
        viewModel.todoList.observe(viewLifecycleOwner, {

            todoAdapter.updateItem(it as ArrayList<TodoModel>)

            if (it.isNotEmpty()) {
                binding.llNoEventAvailable.visibility = View.GONE
                binding.recyclerViewTodoList.visibility = View.VISIBLE
            }

            if (it.isEmpty()) {
                binding.llNoEventAvailable.visibility = View.VISIBLE
                binding.recyclerViewTodoList.visibility = View.GONE
            }

        })
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


        todoAdapter = TodoAdapter(requireContext())
        binding.recyclerViewTodoList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewTodoList.adapter = todoAdapter


        populateRecyclerView()
        selectedEventDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date().time)
        viewModel.getTodoListByEventDate(selectedEventDate)
    }

    private fun populateRecyclerView() {
        for (i in 0..30) {
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_MONTH, i)
            smallCalenderModels.add(
                SmallCalenderModel(calendar.get(Calendar.DAY_OF_MONTH).toString(),
                    SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
                        .uppercase(Locale.getDefault()),
                    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(calendar.time)))
        }

        calenderViewAdapter.updateList(smallCalenderModels)
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this,
            ViewModelFactory(requireContext(),
                Repository.getInstance(requireContext(),
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