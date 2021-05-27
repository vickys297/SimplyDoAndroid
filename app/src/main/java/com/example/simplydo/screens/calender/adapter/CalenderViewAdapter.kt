package com.example.simplydo.screens.calender.adapter

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

class CalenderViewAdapter(
    val context: Context,
    private val calenderAdapterInterface: CalenderAdapterInterface,
) : RecyclerView.Adapter<CalenderViewAdapter.ViewHolder>() {


    private lateinit var dataset: ArrayList<SmallCalenderModel>

    private var activePosition: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            RecyclerCalenderListItemBinding.inflate(
                layoutInflater,
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

                if (activePosition == absoluteAdapterPosition) {
                    val sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        layout.setBackgroundDrawable(ContextCompat.getDrawable(context,
                            R.drawable.active_calender_view))
                    } else {
                        layout.background =
                            ContextCompat.getDrawable(context, R.drawable.active_calender_view)
                    }
                }

                itemView.setOnClickListener {
                    calenderAdapterInterface.onDateSelect(absoluteAdapterPosition, dateEvent = item.date)
                }
            }
        }
    }

    class ViewHolder(private val binding: RecyclerCalenderListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SmallCalenderModel) {
            binding.apply {
                dataModel = item
                executePendingBindings()
            }
        }
    }

    override fun getItemCount(): Int = dataset.size


    fun updateList(smallCalenderModels: ArrayList<SmallCalenderModel>) {
        this.dataset = smallCalenderModels
        notifyDataSetChanged()
    }

    fun setActiveDate(position: Int) {
        this.activePosition = position
        notifyDataSetChanged()
    }

}