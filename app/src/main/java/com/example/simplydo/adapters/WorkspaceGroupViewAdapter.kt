package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerGroupListItemBinding
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utils.AppInterface

class WorkspaceGroupViewAdapter(
    val callback: AppInterface.GroupViewCallback,
) : PagingDataAdapter<WorkspaceGroupModel, WorkspaceGroupViewAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WorkspaceGroupModel>() {
            override fun areItemsTheSame(
                oldItem: WorkspaceGroupModel,
                newItem: WorkspaceGroupModel
            ): Boolean {
                return oldItem.gId == newItem.gId
            }

            override fun areContentsTheSame(
                oldItem: WorkspaceGroupModel,
                newItem: WorkspaceGroupModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    private lateinit var userProfileStackAdapter: UserProfileStackAdapter

    class ViewHolder(val binding: RecyclerGroupListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: WorkspaceGroupModel
        ): RecyclerGroupListItemBinding {
            return binding.apply {
                dataModel = item
                executePendingBindings()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerGroupListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(item).let { bind ->

                bind.cardViewContainer.setOnClickListener {
                    callback.onSelect(item)
                }

                bind.imageViewOptions.setOnClickListener {
                    callback.onOption(item)
                }

                bind.recyclerView.apply {
                    userProfileStackAdapter = UserProfileStackAdapter(dataset = item.people)
                    layoutManager = LinearLayoutManager(
                        holder.binding.root.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = userProfileStackAdapter
                    addItemDecoration(GroupViewAdapter.OverlapRecyclerViewDecoration(4, -25))
                }
            }
        }
    }
}