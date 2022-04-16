package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerStageListItemBinding
import com.example.simplydo.model.TaskStatusDataModel
import com.example.simplydo.utlis.AppInterface

class StageListAdapter(val callback: AppInterface.TaskFullDetailsCallBack.TaskFullDetailsStageCallback) :
    RecyclerView.Adapter<StageListAdapter.StageListViewHolder>() {

    var dataSet: ArrayList<TaskStatusDataModel> = ArrayList()

    class StageListViewHolder(val binding: RecyclerStageListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TaskStatusDataModel) {
            binding.apply {
                executePendingBindings()
            }
            binding.textViewState.text = item.statusName
            binding.indicatorBadge.setIndicatorColor(item.statusColor)

        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StageListViewHolder {
        return StageListViewHolder(
            RecyclerStageListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StageListViewHolder, position: Int) {
        val item = dataSet[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            callback.onStageSelected(item)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    fun updateDatSet(statusList: ArrayList<TaskStatusDataModel>) {
        val startPosition = dataSet.size
        val size = statusList.size
        this.dataSet = statusList
        notifyItemRangeInserted(startPosition, size)
    }
}