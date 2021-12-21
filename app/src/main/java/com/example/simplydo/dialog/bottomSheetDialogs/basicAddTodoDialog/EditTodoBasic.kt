package com.example.simplydo.dialog.bottomSheetDialogs.basicAddTodoDialog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.simplydo.R
import com.example.simplydo.databinding.FragmentAddTodoBasicListDialogBinding
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions
import com.example.simplydo.utlis.EditBasicTodoInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

internal val TAG = EditTodoBasic::class.java.canonicalName
class EditTodoBasic(
    private val parentContext: Context,
    private val currentTodoModel: TodoModel,
    private val editBasicTodoInterface: EditBasicTodoInterface
) : BottomSheetDialogFragment(), View.OnClickListener {

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
        val layoutInflater = LayoutInflater.from(parentContext)
        _binding = FragmentAddTodoBasicListDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupData()

        Log.i(TAG, "onViewCreated: ")

        binding.textViewTitle.text = getString(R.string.edit_task)
        binding.textViewEventDate.text = AppFunctions.getCurrentEventDate()

        eventDateTime = currentTodoModel.eventDateTime

        binding.btnAddMoreDetails.setOnClickListener(this)
        binding.imageButtonBasicAddTodoClose.setOnClickListener(this)
        binding.btnCreateTodo.setOnClickListener(this)
        binding.linearLayoutTitle.setOnClickListener(this)
        binding.linearLayoutTask.setOnClickListener(this)
        binding.linearLayoutEventDateSelector.setOnClickListener(this)
    }

    private fun setupData() {
        binding.editTextTitle.setText(ediTodoModel.title)
        binding.editTextTask.setText(ediTodoModel.todo)
        binding.textViewEventDate.text = AppFunctions.convertTimeInMillsecToPattern(
            ediTodoModel.eventDateTime,
            AppConstant.DATE_PATTERN_EVENT_DATE
        )
    }

    private fun validateInput() {
        val view =
            arrayListOf(binding.editTextTitle, binding.editTextTask, binding.textViewEventDate)

        var flag = true
        for (v in view) {
            if (v.text.toString().isEmpty()) {
                v.error = getString(R.string.required)
                flag = false
            }
        }

        if (flag) {
            editBasicTodoInterface.onUpdateDetails(
                TodoModel(
                    dtId = currentTodoModel.dtId,
                    title = binding.editTextTitle.text.toString(),
                    todo = binding.editTextTask.text.toString(),
                    eventDateTime = eventDateTime,
                    locationData = currentTodoModel.locationData,
                    contactAttachments = currentTodoModel.contactAttachments,
                    galleryAttachments = currentTodoModel.galleryAttachments,
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
            context: Context,
            editBasicTodoInterface: EditBasicTodoInterface,
            todoModel: TodoModel
        ): EditTodoBasic = EditTodoBasic(context, todoModel, editBasicTodoInterface)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            binding.btnAddMoreDetails.id -> {
                editBasicTodoInterface.onAddMoreDetails(ediTodoModel)
                dismiss()
            }
            binding.imageButtonBasicAddTodoClose.id -> {
                dismiss()
            }
            binding.btnCreateTodo.id -> {
                validateInput()
            }
            binding.linearLayoutTitle.id -> {
                binding.editTextTitle.requestFocus()
                requireActivity().runOnUiThread {
                    val inputMethodManager = requireActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE
                    ) as InputMethodManager

                    inputMethodManager.toggleSoftInputFromWindow(
                        binding.editTextTask.applicationWindowToken,
                        InputMethodManager.SHOW_FORCED,
                        0
                    )
                }
            }
            binding.linearLayoutTask.id -> {
                binding.editTextTask.requestFocus()
                requireActivity().runOnUiThread {
                    val inputMethodManager = requireActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE
                    ) as InputMethodManager

                    inputMethodManager.toggleSoftInputFromWindow(
                        binding.editTextTask.applicationWindowToken,
                        InputMethodManager.SHOW_FORCED,
                        0
                    )
                }
            }
            binding.linearLayoutEventDateSelector.id -> {
                pickDateTime()
//                val calender = Calendar.getInstance()
//                calender.timeInMillis = eventDateTime
//                calender.add(Calendar.DAY_OF_MONTH, -1)
//
//                val constrain = CalendarConstraints.Builder()
//                    .setValidator(DateValidatorPointForward.from(calender.timeInMillis))
//                    .setStart(calender.timeInMillis)
//
//                calender.add(Calendar.DAY_OF_MONTH, 1)
//                val datePicker = MaterialDatePicker.Builder.datePicker()
//                    .setTitleText("Select Event Date")
//                    .setSelection(calender.timeInMillis)
//                    .setCalendarConstraints(constrain.build())
//                    .build()
//
//                datePicker.show(requireActivity().supportFragmentManager, "TAG_DATE_PICKER")
//
//                datePicker.addOnPositiveButtonClickListener {
//
//                    calender.timeInMillis = it
//                    calender.set(Calendar.HOUR, 23)
//                    calender.set(Calendar.MINUTE, 59)
//                    calender.set(Calendar.SECOND, 59)
//                    calender.set(Calendar.MILLISECOND, 0)
//
//                    eventDateTime = calender.timeInMillis
//                    binding.textViewEventDate.text = AppFunctions.convertTimeInMillsecToPattern(
//                        eventDateTime,
//                        AppConstant.DATE_PATTERN_EVENT_DATE
//                    )
//                }
            }
        }
    }

    private fun pickDateTime() {

    }


}