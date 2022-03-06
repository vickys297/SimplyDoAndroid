package com.example.simplydo.adapters.myAccounts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplydo.databinding.RecyclerAccountListItemBinding
import com.example.simplydo.model.AccountModel

class MyAccountAdapter(val dataset: ArrayList<AccountModel>) :
    RecyclerView.Adapter<MyAccountAdapter.MyAccountViewHolder>() {

    class MyAccountViewHolder(val binding: RecyclerAccountListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AccountModel): RecyclerAccountListItemBinding {
            return binding.apply {
                accountModel = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAccountViewHolder {
        return MyAccountViewHolder(
            RecyclerAccountListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyAccountViewHolder, position: Int) {
        val item = dataset[position]
        holder.bind(item).let {

            Glide
                .with(it.root)
                .load(item.profilePicture)
                .circleCrop()
                .into(it.profileImageView)

            holder.itemView.setOnClickListener {

            }
        }
    }

    override fun getItemCount(): Int = dataset.size
}