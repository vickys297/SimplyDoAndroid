package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerWorkspaceListItemBinding
import com.example.simplydo.model.WorkspaceAccountModel

class MyWorkspaceAdapter() : RecyclerView.Adapter<MyWorkspaceAdapter.MyWorkspaceViewHolder>() {
    var dataset = ArrayList<WorkspaceAccountModel>()

    class MyWorkspaceViewHolder(val binding: RecyclerWorkspaceListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkspaceAccountModel): RecyclerWorkspaceListItemBinding {
            return binding.apply {
                dataModel = item
                executePendingBindings()
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyWorkspaceViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyWorkspaceViewHolder(
            RecyclerWorkspaceListItemBinding.inflate(layoutInflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyWorkspaceViewHolder, position: Int) {
        val item = dataset[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = dataset.size

    fun loadData(newDataset: ArrayList<WorkspaceAccountModel>) {
        val startPosition = this.dataset.size
        val range = newDataset.size
        this.dataset = newDataset
        notifyItemRangeChanged(startPosition, range)
    }
}