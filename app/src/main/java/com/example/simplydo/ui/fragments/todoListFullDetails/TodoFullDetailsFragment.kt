package com.example.simplydo.ui.fragments.todoListFullDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.simplydo.R

class TodoFullDetailsFragment : Fragment() {

    companion object {
        fun newInstance() = TodoFullDetailsFragment()
    }

    private lateinit var viewModel: TodoFullDetailsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.todo_full_details_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TodoFullDetailsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}