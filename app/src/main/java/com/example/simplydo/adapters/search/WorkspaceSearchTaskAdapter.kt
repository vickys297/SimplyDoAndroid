package com.example.simplydo.adapters.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.adapters.GroupViewAdapter
import com.example.simplydo.adapters.UserProfileStackAdapter
import com.example.simplydo.databinding.RecyclerTodoWorkspaceListItemBinding
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel
import com.example.simplydo.utlis.AppInterface

class WorkspaceSearchTaskAdapter(val taskCallback: AppInterface.WorkspaceGroupTask.Task) :
    PagingDataAdapter<WorkspaceGroupTaskModel, WorkspaceSearchTaskAdapter.TodoViewHolder>(DIFF_UTILS) {

    private lateinit var userProfileStackAdapter: UserProfileStackAdapter

    companion object {
        val DIFF_UTILS = object : DiffUtil.ItemCallback<WorkspaceGroupTaskModel>() {
            override fun areItemsTheSame(
                oldItem: WorkspaceGroupTaskModel,
                newItem: WorkspaceGroupTaskModel
            ): Boolean {
                return oldItem.dtId == newItem.dtId
            }

            override fun areContentsTheSame(
                oldItem: WorkspaceGroupTaskModel,
                newItem: WorkspaceGroupTaskModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }


    class TodoViewHolder(val binding: RecyclerTodoWorkspaceListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkspaceGroupTaskModel): RecyclerTodoWorkspaceListItemBinding {
            return binding.apply {
                dataModel = item
                executePendingBindings()
            }
        }
    }


    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        getItem(position)?.run {
            holder.bind(this@run).let { binding ->

                holder.itemView.setOnClickListener {
                    taskCallback.onTaskSelected(this@run)
                }

                holder.itemView.setOnLongClickListener {
                    taskCallback.onTaskDeleted(this@run)
                    return@setOnLongClickListener false
                }

                binding.recyclerViewStackedProfileView.apply {
                    userProfileStackAdapter =
                        UserProfileStackAdapter(dataset = this@run.taskParticipants)
                    layoutManager = LinearLayoutManager(
                        binding.root.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                    adapter = userProfileStackAdapter
                    addItemDecoration(
                        GroupViewAdapter.OverlapRecyclerViewDecoration(
                            4,
                            -25
                        )
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            RecyclerTodoWorkspaceListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


}