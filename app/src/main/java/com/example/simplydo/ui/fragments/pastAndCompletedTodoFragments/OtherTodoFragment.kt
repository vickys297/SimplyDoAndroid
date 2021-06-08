package com.example.simplydo.ui.fragments.pastAndCompletedTodoFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.databinding.OtherTodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppRepository
import com.example.simplydo.utli.ViewModelFactory
import com.example.simplydo.utli.adapters.TodoListPagingAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OtherTodoFragment : Fragment(), LifecycleObserver {

    companion object {
        fun newInstance() = OtherTodoFragment()
    }

    private lateinit var viewModel: OtherTodoViewModel

    private lateinit var binding: OtherTodoFragmentBinding

    private lateinit var todoListPagingAdapter: TodoListPagingAdapter

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

        todoListPagingAdapter = TodoListPagingAdapter(requireContext())

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