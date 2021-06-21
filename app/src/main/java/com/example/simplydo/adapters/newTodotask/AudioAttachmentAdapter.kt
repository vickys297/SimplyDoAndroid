package com.example.simplydo.adapters.newTodotask

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerAttachmentAudioListItemBinding
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.utli.AudioAttachmentInterface

class AudioAttachmentAdapter(private val audioAttachmentInterface: AudioAttachmentInterface) : RecyclerView.Adapter<AudioAttachmentAdapter.AudioViewHolder>() {

    class AudioViewHolder(val binding: RecyclerAttachmentAudioListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(audioModel: AudioModel): RecyclerAttachmentAudioListItemBinding {
             binding.apply {
                audioDataModel = audioModel
                executePendingBindings()
            }
            return binding
        }

    }

    private var dataSet = ArrayList<AudioModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        return AudioViewHolder(
            RecyclerAttachmentAudioListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val item = dataSet[position]
        item.run {
            val viewHolder = holder.bind(this@run)

            viewHolder.textView8.isSelected = true
            viewHolder.imageButtonPlay.setOnClickListener {
                audioAttachmentInterface.onAudioSelect(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun updateDataSet(contactArrayList: ArrayList<AudioModel>) {
        this.dataSet = contactArrayList
        notifyDataSetChanged()
    }

}