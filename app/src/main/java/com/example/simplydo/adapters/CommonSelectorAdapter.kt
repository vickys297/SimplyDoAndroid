package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.databinding.RecyclerItemSelectorBinding
import com.example.simplydo.model.SelectorDataModal
import com.example.simplydo.utlis.CommonSelector

class CommonSelectorAdapter(
    val arrayList: ArrayList<SelectorDataModal>,
    val callback: CommonSelector,
    private var currentSelectedPeriod: Int
) :
    RecyclerView.Adapter<CommonSelectorAdapter.SelectorViewHolder>() {

    class SelectorViewHolder(
        val binding: RecyclerItemSelectorBinding,
        val callback: CommonSelector
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: SelectorDataModal,
            position: Int,
            currentSelectedPeriod: Int
        ): RecyclerItemSelectorBinding {
            binding.apply {
                selectorData = item
                executePendingBindings()
            }

            if (position == currentSelectedPeriod) {
                binding.button2.setBackgroundResource(R.drawable.day_selector_selected_background)
            }
            return binding
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectorViewHolder {
        return SelectorViewHolder(
            RecyclerItemSelectorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            callback
        )
    }

    override fun onBindViewHolder(holder: SelectorViewHolder, position: Int) {
        val item = arrayList[position]
        val bind = holder.bind(item, position, currentSelectedPeriod)

        holder.itemView.setOnClickListener {
            if (item.type == 1) {
                changeFrequency(position, currentSelectedPeriod, arrayList)
                callback.onPeriodSelected(arrayList)
            } else {
                changeWeek(position)
                callback.onWeekSelected(arrayList)
            }
        }

        when (item.selected) {
            true -> {
                bind.button2.setBackgroundResource(R.drawable.day_selector_selected_background)
            }
            false -> {
                bind.button2.setBackgroundResource(R.drawable.day_selector_not_selected_background)
            }
        }

    }

    private fun changeWeek(position: Int) {
        arrayList[position].selected = !arrayList[position].selected
        notifyItemChanged(position)
    }

    private fun changeFrequency(
        updatedPosition: Int,
        currentSelectedPeriod: Int,
        arrayList: ArrayList<SelectorDataModal>
    ) {
        arrayList[updatedPosition].selected = true
        notifyItemChanged(updatedPosition)
        arrayList[currentSelectedPeriod].selected = false
        notifyItemChanged(currentSelectedPeriod)
        this.currentSelectedPeriod = updatedPosition
    }

    override fun getItemCount() = arrayList.size
}
