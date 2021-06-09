package com.example.simplydo.ui.fragments.todoListFullDetails

import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R
import com.example.simplydo.databinding.TodoFullDetailsFragmentBinding
import com.example.simplydo.model.TodoModel

internal val TAG = TodoFullDetailsFragment::class.java.canonicalName

class TodoFullDetailsFragment : Fragment(R.layout.todo_full_details_fragment) {

    companion object {
        fun newInstance() = TodoFullDetailsFragment()
    }

    private lateinit var _binding: TodoFullDetailsFragmentBinding
    private val binding: TodoFullDetailsFragmentBinding get() = _binding

    private lateinit var viewModel: TodoFullDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = TodoFullDetailsFragmentBinding.bind(view)

        viewModel = ViewModelProvider(this).get(TodoFullDetailsViewModel::class.java)

        arguments?.let {
            Log.d(TAG, "onViewCreated: ${it.getSerializable("todo")}")

            val todoModel = it.getSerializable("todo") as TodoModel
            binding.tvTitle.text = todoModel.title
            binding.tvTodo.text = todoModel.todo
        }


    }

}