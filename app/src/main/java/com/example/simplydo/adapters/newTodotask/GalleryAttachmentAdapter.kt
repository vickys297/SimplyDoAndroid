package com.example.simplydo.adapters.newTodotask

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplydo.databinding.RecyclerAttachmentGalleryListItemBinding
import com.example.simplydo.model.attachmentModel.GalleryModel
import com.example.simplydo.utli.GalleryAttachmentInterface

class GalleryAttachmentAdapter(
    val context: Context,
    val galleryAttachmentInterface: GalleryAttachmentInterface
) :
    RecyclerView.Adapter<GalleryAttachmentAdapter.GalleryViewHolder>() {

    private var dataSet: ArrayList<GalleryModel> = ArrayList()

    class GalleryViewHolder(val binding: RecyclerAttachmentGalleryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(galleryModel: GalleryModel): RecyclerAttachmentGalleryListItemBinding {
            binding.apply {
                data = galleryModel
                executePendingBindings()
            }
            return binding
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            RecyclerAttachmentGalleryListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = dataSet[position]
        item.run {
            val viewHolder = holder.bind(this@run)

            holder.binding.textViewFileName.isSelected = true

            Glide.with(context)
                .load(item.contentUri)
                .thumbnail(0.1f)
                .into(viewHolder.imageViewPreview)

            viewHolder.root.setOnClickListener {
                galleryAttachmentInterface.onItemSelect(this@run)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun updateDataset(galleryArrayList: ArrayList<GalleryModel>) {
        this.dataSet = galleryArrayList
        notifyDataSetChanged()
    }
}
