package com.example.simplydo.ui.fragments.addOrEditTodoTask

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.adapters.newTodotask.AudioAttachmentAdapter
import com.example.simplydo.adapters.newTodotask.ContactAttachmentAdapter
import com.example.simplydo.adapters.newTodotask.FileAttachmentAdapter
import com.example.simplydo.adapters.newTodotask.GalleryAttachmentAdapter
import com.example.simplydo.adapters.todoTaskList.TodoTaskAdapter
import com.example.simplydo.adapters.todoTaskList.TodoTaskFooterAdapter
import com.example.simplydo.databinding.AddNewTodoFragmentBinding
import com.example.simplydo.dialog.bottomSheetDialogs.AddTaskItemBottomSheetModel
import com.example.simplydo.dialog.bottomSheetDialogs.attachments.AddAttachmentsFragments
import com.example.simplydo.dialog.bottomSheetDialogs.priorityDialog.PriorityDialog
import com.example.simplydo.dialog.bottomSheetDialogs.repeatDialog.RepeatDialog
import com.example.simplydo.dialog.bottomSheetDialogs.tags.TagsBottomSheetDialog
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.*
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utlis.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*
import kotlin.collections.ArrayList


internal val TAG = AddNewTodo::class.java.canonicalName

class AddNewTodo : Fragment(R.layout.add_new_todo_fragment), NewTodoOptionsFragmentsInterface {


    private var dataSet: ArrayList<TodoTaskModel> = ArrayList()
    private lateinit var viewModel: AddNewTodoViewModel
    private lateinit var binding: AddNewTodoFragmentBinding

    private lateinit var repeatDialog: RepeatDialog
    private lateinit var priorityDialog: PriorityDialog

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

    private lateinit var todoTaskAdapter: TodoTaskAdapter
    private lateinit var todoTaskFooterAdapter: TodoTaskFooterAdapter

    private var arrayListRepeatFrequency: ArrayList<SelectorDataModal> = ArrayList()
    private var arrayListRepeatWeek: ArrayList<SelectorDataModal> = ArrayList()

    private var taskNoteTextItemListener: AppInterface.TaskNoteTextItemListener =
        object : AppInterface.TaskNoteTextItemListener {
            override fun onAdd(content: String) {
                dataSet.add(
                    TodoTaskModel(
                        type = AppConstant.Task.VIEW_TASK_NOTE_TEXT,
                        content = content
                    )
                )
                todoTaskAdapter.updateDataSet(dataSet)
                binding.textViewTodoListText.isVisible = todoTaskAdapter.dataSet.isEmpty()
            }
        }

    // all interfaces

