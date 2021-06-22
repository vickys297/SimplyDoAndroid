package com.example.simplydo.ui.fragments.addOrEditTodoTask

import android.app.DatePickerDialog
import android.content.Context
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
import com.example.simplydo.adapters.newTodotask.AudioAttachmentAdapter
import com.example.simplydo.adapters.newTodotask.ContactAttachmentAdapter
import com.example.simplydo.adapters.newTodotask.FileAttachmentAdapter
import com.example.simplydo.adapters.newTodotask.GalleryAttachmentAdapter
import com.example.simplydo.bottomSheetDialogs.attachments.AddAttachmentsFragments
import com.example.simplydo.databinding.AddNewTodoFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.LatLngModel
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.collections.ArrayList

internal val TAG = AddNewTodo::class.java.canonicalName

class AddNewTodo : Fragment(), NewTodoOptionsFragmentsInterface {

    companion object {
        fun newInstance() = EditTodo()
    }


    private lateinit var viewModel: AddNewTodoViewModel
    private lateinit var binding: AddNewTodoFragmentBinding

    private lateinit var contactArrayList: ArrayList<ContactModel>
    private lateinit var galleryArrayList: ArrayList<GalleryModel>
    private lateinit var audioArrayList: ArrayList<AudioModel>
    private lateinit var filesArrayList: ArrayList<FileModel>

    private var eventDate: Long = System.currentTimeMillis()
    private lateinit var eventTime: String
    private var latLng: LatLngModel = LatLngModel(0.0, 0.0)


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

    private val fileAttachmentInterface = object  : FileAttachmentInterface{
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
        filesArrayList = ArrayList()

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

            checkForAttachment()

            Log.d(TAG, "NAVIGATION_CONTACT_DATA_KEY: $result")

            if (contactArrayList.isNotEmpty()) {
                binding.linearLayoutContactAttachment.visibility = View.VISIBLE
                contactAttachmentAdapter.updateDataSet(contactArrayList)
            } else {
                binding.linearLayoutContactAttachment.visibility = View.GONE
            }

        }

        // We use a String here, but any type that can be put in a Bundle is supported
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<AudioModel>>(
            AppConstant.NAVIGATION_AUDIO_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            // Do something with the result.
            audioArrayList = result

            checkForAttachment()

            Log.d(TAG, "NAVIGATION_AUDIO_DATA_KEY: $result")

            if (audioArrayList.isNotEmpty()) {
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

            checkForAttachment()

            Log.d(TAG, "NAVIGATION_GALLERY_DATA_KEY: $result")

            if (galleryArrayList.isNotEmpty()) {
                binding.linearLayoutGalleryAttachment.visibility = View.VISIBLE
                galleryAttachmentAdapter.updateDataset(galleryArrayList)
            } else {
                binding.linearLayoutGalleryAttachment.visibility = View.GONE
            }
        }

        // We use a String here, but any type that can be put in a Bundle is supported
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<FileModel>>(
            AppConstant.NAVIGATION_FILES_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->

            filesArrayList = result

            checkForAttachment()

            Log.i(TAG, "NAVIGATION_FILES_DATA_KEY: $result")

            if (filesArrayList.isNotEmpty()){
                binding.linearFilesAttachment.visibility = View.VISIBLE
                fileAttachmentAdapter.updateDataSet(filesArrayList)
            }else{
                binding.linearFilesAttachment.visibility = View.GONE
            }

        }


        // We use a String here, but any type that can be put in a Bundle is supported
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>(
            AppConstant.NAVIGATION_LOCATION_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->

            latLng.apply {
                lat = result.latitude
                lng = result.longitude
            }


            checkForAttachment()


            // Do something with the result.
            Log.i(TAG, "attachmentDataObserver: latLng --> $result")

            binding.linearLocationAttachment.visibility = View.GONE

            result?.let { latlng ->

                binding.linearLocationAttachment.visibility = View.VISIBLE

                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?

                mapFragment?.getMapAsync { googleMap ->
                    googleMap.clear()

                    Log.i(TAG, "attachmentDataObserver: $googleMap")

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

        binding.textViewEventDate.text = AppFunctions.convertTimeInMillsecToPattern(
            eventDate,
            AppConstant.DATE_PATTERN_EVENT_DATE
        )

        binding.textViewEventTime.text = AppFunctions.convertTimeInMillsecToPattern(
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
                        com.example.simplydo.bottomSheetDialogs.basicAddTodoDialog.TAG,
                        "timeInMillis: ${newDate.timeInMillis}/${System.currentTimeMillis()}"
                    )

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

            Log.i(TAG, "onViewCreated: $latLng")

            if (validateDetails()) {

                val title = binding.etTitle.text.toString()
                val task = binding.etTask.text.toString()
                val isPriority = binding.cbPriority.isChecked

                val newInsertId = viewModel.createTodo(
                    title,
                    task,
                    eventDate,
                    eventTime,
                    isPriority,
                    galleryArray = galleryArrayList,
                    contactArray = contactArrayList,
                    audioArray = audioArrayList,
                    filesArray = filesArrayList,
                    location = latLng
                )


                newInsertId.let {
                    val bundle = Bundle()
                    bundle.putLong("dtId", it)
                    bundle.putString("title", title)
                    bundle.putString("task", task)
                    bundle.putBoolean("priority", isPriority)
                    AppFunctions.showSnackBar(binding.root, "New task added")

                    AppFunctions.setupNotification(it, eventDate,eventTime, bundle, requireActivity())
                }

                AppFunctions.showSnackBar(binding.root, "New task added")
                findNavController().navigateUp()
            } else {
                AppFunctions.showSnackBar(binding.root, "Fill the required fields")

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
        Log.i(TAG, "checkForAttachment: $")
        if (
            audioArrayList.isEmpty() &&
            galleryArrayList.isEmpty() &&
            contactArrayList.isEmpty() &&
            filesArrayList.isEmpty() &&
            latLng.lat == 0.0 && latLng.lng == 0.0
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
            binding.textViewEventTime,
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