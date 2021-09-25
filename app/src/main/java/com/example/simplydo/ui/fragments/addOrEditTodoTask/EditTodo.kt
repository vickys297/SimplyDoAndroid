package com.example.simplydo.ui.fragments.addOrEditTodoTask

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.newTodotask.AudioAttachmentAdapter
import com.example.simplydo.adapters.newTodotask.ContactAttachmentAdapter
import com.example.simplydo.adapters.newTodotask.FileAttachmentAdapter
import com.example.simplydo.adapters.newTodotask.GalleryAttachmentAdapter
import com.example.simplydo.bottomSheetDialogs.attachments.AddAttachmentsFragments
import com.example.simplydo.databinding.EditTodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.LatLngModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*


internal val TAG_EDIT = EditTodo::class.java.canonicalName

class EditTodo : Fragment(), NewTodoOptionsFragmentsInterface {

    companion object {
        fun newInstance() = EditTodo()
    }


    private lateinit var viewModel: AddNewTodoViewModel
    private lateinit var binding: EditTodoFragmentBinding

    private lateinit var contactArrayList: ArrayList<ContactModel>
    private lateinit var galleryArrayList: ArrayList<GalleryModel>
    private lateinit var audioArrayList: ArrayList<AudioModel>
    private lateinit var filesArrayList: ArrayList<FileModel>

    private var eventDateTime: Long = System.currentTimeMillis()

    private var latLng: LatLngModel = LatLngModel()


    private lateinit var currentTodoModel: TodoModel


    // all adapter
    private lateinit var audioAttachmentAdapter: AudioAttachmentAdapter
    private lateinit var galleryAttachmentAdapter: GalleryAttachmentAdapter
    private lateinit var contactAttachmentAdapter: ContactAttachmentAdapter
    private lateinit var fileAttachmentAdapter: FileAttachmentAdapter


    // all interfaces

    private val audioAttachmentInterface =
        object : AudioAttachmentInterface {
            override fun onAudioSelect(item: AudioModel) {

            }

            override fun onRemoveItem(position: Int) {
                audioArrayList.removeAt(position)
                audioAttachmentAdapter.notifyItemRemoved(position)
                AppFunctions.showMessage("Removed", requireContext())
            }
        }

    private val galleryAttachmentInterface =
        object : GalleryAttachmentInterface {
            override fun onItemSelect(item: GalleryModel) {

            }
        }

    private val contactAttachmentInterface = object : ContactAttachmentInterface {
        override fun onContactSelect(item: ContactModel) {

        }
    }

    private val fileAttachmentInterface = object : FileAttachmentInterface {
        override fun onFileSelect(item: FileModel) {

        }

    }

    private var addAttachmentInterface: AddAttachmentInterface = object : AddAttachmentInterface {
        override fun onAddDocument() {
            findNavController().navigate(R.id.action_addNewTodo_to_documentListFragment)
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
        binding = EditTodoFragmentBinding.inflate(inflater, container, false)

        setViewModel()
        attachmentDataObserver()

        val sampleText = "0:0".split(":".toRegex())
        Log.d(TAG_EDIT, "onCreateView: ${sampleText[0].toInt()}")

        // setup array list
        contactArrayList = ArrayList()
        galleryArrayList = ArrayList()
        audioArrayList = ArrayList()
        filesArrayList = ArrayList()


        arguments?.let {

            currentTodoModel = it.getSerializable(AppConstant.NAVIGATION_TASK_DATA_KEY) as TodoModel
            val task = it.getSerializable(AppConstant.NAVIGATION_TASK_DATA_KEY) as TodoModel

            binding.etTitle.setText(task.title)
            binding.etTask.setText(task.todo)

            eventDateTime = task.eventDateTime

            binding.textViewEventDate.text =
                AppFunctions.convertTimeInMillsecToPattern(
                    eventDateTime,
                    AppConstant.DATE_PATTERN_EVENT_DATE
                )

            binding.textViewEventTime.text =
                AppFunctions.convertTimeInMillsecToPattern(
                    eventDateTime,
                    AppConstant.TIME_PATTERN_EVENT_TIME
                )

            binding.cbPriority.isChecked = task.isHighPriority

            contactArrayList = task.contactAttachments
            galleryArrayList = task.imageAttachments
            audioArrayList = task.audioAttachments
            filesArrayList = task.fileAttachments
            latLng = task.locationData
        }

        return binding.root
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
            this.viewModel = this@EditTodo.viewModel
            lifecycleOwner = this@EditTodo
            executePendingBindings()
        }


    }

