package com.example.simplydo.utli.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplydo.databinding.RecyclerGalleryListItemBinding
import com.example.simplydo.model.attachmentModel.GalleryModel


class GalleryAdapter(val requireContext: Context) :
    PagingDataAdapter<GalleryModel, GalleryAdapter.GalleryViewHolder>(DIFF_CALLBACK) {


    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GalleryModel>() {
            override fun areItemsTheSame(oldItem: GalleryModel, newItem: GalleryModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: GalleryModel, newItem: GalleryModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    class GalleryViewHolder(val binding: RecyclerGalleryListItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NewApi")
        fun bind(galleryModel: GalleryModel) {
            binding.apply {
                data = galleryModel
                executePendingBindings()
            }

            Glide.with(context)
                .load(galleryModel.contentUri)
                .thumbnail(0.1f)
                .into(binding.imageViewPreview)
        }

    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = getItem(position)

        item?.run {
            holder.bind(this@run)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            RecyclerGalleryListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false),
            requireContext
        )
    }

}