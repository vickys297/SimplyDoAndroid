package com.example.simplydo.dialog.bottomSheetDialogs.repeatDialog

import android.content.Context
import android.os.Bundle
import android.util.Log
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

internal val TAG = RepeatDialog::class.java.canonicalName

class RepeatDialog(
    val requireContext: Context,
    val callback: RepeatDialogInterface,
    private val taskRepeatDays: ArrayList<SelectorDataModal>,
    private val taskRepeatFrequency: ArrayList<SelectorDataModal>
) :
    BottomSheetDialogFragment() {

    companion object {
        fun getInstance(
            requireContext: Context,
            callback: RepeatDialogInterface,
            taskRepeatDays: ArrayList<SelectorDataModal>,
            taskRepeatFrequency: ArrayList<SelectorDataModal>
        ) =
            RepeatDialog(requireContext, callback, taskRepeatDays, taskRepeatFrequency)
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

    private fun clearAllWeekSelected() {
        for (item in arrayWeek) {
            item.selected = false
        }
        for (item in repeatWeaklyAdapter.arrayList) {
            item.selected = false
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
        arrayWeek = ArrayList()
        arrayFrequency = ArrayList()
        initFrequency()
        loadRepeatView()
        initPreSelected()
        binding.buttonSet.setOnClickListener {
            val selectedWeek = arrayWeek.filter { filter -> filter.selected } as ArrayList
            val selectedFrequency = arrayFrequency.filter { filter -> filter.selected } as ArrayList
            if (!arrayFrequency[1].selected) {
                clearAllWeekSelected()
            }
            callback.onSetRepeat(selectedFrequency, selectedWeek)
            dismiss()
        }

        binding.buttonClose.setOnClickListener {
            callback.onCancel()
            dismiss()
        }
    }

    private fun initPreSelected() {
        Log.i(TAG, "initPreSelected: $taskRepeatDays")
        Log.i(TAG, "initPreSelected: $taskRepeatFrequency")
        Log.i(TAG, "currentSelectedPeriod: $currentSelectedPeriod")
    }

    private fun initFrequency() {
        arrayFrequency.add(
            SelectorDataModal(
                value = "Day",
                visibleValue = "Day",
                selected = getPreSelectedStatus("Day", taskRepeatFrequency),
                type = 1
            )
        )
        arrayFrequency.add(
            SelectorDataModal(
                value = "Week",
                visibleValue = "Week",
                selected = getPreSelectedStatus("Week", taskRepeatFrequency),
                type = 1
            )
        )
        arrayFrequency.add(
            SelectorDataModal(
                value = "Month",
                visibleValue = "Month",
                selected = getPreSelectedStatus("Month", taskRepeatFrequency),
                type = 1
            )
        )
        arrayFrequency.add(
            SelectorDataModal(
                value = "Year",
                visibleValue = "Year",
                selected = getPreSelectedStatus("Year", taskRepeatFrequency),
                type = 1
            )
        )

        arrayWeek.add(
            SelectorDataModal(
                value = "S",
                visibleValue = "Sunday",
                selected = getPreSelectedStatus("Sunday", taskRepeatDays),
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "M",
                visibleValue = "Monday",
                selected = getPreSelectedStatus("Monday", taskRepeatDays),
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "T",
                visibleValue = "Tuesday",
                selected = getPreSelectedStatus("Tuesday", taskRepeatDays),
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "W",
                visibleValue = "Wednesday",
                selected = getPreSelectedStatus("Wednesday", taskRepeatDays),
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "T",
                visibleValue = "Thursday",
                selected = getPreSelectedStatus("Thursday", taskRepeatDays),
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "F",
                visibleValue = "Friday",
                selected = getPreSelectedStatus("Friday", taskRepeatDays),
                type = 2
            )
        )
        arrayWeek.add(
            SelectorDataModal(
                value = "S",
                visibleValue = "Saturday",
                selected = getPreSelectedStatus("Saturday", taskRepeatDays),
                type = 2
            )
        )

        currentSelectedPeriod = getSelectedPeriod(arrayFrequency)
    }

    private fun getPreSelectedStatus(
        visibleValue: String,
        arrayDataSet: ArrayList<SelectorDataModal>
    ): Boolean {
        if (arrayDataSet.isNotEmpty()) {
            for (item in arrayDataSet) {
                if (item.visibleValue == visibleValue) {
                    return true
                }
            }
        }
        return false
    }

    private fun getSelectedPeriod(taskRepeatFrequency: ArrayList<SelectorDataModal>): Int {
        for ((index, item) in taskRepeatFrequency.withIndex()) {
            Log.i(TAG, "getSelectedPeriod: $item")
            if (item.selected) {
                return index
            }
        }
//        if nothing selected return 0
        return 0
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
            isVisible = getPreSelectedStatus("Week", taskRepeatFrequency)
            binding.textView29.isVisible = getPreSelectedStatus("Week", taskRepeatFrequency)
        }

    }
}