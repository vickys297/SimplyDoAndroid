package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerPlanListItemBinding
import com.example.simplydo.model.PaidPlanModel
import com.example.simplydo.utils.PaidPlain

class PaidPlansAdapter(
    private var currentSelectedPosition: Int = 0,
    val callback: PaidPlain.Callback
) :
    RecyclerView.Adapter<PaidPlansAdapter.PaidPlanViewHolder>() {
    var dataset = ArrayList<PaidPlanModel>()

    class PaidPlanViewHolder(val binding: RecyclerPlanListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PaidPlanModel): RecyclerPlanListItemBinding {
            return binding.apply {
                dataModel = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaidPlanViewHolder {
        return PaidPlanViewHolder(
            RecyclerPlanListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PaidPlanViewHolder, position: Int) {
        val item = dataset[position]
        holder.bind(item).let { bind ->

            bind.imageView13.isVisible = position == currentSelectedPosition

            bind.root.setOnClickListener {
                dataset[currentSelectedPosition].selected = false
                notifyItemChanged(currentSelectedPosition)

                bind.imageView13.isVisible = true
                currentSelectedPosition = position

                callback.onSelectPlan(position)
            }
        }
    }

    override fun getItemCount(): Int = dataset.size

    fun update(newDataset: ArrayList<PaidPlanModel>) {
        val currentSize = this.dataset.size
        val size = newDataset.size
        this.dataset = newDataset
        notifyItemRangeChanged(currentSize, size)
    }

}