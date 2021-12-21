package com.example.simplydo.adapters

import android.content.Context
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.databinding.RecyclerTodoCompletedListItemBinding
import com.example.simplydo.databinding.RecyclerTodoListItemBinding
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.TodoItemInterface
import java.util.*

class QuickTodoListAdapter(
    private val todoItemInterface: TodoItemInterface,
    val requireContext: Context,
) : PagingDataAdapter<TodoModel, RecyclerView.ViewHolder>(DIFF_CALLBACK) {


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
                    )
                )
            }
            else -> {
                CompletedTaskViewHolder(
                    RecyclerTodoCompletedListItemBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    )
                )
            }

        }


    }

    override fun onBindViewHolder(holderTask: RecyclerView.ViewHolder, position: Int) {

        when (getItemViewType(position)) {
            0 -> {
                val item = getItem(position)
                item?.run {
                    (holderTask as TaskViewHolder).apply {
                        bind(this@run)
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

                        itemView.setOnClickListener {
                            todoItemInterface.onTaskClick(
                                this@run,
                                absoluteAdapterPosition
                            )
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


    class TaskViewHolder(private val binding: RecyclerTodoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            todoModelData: TodoModel
        ): RecyclerTodoListItemBinding {

            binding.apply {
                todoModel = todoModelData
                executePendingBindings()
            }
            binding.textViewPriority.background =
                getTaskPriorityBackground(todoModelData.taskPriority, binding.root.context)
            return binding
        }

        private fun getTaskPriorityBackground(taskPriority: Int, context: Context): Drawable? {
            Log.i(TAG, "getTaskPriorityBackground: $taskPriority")
            return when (taskPriority) {
                AppConstant.TaskPriority.HIGH_PRIORITY -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.card_high_priority,
                        context.theme
                    )
                }
                AppConstant.TaskPriority.MEDIUM_PRIORITY -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.card_medium_priority,
                        context.theme
                    )
                }
                AppConstant.TaskPriority.LOW_PRIORITY -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.card_low_priority,
                        context.theme
                    )
                }
                else -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.card_low_priority,
                        context.theme
                    )
                }
            }
        }
    }

    class CompletedTaskViewHolder(private val binding: RecyclerTodoCompletedListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todoModelData: TodoModel) {
            binding.apply {
                todoModel = todoModelData
                executePendingBindings()
            }
            binding.textViewPriority.background =
                getTaskPriorityBackground(todoModelData.taskPriority, binding.root.context)
            binding.tvTitle.paintFlags = binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvTodo.paintFlags = binding.tvTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

        }

        private fun getTaskPriorityBackground(taskPriority: Int, context: Context): Drawable? {
            Log.i(TAG, "getTaskPriorityBackground: $taskPriority")
            return when (taskPriority) {
                AppConstant.TaskPriority.HIGH_PRIORITY -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.card_high_priority,
                        context.theme
                    )
                }
                AppConstant.TaskPriority.MEDIUM_PRIORITY -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.card_medium_priority,
                        context.theme
                    )
                }
                AppConstant.TaskPriority.LOW_PRIORITY -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.card_low_priority,
                        context.theme
                    )
                }
                else -> {
                    ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.card_low_priority,
                        context.theme
                    )
                }
            }
        }
    }

}