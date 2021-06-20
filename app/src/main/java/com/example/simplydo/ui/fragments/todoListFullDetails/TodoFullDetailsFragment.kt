package com.example.simplydo.ui.fragments.todoListFullDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.databinding.TodoFullDetailsFragmentBinding
import com.example.simplydo.localDatabase.AppDatabase
import com.example.simplydo.model.ContactModel
import com.example.simplydo.model.TodoModel
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.*
import com.example.simplydo.utli.adapters.newTodotask.AudioAttachmentAdapter
import com.example.simplydo.utli.adapters.newTodotask.ContactAttachmentAdapter
import com.example.simplydo.utli.adapters.newTodotask.GalleryAttachmentAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

internal val TAG = TodoFullDetailsFragment::class.java.canonicalName

class TodoFullDetailsFragment : Fragment(R.layout.todo_full_details_fragment) {

    companion object {
        fun newInstance() = TodoFullDetailsFragment()
    }


    private lateinit var todoData: TodoModel
    private lateinit var _binding: TodoFullDetailsFragmentBinding
    private val binding: TodoFullDetailsFragmentBinding get() = _binding

    private lateinit var viewModel: TodoFullDetailsViewModel

    // all adapter
    private lateinit var audioAttachmentAdapter: AudioAttachmentAdapter
    private lateinit var galleryAttachmentAdapter: GalleryAttachmentAdapter
    private lateinit var contactAttachmentAdapter: ContactAttachmentAdapter


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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.i(TAG, "onCreateView: ")
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = TodoFullDetailsFragmentBinding.bind(view)

        setupBinding()

        val appDatabase = AppDatabase.getInstance(requireContext())
        val appRepository = AppRepository.getInstance(requireContext(), appDatabase)
        viewModel = ViewModelProvider(
            this, ViewModelFactory(
                requireContext(),
                appRepository
            )
        ).get(TodoFullDetailsViewModel::class.java)

        arguments?.let {
            todoData = getTodoData(dtId = it.getLong(AppConstant.NAVIGATION_TASK_KEY))
        }

        todoData.let { data ->
            binding.tvTitle.text = data.title
            binding.tvTodo.text = data.todo

            if (AppFunctions.checkForDateTimeExpire(data)) {
                binding.chipDateExpired.visibility = View.VISIBLE
            }

            binding.chipPriority.visibility = data.isPriorityVisible()

            if (data.isCompleted) {
                binding.imCompleted.visibility = View.VISIBLE
            }

            binding.buttonEdit.setOnClickListener {
                val bundle = Bundle()
                bundle.putSerializable(AppConstant.NAVIGATION_TASK_DATA_KEY, data)
                findNavController().navigate(R.id.action_todoFullDetailsFragment_to_editFragment, bundle)
            }

            when (AppFunctions.getEventDateText(data.eventDate)) {
                AppConstant.EVENT_TODAY -> {
                    binding.textViewEventDateAndTime.text = AppConstant.EVENT_TODAY
                }
                AppConstant.EVENT_TOMORROW -> {
                    binding.textViewEventDateAndTime.text = AppConstant.EVENT_TOMORROW
                }
                AppConstant.EVENT_YESTERDAY -> {
                    binding.textViewEventDateAndTime.text = AppConstant.EVENT_YESTERDAY
                }
                else -> {
                    binding.textViewEventDateAndTime.text =
                        AppFunctions.getDateStringFromMilliseconds(
                            data.eventDate,
                            AppConstant.DATE_PATTERN_EVENT_DATE
                        )
                }
            }

            if (data.eventTime.isNotEmpty()) {
                binding.textViewEventTime.text = String.format(
                    "@ %s",
                    AppFunctions.convertTimeStringToDisplayFormat(data.eventDate,data.eventTime)
                )
            }

            if (
                data.audioAttachments.isEmpty() &&
                data.imageAttachments.isEmpty() &&
                data.contactAttachments.isEmpty() &&
                data.locationData.lat == 0.0 && data.locationData.lng == 0.0
            ) {
                binding.noAttachmentFound.root.visibility = View.VISIBLE
            } else {
                binding.noAttachmentFound.root.visibility = View.GONE
            }


            if (data.audioAttachments.isEmpty()) {
                binding.linearLayoutAudioAttachment.visibility = View.GONE
            } else {
                audioAttachmentAdapter.updateDataSet(data.audioAttachments)
                binding.linearLayoutAudioAttachment.visibility = View.VISIBLE
            }

            if (data.imageAttachments.isEmpty()) {
                binding.linearLayoutGalleryAttachment.visibility = View.GONE
            } else {
                galleryAttachmentAdapter.updateDataset(data.imageAttachments)
                binding.linearLayoutGalleryAttachment.visibility = View.VISIBLE
            }

            if (data.contactAttachments.isEmpty()) {
                binding.linearLayoutContactAttachment.visibility = View.GONE
            } else {
                contactAttachmentAdapter.updateDataSet(data.contactAttachments)
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

    private fun getTodoData(dtId: Long): TodoModel {
        return viewModel.getTodoDataById(dtId = dtId)
    }


    private fun setupBinding() {
        audioAttachmentAdapter =
            AudioAttachmentAdapter(audioAttachmentInterface = audioAttachmentInterface)
        galleryAttachmentAdapter =
            GalleryAttachmentAdapter(requireContext(), galleryAttachmentInterface)
        contactAttachmentAdapter =
            ContactAttachmentAdapter(requireContext(), contactAttachmentInterface)

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
    }

}