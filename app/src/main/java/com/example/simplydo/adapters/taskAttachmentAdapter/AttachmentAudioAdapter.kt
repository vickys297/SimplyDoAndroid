package com.example.simplydo.adapters.taskAttachmentAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerAttachmentAudioListItemBinding
import com.example.simplydo.model.attachmentModel.AudioModel
import com.example.simplydo.utils.AudioAttachmentInterface

class AttachmentAudioAdapter(private val audioAttachmentInterface: AudioAttachmentInterface) : RecyclerView.Adapter<AttachmentAudioAdapter.AudioViewHolder>() {

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
          holder.bind(this@run).let {
                it.textView8.isSelected = true
                it.imageButtonPlay.setOnClickListener {
                    audioAttachmentInterface.onAudioSelect(item)
                }
                it.imageViewRemove.setOnClickListener {
                    notifyItemRemoved(position)
                    audioAttachmentInterface.onRemoveItem(position)
                }
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    fun updateDataSet(contactArrayList: ArrayList<AudioModel>) {
        this.dataSet = contactArrayList
        notifyDataSetChanged()
    }

}