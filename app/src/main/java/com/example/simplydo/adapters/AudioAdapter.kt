package com.example.simplydo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerAudioListItemBinding
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.utli.AudioInterface

class AudioAdapter(val context: Context,val audioInterface: AudioInterface) :
    PagingDataAdapter<AudioModel, AudioAdapter.AudioViewHolder>(DIFF_CALLBACK) {

    companion object{
        val DIFF_CALLBACK = object :DiffUtil.ItemCallback<AudioModel>() {
            override fun areItemsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: AudioModel, newItem: AudioModel): Boolean {
                return oldItem == newItem
            }

        }
    }
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
        val item = getItem(position)
        item?.run {
            holder.bind(this@run)

            holder.itemView.setOnClickListener {

                this.isSelected = !this.isSelected
                notifyItemChanged(position)

                audioInterface.onAudioSelect(this@run)
            }
        }
    }


}