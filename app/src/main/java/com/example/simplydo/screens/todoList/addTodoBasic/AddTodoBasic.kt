package com.example.simplydo.screens.todoList.addTodoBasic

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.databinding.FragmentAddTodoBasicListDialogBinding
import com.example.simplydo.utli.CreateBasicTodoInterface
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
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
class AddTodoBasic(private val appInterface: CreateBasicTodoInterface) : BottomSheetDialogFragment() {

    private var _binding: FragmentAddTodoBasicListDialogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var eventDate: String

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
            appInterface.onAddMoreDetails()
            dismiss()
        }

        binding.btnCreateTodo.setOnClickListener {
            validateInput()
        }


        val simpleDate = SimpleDateFormat("dd-MM-yyyy")
        binding.etDate.setText(simpleDate.format(Date()).toString())
        eventDate = simpleDate.format(Date()).toString()

        binding.tvDayOfMonth.text = SimpleDateFormat("dd").format(Date().time)
        binding.tvMonth.text = SimpleDateFormat("MMM").format(Date().time)

        binding.llDateSelector.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val datePicker = DatePickerDialog(requireContext())
                datePicker.datePicker.minDate = System.currentTimeMillis()
                datePicker.setOnDateSetListener { picker, year, month, dayOfMonth ->
                    val newDate = Calendar.getInstance()
                    newDate.set(year, month, dayOfMonth)
                    eventDate = (simpleDate.format(newDate.time))

                    binding.tvDayOfMonth.text = SimpleDateFormat("dd").format(newDate.time)
                    binding.tvMonth.text = SimpleDateFormat("MMM").format(newDate.time)

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
        fun newInstance(appInterface: CreateBasicTodoInterface): AddTodoBasic =
            AddTodoBasic(appInterface)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}