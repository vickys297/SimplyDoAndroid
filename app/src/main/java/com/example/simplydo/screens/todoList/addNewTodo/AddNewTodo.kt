package com.example.simplydo.screens.todoList.addNewTodo

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.simplydo.R
import com.example.simplydo.databinding.AddNewTodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactInfo
import com.example.simplydo.utli.Repository
import com.example.simplydo.utli.ViewModelFactory
import java.util.*
import kotlin.collections.ArrayList

class AddNewTodo : Fragment() {

    companion object {
        fun newInstance() = AddNewTodo()
    }

    private lateinit var viewModel: AddNewTodoViewModel
    private lateinit var binding: AddNewTodoFragmentBinding

    private lateinit var contactInfo: ArrayList<ContactInfo>
    private lateinit var imagesList: ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.add_new_todo_fragment, container, false)
        setViewModel()
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

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
            findNavController().navigate(R.id.action_addNewTodo_to_contactFragment)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.todo_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuCreateTodo -> {
                createToDo()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createToDo() {

        viewModel.insertIntoLocalDatabase(binding.etTitle.text.toString(),
            binding.etTask.text.toString(),
            binding.etDateTime.text.toString(),
            contactInfo,
            imagesList
        )
        Toast.makeText(requireContext(), "New Todo added", Toast.LENGTH_LONG).show()
        findNavController().navigateUp()
    }

}