package com.example.simplydo.screens.todoList.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerTodoListItemBinding
import com.example.simplydo.model.TodoList

class TodoAdapter(private val context: Context) :
    RecyclerView.Adapter<TodoAdapter.ViewHolder>() {
    private var dataSet = ArrayList<TodoList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            RecyclerTodoListItemBinding.inflate(
                layoutInflater,
                parent,
                false
            ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        item.run {
            holder.apply {
                bind(this@run)
                itemView.tag = this@run
            }
        }
        holder.itemView
    }

    override fun getItemCount(): Int = dataSet.size

    fun updateItem(it: ArrayList<TodoList>) {
        this.dataSet = it
        notifyDataSetChanged()
    }

    fun removeItemAtPosition(position: Int) {
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }


    class ViewHolder(private val binding: RecyclerTodoListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TodoList) {
            binding.apply {
                todoModel = item
                executePendingBindings()
            }
        }
    }
}