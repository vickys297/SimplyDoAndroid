package com.example.simplydo.utli.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerAudioListItemBinding
import com.example.simplydo.model.attachmentModel.AudioModel

class AudioAdapter(val context: Context, private var dataSet: ArrayList<AudioModel>) :
    RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    class AudioViewHolder(val binding: RecyclerAudioListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(audioModel: AudioModel) {
            binding.apply {
                audioDataModel = audioModel
                executePendingBindings()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        return AudioViewHolder(
            RecyclerAudioListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val item = dataSet[position]
        item.run {
            holder.bind(this@run)
        }
    }

    override fun getItemCount(): Int = dataSet.size

}