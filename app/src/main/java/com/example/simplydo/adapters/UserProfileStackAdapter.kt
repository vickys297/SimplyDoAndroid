package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplydo.databinding.RecyclerUserProfileSmallListItemBinding
import com.example.simplydo.model.UserAccountModel
import com.example.simplydo.model.AccountModel

class UserProfileStackAdapter(val dataset: ArrayList<UserAccountModel>) :
    RecyclerView.Adapter<UserProfileStackAdapter.ViewHolder>() {
    class ViewHolder(val binding: RecyclerUserProfileSmallListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AccountModel): RecyclerUserProfileSmallListItemBinding {
            return binding.apply {
                dataModel = item
                executePendingBindings()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerUserProfileSmallListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position].account

        holder.bind(item).let { bind ->
            Glide
                .with(bind.root)
                .load(item.profilePicture)
                .centerCrop()
                .into(bind.profileImageView)
        }
    }

    override fun getItemCount(): Int = dataset.size
}