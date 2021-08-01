package com.example.simplydo.ui.fragments.addOrEditTodoTask.addItem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerNewTaskListItemBinding


class AddListItemAdapter(val dataSet: ArrayList<String>) :
    RecyclerView.Adapter<AddListItemAdapter.ListItemViewHolder>() {

    class ListItemViewHolder(val binding: RecyclerNewTaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String): RecyclerNewTaskListItemBinding {
            return binding.apply {
                data = item
                executePendingBindings()
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {

        return ListItemViewHolder(
            RecyclerNewTaskListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }


    override fun getItemCount(): Int = dataSet.size
    override fun onBindViewHolder(holder: ListItemViewHolder, position: Int) {
        val item = dataSet[position]
        holder.bind(item).apply {

        }
    }

}