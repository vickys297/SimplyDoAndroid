package com.example.simplydo.bottomSheetDialogs.basicAddTodoDialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.simplydo.databinding.FragmentAddTodoBasicListDialogBinding
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.EditBasicTodoInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*


/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    AddTodoBasic.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */

internal val TAG = EditTodoBasic::class.java.canonicalName

class EditTodoBasic(
    private val currentTodoModel: TodoModel,
    private val editBasicTodoInterface: EditBasicTodoInterface
) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentAddTodoBasicListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val ediTodoModel: TodoModel get() = currentTodoModel
    private var eventDateTime: Long = System.currentTimeMillis()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddTodoBasicListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()

        Log.i(TAG, "onViewCreated: ")

        eventDateTime = currentTodoModel.eventDateTime

        binding.btnAddMoreDetails.setOnClickListener {
            editBasicTodoInterface.onAddMoreDetails(ediTodoModel)
            dismiss()
        }

        binding.imageButtonBasicAddTodoClose.setOnClickListener {
            dismiss()
        }

        binding.btnCreateTodo.setOnClickListener {
            validateInput()
        }

        binding.linearLayoutTitle.setOnClickListener {
            binding.etTitle.requestFocus()
            requireActivity().runOnUiThread {
                val inputMethodManager = requireActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager

                inputMethodManager.toggleSoftInputFromWindow(
                    binding.etTitle.applicationWindowToken,
                    InputMethodManager.SHOW_FORCED,
                    0
                )
            }
        }

        binding.linearLayoutTask.setOnClickListener {
            binding.etTask.requestFocus()
            requireActivity().runOnUiThread {
                val inputMethodManager = requireActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE
                ) as InputMethodManager

                inputMethodManager.toggleSoftInputFromWindow(
                    binding.etTask.applicationWindowToken,
                    InputMethodManager.SHOW_FORCED,
                    0
                )
            }
        }

        binding.textViewEventDate.text = AppFunctions.getCurrentEventDate()

        binding.linearLayoutEventDateSelector.setOnClickListener {
            val calender = Calendar.getInstance()
            calender.timeInMillis = eventDateTime
            calender.add(Calendar.DAY_OF_MONTH, -1)

            val constrain = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.from(calender.timeInMillis))
                .setStart(calender.timeInMillis)

            calender.add(Calendar.DAY_OF_MONTH, 1)
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Event Date")
                .setSelection(calender.timeInMillis)
                .setCalendarConstraints(constrain.build())
                .build()

            datePicker.show(requireActivity().supportFragmentManager, "TAG_DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener {

                calender.timeInMillis = it
                calender.set(Calendar.HOUR, 23)
                calender.set(Calendar.MINUTE, 59)
                calender.set(Calendar.SECOND, 59)
                calender.set(Calendar.MILLISECOND, 0)

                eventDateTime = calender.timeInMillis
                binding.textViewEventDate.text = AppFunctions.convertTimeInMillsecToPattern(
                    eventDateTime,
                    AppConstant.DATE_PATTERN_EVENT_DATE
                )
            }
        }
    }

    private fun setupData() {
        binding.etTitle.setText(ediTodoModel.title)
        binding.etTask.setText(ediTodoModel.todo)
        binding.textViewEventDate.text = AppFunctions.convertTimeInMillsecToPattern(
            ediTodoModel.eventDateTime,
            AppConstant.DATE_PATTERN_EVENT_DATE
        )
    }

    private fun validateInput() {
        val view = arrayListOf(binding.etTitle, binding.etTask, binding.textViewEventDate)

        var flag = true
        for (v in view) {
            if (v.text.toString().isEmpty()) {
                v.error = "Required"
                flag = false
            }
        }

        if (flag) {
            editBasicTodoInterface.onUpdateDetails(
                TodoModel(
                    dtId = currentTodoModel.dtId,
                    title = binding.etTitle.text.toString(),
                    todo = binding.etTask.text.toString(),
                    eventDateTime = eventDateTime,
                    imageAttachments = currentTodoModel.imageAttachments,
                    contactAttachments = currentTodoModel.contactAttachments,
                    locationData = currentTodoModel.locationData,
                    fileAttachments = currentTodoModel.fileAttachments,
                    createdAt = currentTodoModel.createdAt,
                    updatedAt = System.currentTimeMillis().toString()
                )
            )
            dismiss()
        }
    }


    companion object {
        fun newInstance(
            editBasicTodoInterface: EditBasicTodoInterface,
            todoModel: TodoModel,
        ): EditTodoBasic =
            EditTodoBasic(todoModel, editBasicTodoInterface)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}