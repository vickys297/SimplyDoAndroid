package com.example.simplydo.adapters

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerGroupListItemBinding
import com.example.simplydo.model.entity.WorkspaceGroupModel
import com.example.simplydo.utlis.AppInterface

//internal val TAG = GroupViewAdapter::class.java.canonicalName

class GroupViewAdapter(
    val dataset: ArrayList<WorkspaceGroupModel>,
    val callback: AppInterface.GroupViewCallback,
    private val fragmentContext: Context
) :
    RecyclerView.Adapter<GroupViewAdapter.GroupViewItemViewHolder>() {

    private lateinit var userProfileStackAdapter: UserProfileStackAdapter

    class GroupViewItemViewHolder(val binding: RecyclerGroupListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: WorkspaceGroupModel,
            callback: AppInterface.GroupViewCallback
        ): RecyclerGroupListItemBinding {
            binding.apply {
                dataModel = item
                executePendingBindings()
            }

            binding.root.setOnClickListener {
                callback.onSelect(item)
            }

            return binding
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewItemViewHolder {
        return GroupViewItemViewHolder(
            RecyclerGroupListItemBinding.inflate(
                LayoutInflater.from(
                    fragmentContext
                ), parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GroupViewItemViewHolder, position: Int) {
        val item = dataset[position]

        userProfileStackAdapter = UserProfileStackAdapter(dataset = item.people)

        holder.bind(item, callback).let { bind ->


            bind.recyclerView.apply {
                layoutManager = LinearLayoutManager(
                    holder.binding.root.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                adapter = userProfileStackAdapter
                addItemDecoration(OverlapRecyclerViewDecoration(4, -25))
            }


        }


    }


    override fun getItemCount(): Int = dataset.size

    class OverlapRecyclerViewDecoration(
        private val overlapLimit: Int,
        private val overlapWidth: Int
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            //-----get current position of item
            val itemPosition = parent.getChildAdapterPosition(view)

            //-----avoid first item decoration else it will go of the screen
            if (itemPosition == 0) {
                return
            } else {
                //-----apply decoration
                when {
                    itemPosition < overlapLimit -> outRect.set(overlapWidth, 0, 0, 0)
                    else -> outRect.set(0, 0, 0, 0)
                }
            }
        }
    }
}