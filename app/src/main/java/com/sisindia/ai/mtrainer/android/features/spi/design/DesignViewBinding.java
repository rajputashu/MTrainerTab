package com.sisindia.ai.mtrainer.android.features.spi.design;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DesignViewBinding {

    @BindingAdapter(value = {"setDesignRecyclerAdapter"})
    public static void setMyUnitsReclerAdpter(RecyclerView recyclerView, DesignRecyclerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
