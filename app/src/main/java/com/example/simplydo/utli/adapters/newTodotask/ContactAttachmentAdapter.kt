package com.example.simplydo.utli.adapters.newTodotask

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplydo.databinding.RecyclerAttachmentContactListItemBinding
import com.example.simplydo.model.ContactModel
import com.example.simplydo.utli.ContactAttachmentInterface

class ContactAttachmentAdapter(
    val context: Context,
    private val contactAttachmentInterface: ContactAttachmentInterface
) : RecyclerView.Adapter<ContactAttachmentAdapter.ContactViewHolder>() {

    class ContactViewHolder(
        val binding: RecyclerAttachmentContactListItemBinding,
        val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contactModel: ContactModel): RecyclerAttachmentContactListItemBinding {
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


            return binding
        }

    }

    private var dataSet = ArrayList<ContactModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(
            RecyclerAttachmentContactListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            context
        )
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val item = dataSet[position]
        item.run {
            val viewHolder = holder.bind(this@run)

            viewHolder.tvContactName.isSelected = true
            viewHolder.root.setOnClickListener {
                contactAttachmentInterface.onContactSelect(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun updateDataSet(contactArrayList: ArrayList<ContactModel>) {
        this.dataSet = contactArrayList
        notifyDataSetChanged()
    }

}