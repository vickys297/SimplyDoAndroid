package com.example.simplydo.adapters.attachment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerFilesListItemBinding
import com.example.simplydo.model.attachmentModel.FileModel
import com.example.simplydo.utils.AppFunctions
import com.example.simplydo.utils.MimeTypes

class SelectionListFilesAdapter : RecyclerView.Adapter<SelectionListFilesAdapter.DocumentViewHolder>() {

    private var dataSet: ArrayList<FileModel> = ArrayList()

    companion object {
        const val VIEW_TYPE_PDF = 1
        val VIEW_TYPE_WORD = 2
        val VIEW_TYPE_EXCEL = 3
        val VIEW_TYPE_CSV = 4
        const val VIEW_TYPE_IMAGE = 5
        val VIEW_TYPE_VIDEO = 6
        const val VIEW_TYPE_AUDIO = 7
    }

    class DocumentViewHolder(val binding: RecyclerFilesListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(fileModel: FileModel): RecyclerFilesListItemBinding {
            return binding.apply {
                dataModel = fileModel
                executePendingBindings()
            }
        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        return DocumentViewHolder(
            RecyclerFilesListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val item = dataSet[position]

        item.run {
            holder.apply {
                bind(this@run).apply {
                    binding.textViewFileSize.text = AppFunctions.formatSize(item.size.toLong())
                    binding.textViewDocumentFileName.isSelected = true
                }
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        return when (dataSet[position].mimeType) {
            MimeTypes.Application.PDF -> {
                VIEW_TYPE_PDF
            }
            MimeTypes.Image.JPEG -> {
                VIEW_TYPE_IMAGE
            }
            else -> {
                VIEW_TYPE_AUDIO
            }
        }
    }

    fun updateDataSet(arrayListFile: ArrayList<FileModel>) {
        this.dataSet = arrayListFile
        notifyDataSetChanged()
    }
}