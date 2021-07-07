package com.example.simplydo.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplydo.databinding.RecyclerContactListItemBinding
import com.example.simplydo.model.ContactModel
import com.example.simplydo.utli.ContactAdapterInterface


class ContactListAdapter(
    private val contactAdapterInterface: ContactAdapterInterface,
    private val context: Context,
) :
    PagingDataAdapter<ContactModel, ContactListAdapter.ContactViewHolder>(DIFF_CALLBACK) {

    companion object {
        var DIFF_CALLBACK = object : DiffUtil.ItemCallback<ContactModel>() {
            override fun areItemsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
                return oldItem.mobile == newItem.mobile
            }

            override fun areContentsTheSame(oldItem: ContactModel, newItem: ContactModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    class ContactViewHolder(
        val binding: RecyclerContactListItemBinding,
        val context: Context,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contactModel: ContactModel) {
            binding.apply {
                dataModel = contactModel
                executePendingBindings()
            }

            if (!contactModel.photoThumbnailUri.isNullOrEmpty()) {
                binding.imageViewContactAvatar.visibility = View.VISIBLE
                binding.textViewAvatarName.visibility = View.GONE
                Glide.with(context)
                    .load(contactModel.photoThumbnailUri)
                    .circleCrop()
                    .into(binding.imageViewContactAvatar)
            } else {
                binding.imageViewContactAvatar.visibility = View.GONE
                binding.textViewAvatarName.visibility = View.VISIBLE
                binding.textViewAvatarName.text = contactModel.name[0].toString()
            }

            if (contactModel.isSelected) {
                binding.imageViewSelected.visibility = View.VISIBLE
            } else {
                binding.imageViewSelected.visibility = View.GONE
            }

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            RecyclerContactListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            context
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = getItem(position)

        item?.run {
            holder.bind(this@run)
            holder.itemView.tag = this@run

            holder.itemView.setOnClickListener {
                contactAdapterInterface.onContactSelect(item)

                item.isSelected = !item.isSelected
                notifyItemChanged(position)
            }

        }

    }


}