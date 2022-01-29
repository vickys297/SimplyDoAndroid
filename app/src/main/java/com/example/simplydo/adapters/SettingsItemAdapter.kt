package com.example.simplydo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplydo.databinding.RecyclerSettingsItemBinding
import com.example.simplydo.model.SettingsItemModel

class SettingsItemAdapter(
    val dataSet: ArrayList<SettingsItemModel>,
    private val settingsCallback: SettingsCallback
) :
    RecyclerView.Adapter<SettingsItemAdapter.SettingsViewHolder>() {

    class SettingsViewHolder(val binding: RecyclerSettingsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SettingsItemModel): RecyclerSettingsItemBinding {
            return binding.apply {
                itemModel = item
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        return SettingsViewHolder(
            RecyclerSettingsItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        val item = dataSet[position]
        holder.bind(item).let {
            Glide
                .with(it.root)
                .load(item.drawable)
                .into(it.imageViewIcon)


            it.root.setOnClickListener {
                settingsCallback.onSettingsClick(item)
            }
        }

    }

    override fun getItemCount(): Int = dataSet.size

    interface SettingsCallback {
        fun onSettingsClick(item: SettingsItemModel)
    }
}