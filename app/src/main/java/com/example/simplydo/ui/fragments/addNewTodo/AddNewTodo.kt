package com.example.simplydo.ui.fragments.addNewTodo

import android.app.DatePickerDialog
import android.content.Context
import android.graphics.PorterDuff
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.databinding.AddNewTodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.adapters.newTodotask.AudioAttachmentAdapter
import com.example.simplydo.utli.bottomSheetDialogs.attachments.AddAttachmentsFragments
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.collections.ArrayList

internal val TAG = AddNewTodo::class.java.canonicalName

class AddNewTodo : Fragment(), NewTodoOptionsFragmentsInterface {

    companion object {
        fun newInstance() = AddNewTodo()
    }


    private lateinit var viewModel: AddNewTodoViewModel
    private lateinit var binding: AddNewTodoFragmentBinding

    private lateinit var contactArrayList: ArrayList<ContactModel>
    private lateinit var galleryArrayList: ArrayList<GalleryModel>
    private lateinit var audioArrayList: ArrayList<AudioModel>

    private var eventDate: Long = System.currentTimeMillis()
    private lateinit var eventTime: String


    // all adapter
    lateinit var audioAttachmentAdapter: AudioAttachmentAdapter


    // all interfaces

    private val audioAttachmentInterface =
        object : AudioAttachmentAdapter.AudioAttachmentInterface {
            override fun onAudioSelect(item: AudioModel) {

            }

        }

    private var addAttachmentInterface: AddAttachmentInterface = object : AddAttachmentInterface {
        override fun onAddDocument() {
        }

        override fun onAddAudio() {
            findNavController().navigate(R.id.action_addNewTodo_to_audioListFragment)
        }

        override fun onOpenGallery() {
            findNavController().navigate(R.id.action_addNewTodo_to_galleryListFragment)
        }

        override fun onAddContact() {
            findNavController().navigate(R.id.action_addNewTodo_to_contactsFragment)
        }

        override fun onAddLocation() {
            findNavController().navigate(R.id.action_addNewTodo_to_mapsFragment)
        }

        override fun onCancelTask() {
            findNavController().navigateUp()
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = AddNewTodoFragmentBinding.inflate(inflater, container, false)
        setupObserver()
        setViewModel()


        eventTime =
            "${AppFunctions.getHoursOfDay(System.currentTimeMillis())}:${
                AppFunctions.getMinutes(
                    System.currentTimeMillis()
                )
            }"
        Log.i(TAG, "onCreateView: eventTime --> $eventTime")

        arguments?.let {
            eventDate = it.getLong(getString(R.string.eventDateString))
        }

        // setup array list
        contactArrayList = ArrayList()
        galleryArrayList = ArrayList()
        audioArrayList = ArrayList()

        return binding.root
    }

    private fun setupObserver() {
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(
                requireContext(),
                AppRepository.getInstance(
                    requireContext(),
                    AppDatabase.getInstance(context = requireContext())
                )
            )
        ).get(
            AddNewTodoViewModel::class.java
        )
        binding.apply {
            this.viewModel = this@AddNewTodo.viewModel
            lifecycleOwner = this@AddNewTodo
            executePendingBindings()
        }

