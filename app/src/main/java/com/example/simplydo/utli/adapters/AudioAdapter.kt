package com.example.simplydo.utli.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerAudioListItemBinding
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.utli.AudioInterface

class AudioAdapter(val context: Context, private var dataSet: ArrayList<AudioModel>, val audioInterface: AudioInterface) :
    RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    class AudioViewHolder(val binding: RecyclerAudioListItemBinding,val  audioInterface: AudioInterface) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(audioModel: AudioModel) {
            binding.apply {
                audioDataModel = audioModel
                executePendingBindings()
            }

            binding.imageButtonPlay.setOnClickListener {
                audioInterface.onPlay(audioModel)
            }

            if (audioModel.isSelected) {
                binding.imCompleted.visibility = View.VISIBLE
            } else {
                binding.imCompleted.visibility = View.GONE
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        return AudioViewHolder(
            RecyclerAudioListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            audioInterface
        )
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val item = dataSet[position]
        item.run {
            holder.bind(this@run)

            holder.itemView.setOnClickListener {

                this.isSelected = !this.isSelected
                notifyItemChanged(position)

                audioInterface.onAudioSelect(this@run)
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

}