package com.example.simplydo.utli.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerAttachmentDocumentListItemBinding
import com.example.simplydo.model.attachmentModel.DocumentModel

class DocumentAdapter :
    PagingDataAdapter<DocumentModel, DocumentAdapter.DocumentViewHolder>(DIFF_CALLBACK) {


    class DocumentViewHolder(val binding: RecyclerAttachmentDocumentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(documentModel: DocumentModel): RecyclerAttachmentDocumentListItemBinding {
            return binding.apply {
                dataModel = documentModel
                executePendingBindings()
            }
        }

    }


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DocumentModel>() {
            override fun areItemsTheSame(oldItem: DocumentModel, newItem: DocumentModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DocumentModel,
                newItem: DocumentModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocumentViewHolder {
        return DocumentViewHolder(
            RecyclerAttachmentDocumentListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DocumentViewHolder, position: Int) {
        val item = getItem(position)
        item?.run {
            holder.apply {
                bind(this@run)

            }
        }
    }
}