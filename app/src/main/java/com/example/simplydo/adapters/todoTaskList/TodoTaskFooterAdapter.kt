package com.example.simplydo.adapters.todoTaskList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerTaskFooterItemBinding
import com.example.simplydo.utlis.NewTodo

class TodoTaskFooterAdapter(private val addTodoInterface: NewTodo.AddTask) :
    RecyclerView.Adapter<TodoTaskFooterAdapter.TodoFooterViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoFooterViewHolder {
        return TodoFooterViewHolder(
            RecyclerTaskFooterItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TodoFooterViewHolder, position: Int) {
        holder.apply {
            bind(addTodoInterface)
        }
    }

    override fun getItemCount(): Int = 1

    class TodoFooterViewHolder(val binding: RecyclerTaskFooterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(addTodoInterface: NewTodo.AddTask): RecyclerTaskFooterItemBinding {
            binding.apply {
                executePendingBindings()
            }

            binding.buttonAddText.setOnClickListener {
                addTodoInterface.onAddText()
            }
            binding.buttonAddList.setOnClickListener {
                addTodoInterface.onAddList()
            }
            return binding
        }

    }
}