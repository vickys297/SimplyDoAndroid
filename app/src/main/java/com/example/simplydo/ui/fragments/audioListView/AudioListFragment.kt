package com.example.simplydo.ui.fragments.audioListView

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplydo.databinding.AudioListFragmentBinding
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.utli.adapters.AudioAdapter

internal val TAG = AudioListFragment::class.java.canonicalName

class AudioListFragment : Fragment() {

    companion object {
        fun newInstance() = AudioListFragment()
    }

    private lateinit var audioListObserver: Observer<ArrayList<AudioModel>>
    private lateinit var viewModel: AudioListViewModel

    lateinit var binding: AudioListFragmentBinding

    lateinit var audioAdapter: AudioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = AudioListFragmentBinding.inflate(inflater, container, false)
        setupObserver()
        setupViewModel()
        return binding.root
    }

    private fun setupObserver() {

        audioListObserver = Observer {
            Log.i(TAG, "setupObserver: ${it.size}")

            binding.recyclerViewListAudio.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = AudioAdapter(requireContext(), it)
            }
        }


    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(AudioListViewModel::class.java)
        binding.apply {
            viewModel = this@AudioListFragment.viewModel
            lifecycleOwner = this@AudioListFragment
            executePendingBindings()
        }

        viewModel.audioList.observe(viewLifecycleOwner, audioListObserver)
    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
        } else {
            getAudioList()
            Log.i(TAG, "checkPermission: Permission Granted")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 && grantResults.isNotEmpty()) {

            var flag = true
            for (i in grantResults) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    flag = false
                }
            }

            if (!flag) {
                Toast.makeText(requireContext(), "Al permissions required", Toast.LENGTH_LONG)
                    .show()
                checkPermission()
            } else
                getAudioList()

        } else {
            Toast.makeText(requireContext(),
                "Permission required to view contact",
                Toast.LENGTH_LONG).show()
        }

    }


    private fun getAudioList() {
        viewModel.getAudioList(requireActivity())
    }
}