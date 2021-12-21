package com.example.simplydo.adapters.attachment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simplydo.databinding.RecyclerSelectedContactListItemBinding
import com.example.simplydo.model.ContactModel
import com.example.simplydo.utlis.SelectedContactInterface

class SelectedListContactAdapter(private val selectedContactInterFace: SelectedContactInterface) :
    RecyclerView.Adapter<SelectedListContactAdapter.ViewHolder>() {

    private var dataSet = ArrayList<ContactModel>()

    class ViewHolder(val binding: RecyclerSelectedContactListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContactModel) {
            binding.apply {
                dataModel = item
                executePendingBindings()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RecyclerSelectedContactListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        item.run {
            holder.bind(this@run)
            holder.itemView.tag = this@run

            holder.itemView.setOnClickListener {
                selectedContactInterFace.onContactRemove(item)
            }
        }

    }

    override fun getItemCount() = dataSet.size


    fun updateDatSet(newDataSet: ArrayList<ContactModel>) {
        this.dataSet = newDataSet
        notifyDataSetChanged()
    }
}