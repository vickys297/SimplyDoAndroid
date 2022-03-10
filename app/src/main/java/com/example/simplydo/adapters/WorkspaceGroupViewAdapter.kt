package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerGroupListItemBinding
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utlis.AppInterface

class WorkspaceGroupViewAdapter(
    val dataset: ArrayList<WorkspaceGroupModel>,
    val callback: AppInterface.GroupViewCallback,
) : RecyclerView.Adapter<WorkspaceGroupViewAdapter.ViewHolder>() {

    private lateinit var userProfileStackAdapter: UserProfileStackAdapter

    class ViewHolder(val binding: RecyclerGroupListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: WorkspaceGroupModel,
            callback: AppInterface.GroupViewCallback
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
        val item = dataset[position]

        holder.bind(item, callback).let { bind ->

            bind.cardViewContainer.setOnClickListener {
                callback.onSelect(item)
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

    override fun getItemCount(): Int = dataset.size
}