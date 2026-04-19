package com.sisindia.ai.mtrainer.android.features.spi

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sisindia.ai.mtrainer.android.R
import com.sisindia.ai.mtrainer.android.databinding.SpiPostDialogItemBinding
import com.sisindia.ai.mtrainer.android.features.spi.model.SpiPostResponse

class SpiPostDialogAdapter : RecyclerView.Adapter<SpiPostDialogAdapter.SpiPostDialogViewHolder>() {
    val list = mutableListOf<SpiPostResponse.SpiPostdata>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpiPostDialogViewHolder {
        val binding = DataBindingUtil.inflate<SpiPostDialogItemBinding>(LayoutInflater.from(parent.context), R.layout.spi_post_dialog_item, parent, false)
        return SpiPostDialogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpiPostDialogViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount() = list.size

    fun setData(data : List<SpiPostResponse.SpiPostdata>) {
        list.clear()
        list.addAll(data)
    }

    class SpiPostDialogViewHolder(val binding : SpiPostDialogItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(data : SpiPostResponse.SpiPostdata) {
            binding.item = data
        }
    }
}