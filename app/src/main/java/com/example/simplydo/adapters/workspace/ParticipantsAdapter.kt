package com.example.simplydo.adapters.workspace

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.simplydo.databinding.RecyclerParticipantListItemBinding
import com.example.simplydo.model.UserAccountModel
import com.example.simplydo.utlis.ParticipantInterface

class ParticipantsAdapter(
    private val isRemoveVisible: Boolean,
    private val callback: ParticipantInterface? = null
) : RecyclerView.Adapter<ParticipantsAdapter.ViewHolder>() {

    var dataset: ArrayList<UserAccountModel> = arrayListOf()

    class ViewHolder(val binding: RecyclerParticipantListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userAccountModel: UserAccountModel): RecyclerParticipantListItemBinding {
            return binding.apply {
                dataModel = userAccountModel
                executePendingBindings()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            RecyclerParticipantListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]
        item.run {
            holder.bind(this@run).let { bind ->

                if (isRemoveVisible) {
                    bind.imageButtonSelected.isVisible = item.selected
                    holder.itemView.setOnClickListener {
                        item.selected = !item.selected
                        bind.imageButtonSelected.isVisible = item.selected
                        callback?.onParticipantSelected(item)
                    }
                } else {
                    bind.imageButtonSelected.isVisible = isRemoveVisible
                }

                Glide
                    .with(bind.root)
                    .load(item.user.profilePicture)
                    .centerCrop()
                    .into(bind.profileImageView)
            }


        }
    }

    override fun getItemCount(): Int = dataset.size

    fun updateDataset(newDataset: ArrayList<UserAccountModel>) {
        val lastPosition = dataset.size
        this.dataset = newDataset
        notifyItemRangeChanged(lastPosition, newDataset.size)
    }
}