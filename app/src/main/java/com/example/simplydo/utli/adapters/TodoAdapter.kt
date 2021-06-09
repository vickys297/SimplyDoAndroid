package com.example.simplydo.utli.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerTodoCompletedListItemBinding
import com.example.simplydo.databinding.RecyclerTodoListItemBinding
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.TodoItemInterface

class TodoAdapter(
    @Nullable
    private val todoItemInterface: TodoItemInterface,
    val requireContext: Context,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var dataSet = ArrayList<TodoModel>()

    companion object {
        const val VIEW_TYPE_TASK = 0
        const val VIEW_TYPE_TASK_COMPLETED = 1
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
                val item = dataSet[position]
                item.run {
                    (holderTask as TaskViewHolder).apply {
                        val holder = bind(this@run, todoItemInterface)
                        itemView.tag = this@run

                        itemView.setOnLongClickListener {
                            todoItemInterface.onLongClick(item)
                            return@setOnLongClickListener true
                        }

                        holder.tvTitle.transitionName = "task_title_$position"
                        holder.tvTodo.transitionName = "task_todo_$position"

                        itemView.setOnClickListener {
                            val extras = FragmentNavigatorExtras(
                                holder.tvTitle to "transition_title",
                                holder.tvTodo to "transition_todo"
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
            1 -> {
                val item = dataSet[position]
                item.run {
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

    override fun getItemCount(): Int = dataSet.size

    fun updateDataSet(it: ArrayList<TodoModel>) {
        this.dataSet = it
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        return if (dataSet[position].isCompleted) VIEW_TYPE_TASK_COMPLETED else VIEW_TYPE_TASK
    }

    fun removeItemAtPosition(position: Int) {
        notifyItemRemoved(position)
        notifyDataSetChanged()
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