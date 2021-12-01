package com.example.simplydo.dialog.bottomSheetDialogs.repeatDialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.adapters.CommonSelectorAdapter
import com.example.simplydo.databinding.RepeatDialogFragmentBinding
import com.example.simplydo.model.SelectorDataModal
import com.example.simplydo.utlis.CommonSelector
import com.example.simplydo.utlis.RepeatDialogInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RepeatDialog(val requireContext: Context, val callback: RepeatDialogInterface) :
    BottomSheetDialogFragment() {

    companion object {
        fun getInstance(requireContext: Context, callback: RepeatDialogInterface) =
            RepeatDialog(requireContext, callback)
    }

    private lateinit var binding: RepeatDialogFragmentBinding
    private lateinit var viewModel: RepeatDialogViewModel

    private lateinit var repeatFrequencyAdapter: CommonSelectorAdapter
    private lateinit var repeatWeaklyAdapter: CommonSelectorAdapter

    private var arrayFrequency: ArrayList<SelectorDataModal> = ArrayList()
    private var arrayWeek: ArrayList<SelectorDataModal> = ArrayList()

    private var currentSelectedPeriod = 0

    private var commonSelectorCallback: CommonSelector = object : CommonSelector {
        override fun onWeekSelected(arrayList: ArrayList<SelectorDataModal>) {
            arrayWeek = arrayList
        }

        override fun onPeriodSelected(arrayList: ArrayList<SelectorDataModal>) {
            binding.textView29.isVisible = arrayList[1].selected
            binding.recyclerViewRepeatWeek.isVisible = arrayList[1].selected
            arrayFrequency = arrayList
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val layoutInflater = LayoutInflater.from(context)
        binding = RepeatDialogFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[RepeatDialogViewModel::class.java]
        initFrequency()
        loadRepeatView()


        binding.buttonSet.setOnClickListener {
            val selectedWeek = arrayWeek.filter { filter -> filter.selected } as ArrayList
            val selectedFrequency = arrayFrequency.filter { filter -> filter.selected } as ArrayList
            callback.onSetRepeat(selectedFrequency, selectedWeek)
            dismiss()
        }

        binding.buttonClose.setOnClickListener {
            callback.onCancel()
            dismiss()
        }
    }

    private fun initFrequency() {
        arrayFrequency.add(SelectorDataModal(value = "Day", selected = true, type = 1))
        arrayFrequency.add(SelectorDataModal(value = "Week", selected = false, type = 1))
        arrayFrequency.add(SelectorDataModal(value = "Month", selected = false, type = 1))
        arrayFrequency.add(SelectorDataModal(value = "Year", selected = false, type = 1))

        arrayWeek.add(
            SelectorDataModal(
                value = "S",
                visibleValue = "Sunday",
                selected = false,
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "M",
                visibleValue = "Monday",
                selected = false,
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "T",
                visibleValue = "Tuesday",
                selected = false,
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "W",
                visibleValue = "Wednesday",
                selected = false,
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "T",
                visibleValue = "Thursday",
                selected = false,
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "F",
                visibleValue = "Friday",
                selected = false,
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "S",
                visibleValue = "Saturday",
                selected = false,
                type = 2
            )
        )

    }


    private fun loadRepeatView() {
        repeatFrequencyAdapter =
            CommonSelectorAdapter(arrayFrequency, commonSelectorCallback, currentSelectedPeriod)
        binding.recyclerViewRepeatFrequency.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = repeatFrequencyAdapter
        }

        repeatWeaklyAdapter = CommonSelectorAdapter(
            arrayWeek,
            commonSelectorCallback,
            currentSelectedPeriod
        )
        binding.recyclerViewRepeatWeek.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = repeatWeaklyAdapter
            isVisible = false
            binding.textView29.isVisible = false
        }

    }
}