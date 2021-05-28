package com.example.simplydo.utli.adapters.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TodoOptionsFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_options, container, false)
    }

    companion object {
        fun newInstance() = TodoOptionsFragment()
    }
}