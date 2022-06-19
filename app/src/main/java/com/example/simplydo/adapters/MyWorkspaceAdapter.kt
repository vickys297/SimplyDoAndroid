package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerWorkspaceListItemBinding
import com.example.simplydo.model.WorkspaceModel
import com.example.simplydo.utils.AppInterface

class MyWorkspaceAdapter(val callback: AppInterface.MyWorkspace.Callback?) :
    RecyclerView.Adapter<MyWorkspaceAdapter.MyWorkspaceViewHolder>() {
    var dataset = ArrayList<WorkspaceModel>()

    class MyWorkspaceViewHolder(val binding: RecyclerWorkspaceListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkspaceModel): RecyclerWorkspaceListItemBinding {
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
        holder.itemView.setOnClickListener { callback!!.onSelect(item) }
        holder.bind(item)
    }

    override fun getItemCount(): Int = dataset.size

    fun loadData(newDataset: ArrayList<WorkspaceModel>) {
        val startPosition = this.dataset.size
        val range = newDataset.size
        this.dataset = newDataset
        notifyItemRangeChanged(startPosition, range)
    }
}