        attachmentDataObserver()
    }

    private fun attachmentDataObserver() {
        // We use a String here, but any type that can be put in a Bundle is supported
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<ContactModel>>(
            AppConstant.NAVIGATION_CONTACT_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            // Do something with the result.
            contactArrayList = result
            Log.d(TAG, "NAVIGATION_CONTACT_DATA_KEY: $result")


        }

        // We use a String here, but any type that can be put in a Bundle is supported
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<AudioModel>>(
            AppConstant.NAVIGATION_AUDIO_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            // Do something with the result.
            audioArrayList = result
            Log.d(TAG, "NAVIGATION_AUDIO_DATA_KEY: $result")

            if (result.isNotEmpty()) {
                binding.linearLayoutAudioAttachment.visibility = View.VISIBLE
                audioAttachmentAdapter.updateDataSet(audioArrayList)
            } else {
                binding.linearLayoutAudioAttachment.visibility = View.GONE
            }
        }


        // We use a String here, but any type that can be put in a Bundle is supported
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<GalleryModel>>(
            AppConstant.NAVIGATION_GALLERY_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            // Do something with the result.
            galleryArrayList = result
            Log.d(TAG, "NAVIGATION_GALLERY_DATA_KEY: $result")
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewEventDate.text = AppFunctions.getDateStringFromMilliseconds(
            eventDate,
            AppConstant.DATE_PATTERN_EVENT_DATE
        )

        binding.textViewEventTime.text = AppFunctions.getDateStringFromMilliseconds(
            eventDate,
            AppConstant.TIME_PATTERN_EVENT_TIME
        )

        binding.linearLayoutEventTimeSelector.setOnClickListener {


            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(AppFunctions.getHoursOfDay(System.currentTimeMillis()))
                .setMinute(AppFunctions.getMinutes(System.currentTimeMillis()))
                .setTitleText("Select Event Time")
                .build()

            picker.show(requireActivity().supportFragmentManager, "tag")

            picker.addOnPositiveButtonClickListener {
                // call back code
                val selectedTime =
                    "${picker.hour}:${picker.minute} ${if (picker.hour >= 12) "PM" else "AM"}"

                binding.textViewEventTime.text = selectedTime
                eventTime = "${picker.hour}:${picker.minute}"
            }
            picker.addOnNegativeButtonClickListener {
                // call back code
                Log.i(TAG, "onViewCreated: addOnNegativeButtonClickListener")
            }
            picker.addOnCancelListener {
                // call back code
                Log.i(TAG, "onViewCreated: addOnCancelListener")
            }
            picker.addOnDismissListener {
                // call back code
                Log.i(TAG, "onViewCreated: addOnDismissListener")
            }

        }

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
                        com.example.simplydo.utli.bottomSheetDialogs.basicAddTodoDialog.TAG,
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


        binding.btnCreateTodoTask.setOnClickListener {
            val snackBar: Snackbar

            if (validateDetails()) {
                viewModel.createTodo(
                    binding.etTitle.text.toString(),
                    binding.etTask.text.toString(),
                    eventDate,
                    eventTime,
                    binding.cbPriority.isChecked,
                    galleryArray = galleryArrayList,
                    contactArray = contactArrayList,
                    audioArray = audioArrayList
                )
                snackBar = Snackbar.make(binding.root, "New task add", Snackbar.LENGTH_SHORT)
                findNavController().navigateUp()
            } else {
                snackBar =
                    Snackbar.make(binding.root, "Fill the required fields", Snackbar.LENGTH_SHORT)
            }

            snackBar.animationMode = Snackbar.ANIMATION_MODE_SLIDE
            snackBar.setBackgroundTintMode(PorterDuff.Mode.DARKEN)
            snackBar.show()


        }

        binding.imageButtonNewTodoOptions.setOnClickListener {
            AddAttachmentsFragments.newInstance(addAttachmentInterface)
                .show(requireActivity().supportFragmentManager, "dialog")
        }

        audioAttachmentAdapter =
            AudioAttachmentAdapter(audioAttachmentInterface = audioAttachmentInterface)

        binding.recyclerViewAudioAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = audioAttachmentAdapter
        }


    }


    private fun validateDetails(): Boolean {
        var flag = true
        val allFieldsAreAvailable = arrayOf(
            binding.etTask,
            binding.etTitle,
            binding.textViewEventTime,
            binding.textViewEventTime
        )
        for (item in allFieldsAreAvailable) {
            if (item.text.isNullOrEmpty()) {
                item.error = "Required"
                flag = false
            }
        }
        return flag
    }

    override fun onAddAttachments() {
        AddAttachmentsFragments.newInstance(addAttachmentInterface)
            .show(requireActivity().supportFragmentManager, "dialog")
    }

    override fun onClose() {
        findNavController().navigateUp()
    }

}