    private val onPriorityCallback = object : AppInterface.PriorityDialog.Callback {
        override fun onSelect(i: Int) {

            val parsedText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(
                    "This task has <font color='#6200EE'>${
                        AppFunctions.Priority.getPriorityById(i)
                    }</font> priority", Html.FROM_HTML_MODE_LEGACY
                )
            } else {
                Html.fromHtml(
                    "This task has <font color='#6200EE'>${
                        AppFunctions.Priority.getPriorityById(i)
                    }</font> priority"
                )
            }
            binding.textViewPriorityText.text = parsedText
        }
    }

    private val tagCallback: AppInterface.TagDialog.Callback =
        object : AppInterface.TagDialog.Callback {
            override fun onDone(selectedTag: ArrayList<TagModel>) {
                binding.textViewNoTagAdded.isVisible = selectedTag.isEmpty()
                for (tag in selectedTag) {
                    val chipTag = Chip(requireContext())
                    chipTag.text = tag.tagName
                    chipTag.setChipBackgroundColorResource(R.color.colorPrimary)
                    chipTag.isCloseIconVisible = true
                    chipTag.setTextColor(
                        requireContext().resources.getColor(
                            R.color.white,
                            requireContext().theme
                        )
                    )
                    chipTag.setOnClickListener {
                        selectedTag.remove(tag)
                        binding.chipGroupTaskTags.removeView(it)
                        binding.chipGroupTaskTags.isVisible = selectedTag.isNotEmpty()
                        binding.textViewNoTagAdded.isVisible = selectedTag.isEmpty()
                    }
                    binding.chipGroupTaskTags.addView(chipTag)
                }
                binding.chipGroupTaskTags.isVisible = selectedTag.isNotEmpty()
            }

        }

    private val onRepeatCallback: RepeatDialogInterface = object : RepeatDialogInterface {
        override fun onSetRepeat(
            arrayFrequency: ArrayList<SelectorDataModal>,
            arrayWeek: ArrayList<SelectorDataModal>
        ) {
            arrayListRepeatFrequency = arrayFrequency
            arrayListRepeatWeek = arrayWeek
            binding.chipGroupWeeks.isVisible = arrayListRepeatWeek.isNotEmpty()
            setRepeatDate()
        }

        override fun onCancel() {

        }

    }

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

    private val addTodoInterface = object : NewTodo.AddTask {
        override fun onAddText() {
            AddTaskItemBottomSheetModel
                .newInstance(
                    taskNoteTextItemListener = taskNoteTextItemListener,
                    context = requireContext()
                )
                .show(
                    requireActivity().supportFragmentManager, "TaskNoteTextItemListener"
                )
        }

        override fun onAddList() {
            findNavController().navigate(R.id.action_addNewTodo_to_addNewTaskItemFragment)
        }

        override fun onClose(item: TodoTaskModel, position: Int) {
            dataSet.removeAt(position)
            todoTaskAdapter.notifyItemRemoved(position)
            binding.textViewTodoListText.isVisible = todoTaskAdapter.dataSet.isEmpty()
        }

    }

    private val todoTaskInterface = object : NewTodo.TodoTask {
        override fun onTaskSelect(item: TodoTaskModel) {

            when (item.type) {
                AppConstant.Task.VIEW_TASK_NOTE_TEXT -> {
                    val bundle = Bundle()
                    bundle.putString(AppConstant.Bundle.Key.TODO_TASK_TEXT, item.content)
                    AddTaskItemBottomSheetModel.newInstance(
                        context = requireContext(),
                        taskNoteTextItemListener = taskNoteTextItemListener,
                        bundle = bundle
                    )
                }
                AppConstant.Task.VIEW_TASK_NOTE_LIST -> {
                    val bundle = Bundle()
                    bundle.putSerializable(AppConstant.Bundle.Key.TODO_TASK_TEXT, item.contentList)
                    findNavController().navigate(
                        R.id.action_addNewTodo_to_addNewTaskItemFragment,
                        bundle
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = AddNewTodoFragmentBinding.bind(view)

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

        repeatDialog =
            RepeatDialog.getInstance(requireContext(), callback = onRepeatCallback)
        priorityDialog = PriorityDialog.newInstance(callback = onPriorityCallback)

        binding.textViewEventDate.text = AppFunctions.convertTimeInMillsecToPattern(
            eventDate,
            AppConstant.DATE_PATTERN_EVENT_DATE
        )

        binding.textViewEventTime.text = AppFunctions.convertTimeInMillsecToPattern(
            eventDate,
            AppConstant.TIME_PATTERN_EVENT_TIME
        )


        binding.linearLayoutRepeatPeriod.setOnClickListener {
            repeatDialog.show(
                requireActivity().supportFragmentManager,
                repeatDialog.tag
            )
        }

        binding.linearLayoutPriority.setOnClickListener {
            priorityDialog.show(
                requireActivity().supportFragmentManager,
                priorityDialog.tag
            )
        }

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
                        com.example.simplydo.dialog.bottomSheetDialogs.basicAddTodoDialog.TAG,
                        "timeInMillis: ${newDate.timeInMillis}/${System.currentTimeMillis()}"
                    )

                    eventDate = newDate.timeInMillis

                    binding.textViewEventDate.text = AppFunctions.convertTimeInMillsecToPattern(
                        newDate.timeInMillis,
                        AppConstant.DATE_PATTERN_EVENT_DATE
                    )
                    binding.linearLayoutEventTimeSelector.performClick()
                    datePicker.dismiss()
                }
                datePicker.show()
            }
        }

        binding.linearLayoutTitle.setOnClickListener {
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

        binding.linearLayoutTask.setOnClickListener {
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

        binding.btnCreateTodoTask.setOnClickListener {
            Log.i(TAG, "onViewCreated: $latLng")
            if (validateDetails()) {
                val title = binding.editTextTitle.text.toString()
                val task = binding.editTextTask.text.toString()
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
                    location = latLng,
                    repeatFrequency = arrayListRepeatFrequency,
                    repeatWeek = arrayListRepeatWeek,
                )


                newInsertId.let {
                    val bundle = Bundle()
                    bundle.putLong("dtId", it)
                    bundle.putString("title", title)
                    bundle.putString("task", task)
                    bundle.putBoolean("priority", isPriority)
                    AppFunctions.showSnackBar(binding.root, "New task added")

                    AppFunctions.setupNotification(
                        it,
                        eventDate,
                        eventTime,
                        bundle,
                        requireActivity()
                    )
                }

                AppFunctions.showSnackBar(binding.root, getString(R.string.new_task_added))
                findNavController().navigateUp()
            } else {
                AppFunctions.showSnackBar(
                    binding.root,
                    getString(R.string.fill_the_required_fields)
                )
            }
        }

        binding.imageButtonNewTodoOptions.setOnClickListener {
            AddAttachmentsFragments.newInstance(requireContext(), addAttachmentInterface)
                .show(requireActivity().supportFragmentManager, "dialog")
        }

        binding.linearLayoutTags.setOnClickListener {
            TagsBottomSheetDialog.newInstance(requireContext(), tagCallback).show(
                requireActivity().supportFragmentManager,
                TagsBottomSheetDialog::class.java.canonicalName
            )
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

        todoTaskAdapter = TodoTaskAdapter(
            dataSet = dataSet,
            addTodoInterface,
            todoTaskInterface = todoTaskInterface
        )

        todoTaskFooterAdapter = TodoTaskFooterAdapter(addTodoInterface = addTodoInterface)

        val simpleItemTouchHelper = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeFlag(
                    ItemTouchHelper.ACTION_STATE_DRAG,
                    ItemTouchHelper.DOWN or ItemTouchHelper.UP
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.absoluteAdapterPosition
                val targetPosition = target.absoluteAdapterPosition

                Collections.swap(
                    dataSet, fromPosition, targetPosition
                )


                todoTaskAdapter.notifyItemMoved(
                    fromPosition,
                    targetPosition
                )

                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchHelper)
        val contactAdapter = ConcatAdapter(todoTaskAdapter, todoTaskFooterAdapter)
        binding.recyclerViewTaskItems.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = contactAdapter
        }

        itemTouchHelper.attachToRecyclerView(binding.recyclerViewTaskItems)

        checkForAttachment()
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
        )[AddNewTodoViewModel::class.java]
        binding.apply {
            this.viewModel = this@AddNewTodo.viewModel
            lifecycleOwner = this@AddNewTodo
            executePendingBindings()
        }

        attachmentDataObserver()
    }

    private fun attachmentDataObserver() {

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<String>>(
            AppConstant.Key.NAVIGATION_ADD_TASK_LIST
        )?.observe(viewLifecycleOwner) { result ->
            dataSet.add(
                TodoTaskModel(
                    type = AppConstant.Task.VIEW_TASK_NOTE_LIST,
                    contentList = result
                )
            )

            todoTaskAdapter.updateDataSet(dataSet)
            binding.textViewTodoListText.isVisible = todoTaskAdapter.dataSet.isEmpty()
        }

        // We use a String here, but any type that can be put in a Bundle is supported
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<ArrayList<ContactModel>>(
            AppConstant.Key.NAVIGATION_CONTACT_DATA_KEY
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
            AppConstant.Key.NAVIGATION_AUDIO_DATA_KEY
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
            AppConstant.Key.NAVIGATION_GALLERY_DATA_KEY
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
            AppConstant.Key.NAVIGATION_FILES_DATA_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->

            filesArrayList = result

            checkForAttachment()

            Log.i(TAG, "NAVIGATION_FILES_DATA_KEY: $result")

            if (filesArrayList.isNotEmpty()) {
                binding.linearFilesAttachment.visibility = View.VISIBLE
                fileAttachmentAdapter.updateDataSet(filesArrayList)
            } else {
                binding.linearFilesAttachment.visibility = View.GONE
            }

        }


        // We use a String here, but any type that can be put in a Bundle is supported
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LatLng>(
            AppConstant.Key.NAVIGATION_LOCATION_DATA_KEY
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

    private fun checkForAttachment() {
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
            binding.editTextTask,
            binding.editTextTitle,
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
        AddAttachmentsFragments.newInstance(requireContext(), addAttachmentInterface)
            .show(requireActivity().supportFragmentManager, "dialog")
    }

    override fun onClose() {
        findNavController().navigateUp()
    }

    private fun setRepeatDate() {

        val stringBuilder = StringBuilder()
        for (frequency in arrayListRepeatFrequency) {
            if (frequency.selected) {
                stringBuilder.append("Repeat every ${frequency.value}")
            }
        }

        for (week in arrayListRepeatWeek) {
            if (week.selected) {
                val chipWeek = Chip(requireContext())
                chipWeek.text = week.visibleValue
                chipWeek.setChipBackgroundColorResource(R.color.colorPrimary)
                chipWeek.isCloseIconVisible = true
                chipWeek.setTextColor(
                    requireContext().resources.getColor(
                        R.color.white,
                        requireContext().theme
                    )
                )
                chipWeek.setOnCloseIconClickListener {
                    binding.chipGroupWeeks.removeView(it)
                    arrayListRepeatWeek.remove(week)

                    if (arrayListRepeatWeek.isEmpty()) {
                        binding.chipGroupWeeks.isVisible = arrayListRepeatWeek.isNotEmpty()
                        binding.textViewRepeatText.text =
                            requireContext().getText(R.string.tap_to_set_repeat_period_to_this_task)
                    }
                }

                binding.chipGroupWeeks.addView(chipWeek)
            }
        }
        binding.textViewRepeatText.apply {
            text = stringBuilder
        }
    }


}

