package com.example.simplydo.utli.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerTodoCompletedListItemBinding
import com.example.simplydo.databinding.RecyclerTodoListItemBinding
import com.example.simplydo.model.TodoModel

class TodoListPagingAdapter internal constructor(val context: Context) :
    PagingDataAdapter<TodoModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TodoModel>() {
            override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
                return oldItem.dtId == newItem.dtId
            }

            override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
                return oldItem == newItem
            }

        }

        const val VIEW_TYPE_TASK = 0
        const val VIEW_TYPE_TASK_COMPLETED = 1
    }


    class TodoViewHolder(val binding: RecyclerTodoListItemBinding, requireContext: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todoModelData: TodoModel) {
            binding.apply {
                todoModel = todoModelData
                executePendingBindings()
            }
        }

    }

    class CompletedTaskViewHolder(private val binding: RecyclerTodoCompletedListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoModel) {
            binding.apply {
                todoModel = item
                executePendingBindings()
            }
            binding.tvTitle.paintFlags = binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvTodo.paintFlags = binding.tvTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when (getItemViewType(position)) {
            VIEW_TYPE_TASK -> {
                val item = getItem(position)
                item?.run {
                    (holder as TodoViewHolder).apply {
                        bind(this@run)
                    }
                }
            }
            VIEW_TYPE_TASK_COMPLETED -> {
                val item = getItem(position)
                item?.run {
                    (holder as CompletedTaskViewHolder).apply {
                        bind(this@run)
                    }
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return when (viewType) {
            VIEW_TYPE_TASK -> {
                TodoViewHolder(
                    RecyclerTodoListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    context
                )
            }
            VIEW_TYPE_TASK_COMPLETED -> {
                CompletedTaskViewHolder(
                    RecyclerTodoCompletedListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false
                    )
                )
            }
            else -> throw IllegalArgumentException("View Type is is not included")
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)!!.isCompleted) {
            TodoAdapter.VIEW_TYPE_TASK_COMPLETED
        } else {
            TodoAdapter.VIEW_TYPE_TASK
        }
    }

}