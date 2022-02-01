package com.example.simplydo.adapters

import androidx.recyclerview.widget.RecyclerView

class RecyclerViewEmptyStateAdapter(

):RecyclerView.AdapterDataObserver() {
    override fun onChanged() {
        super.onChanged()
        checkIsDatasetEmpty()

    }

    private fun checkIsDatasetEmpty() {
        TODO("Not yet implemented")
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        super.onItemRangeChanged(positionStart, itemCount)

    }
}