package com.example.simplydo.ui.fragments.attachmentsFragments.audio

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.R
import com.example.simplydo.adapters.AudioAdapter
import com.example.simplydo.dialog.bottomSheetDialogs.playAudio.PlayAudioBottomSheetDialog
import com.example.simplydo.databinding.AudioListFragmentBinding
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.AppFunctions
import com.example.simplydo.utlis.AudioInterface
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

internal val TAG = AudioListFragment::class.java.canonicalName

class AudioListFragment : Fragment(R.layout.audio_list_fragment) {


    private lateinit var viewModel: AudioListViewModel

    private lateinit var _binding: AudioListFragmentBinding
    val binding: AudioListFragmentBinding get() = _binding

    private lateinit var audioAdapter: AudioAdapter

    private val selectedAudioArrayList = ArrayList<AudioModel>()

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val audioInterface = object : AudioInterface {
        override fun onPlay(audioModel: AudioModel) {
            PlayAudioBottomSheetDialog.newInstance(audioModel)
                .show(parentFragmentManager, "dialog")
        }

        override fun onAudioSelect(audioModel: AudioModel) {
            if (isNotDuplicateAudio(audioModel)) {
                selectedAudioArrayList.add(audioModel)
            } else {
                selectedAudioArrayList.let {
                    it.removeAt(it.indexOf(audioModel))
                }
            }

            Log.d(TAG, "onAudioSelect: ${audioModel.name}/${selectedAudioArrayList.size}")

            if (selectedAudioArrayList.isEmpty()) {
                binding.buttonAddAudio.visibility = View.GONE
            } else {
                binding.buttonAddAudio.visibility = View.VISIBLE
            }
        }
    }

    private fun isNotDuplicateAudio(audioModel: AudioModel): Boolean {
        selectedAudioArrayList.forEach {
            if (it.uri == audioModel.uri)
                return false
        }
        return true
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = AudioListFragmentBinding.bind(view)
        setupViewModel()

        binding.buttonAddAudio.setOnClickListener {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                AppConstant.Key.NAVIGATION_AUDIO_DATA_KEY,
                selectedAudioArrayList
            )
            findNavController().popBackStack()
        }


        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                var flag = true
                for (permission in permissions) {
                    if (!permission.value) {
                        flag = false
                    }
                }

                if (flag) {
                    binding.permissionDenied.linearLayoutPermissionRequired.visibility = View.GONE
                    setupRecyclerAdapter()
                    getAudioList()
                } else {
                    showFilePermissionNotProvided()
                }
            }



        if (AppFunctions.Permission.checkPermission(requiredPermissions, requireContext())) {
            binding.permissionDenied.linearLayoutPermissionRequired.visibility = View.GONE
            setupRecyclerAdapter()
            getAudioList()
        } else {
            requestPermissionLauncher.launch(requiredPermissions)
        }

    }

    private fun showFilePermissionNotProvided() {
        binding.permissionDenied.linearLayoutPermissionRequired.visibility = View.VISIBLE
    }

    private fun setupRecyclerAdapter() {
        audioAdapter = AudioAdapter(requireContext(), audioInterface)
        binding.recyclerViewListAudio.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = audioAdapter
        }
    }


    private fun getAudioList() {
        lifecycleScope.launch {

            viewModel.getAudioList(requireContext()).collectLatest {
                audioAdapter.submitData(it)
            }
            viewModel.getAudioList(requireContext()).count {
                Log.i(TAG, "getAudioList: $it")
                return@count true
            }
//            if (viewModel.getAudioList(requireContext()).count() == 0) {
//                binding.linearLayoutNoAudioFileAvailable.visibility = View.VISIBLE
//            } else {
//                binding.linearLayoutNoAudioFileAvailable.visibility = View.GONE
//            }
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(AudioListViewModel::class.java)
        binding.apply {
            viewModel = this@AudioListFragment.viewModel
            lifecycleOwner = this@AudioListFragment
            executePendingBindings()
        }
    }
}