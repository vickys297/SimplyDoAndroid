package com.example.simplydo.adapters.taskAttachmentAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerAttachmentFilesListItemBinding
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.utils.FileAttachmentInterface

class AttachmentFileAdapter(private val fileAttachmentInterface: FileAttachmentInterface) : RecyclerView.Adapter<AttachmentFileAdapter.AudioViewHolder>() {

    class AudioViewHolder(val binding: RecyclerAttachmentFilesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fileModel: FileModel): RecyclerAttachmentFilesListItemBinding {
             binding.apply {
                dataModel = fileModel
                executePendingBindings()
            }
            return binding
        }

    }

    private var dataSet = ArrayList<FileModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        return AudioViewHolder(
            RecyclerAttachmentFilesListItemBinding.inflate(
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
                fileAttachmentInterface.onFileSelect(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun updateDataSet(fileModel: ArrayList<FileModel>) {
        this.dataSet = fileModel
        notifyDataSetChanged()
    }

}