package com.example.simplydo.screens.todoList.addNewTodo

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.simplydo.R
import com.example.simplydo.databinding.AddNewTodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.utli.Constant
import com.example.simplydo.utli.Repository
import com.example.simplydo.utli.ViewModelFactory
import java.util.*
import kotlin.collections.ArrayList

internal val TAG = AddNewTodo::class.java.canonicalName

class AddNewTodo : Fragment() {

    companion object {
        fun newInstance() = AddNewTodo()
    }

    private lateinit var viewModel: AddNewTodoViewModel
    private lateinit var binding: AddNewTodoFragmentBinding

    private lateinit var contactInfo: ArrayList<ContactInfo>
    private lateinit var imagesList: ArrayList<String>

    lateinit var selectedEventDate: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.add_new_todo_fragment, container, false)
        setViewModel()

        arguments?.let {

            selectedEventDate = it.get(getString(R.string.eventDateString)) as String
            binding.include.tvDayOfMonth.text =
                Constant.parseStringDateToCalender(selectedEventDate)
                    .get(Calendar.DAY_OF_MONTH)
                    .toString()

            binding.include.tvMonth.text = Constant.dateFormatter(
                Constant.DATE_PATTERN_MONTH_TEXT)
                .format(Constant.parseStringDateToCalender(selectedEventDate).time)
                .uppercase(Locale.getDefault())
        }


        // setup array list
        contactInfo = ArrayList()
        imagesList = ArrayList()

        return binding.root
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this,
            ViewModelFactory(requireContext(),
                Repository.getInstance(requireContext(),
                    AppDatabase.getInstance(context = requireContext())))).get(
            AddNewTodoViewModel::class.java)
        binding.apply {
            this.viewModel = this@AddNewTodo.viewModel
            lifecycleOwner = this@AddNewTodo
            executePendingBindings()
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.btnAddContacts.setOnClickListener {
//            ContactFragment.newInstance(30).show(parentFragmentManager, "dialog")
            findNavController().navigate(R.id.action_addNewTodo_to_contactsFragment)
        }

        binding.btnCreateTodoTask.setOnClickListener {
            createToDo()
        }

        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }



    }



    private fun createToDo() {

    }

}