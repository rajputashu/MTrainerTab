package com.sisindia.ai.mtrainer.android.features.spi

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter("spiPostRecyclerAdapter")
fun setSpiPostRecyclerAdapter(rv : RecyclerView, adapter : SpiPostDialogAdapter) {
    rv.layoutManager = LinearLayoutManager(rv.context)
    rv.adapter = adapter;
}