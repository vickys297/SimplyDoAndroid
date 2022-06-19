package com.example.simplydo.dialog.bottomSheetDialogs.basicAddTodoDialog

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.example.simplydo.R
import com.example.simplydo.databinding.FragmentAddTodoBasicListDialogBinding
import com.example.simplydo.utils.AppConstant
import com.example.simplydo.utils.AppFunctions
import com.example.simplydo.utils.NewRemainderInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    AddNewRemainder.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */


class AddNewRemainder(
    private val callback: NewRemainderInterface,
    defaultEventDate: Long,
) : BottomSheetDialogFragment(), View.OnClickListener {

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

        binding.textViewEventDate.text = AppFunctions.getCurrentEventDate()

        binding.btnAddMoreDetails.setOnClickListener(this)
        binding.imageButtonBasicAddTodoClose.setOnClickListener(this)
        binding.btnCreateTodo.setOnClickListener(this)
        binding.linearLayoutTitle.setOnClickListener(this)
        binding.linearLayoutTask.setOnClickListener(this)
        binding.linearLayoutEventDateSelector.setOnClickListener(this)
    }

    private fun validateInput() {
        val view = arrayListOf(binding.editTextTitle, binding.editTextTask)

        var flag = true
        for (v in view) {
            if (v.text.toString().isEmpty()) {
                v.error = "Required"
                flag = false
            }
        }

        if (flag) {
            callback.onCreateTodo(
                title = binding.editTextTitle.text.toString(),
                task = binding.editTextTask.text.toString(),
                eventDate = eventDate,
                isPriority = binding.cbPriority.isChecked,
                isAllDayTask = binding.checkBoxBoxAllDayTask.isChecked
            )
            dismiss()
        }
    }


    companion object {
        fun newInstance(
            createBasicTodoInterface: NewRemainderInterface,
            eventDate: Long,
        ): AddNewRemainder =
            AddNewRemainder(createBasicTodoInterface, eventDate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnAddMoreDetails -> {
                callback.onAddMoreDetails(eventDate)
                dismiss()
            }
            R.id.imageButtonBasicAddTodoClose -> {
                dismiss()
            }
            R.id.btnCreateTodo -> {
                validateInput()
            }

            R.id.linearLayoutTitle -> {
                binding.editTextTitle.requestFocus()
                requireActivity().runOnUiThread {
                    val inputMethodManager = requireActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE
                    ) as InputMethodManager

                    inputMethodManager.toggleSoftInputFromWindow(
                        binding.editTextTitle.applicationWindowToken,
                        InputMethodManager.SHOW_FORCED,
                        0
                    )
                }
            }

            R.id.linearLayoutTask -> {
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

            R.id.linearLayoutEventDateSelector -> {
                /*            DateTimePickerFragment.newInstance()
                                .show(requireActivity().supportFragmentManager, "date_time_dialog")*/

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

                        // default time to be end of the data
                        newDate.set(year, month, dayOfMonth, 23, 59, 59)
                        eventDate = newDate.timeInMillis
                        binding.textViewEventDate.text = AppFunctions.convertTimeInMillsecToPattern(
                            newDate.timeInMillis,
                            AppConstant.DATE_PATTERN_EVENT_DATE
                        )

                        datePicker.dismiss()
                    }
                    datePicker.show()
                }
            }


        }
    }

}