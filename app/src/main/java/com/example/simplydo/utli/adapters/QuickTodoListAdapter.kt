package com.example.simplydo.utli.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerTodoCompletedListItemBinding
import com.example.simplydo.databinding.RecyclerTodoListItemBinding
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.TodoItemInterface

class QuickTodoListAdapter(
    @Nullable
    private val todoItemInterface: TodoItemInterface,
    val requireContext: Context,
) :
    PagingDataAdapter<TodoModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {


    companion object {
        const val VIEW_TYPE_TASK = 0
        const val VIEW_TYPE_TASK_COMPLETED = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TodoModel>() {
            override fun areItemsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
                return oldItem.dtId == newItem.dtId
            }

            override fun areContentsTheSame(oldItem: TodoModel, newItem: TodoModel): Boolean {
                return oldItem == newItem
            }

        }
    }

    init {
        hasStableIds()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TASK -> {
                TaskViewHolder(
                    RecyclerTodoListItemBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ))
            }
            VIEW_TYPE_TASK_COMPLETED -> {
                CompletedTaskViewHolder(
                    RecyclerTodoCompletedListItemBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ))
            }
            else -> throw IllegalArgumentException("View Type is is not included")
        }


    }

    override fun onBindViewHolder(holderTask: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {

            0 -> {
                val item = getItem(position)
                item?.run {
                    (holderTask as TaskViewHolder).apply {
                        val holder = bind(this@run, todoItemInterface)
                        itemView.tag = this@run

                        itemView.setOnLongClickListener {
                            todoItemInterface.onLongClick(item)
                            return@setOnLongClickListener true
                        }


                        itemView.setOnClickListener {
                            todoItemInterface.onTaskClick(
                                this@run,
                                absoluteAdapterPosition
                            )
                        }
                    }
                }
            }

            1 -> {
                val item = getItem(position)
                item?.run {
                    (holderTask as CompletedTaskViewHolder).apply {
                        bind(this@run)
                        itemView.tag = this@run

                        itemView.setOnLongClickListener {
                            todoItemInterface.onLongClick(item)
                            return@setOnLongClickListener true
                        }
                    }
                }
            }

        }

    }


    fun getItemAtPosition(position: Int): TodoModel? {
        return getItem(position)
    }


    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.isCompleted == true) VIEW_TYPE_TASK_COMPLETED else VIEW_TYPE_TASK
    }

    fun removeItemAtPosition(position: Int) {
        notifyItemRemoved(position)
    }


    class TaskViewHolder(private val binding: RecyclerTodoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: TodoModel,
            todoItemInterface: TodoItemInterface
        ): RecyclerTodoListItemBinding {


            return binding.apply {
                todoModel = item
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

}