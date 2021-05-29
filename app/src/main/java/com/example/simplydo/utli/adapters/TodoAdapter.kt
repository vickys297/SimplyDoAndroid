package com.example.simplydo.utli.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerCompletedTodoListItemBinding
import com.example.simplydo.databinding.RecyclerTodoListItemBinding
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.TodoAdapterInterface

class TodoAdapter(
    private val context: Context,
    private val todoAdapterInterface: TodoAdapterInterface,
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
                    RecyclerCompletedTodoListItemBinding.inflate(
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
                        bind(this@run)
                        itemView.tag = this@run

                        itemView.setOnLongClickListener {
                            todoAdapterInterface.onLongClick(item)
                            return@setOnLongClickListener true
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
                            todoAdapterInterface.onLongClick(item)
                            return@setOnLongClickListener true
                        }
                    }
                }
            }

        }


    }

    override fun getItemCount(): Int = dataSet.size

    fun updateItem(it: ArrayList<TodoModel>) {

        this.dataSet = it
        notifyDataSetChanged()
    }


    override fun getItemViewType(position: Int): Int {
        return dataSet[position].isCompleted
    }

    fun removeItemAtPosition(position: Int) {
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }


    class TaskViewHolder(private val binding: RecyclerTodoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoModel) {
            binding.apply {
                todoModel = item
                executePendingBindings()
            }

        }
    }

    class CompletedTaskViewHolder(private val binding: RecyclerCompletedTodoListItemBinding) :
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