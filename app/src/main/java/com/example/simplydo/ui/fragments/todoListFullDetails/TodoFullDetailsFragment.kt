package com.example.simplydo.ui.fragments.todoListFullDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val animation =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
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
            todoData = getTodoData(dtId = it.getLong(getString(R.string.TODO_ITEM_KEY)))
            Log.i(TAG, "onViewCreated: todoData --> $todoData")
        }
    }

    private fun getTodoData(dtId: Long): TodoModel {
        return viewModel.getTodoDataById(dtId = dtId)
    }

    override fun onStart() {
        super.onStart()
        todoData.let { data ->
            binding.tvTitle.text = data.title
            binding.tvTodo.text = data.todo
            binding.textViewDateExpired.visibility = data.dateExpiredVisibility()
            binding.textViewPriority.visibility = data.isVisible()

            if (data.eventTime.isEmpty()) {
                binding.textViewEventDateAndTime.text = data.getEventDateTextValue()
            } else {
                binding.textViewEventDateAndTime.text =
                    String.format(
                        "%s @ %s",
                        data.getEventDateTextValue(),
                        AppFunctions.convertTimeStringToDisplayFormat(data.eventTime)
                    )
            }

            if (
                data.audioAttachments.isEmpty() &&
                data.imageAttachments.isEmpty() &&
                data.contactAttachments.isEmpty() &&
                data.locationData.isEmpty()
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

            if (data.documentAttachments.isEmpty()) {
                binding.linearDocumentAttachment.visibility = View.GONE
            } else {
                binding.linearDocumentAttachment.visibility = View.VISIBLE
            }

            if (data.locationData.isEmpty()) {
                binding.linearLocationAttachment.visibility = View.GONE
            } else {
                binding.linearLocationAttachment.visibility = View.VISIBLE
                val mapFragment =
                    childFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?

                val latLng = data.locationData.split(",".toRegex()).toTypedArray()

                val latitude: Double = latLng[0].toDouble()
                val longitude: Double = latLng[1].toDouble()

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
                                LatLng(latitude, longitude), 12f
                            )
                    )
                    googleMap.addMarker(
                        MarkerOptions()
                            .position(LatLng(latitude, longitude))
                            .icon(
                                AppFunctions.getDrawableToBitmap(
                                    R.drawable.ic_map_marker,
                                    requireActivity()
                                )
                            )
                    )
                }

                val mapUri = Uri.parse("google.navigation:q=$latitude,$longitude")
                binding.buttonViewInMap.setOnClickListener {
                    val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                }
            }
        }
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