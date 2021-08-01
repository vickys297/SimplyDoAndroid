package com.example.simplydo.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerTaskFooterItemBinding
import com.example.simplydo.databinding.RecyclerTaskListItemBinding
import com.example.simplydo.databinding.RecyclerTaskTextItemBinding
import com.example.simplydo.model.TodoTaskModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.NewTodo

internal const val VIEW_TYPE_LIST = 0
internal const val VIEW_TYPE_TEXT = 1
internal const val VIEW_TYPE_FOOTER = 2


internal val TAG_TASK = TodoTaskAdapter::class.java.canonicalName

class TodoTaskAdapter(
    val dataSet: ArrayList<TodoTaskModel>,
    val addTodoInterface: NewTodo.AddTask
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        Log.i(TAG_TASK, "onCreateViewHolder: $viewType")
        return when (viewType) {
            VIEW_TYPE_LIST -> {
                TodoListViewHolder(
                    RecyclerTaskListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_TEXT -> {
                TodoTextViewHolder(
                    RecyclerTaskTextItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_FOOTER -> {
                TodoFooterViewHolder(
                    RecyclerTaskFooterItemBinding.inflate(
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

    class TodoTextViewHolder(val binding: RecyclerTaskTextItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(): RecyclerTaskTextItemBinding {
            return binding.apply {
                executePendingBindings()
            }
        }

    }

    class TodoListViewHolder(val binding: RecyclerTaskListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(): RecyclerTaskListItemBinding {
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
                    addListViewToParent(binding, item.taskList!!)
                }
            }
            VIEW_TYPE_TEXT -> {
                val item = dataSet[position]
                (holder as TodoTextViewHolder).apply {
                    val binding = bind()
                    binding.textViewNote.text = item.taskText
                }
            }
            VIEW_TYPE_FOOTER -> {
                (holder as TodoFooterViewHolder).apply {
                    bind(addTodoInterface)
                }
            }
        }


    }

    private fun addListViewToParent(
        binding: RecyclerTaskListItemBinding,
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

        if ((dataSet.size - 1) == position) {
            return VIEW_TYPE_FOOTER
        }

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

}