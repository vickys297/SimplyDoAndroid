package com.example.simplydo.utli.adapters

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerTodoCompletedListItemBinding
import com.example.simplydo.databinding.RecyclerTodoListItemBinding
import com.example.simplydo.model.TodoModel
import com.example.simplydo.utli.AppConstant
import com.example.simplydo.utli.AppFunctions
import com.example.simplydo.utli.TodoItemInterface
import java.util.*

internal val TAG_Other = TodoListPagingAdapter::class.java.canonicalName
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

        const val VIEW_TYPE_TASK_PAST = 0
        const val VIEW_TYPE_TASK_COMPLETED = 1
    }


    class TodoViewHolder(val binding: RecyclerTodoListItemBinding, requireContext: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(todoModelData: TodoModel): RecyclerTodoListItemBinding {
            binding.apply {
                todoModel = todoModelData
                executePendingBindings()
            }

            when (AppFunctions.getEventDateText(todoModelData.eventDate)) {
                AppConstant.EVENT_TODAY -> {
                    binding.tvEventDate.text = AppConstant.EVENT_TODAY
                }
                AppConstant.EVENT_TOMORROW -> {
                    binding.tvEventDate.text = AppConstant.EVENT_TOMORROW
                }
                AppConstant.EVENT_YESTERDAY -> {
                    binding.tvEventDate.text = AppConstant.EVENT_YESTERDAY
                    binding.chipDateExpired.visibility = View.VISIBLE
                }
                else -> {
                    binding.tvEventDate.text = AppFunctions.getDateStringFromMilliseconds(
                        todoModelData.eventDate,
                        AppConstant.DATE_PATTERN_EVENT_DATE
                    )
                }
            }

            if (AppFunctions.checkForDateTimeExpire(todoModelData)) {
                binding.chipDateExpired.visibility = View.VISIBLE
            } else {
                binding.chipDateExpired.visibility = View.GONE
            }

            if (todoModelData.eventTime.isNotEmpty()) {
                binding.tvEventTime.text = String.format(
                    "@ %s",
                    AppFunctions.convertTimeStringToDisplayFormat(todoModelData.eventTime)
                )
            }

            return binding
        }


    }

    class CompletedTaskViewHolder(private val binding: RecyclerTodoCompletedListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(todoModelData: TodoModel): RecyclerTodoCompletedListItemBinding {

            binding.apply {
                todoModel = todoModelData
                executePendingBindings()
            }
            binding.tvTitle.paintFlags = binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            binding.tvTodo.paintFlags = binding.tvTodo.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG


            when (AppFunctions.getEventDateText(todoModelData.eventDate)) {
                AppConstant.EVENT_TODAY -> {
                    binding.tvEventDate.text = AppConstant.EVENT_TODAY
                }
                AppConstant.EVENT_TOMORROW -> {
                    binding.tvEventDate.text = AppConstant.EVENT_TOMORROW
                }
                AppConstant.EVENT_YESTERDAY -> {
                    binding.tvEventDate.text = AppConstant.EVENT_YESTERDAY
                }
                else -> {
                    binding.tvEventDate.text = AppFunctions.getDateStringFromMilliseconds(
                        todoModelData.eventDate,
                        AppConstant.DATE_PATTERN_EVENT_DATE
                    )
                }
            }

            if (todoModelData.eventTime.isNotEmpty()) {
                binding.tvEventTime.text = String.format(
                    "@ %s",
                    AppFunctions.convertTimeStringToDisplayFormat(todoModelData.eventTime)
                )
            }

            return binding

        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


        when (getItemViewType(position)) {
            VIEW_TYPE_TASK_PAST -> {
                val item = getItem(position)
                item?.run {
                    (holder as TodoViewHolder).apply {
                        val todoHolder = bind(this@run)
                        itemView.setOnClickListener {
                            todoItemInterface.onTaskClick(
                                this@run,
                                absoluteAdapterPosition
                            )
                        }
                    }
                }
            }
            VIEW_TYPE_TASK_COMPLETED -> {
                val item = getItem(position)
                item?.run {
                    (holder as CompletedTaskViewHolder).apply {
                     bind(this@run)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {


        return when (viewType) {
            VIEW_TYPE_TASK_PAST -> {
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