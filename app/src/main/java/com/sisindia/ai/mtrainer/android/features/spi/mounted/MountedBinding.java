package com.sisindia.ai.mtrainer.android.features.spi.mounted;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MountedBinding {

    @BindingAdapter(value = {"setMountedRecyclerAdapter","myMountedViewListener"})
    public static void setMyUnitsReclerAdpter(RecyclerView recyclerView, MountedRecyclerAdapter recyclerAdapter, MountedViewListener mountedViewListener) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setListener(mountedViewListener);
    }
}