    private fun attachmentDataObserver() {

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<ContactModel>>(
            AppConstant.Key.NAVIGATION_CONTACT_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            contactArrayList = result

            checkForAttachment()

            Log.d(TAG_EDIT, "NAVIGATION_CONTACT_DATA_KEY: $result")

            if (contactArrayList.isNotEmpty()) {
                binding.linearLayoutContactAttachment.visibility = View.VISIBLE
                contactAttachmentAdapter.updateDataSet(contactArrayList)
            } else {
                binding.linearLayoutContactAttachment.visibility = View.GONE
            }

        }


        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<AudioModel>>(
            AppConstant.Key.NAVIGATION_AUDIO_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->

            audioArrayList = result

            checkForAttachment()

            Log.d(TAG_EDIT, "NAVIGATION_AUDIO_DATA_KEY: $result")

            if (audioArrayList.isNotEmpty()) {
                binding.linearLayoutAudioAttachment.visibility = View.VISIBLE
                audioAttachmentAdapter.updateDataSet(audioArrayList)
            } else {
                binding.linearLayoutAudioAttachment.visibility = View.GONE
            }
        }



        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<GalleryModel>>(
            AppConstant.Key.NAVIGATION_GALLERY_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->

            galleryArrayList = result

            checkForAttachment()

            Log.d(TAG_EDIT, "NAVIGATION_GALLERY_DATA_KEY: $result")

            if (galleryArrayList.isNotEmpty()) {
                binding.linearLayoutGalleryAttachment.visibility = View.VISIBLE
                galleryAttachmentAdapter.updateDataset(galleryArrayList)
            } else {
                binding.linearLayoutGalleryAttachment.visibility = View.GONE
            }
        }


        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<FileModel>>(
            AppConstant.Key.NAVIGATION_FILES_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->

            filesArrayList = result

            checkForAttachment()

            Log.d(TAG_EDIT, "NAVIGATION_FILES_DATA_KEY: $result")

            if (filesArrayList.isNotEmpty()) {
                binding.linearFilesAttachment.visibility = View.VISIBLE
                fileAttachmentAdapter.updateDataSet(filesArrayList)
            } else {
                binding.linearFilesAttachment.visibility = View.GONE
            }

        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>(
            AppConstant.Key.NAVIGATION_LOCATION_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->

            Log.d(TAG_EDIT, "NAVIGATION_LOCATION_DATA_KEY: $result")

            latLng.apply {
                lat = result.latitude
                lng = result.longitude
            }

            checkForAttachment()


            binding.linearLocationAttachment.visibility = View.GONE

            result?.let { latlng ->
                binding.linearLocationAttachment.visibility = View.VISIBLE

                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?

                mapFragment?.getMapAsync { googleMap ->
                    googleMap.clear()

                    Log.d(TAG_EDIT, "attachmentDataObserver: $googleMap")

                    googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            requireContext(),
                            R.raw.map_styled_json
                        )
                    )

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12f))
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(latlng)
                            .icon(
                                AppFunctions.getDrawableToBitmap(
                                    R.drawable.ic_map_marker,
                                    requireActivity()
                                )
                            )
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.linearLayoutEventTimeSelector.setOnClickListener {
            val et = AppFunctions.getCurrentDateCalender()
            et.timeInMillis = eventDateTime
            val hour = et.get(Calendar.HOUR_OF_DAY)
            val minute = et.get(Calendar.MINUTE)

            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(hour)
                .setMinute(minute)
                .setTitleText("Select Event Time")
                .build()

            picker.show(requireActivity().supportFragmentManager, "TAG_EDIT")

            picker.addOnPositiveButtonClickListener {
                // call back code

//                val selectedTime =
//                    "${picker.hour}:${picker.minute} ${if (picker.hour >= 12) "PM" else "AM"}"

                et.set(Calendar.HOUR_OF_DAY, picker.hour)
                et.set(Calendar.MINUTE, picker.minute)
                et.set(Calendar.SECOND, 0)
                et.set(Calendar.MILLISECOND, 0)
                eventDateTime = et.timeInMillis

                binding.textViewEventTime.text = AppFunctions.convertTimeInMillsecToPattern(
                    eventDateTime,
                    AppConstant.TIME_PATTERN_EVENT_TIME
                )
            }
        }

        binding.linearLayoutEventDateSelector.setOnClickListener {

            val calender = Calendar.getInstance()
            calender.timeInMillis = currentTodoModel.eventDateTime
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
                    calender.timeInMillis,
                    AppConstant.DATE_PATTERN_EVENT_DATE
                )
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
            if (validateDetails()) {

                Log.d(
                    TAG_EDIT,
                    "onViewCreated: ${
                        AppFunctions.convertTimeInMillsecToPattern(
                            eventDateTime,
                            AppConstant.DATE_PATTERN_EVENT_DATE_TIME
                        )
                    }"
                )
                val updateTodo = viewModel.updateTodo(
                    dtId = currentTodoModel.dtId,
                    binding.etTitle.text.toString(),
                    binding.etTask.text.toString(),
                    eventDateTime,
                    binding.cbPriority.isChecked,
                    galleryArray = galleryArrayList,
                    contactArray = contactArrayList,
                    audioArray = audioArrayList,
                    filesArray = filesArrayList,
                    location = latLng,
                    createAt = currentTodoModel.createdAt,
                )

                Log.d(TAG, "onViewCreated: update task $updateTodo")
                AppFunctions.showMessage("New task added", requireContext())
                findNavController().navigateUp()
            } else {
                AppFunctions.showMessage("Fill the required fields", requireContext())
            }
        }

        binding.imageButtonNewTodoOptions.setOnClickListener {
            AddAttachmentsFragments.newInstance(addAttachmentInterface)
                .show(requireActivity().supportFragmentManager, "dialog")
        }

        audioAttachmentAdapter =
            AudioAttachmentAdapter(audioAttachmentInterface = audioAttachmentInterface)
        galleryAttachmentAdapter =
            GalleryAttachmentAdapter(requireContext(), galleryAttachmentInterface)
        contactAttachmentAdapter =
            ContactAttachmentAdapter(requireContext(), contactAttachmentInterface)
        fileAttachmentAdapter =
            FileAttachmentAdapter(fileAttachmentInterface)


        binding.recyclerViewAudioAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = audioAttachmentAdapter
        }

        binding.recyclerViewGalleryAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = galleryAttachmentAdapter
        }

        binding.recyclerViewContactAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = contactAttachmentAdapter
        }

        binding.recyclerViewDocumentAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = fileAttachmentAdapter
        }

        checkForAttachment()
    }

    private fun checkForAttachment() {
        if (
            audioArrayList.isEmpty() &&
            galleryArrayList.isEmpty() &&
            contactArrayList.isEmpty() &&
            filesArrayList.isEmpty() &&
            latLng.lng == 0.0 && latLng.lat == 0.0
        ) {
            binding.noAttachmentFound.root.visibility = View.VISIBLE
        } else {
            binding.noAttachmentFound.root.visibility = View.GONE
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