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
    private val todoModel: TodoModel,
    private val editBasicTodoInterface: EditBasicTodoInterface
) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentAddTodoBasicListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val ediTodoModel: TodoModel get() = todoModel

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
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//
//                val datePicker = DatePickerDialog(requireContext())
//                datePicker.datePicker.minDate = System.currentTimeMillis()
//
//                val calendar = Calendar.getInstance()
//                calendar.timeInMillis = eventDate
//
//                datePicker.datePicker.updateDate(
//                    calendar.get(Calendar.YEAR),
//                    calendar.get(Calendar.MONTH),
//                    calendar.get(Calendar.DAY_OF_MONTH)
//                )
//
//                datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
//                    val newDate = Calendar.getInstance()
//
//                    // default time to be end of the data
//                    newDate.set(year, month, dayOfMonth, 23, 59, 59)
//                    eventDate = newDate.timeInMillis
//                    binding.textViewEventDate.text = AppFunctions.convertTimeInMillsecToPattern(
//                        newDate.timeInMillis,
//                        AppConstant.DATE_PATTERN_EVENT_DATE
//                    )
//
//                    datePicker.dismiss()
//                }
//                datePicker.show()
//            }
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
        val view = arrayListOf(binding.etTitle, binding.etTask)

        var flag = true
        for (v in view) {
            if (v.text.toString().isEmpty()) {
                v.error = "Required"
                flag = false
            }
        }

        if (flag) {
            editBasicTodoInterface.onUpdateDetails(
                ediTodoModel
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