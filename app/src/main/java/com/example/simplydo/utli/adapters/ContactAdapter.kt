package com.example.simplydo.utli.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerContactListItemBinding
import com.example.simplydo.model.attachmentModel.ContactModel
import com.example.simplydo.utli.ContactAdapterInterface


internal const val VIEW_TYPE_CONTACT: Int = 0
internal const val VIEW_TYPE_LOADING: Int = 1

class ContactAdapter(val contactAdapterInterface: ContactAdapterInterface) :
    PagingDataAdapter<ContactModel, ContactAdapter.ContactViewHolder>(DIFF_CALLBACK) {

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

    class ContactViewHolder(val binding: RecyclerContactListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(contactModel: ContactModel) {
            binding.apply {
                dataModel = contactModel
                executePendingBindings()
            }
        }


    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = getItem(position)

        item?.run {
            holder.bind(this@run)
            holder.itemView.tag = this@run

            holder.itemView.setOnClickListener {
                contactAdapterInterface.onContactSelect(item)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            RecyclerContactListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


}