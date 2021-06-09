package com.example.simplydo.ui.fragments.pastAndCompletedTodoFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.databinding.OtherTodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.adapters.TodoListPagingAdapter
import com.example.simplydo.utli.bottomSheetDialogs.todoOptions.TodoOptionsFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OtherTodoFragment : Fragment(), LifecycleObserver {

    companion object {
        fun newInstance() = OtherTodoFragment()
    }

    private lateinit var viewModel: OtherTodoViewModel

    private lateinit var binding: OtherTodoFragmentBinding

    private lateinit var todoListPagingAdapter: TodoListPagingAdapter

    lateinit var recentSelectedItem: TodoModel

    private val todoOptionDialogFragments = object : TodoOptionDialogFragments {
        override fun onDelete(item: TodoModel) {
            viewModel.removeTaskById(item)
            AppConstant.showMessage("Task Removed", requireContext())
        }

        override fun onEdit(item: TodoModel) {
        }

        override fun onRestore(item: TodoModel) {
        }
    }

    private val todoItemInterface = object : TodoItemInterface {
        override fun onLongClick(item: TodoModel) {
            recentSelectedItem = item
            val options = TodoOptionsFragment.newInstance(todoOptionDialogFragments, item)
            options.show(requireActivity().supportFragmentManager, "dialog")
        }

        override fun onTaskClick(
            item: TodoModel,
            absoluteAdapterPosition: Int,
            extras: FragmentNavigator.Extras
        ) {
            val bundle = Bundle()
            bundle.putSerializable("todo", item)
            findNavController().navigate(
                R.id.action_otherTodoFragment_to_todoFullDetailsFragment,
                bundle,
                null,
                extras
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OtherTodoFragmentBinding.inflate(inflater, container, false)
        setupViewModel()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todoListPagingAdapter = TodoListPagingAdapter(requireContext(), todoItemInterface)

        binding.recyclerViewTodoList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = todoListPagingAdapter
        }

        arguments?.let {
            when (it.getInt(getString(R.string.view_type))) {
                AppConstant.TODO_VIEW_PAST -> {
                    binding.textViewTitle.text = getString(R.string.past)

                    lifecycleScope.launch {
                        viewModel.flowGetPastTask.collectLatest { pagingData ->
                            todoListPagingAdapter.submitData(pagingData)
                        }
                    }

                }
                AppConstant.TODO_VIEW_COMPLETED -> {
                    binding.textViewTitle.text = getString(R.string.completed)
                    lifecycleScope.launch {
                        viewModel.flowGetCompletedTask.collectLatest { pagingData ->
                            todoListPagingAdapter.submitData(pagingData)
                        }
                    }
                }
                else -> {
                    throw Exception("Type not added")
                }
            }
        }

    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                requireContext(),
                AppRepository.getInstance(
                    requireContext(),
                    AppDatabase.getInstance(requireContext())
                )
            )
        ).get(OtherTodoViewModel::class.java)
        binding.apply {
            viewModel = this@OtherTodoFragment.viewModel
            lifecycleOwner = this@OtherTodoFragment
            executePendingBindings()
        }
    }


}