package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplydo.databinding.RecyclerAccountListItemBinding
import com.example.simplydo.model.LinkedWorkspaceDataModel
import com.example.simplydo.utils.AppInterface

class WorkspaceAdapter(private val linkedAccounts: ArrayList<LinkedWorkspaceDataModel>,private  val callback : AppInterface.WorkspaceAdapter.Callback) :
    RecyclerView.Adapter<WorkspaceAdapter.AccountItemViewHolder>() {

    class AccountItemViewHolder(val binding: RecyclerAccountListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LinkedWorkspaceDataModel): RecyclerAccountListItemBinding {
            return binding.apply {
                executePendingBindings()
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccountItemViewHolder {
        return AccountItemViewHolder(
            RecyclerAccountListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AccountItemViewHolder, position: Int) {
        val item = linkedAccounts[position]
        holder.bind(item).let {
            Glide
                .with(it.root)
                .load(item.workspaceIcon)
                .centerCrop()
                .into(it.profileImageView)

            it.root.setOnClickListener {
                callback.onWorkSpaceSelected(item)
            }
        }
    }

    override fun getItemCount(): Int = linkedAccounts.size
}