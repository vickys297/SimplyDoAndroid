package com.example.simplydo.ui.fragments.searchTask

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.R

class SearchTaskFragment : Fragment() {

    companion object {
        fun newInstance() = SearchTaskFragment()
    }

    private lateinit var viewModel: SearchTaskViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.search_task_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SearchTaskViewModel::class.java)
        // TODO: Use the ViewModel
    }

}