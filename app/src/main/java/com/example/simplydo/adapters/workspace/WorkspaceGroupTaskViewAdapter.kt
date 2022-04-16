package com.example.simplydo.adapters.workspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.adapters.GroupViewAdapter
import com.example.simplydo.adapters.UserProfileStackAdapter
import com.example.simplydo.databinding.RecyclerEmptyListItemBinding
import com.example.simplydo.databinding.RecyclerTaskStatusHeaderBinding
import com.example.simplydo.databinding.RecyclerTodoWorkspaceListItemBinding
import com.example.simplydo.model.GroupTaskByProgressModel
import com.example.simplydo.model.privateWorkspace.WorkspaceGroupTaskModel
import com.example.simplydo.utlis.AppInterface

internal const val ITEM_VIEW_HEADER = 0
internal const val ITEM_VIEW_CONTENT = 1
internal const val ITEM_VIEW_EMPTY = 2

class WorkspaceGroupTaskViewAdapter(private val callback: AppInterface.WorkspaceGroupTask.Task) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var dataSet: ArrayList<GroupTaskByProgressModel> = arrayListOf()

    private lateinit var userProfileStackAdapter: UserProfileStackAdapter

    override fun getItemViewType(position: Int): Int {
        val item = dataSet[position]
        return when {
            item.taskHeader != null -> {
                ITEM_VIEW_HEADER
            }
            item.message != null -> {
                ITEM_VIEW_EMPTY
            }
            else -> {
                ITEM_VIEW_CONTENT
            }
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataSet[position]
        when (getItemViewType(position)) {
            ITEM_VIEW_HEADER -> {
                item.taskHeader?.run {
                    (holder as TodoViewHeaderHolder).apply {
                        bind(this@run)
                    }
                }
            }
            ITEM_VIEW_EMPTY -> {
                item.message?.run {
                    (holder as TodoViewEmptyHolder).apply {
                        bind(this@run)
                    }
                }
            }
            ITEM_VIEW_CONTENT -> {
                item.content?.run {
                    (holder as TodoViewHolder).apply {
                        bind(this@run).let { binding ->
                            binding.recyclerViewStackedProfileView.apply {
                                userProfileStackAdapter =
                                    UserProfileStackAdapter(dataset = this@run.taskParticipants)
                                layoutManager = LinearLayoutManager(
                                    binding.root.context,
                                    LinearLayoutManager.HORIZONTAL,
                                    false
                                )
                                adapter = userProfileStackAdapter
                                addItemDecoration(
                                    GroupViewAdapter.OverlapRecyclerViewDecoration(
                                        4,
                                        -25
                                    )
                                )
                            }
                        }
                    }

                    holder.itemView.setOnClickListener {
                        callback.onTaskSelected(this@run)
                    }

                }


            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_CONTENT -> {
                TodoViewHolder(
                    RecyclerTodoWorkspaceListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ITEM_VIEW_HEADER -> {
                TodoViewHeaderHolder(
                    RecyclerTaskStatusHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            ITEM_VIEW_EMPTY -> {
                TodoViewEmptyHolder(
                    RecyclerEmptyListItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> {
                throw Exception("No View Attached")
            }
        }
    }

    class TodoViewHeaderHolder(val binding: RecyclerTaskStatusHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(groupTaskByProgressModel: GroupTaskByProgressModel.TaskHeaderContent) {
            binding.apply {
                dataMode = groupTaskByProgressModel
                executePendingBindings()
            }
        }
    }

    class TodoViewHolder(
        val binding: RecyclerTodoWorkspaceListItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WorkspaceGroupTaskModel): RecyclerTodoWorkspaceListItemBinding {
            return binding.apply {
                dataModel = item
                executePendingBindings()
            }


        }
    }

    class TodoViewEmptyHolder(val binding: RecyclerEmptyListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String): RecyclerEmptyListItemBinding {
            binding.textView55.text = message

            return binding.apply {
                executePendingBindings()
            }
        }

    }

    fun updateDataset(groupTaskByProgressModel: ArrayList<GroupTaskByProgressModel>) {
//        val lastPosition = dataSet.size
        this.dataSet = groupTaskByProgressModel
        notifyDataSetChanged()
    }
}


