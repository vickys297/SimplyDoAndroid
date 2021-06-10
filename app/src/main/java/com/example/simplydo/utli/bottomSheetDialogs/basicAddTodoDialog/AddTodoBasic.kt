package com.example.simplydo.utli.bottomSheetDialogs.basicAddTodoDialog

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.simplydo.databinding.FragmentAddTodoBasicListDialogBinding
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.CreateBasicTodoInterface
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

internal val TAG = AddTodoBasic::class.java.canonicalName

class AddTodoBasic(
    private val appInterface: CreateBasicTodoInterface,
    defaultEventDate: Long,
) :
    BottomSheetDialogFragment() {

    private var _binding: FragmentAddTodoBasicListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var eventDate = (defaultEventDate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAddTodoBasicListDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {


                val datePicker = DatePickerDialog(requireContext())
                datePicker.datePicker.minDate = System.currentTimeMillis()

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = eventDate

                datePicker.datePicker.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                    val newDate = Calendar.getInstance()
                    newDate.set(year, month, dayOfMonth)

                    Log.i(
                        TAG,
                        "timeInMillis: ${newDate.timeInMillis}/${System.currentTimeMillis()}"
                    )

                    eventDate = newDate.timeInMillis

                    binding.textViewEventDate.text = AppFunctions.getDateStringFromMilliseconds(
                        newDate.timeInMillis,
                        AppConstant.DATE_PATTERN_EVENT_DATE
                    )

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
                title = binding.etTitle.text.toString(),
                task = binding.etTask.text.toString(),
                eventDate = eventDate,
                eventTime = "23:59",
                isPriority = binding.cbPriority.isChecked
            )
            dismiss()
        }
    }


    companion object {
        fun newInstance(
            createBasicTodoInterface: CreateBasicTodoInterface,
            eventDate: Long,
        ): AddTodoBasic =
            AddTodoBasic(createBasicTodoInterface, eventDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}