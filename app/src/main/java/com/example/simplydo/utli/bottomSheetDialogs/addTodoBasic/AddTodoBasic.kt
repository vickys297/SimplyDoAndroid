package com.example.simplydo.utli.bottomSheetDialogs.addTodoBasic

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.databinding.FragmentAddTodoBasicListDialogBinding
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.CreateBasicTodoInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

// TODO: Customize parameter argument names
const val ARG_ITEM_COUNT = "item_count"

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    AddTodoBasic.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */

internal val TAG = AddTodoBasic::class.java.canonicalName

class AddTodoBasic(
    private val appInterface: CreateBasicTodoInterface,
    defaultEventDate: String,
) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentAddTodoBasicListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var eventDate: String

    init {
        // start with the given date
        eventDate = defaultEventDate

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddTodoBasicListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.btnAddMoreDetails.setOnClickListener {
            appInterface.onAddMoreDetails(eventDate)
            dismiss()
        }

        binding.imageButtonBasicAddTodoClose.setOnClickListener {
            dismiss()
        }

        binding.btnCreateTodo.setOnClickListener {
            validateInput()
        }

        binding.tvDayOfMonth.text =
            AppConstant.parseStringDateToCalender(eventDate).get(Calendar.DAY_OF_MONTH).toString()

        binding.tvMonth.text = AppConstant.dateFormatter(AppConstant.DATE_PATTERN_MONTH_TEXT)
            .format(AppConstant.parseStringDateToCalender(eventDate).time)
            .uppercase(Locale.getDefault())

        binding.llDateSelector.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {


                val date = AppConstant.dateFormatter(AppConstant.DATE_PATTERN_COMMON).parse(eventDate)
                val calendar = Calendar.getInstance()
                calendar.time = date!!

                val datePicker = DatePickerDialog(requireContext())
                datePicker.datePicker.minDate = System.currentTimeMillis()

                datePicker.datePicker.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                datePicker.setOnDateSetListener { picker, year, month, dayOfMonth ->
                    val newDate = Calendar.getInstance()
                    newDate.set(year, month, dayOfMonth)

                    eventDate = (AppConstant.dateFormatter(AppConstant.DATE_PATTERN_COMMON).format(newDate.time))

                    binding.tvDayOfMonth.text =
                        AppConstant.dateFormatter(AppConstant.DATE_PATTERN_DAY_OF_MONTH).format(newDate.time)

                    binding.tvMonth.text =
                        AppConstant.dateFormatter(AppConstant.DATE_PATTERN_MONTH_TEXT).format(newDate.time)
                            .uppercase(Locale.getDefault())

                    datePicker.dismiss()
                }
                datePicker.show()
            }
        }
    }

    private fun validateInput() {
        val view = arrayListOf(binding.etTitle, binding.etTask)

        var flag = true
        for (v in view) {
            if (v.text.toString().isEmpty()) {
                v.error = "Required"
                flag = false
            }
        }

        if (flag) {
            appInterface.onCreateTodo(
                binding.etTitle.text.toString(),
                binding.etTask.text.toString(),
                eventDate,
                binding.cbPriority.isChecked)
            dismiss()
        }
    }


    companion object {
        fun newInstance(
            createBasicTodoInterface: CreateBasicTodoInterface,
            eventDate: String,
        ): AddTodoBasic =
            AddTodoBasic(createBasicTodoInterface, eventDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}