package com.example.simplydo.utli.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.R
import com.example.simplydo.databinding.RecyclerCalenderListItemBinding
import com.example.simplydo.model.SmallCalenderModel
import com.example.simplydo.utli.CalenderAdapterInterface

internal val TAG = CalenderViewAdapter::class.java.canonicalName
class CalenderViewAdapter(
    val context: Context,
    private val calenderAdapterInterface: CalenderAdapterInterface,
) :
    RecyclerView.Adapter<CalenderViewAdapter.ViewHolder>() {

    private var dataset = ArrayList<SmallCalenderModel>()
    private var activePosition: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            RecyclerCalenderListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        item.run {
            holder.apply {
                bind(this@run)
                itemView.tag = this@run

                val layout = itemView.rootView.findViewById<ConstraintLayout>(R.id.calenderLayout)

                if (item.isActive) {
                    layout.background =
                        ContextCompat.getDrawable(context, R.drawable.active_calender_view)
                }

                itemView.setOnClickListener {

                }

            }
        }
    }

    inner class ViewHolder(private val binding: RecyclerCalenderListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SmallCalenderModel) {
            binding.apply {
                dataModel = item
                executePendingBindings()
            }

            binding.root.setOnClickListener {

                dataset[activePosition].isActive = false
                notifyItemChanged(activePosition)
                item.isActive = true
                notifyItemChanged(absoluteAdapterPosition)

                calenderAdapterInterface.onDateSelect(position = absoluteAdapterPosition,
                    dateEvent = item.date)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    fun updateList(smallCalenderModels: ArrayList<SmallCalenderModel>) {
        dataset = smallCalenderModels
        notifyDataSetChanged()
    }

    fun setActiveDate(position: Int) {

        this.activePosition = position

//        Log.i(TAG, "setActiveDate: position : $position")
//
//        //  update old position task to not active
//        dataset[this.activePosition].isActive = false
//        notifyItemChanged(this.activePosition)
//
//        //  update new position task to active
//        dataset[position].isActive = true
//        notifyItemChanged(position)
//
//        //  update the active position to new position
//        this.activePosition = position
//
//        dataset.forEach {
//            Log.i(TAG,
//                "setActiveDate: Event Date : ${it.dateOfMonth} is Active : ${it.isActive}")
//        }
    }

}