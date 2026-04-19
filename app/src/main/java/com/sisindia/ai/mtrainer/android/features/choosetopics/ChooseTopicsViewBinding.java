package com.sisindia.ai.mtrainer.android.features.choosetopics;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChooseTopicsViewBinding {
    @BindingAdapter(value = {"setChoosetopicsRecyclerAdapter"})
    public static void setTakeAssessmentReclerAdpter(RecyclerView recyclerView, ChooseTopicsRecyclerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
    @BindingAdapter(value = {"setAdhocTopicRecyclerAdapter"})
    public static void setAdhocTopicReclerAdpter(RecyclerView recyclerView, AdhocTopicRecyclerAdapter recyclerAdapter) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }
}
