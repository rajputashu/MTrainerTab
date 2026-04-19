package com.sisindia.ai.mtrainer.android.features.timeline;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sisindia.ai.mtrainer.android.features.rota.TimeLineRecyclerAdapter;

public class TimelineViewBindings {
    @BindingAdapter(value = {"timelineRecyclerAdapter1"})
    public static void setTimelineRecyclerAdapter1 (RecyclerView recyclerView, TimeLineRecyclerAdapter1 recyclerAdapter){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
