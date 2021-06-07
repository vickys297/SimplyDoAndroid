package com.example.simplydo.utli.bottomSheetDialogs.playAudio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.simplydo.R
import com.example.simplydo.model.attachmentModel.AudioModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class PlayAudioBottomSheetDialog(val audioModel: AudioModel) :
    BottomSheetDialogFragment() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_play_audio_bottom_sheet_dialog, container, false)
    }

    companion object {
        fun newInstance(audioModel: AudioModel) =
            PlayAudioBottomSheetDialog(audioModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val myUri: Uri = audioModel.uri
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
            setDataSource(requireContext(), myUri)
            prepare()
            isLooping = false
        }

        mediaPlayer.start()
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.isPlaying.let {
            mediaPlayer.stop()
            mediaPlayer.isLooping = false
        }
    }
}