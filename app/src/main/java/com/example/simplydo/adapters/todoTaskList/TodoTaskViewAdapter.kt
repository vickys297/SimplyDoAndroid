package com.example.simplydo.adapters.todoTaskList

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerTaskListViewItemBinding
import com.example.simplydo.databinding.RecyclerTaskTextViewItemBinding
import com.example.simplydo.model.TodoTaskModel
import com.example.simplydo.utlis.AppConstant
import com.example.simplydo.utlis.NewTodo


internal val TAG_TASK_VIEW = TodoTaskViewAdapter::class.java.canonicalName

class TodoTaskViewAdapter(
    var dataSet: ArrayList<TodoTaskModel>,
    private val addTodoInterface: NewTodo.AddTask?,
    private val todoTaskInterface: NewTodo.TodoTask?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LIST -> {
                TodoListViewHolder(
                    RecyclerTaskListViewItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_TEXT -> {
                TodoTextViewHolder(
                    RecyclerTaskTextViewItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw Exception("No View Found")
            }
        }
    }


    class TodoTextViewHolder(val binding: RecyclerTaskTextViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(): RecyclerTaskTextViewItemBinding {
            return binding.apply {
                executePendingBindings()
            }
        }

    }

    class TodoListViewHolder(val binding: RecyclerTaskListViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(): RecyclerTaskListViewItemBinding {
            return binding.apply {
                executePendingBindings()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.i(TAG_TASK, "onBindViewHolder: ${getItemViewType(position)}")
        when (getItemViewType(position)) {
            VIEW_TYPE_LIST -> {
                val item = dataSet[position]
                (holder as TodoListViewHolder).apply {
                    val binding = bind()
                    addListViewToParent(binding, item.contentList!!)



                    holder.itemView.setOnClickListener {
                        todoTaskInterface?.onTaskSelect(item)
                    }
                }
            }
            VIEW_TYPE_TEXT -> {
                val item = dataSet[position]
                (holder as TodoTextViewHolder).apply {
                    val binding = bind()
                    binding.textViewNote.text = item.content


                    holder.itemView.setOnClickListener {
                        todoTaskInterface?.onTaskSelect(item)
                    }
                }
            }
        }


    }

    private fun addListViewToParent(
        binding: RecyclerTaskListViewItemBinding,
        tasks: ArrayList<String>
    ) {

        binding.linearLayoutListItem.removeAllViews()

        val layoutParams = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        for (task in tasks) {

            val textView = TextView(binding.root.context)
            textView.layoutParams = layoutParams
            textView.text = task
            textView.textSize = 14F
            textView.layout(4, 4, 4, 4)

            binding.linearLayoutListItem.addView(textView)
        }


    }

    override fun getItemViewType(position: Int): Int {
        super.getItemViewType(position)

        return when (dataSet[position].type) {
            AppConstant.Task.VIEW_TASK_NOTE_LIST -> {
                VIEW_TYPE_LIST
            }
            AppConstant.Task.VIEW_TASK_NOTE_TEXT -> {
                VIEW_TYPE_TEXT
            }
            else -> {
                throw Exception("No View Found")
            }
        }

    }

    override fun getItemCount(): Int = dataSet.size

    fun updateDataSet(dataSet: ArrayList<TodoTaskModel>) {
        val lastSize = this.dataSet.size
        this.dataSet = dataSet
        notifyDataSetChanged()
    }

}