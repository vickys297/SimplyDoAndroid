package com.example.simplydo.ui.activity.privateWorkspace.workspaceTaskListFullDetails

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.GroupViewAdapter
import com.example.simplydo.adapters.UserProfileStackAdapter
import com.example.simplydo.adapters.taskAttachmentAdapter.AttachmentAudioAdapter
import com.example.simplydo.adapters.taskAttachmentAdapter.AttachmentContactAdapter
import com.example.simplydo.adapters.taskAttachmentAdapter.AttachmentGalleryAdapter
import com.example.simplydo.adapters.taskAttachmentAdapter.AttachmentTodoTaskAdapter
import com.example.simplydo.databinding.TodoFullDetailsFragmentBinding
import com.example.simplydo.dialog.bottomSheetDialogs.TaskFullDetailsBottomSheetDialog
import com.example.simplydo.dialog.bottomSheetDialogs.basicAddTodoDialog.EditWorkspaceTaskBasic
import com.example.simplydo.database.AppDatabase
import com.example.simplydo.model.*
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel
import com.example.simplydo.utils.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class WorkspaceTaskFullDetailsFragment : Fragment(R.layout.todo_full_details_fragment) {

    companion object {
        fun newInstance() = WorkspaceTaskFullDetailsFragment()
    }


    private lateinit var todoData: WorkspaceGroupTaskModel
    private lateinit var _binding: TodoFullDetailsFragmentBinding
    private val binding: TodoFullDetailsFragmentBinding get() = _binding

    private lateinit var viewModel: WorkspaceTaskFullDetailsViewModel

    // all adapter
    private lateinit var attachmentAudioAdapter: AttachmentAudioAdapter
    private lateinit var attachmentGalleryAdapter: AttachmentGalleryAdapter
    private lateinit var attachmentContactAdapter: AttachmentContactAdapter
    private lateinit var attachmentTodoTaskAdapter: AttachmentTodoTaskAdapter
    private lateinit var userProfileStackAdapter: UserProfileStackAdapter

    private lateinit var observerTodoTask: Observer<WorkspaceGroupTaskModel>
    private lateinit var observerAddParticipants: Observer<ArrayList<UserAccountModel>>

    private val taskFullDetailsCallBack: AppInterface.TaskFullDetailsCallBack =
        object : AppInterface.TaskFullDetailsCallBack {
            override fun onDelete() {
                lifecycleScope.launch {
                    viewModel.deleteTask(todoData)
                    findNavController().navigateUp()
                }
            }

            override fun onViewCalendar() {
            }

            override fun onShare() {
            }

            override fun onStageSelect(item: TaskStatusDataModel) {
                todoData.taskStatus = item.statusId
                viewModel.updateWorkspaceTaskData(todoData)
            }

            override fun onAddParticipants() {
                findNavController().navigate(R.id.action_workspace_privateTaskFullDetailsFragment_to_selectParticipantsFragment)
            }

        }

    private val attachmentTodoTaskAdapterCallback: NewTodo.TodoTask = object : NewTodo.TodoTask {
        override fun onTaskSelect(item: TodoTaskModel) {
            val todoTaskData = todoData.arrayListTodoTask
            val indexOf = todoTaskData.indexOf(item)
            todoData.arrayListTodoTask[indexOf] = item
            viewModel.mutableTodoDataSet.postValue(todoData)
        }

        override fun onCompleted(item: TodoTaskModel) {

        }
    }

    // all interfaces
    private val audioAttachmentInterface =
        object : AudioAttachmentInterface {
            override fun onAudioSelect(item: AudioModel) {

            }

            override fun onRemoveItem(position: Int) {
            }

        }

    private val galleryAttachmentInterface =
        object : GalleryAttachmentInterface {
            override fun onItemSelect(item: GalleryModel, indexOf: Int) {
                val bundle = Bundle()
                bundle.putSerializable("ImageKey", todoData.galleryAttachments)
                bundle.putInt("currentPosition", indexOf)
                findNavController().navigate(
                    R.id.action_todoFullDetailsFragment_to_imageSliderFullScreenFragment,
                    bundle
                )
            }

            override fun onItemRemoved(removedItem: GalleryModel, indexOf: Int) {
            }
        }

    private val contactAttachmentInterface = object : ContactAttachmentInterface {
        override fun onContactSelect(item: ContactModel) {

        }
    }

    private val editBasicTodoInterface = object : EditBasicWorkspaceTaskInterface {
        override fun onUpdateDetails(todoModel: WorkspaceGroupTaskModel) {
            viewModel.updateWorkspaceTaskData(todoModel)
        }

        override fun onAddMoreDetails(todoModel: WorkspaceGroupTaskModel) {
            // show edit fragment
            val bundle = Bundle()
            bundle.putSerializable(AppConstant.NAVIGATION_TASK_DATA_KEY, todoModel)
            findNavController().navigate(
                R.id.action_todoFullDetailsFragment_to_editFragment,
                bundle
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = TodoFullDetailsFragmentBinding.bind(view)

        setupBinding()
        loadObservers()

        loadTodoDetails()

        binding.buttonOptions.setOnClickListener {
            TaskFullDetailsBottomSheetDialog.newInstance(
                requireContext(),
                callback = taskFullDetailsCallBack
            ).show(
                requireActivity().supportFragmentManager,
                TaskFullDetailsBottomSheetDialog::class.java.canonicalName
            )
        }
        binding.buttonEdit.setOnClickListener {

            Log.e(
                com.example.simplydo.ui.fragments.todoListFullDetails.TAG,
                "taskType: ${todoData.taskType}"
            )
            if (todoData.taskType == AppConstant.Task.TASK_TYPE_BASIC) {
                // show basic edit
                EditWorkspaceTaskBasic.newInstance(
                    requireContext(),
                    editBasicTodoInterface,
                    todoData
                )
                    .show(requireActivity().supportFragmentManager, "dialog")
            }


            if (todoData.taskType == AppConstant.Task.TASK_TYPE_EVENT) {
                // show edit fragment
                val bundle = Bundle()
                bundle.putSerializable(AppConstant.NAVIGATION_TASK_DATA_KEY, todoData)
                findNavController().navigate(
                    R.id.action_workspace_privateTaskFullDetailsFragment2_to_editWorkspaceTaskDetails,
                    bundle
                )
            }
        }
        binding.imageButtonBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun loadTodoDetails() {
        arguments?.let {
            todoData = getTodoData(dtId = it.getLong(AppConstant.NAVIGATION_TASK_KEY))
        }
        todoData.let { data ->
            binding.tvTitle.text = data.title
            binding.tvTodo.text = data.todo
            binding.textViewPriority.text = data.getTaskPriorityText()
            binding.textViewPriority.background =
                getTaskPriorityBackground(data.taskPriority, requireContext())

            loadTodoTaskRecyclerView(data.arrayListTodoTask)


            binding.recyclerViewParticipants.apply {
                userProfileStackAdapter = UserProfileStackAdapter(dataset = data.taskParticipants)
                layoutManager = LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = userProfileStackAdapter
                addItemDecoration(GroupViewAdapter.OverlapRecyclerViewDecoration(4, -25))
            }

            binding.textViewEventDateAndTime.text = data.getEventDate()
            binding.textViewEventTime.visibility = data.isEventTimeVisible()
            binding.textViewEventTime.text = data.getEventTime()
            binding.imCompleted.visibility = data.isCompletedVisible()
//            binding.chipPriority.visibility = data.isCompletedVisible()
//            binding.chipDateExpired.visibility = data.isDateExpiredVisible()
            loadTaskTag(data.taskTags)
            checkAttachment(data)

            if (data.audioAttachments.isEmpty()) {
                binding.linearLayoutAudioAttachment.visibility = View.GONE
            } else {
                attachmentAudioAdapter.updateDataSet(data.audioAttachments)
                binding.linearLayoutAudioAttachment.visibility = View.VISIBLE
            }

            if (data.galleryAttachments.isEmpty()) {
                binding.linearLayoutGalleryAttachment.visibility = View.GONE
            } else {
                attachmentGalleryAdapter.updateDataset(data.galleryAttachments)
                binding.linearLayoutGalleryAttachment.visibility = View.VISIBLE
            }

            if (data.contactAttachments.isEmpty()) {
                binding.linearLayoutContactAttachment.visibility = View.GONE
            } else {
                attachmentContactAdapter.updateDataSet(data.contactAttachments)
                binding.linearLayoutContactAttachment.visibility = View.VISIBLE
            }

            if (data.fileAttachments.isEmpty()) {
                binding.linearFilesAttachment.visibility = View.GONE
            } else {
                binding.linearFilesAttachment.visibility = View.VISIBLE
            }

            if (data.locationData.lat == 0.toDouble() && data.locationData.lng == 0.toDouble()) {
                binding.linearLocationAttachment.visibility = View.GONE
            } else {
                binding.linearLocationAttachment.visibility = View.VISIBLE
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?


                mapFragment?.getMapAsync { googleMap ->
                    googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            requireContext(),
                            R.raw.map_styled_json
                        )
                    )
                    googleMap.moveCamera(
                        CameraUpdateFactory
                            .newLatLngZoom(
                                LatLng(data.locationData.lat, data.locationData.lng), 12f
                            )
                    )
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(data.locationData.lat, data.locationData.lng))
                            .icon(
                                AppFunctions.getDrawableToBitmap(
                                    R.drawable.ic_map_marker,
                                    requireActivity()
                                )
                            )
                    )
                }

                val mapUri =
                    Uri.parse("google.navigation:q=${data.locationData.lat},${data.locationData.lng}")
                binding.buttonViewInMap.setOnClickListener {
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            }

        }


    }

    override fun onResume() {
        super.onResume()
        viewModel.mutableTodoDataSet.postValue(null)
    }

    private fun loadTodoTaskRecyclerView(arrayListTodoTask: ArrayList<TodoTaskModel>) {
        attachmentTodoTaskAdapter =
            AttachmentTodoTaskAdapter(arrayListTodoTask, null, attachmentTodoTaskAdapterCallback)
        binding.recyclerViewTodoTaskList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = attachmentTodoTaskAdapter
        }
    }

    private fun getTaskPriorityBackground(taskPriority: Int, context: Context): Drawable? {
        Log.i(com.example.simplydo.adapters.TAG, "getTaskPriorityBackground: $taskPriority")
        return when (taskPriority) {
            AppConstant.TaskPriority.HIGH_PRIORITY -> {
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.card_high_priority,
                    context.theme
                )
            }
            AppConstant.TaskPriority.MEDIUM_PRIORITY -> {
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.card_medium_priority,
                    context.theme
                )
            }
            AppConstant.TaskPriority.LOW_PRIORITY -> {
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.card_low_priority,
                    context.theme
                )
            }
            else -> {
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.card_low_priority,
                    context.theme
                )
            }
        }
    }

    private fun loadTaskTag(taskTags: ArrayList<TagModel>) {
        binding.chipGroupTaskTags.removeAllViews()

        for (item in taskTags) {
            val chip = Chip(requireContext())
            chip.text = item.tagName
            chip.setChipBackgroundColorResource(R.color.colorPrimary)
            binding.chipGroupTaskTags.addView(chip)
        }
    }

    private fun checkAttachment(data: WorkspaceGroupTaskModel) {
        if (
            data.audioAttachments.isEmpty() &&
            data.galleryAttachments.isEmpty() &&
            data.contactAttachments.isEmpty() &&
            data.locationData.lat == 0.0 && data.locationData.lng == 0.0
        ) {
            binding.noAttachmentFound.root.visibility = View.VISIBLE
        } else {
            binding.noAttachmentFound.root.visibility = View.GONE
        }
    }

    private fun getTodoData(dtId: Long): WorkspaceGroupTaskModel {
        return viewModel.getTodoDataById(dtId = dtId)
    }

    private fun setupBinding() {
        val appDatabase = AppDatabase.getInstance(requireContext())
        val appRepository = AppRepository.getInstance(requireContext(), appDatabase)
        viewModel = ViewModelProvider(
            this, ViewModelFactory(
                requireContext(),
                appRepository,
            )
        )[WorkspaceTaskFullDetailsViewModel::class.java]

        attachmentAudioAdapter =
            AttachmentAudioAdapter(audioAttachmentInterface = audioAttachmentInterface)
        attachmentGalleryAdapter =
            AttachmentGalleryAdapter(requireContext(), galleryAttachmentInterface)
        attachmentContactAdapter =
            AttachmentContactAdapter(requireContext(), contactAttachmentInterface)


        binding.recyclerViewAudioAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = attachmentAudioAdapter
        }

        binding.recyclerViewGalleryAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = attachmentGalleryAdapter
        }

        binding.recyclerViewContactAttachments.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = attachmentContactAdapter
        }
    }

    private fun loadObservers() {
        observerTodoTask = Observer { result ->
            result?.let {
                viewModel.updateWorkspaceTaskData(it)
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<java.util.ArrayList<UserAccountModel>>(
            AppConstant.Key.NAVIGATION_PARTICIPANT_KEY
        )?.observe(viewLifecycleOwner) { result ->
            Log.i(TAG, "NAVIGATION_PARTICIPANT_KEY: $result")

            if (!result.isNullOrEmpty()) {
                todoData.taskParticipants.addAll(result)
                viewModel.updateWorkspaceTaskData(todoData)
                loadTodoDetails()
            }
        }

        viewModel.mutableTodoDataSet.observe(viewLifecycleOwner, observerTodoTask)
    }

}