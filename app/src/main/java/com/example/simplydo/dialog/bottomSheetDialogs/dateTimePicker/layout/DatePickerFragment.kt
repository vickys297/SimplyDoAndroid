package com.example.simplydo.dialog.bottomSheetDialogs.dateTimePicker.layout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.R

class DatePickerFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_date_picker, container, false)
    }

    companion object {

        fun newInstance() =
            DatePickerFragment()
    }
}