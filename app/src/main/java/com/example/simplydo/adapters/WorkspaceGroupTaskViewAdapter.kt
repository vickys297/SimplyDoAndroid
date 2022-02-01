package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerTaskStatusHeaderBinding
import com.example.simplydo.databinding.RecyclerTodoWorkspaceListItemBinding
import com.example.simplydo.model.GroupTaskByProgressModel
import com.example.simplydo.model.WorkspaceGroupTaskModel

internal const val ITEM_VIEW_HEADER = 0
internal const val ITEM_VIEW_CONTENT = 1

class WorkspaceGroupTaskViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSet: ArrayList<GroupTaskByProgressModel> = arrayListOf()

    override fun getItemViewType(position: Int): Int {
        val item = dataSet[position]
        return if (item.taskHeader != null) {
            ITEM_VIEW_HEADER
        } else {
            ITEM_VIEW_CONTENT
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataSet[position]
        when (getItemViewType(position)) {
            ITEM_VIEW_HEADER -> {
                item.taskHeader?.run {
                    (holder as TodoViewHeaderHolder).apply {
                        bind(this@run)
                    }
                }
            }
            ITEM_VIEW_CONTENT -> {
                item.content?.run {
                    (holder as TodoViewHolder).apply {
                        bind(this@run)
                    }
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_CONTENT -> {
                TodoViewHolder(
                    RecyclerTodoWorkspaceListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ITEM_VIEW_HEADER -> {
                TodoViewHeaderHolder(
                    RecyclerTaskStatusHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw Exception("No View Attached")
            }
        }
    }

    class TodoViewHeaderHolder(val binding: RecyclerTaskStatusHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(groupTaskByProgressModel: GroupTaskByProgressModel.TaskHeaderContent) {
            binding.apply {
                dataMode = groupTaskByProgressModel
                executePendingBindings()
            }
        }
    }

    class TodoViewHolder(val binding: RecyclerTodoWorkspaceListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkspaceGroupTaskModel) {
            binding.apply {
                dataModel = item
                executePendingBindings()
            }
        }
    }

    fun updateDataset(groupTaskByProgressModel: ArrayList<GroupTaskByProgressModel>) {
        val lastPosition = dataSet.size
        this.dataSet = groupTaskByProgressModel
        notifyItemChanged(lastPosition, dataSet.size)
    }
}