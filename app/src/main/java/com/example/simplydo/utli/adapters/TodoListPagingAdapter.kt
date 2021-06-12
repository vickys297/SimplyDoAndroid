package com.example.simplydo.utli.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerTodoCompletedListItemBinding
import com.example.simplydo.databinding.RecyclerTodoListItemBinding
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.TodoItemInterface

class TodoListPagingAdapter internal constructor(
    val context: Context,
    val todoItemInterface: TodoItemInterface
) :
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

        fun bind(todoModelData: TodoModel): RecyclerTodoListItemBinding {
            return binding.apply {
                todoModel = todoModelData
                executePendingBindings()
            }
        }

    }

    class CompletedTaskViewHolder(private val binding: RecyclerTodoCompletedListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoModel): RecyclerTodoCompletedListItemBinding {
            binding.tvTitle.paintFlags = binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvTodo.paintFlags = binding.tvTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            return binding.apply {
                todoModel = item
                executePendingBindings()
            }

        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when (getItemViewType(position)) {
            VIEW_TYPE_TASK -> {
                val item = getItem(position)
                item?.run {
                    (holder as TodoViewHolder).apply {
                        val todoHolder = bind(this@run)

                        todoHolder.tvTitle.transitionName = "past_task_title_$position"
                        todoHolder.tvTodo.transitionName = "past_task_todo_$position"

                        itemView.setOnClickListener {
                            val extras = FragmentNavigatorExtras(
                                todoHolder.tvTitle to "transition_title",
                                todoHolder.tvTodo to "transition_todo"
                            )

                            todoItemInterface.onTaskClick(
                                this@run,
                                absoluteAdapterPosition,
                                extras
                            )
                        }
                    }
                }
            }
            VIEW_TYPE_TASK_COMPLETED -> {
                val item = getItem(position)
                item?.run {
                    (holder as CompletedTaskViewHolder).apply {
                       val completedHolder =  bind(this@run)

                        completedHolder.tvTitle.transitionName = "completed_task_title_$position"
                        completedHolder.tvTodo.transitionName = "completed_task_todo_$position"

                        itemView.setOnClickListener {
                            val extras = FragmentNavigatorExtras(
                                completedHolder.tvTitle to "transition_title",
                                completedHolder.tvTodo to "transition_todo"
                            )

                            todoItemInterface.onTaskClick(
                                this@run,
                                absoluteAdapterPosition,
                                extras
                            )
                        }
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
            QuickTodoListAdapter.VIEW_TYPE_TASK_COMPLETED
        } else {
            QuickTodoListAdapter.VIEW_TYPE_TASK
        }
    }

}