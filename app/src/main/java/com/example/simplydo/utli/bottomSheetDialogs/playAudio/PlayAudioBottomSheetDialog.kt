package com.example.simplydo.utli.bottomSheetDialogs.playAudio

import android.content.DialogInterface
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.example.simplydo.databinding.FragmentPlayAudioBottomSheetDialogBinding
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.utli.AppFunctions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*


internal val TAG = PlayAudioBottomSheetDialog::class.java.canonicalName

class PlayAudioBottomSheetDialog(val audioModel: AudioModel) :
    BottomSheetDialogFragment() {

    private lateinit var myUri: Uri
    private lateinit var binding: FragmentPlayAudioBottomSheetDialogBinding

    private lateinit var mediaPlayer: MediaPlayer
    private var timer = Timer()
    private lateinit var timerTask: TimerTask

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPlayAudioBottomSheetDialogBinding.inflate(inflater, container, false)
        setupBinding()
        return binding.root
    }

    private fun setupBinding() {
        binding.apply {
            lifecycleOwner = this@PlayAudioBottomSheetDialog
            executePendingBindings()
        }
    }

    companion object {
        fun newInstance(audioModel: AudioModel) =
            PlayAudioBottomSheetDialog(audioModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: $audioModel")

        myUri = Uri.parse(audioModel.uri)

        binding.textViewFileName.text = audioModel.name
        binding.textViewDuration.text = AppFunctions.millisecondsToMinutes(audioModel.duration)
        binding.seekBar.max = audioModel.duration / 1000

        binding.imageButtonPlay.setOnClickListener {
            it.visibility = View.GONE
            binding.imageButtonStop.visibility = View.VISIBLE

            playAudio()
        }

        binding.imageButtonStop.setOnClickListener {
            it.visibility = View.GONE
            binding.imageButtonPlay.visibility = View.VISIBLE

            stopAudio()
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) mediaPlayer.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.buttonCloseAudioPlayer.setOnClickListener {
            dismiss()
        }

        playAudio()
    }

    private fun initSeekBar() {

        timerTask = object : TimerTask() {
            override fun run() {
                var currentPosition = mediaPlayer.currentPosition
                val duration = mediaPlayer.duration

                while (mediaPlayer.isPlaying && currentPosition < duration) {
                    Log.d(TAG, "run: Current Position --> $currentPosition , duration $duration")

                    // get Time elapsed
                    requireActivity().runOnUiThread {
                        binding.textViewDuration.text =
                            AppFunctions.millisecondsToMinutes(duration - currentPosition)
                    }

                    currentPosition = try {
                        binding.seekBar.progress = currentPosition
                        Thread.sleep(1000)
                        mediaPlayer.currentPosition

                    } catch (e: InterruptedException) {
                        return
                    } catch (e: Exception) {
                        return
                    }


                    if (!mediaPlayer.isPlaying) {
                        stopAudio()
                    }
                }
            }
        }
        timer = Timer()
        timer.scheduleAtFixedRate(timerTask, 0, 1000)
    }

    private fun stopAudio() {
        Log.d(TAG, "stopAudio: ")
        mediaPlayer.isPlaying.let {
            mediaPlayer.stop()
            timer.cancel()
            timer.purge()
        }
        binding.seekBar.progress = 0
        requireActivity().runOnUiThread {
            binding.imageButtonPlay.visibility = View.VISIBLE
            binding.imageButtonStop.visibility = View.GONE
            binding.textViewDuration.text = AppFunctions.millisecondsToMinutes(audioModel.duration)
        }
    }

    private fun playAudio() {
        Log.d(TAG, "playAudio: ")
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(requireContext(), myUri)
            prepare()
        }
        mediaPlayer.start()

        binding.imageButtonPlay.visibility = View.GONE
        binding.imageButtonStop.visibility = View.VISIBLE

        binding.seekBar.max = mediaPlayer.duration

        initSeekBar()
    }


    override fun onPause() {
        super.onPause()
        mediaPlayer.isPlaying.let {
            mediaPlayer.stop()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        mediaPlayer.isPlaying.let {
            mediaPlayer.stop()
        }
    }
}